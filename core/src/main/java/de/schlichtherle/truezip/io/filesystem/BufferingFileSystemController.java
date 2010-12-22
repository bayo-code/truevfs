/*
 * Copyright (C) 2010 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schlichtherle.truezip.io.filesystem;

import de.schlichtherle.truezip.io.filesystem.concurrent.ConcurrentFileSystemModel;
import de.schlichtherle.truezip.io.entry.Entry;
import de.schlichtherle.truezip.io.rof.ReadOnlyFile;
import de.schlichtherle.truezip.io.socket.DecoratingInputSocket;
import de.schlichtherle.truezip.io.socket.DecoratingOutputSocket;
import de.schlichtherle.truezip.io.socket.IOBuffer;
import de.schlichtherle.truezip.io.socket.IOPool;
import de.schlichtherle.truezip.io.socket.OutputSocket;
import de.schlichtherle.truezip.io.socket.InputSocket;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.ExceptionBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;

import static de.schlichtherle.truezip.io.entry.Entry.Type.FILE;
import static de.schlichtherle.truezip.io.socket.IOBuffer.Strategy.*;
import static de.schlichtherle.truezip.io.filesystem.SyncOption.ABORT_CHANGES;
import static de.schlichtherle.truezip.io.filesystem.SyncOption.CLEAR_BUFFERS;

/**
 * A caching archive controller implements a caching strategy for entries
 * within its target archive file.
 * Decorating an archive controller with this class has the following effects:
 * <ul>
 * <li>Upon the first read operation, the data will be read from the archive
 *     entry and stored in the buffer.
 *     Subsequent or concurrent read operations will be served from the buffer
 *     without re-reading the data from the archive entry again until the
 *     target archive file gets {@link #sync synced}.
 * <li>Any data written to the buffer will get written to the target archive
 *     file if and only if the target archive file gets {@link #sync synced}.
 * <li>After a write operation, the data will be stored in the buffer for
 *     subsequent read operations until the target archive file gets
 *     {@link #sync synced}.
 * </ul>
 * <p>
 * Caching an archive entry is automatically used for an
 * {@link #getInputSocket input socket} with the input option
 * {@link InputOption#BUFFER} set or an {@link #getOutputSocket output socket}
 * with the output option {@link OutputOption#BUFFER} set.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@NotThreadSafe
public final class BufferingFileSystemController<
        M extends ConcurrentFileSystemModel,
        C extends FileSystemController<? extends M>>
extends DecoratingFileSystemController<M, C> {

    private final IOPool<?> pool;
    private final Map<FileSystemEntryName, EntryBuffer> buffers
            = new HashMap<FileSystemEntryName, EntryBuffer>();

    /**
     * Constructs a new content caching file system controller.
     *
     * @param controller the decorated file system controller.
     */
    public BufferingFileSystemController(   @NonNull final C controller,
                                            @NonNull final IOPool<?> pool) {
        super(controller);
        if (null == pool)
            throw new NullPointerException();
        this.pool = pool;
    }

    @Override
    public InputSocket<?> getInputSocket(
            final FileSystemEntryName name,
            final BitField<InputOption> options) {
        return new Input(name, options);
    }

    private class Input extends DecoratingInputSocket<Entry> {
        final FileSystemEntryName name;
        final BitField<InputOption> options;

        Input(final FileSystemEntryName name, final BitField<InputOption> options) {
            super(delegate.getInputSocket(name, options));
            this.name = name;
            this.options = options;
        }

        @Override
        public InputSocket<?> getBoundSocket() throws IOException {
            final EntryBuffer buffer = buffers.get(name);
            if (null == buffer && !options.get(InputOption.BUFFER))
                return super.getBoundSocket(); // dont buffer
            return (null != buffer ? buffer : new EntryBuffer(name))
                    .configure(options).getInputSocket().bind(this);
        }
    } // class Input

    @Override
    public OutputSocket<?> getOutputSocket(
            final FileSystemEntryName name,
            final BitField<OutputOption> options,
            final Entry template) {
        return new Output(name, options, template);
    }

    private class Output extends DecoratingOutputSocket<Entry> {
        final FileSystemEntryName name;
        final BitField<OutputOption> options;
        final Entry template;

        Output( final FileSystemEntryName name,
                final BitField<OutputOption> options,
                final Entry template) {
            super(delegate.getOutputSocket(name, options, template));
            this.name = name;
            this.options = options;
            this.template = template;
        }

        @Override
        public OutputSocket<?> getBoundSocket() throws IOException {
            assert getModel().writeLock().isHeldByCurrentThread();

            final EntryBuffer buffer = buffers.get(name);
            if (null == buffer) {
                if (!options.get(OutputOption.BUFFER))
                    return super.getBoundSocket(); // dont buffer
            } else {
                if (options.get(OutputOption.APPEND)) {
                    // This combination of features would be expected to work
                    // with a WRITE_THROUGH buffer strategy.
                    // However, we are using WRITE_BACK for performance reasons
                    // and we can't change the strategy because the buffer might
                    // be busy on input!
                    // So if this is really required, change the caching
                    // strategy to WRITE_THROUGH and bear the performance
                    // impact.
                    assert false; // FIXME: Check and fix this!
                    buffer.flush();
                }
            }
            // Create marker entry and mind CREATE_PARENTS!
            delegate.mknod(name, FILE, options, template);
            getModel().setTouched(true);
            return (null != buffer ? buffer : new EntryBuffer(name))
                    .configure(options, template).getOutputSocket().bind(this);
        }
    } // class Output

    @Override
    public void unlink(final FileSystemEntryName name) throws IOException {
        assert getModel().writeLock().isHeldByCurrentThread();

        delegate.unlink(name);
        final EntryBuffer buffer = buffers.remove(name);
        if (null != buffer)
            buffer.clear();
    }

    @Override
    public <X extends IOException>
    void sync(  @NonNull final BitField<SyncOption> options,
                @NonNull final ExceptionBuilder<? super SyncException, X> builder)
    throws X, FileSystemException {
        assert getModel().writeLock().isHeldByCurrentThread();

        if (0 < buffers.size()) {
            final boolean flush = !options.get(ABORT_CHANGES);
            final boolean clear = !flush || options.get(CLEAR_BUFFERS);
            for (final EntryBuffer buffer : buffers.values()) {
                try {
                    if (flush)
                        buffer.flush();
                } catch (IOException ex) {
                    throw builder.fail(new SyncException(getModel(), ex));
                } finally  {
                    try {
                        if (clear)
                            buffer.clear();
                    } catch (IOException ex) {
                        builder.warn(new SyncWarningException(getModel(), ex));
                    }
                }
            }
            if (clear)
                buffers.clear();
        }
        delegate.sync(options.clear(CLEAR_BUFFERS), builder);
    }

    private static final BitField<InputOption> NO_INPUT_OPTIONS
            = BitField.noneOf(InputOption.class);

    private static final BitField<OutputOption> NO_OUTPUT_OPTIONS
            = BitField.noneOf(OutputOption.class);

    private final class EntryBuffer {
        final FileSystemEntryName name;
        final IOBuffer<Entry> buffer;
        volatile InputSocket<Entry> input;
        volatile OutputSocket<Entry> output;

        EntryBuffer(@NonNull final FileSystemEntryName name) {
            this.name = name;
            this.buffer = WRITE_BACK.newIOBuffer(Entry.class, pool);
            configure(NO_INPUT_OPTIONS);
            configure(NO_OUTPUT_OPTIONS, null);
        }

        @NonNull
        public EntryBuffer configure(@NonNull BitField<InputOption> options) {
            buffer.configure(new RegisteringInputSocket(delegate.getInputSocket(
                    name, options.clear(InputOption.BUFFER))));
            input = null;
            return this;
        }

        @NonNull
        public EntryBuffer configure(@NonNull BitField<OutputOption> options,
                                    @Nullable Entry template) {
            buffer.configure(delegate.getOutputSocket(
                    name, options.clear(OutputOption.BUFFER), template));
            output = null;
            return this;
        }

        public InputSocket<Entry> getInputSocket() {
            return null != input
                    ? input
                    : (input = buffer.getInputSocket());
        }

        public OutputSocket<Entry> getOutputSocket() {
            return null != output
                    ? output
                    : (output = new RegisteringOutputSocket(buffer.getOutputSocket()));
        }

        public void flush() throws IOException {
            buffer.flush();
        }

        public void clear() throws IOException {
            buffer.clear();
        }

        final class RegisteringInputSocket extends DecoratingInputSocket<Entry> {
            RegisteringInputSocket(final InputSocket <?> input) {
                super(input);
            }

            @Override
            public InputStream newInputStream() throws IOException {
                getModel().assertWriteLockedByCurrentThread();
                final InputStream in = getBoundSocket().newInputStream();
                buffers.put(name, EntryBuffer.this);
                return in;
            }

            @Override
            public ReadOnlyFile newReadOnlyFile() throws IOException {
                getModel().assertWriteLockedByCurrentThread();
                final ReadOnlyFile rof = getBoundSocket().newReadOnlyFile();
                buffers.put(name, EntryBuffer.this);
                return rof;
            }
        } // class RegisteringInputSocket

        final class RegisteringOutputSocket extends DecoratingOutputSocket<Entry> {
            RegisteringOutputSocket(OutputSocket <?> output) {
                super(output);
            }

            @Override
            public OutputStream newOutputStream() throws IOException {
                assert getModel().writeLock().isHeldByCurrentThread();

                final OutputStream out = getBoundSocket().newOutputStream();
                // Create marker entry and mind CREATE_PARENTS!
                //controller.mknod(name, FILE, outputOptions, null);
                //getModel().setTouched(true);
                buffers.put(name, EntryBuffer.this);
                return out;
            }
        } // class RegisteringOutputSocket
    } // class EntryCache
}
