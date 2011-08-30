/*
 * Copyright (C) 2011 Schlichtherle IT Services
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
package de.schlichtherle.truezip.fs;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.entry.Entry.Access;
import de.schlichtherle.truezip.entry.Entry.Type;
import static de.schlichtherle.truezip.fs.FsSyncOption.*;
import de.schlichtherle.truezip.rof.ReadOnlyFile;
import de.schlichtherle.truezip.socket.DecoratingInputSocket;
import de.schlichtherle.truezip.socket.DecoratingOutputSocket;
import de.schlichtherle.truezip.socket.InputSocket;
import de.schlichtherle.truezip.socket.OutputSocket;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.ExceptionHandler;
import de.schlichtherle.truezip.util.JSE7;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.Map;
import javax.swing.Icon;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * A decorating file system controller which performs a
 * {@link FsController#sync(BitField, ExceptionHandler) sync} operation on the
 * file system if the decorated file system controller throws an
 * {@link FsNotSyncedException}.
 * 
 * @since   TrueZIP 7.3
 * @see     FsNotSyncedException
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@ThreadSafe
@DefaultAnnotation(NonNull.class)
public final class FsSyncController<
        M extends FsModel,
        C extends FsController<? extends M>>
extends FsDecoratingController<M, C> {

    private static final BitField<FsSyncOption>
            SYNC_OPTIONS = BitField.of(WAIT_CLOSE_INPUT, WAIT_CLOSE_OUTPUT);

    private static final SyncSocketFactory
            SYNC_SOCKET_FACTORY = JSE7.AVAILABLE
                ? SyncSocketFactory.NIO2
                : SyncSocketFactory.OIO;

    /**
     * Constructs a new file system sync controller.
     *
     * @param controller the decorated concurrent file system controller.
     */
    public FsSyncController(C controller) {
        super(controller);
    }

    @Override
    public Icon getOpenIcon() throws IOException {
        while (true) {
            try {
                return delegate.getOpenIcon();
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public Icon getClosedIcon() throws IOException {
        while (true) {
            try {
                return delegate.getClosedIcon();
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public boolean isReadOnly() throws IOException {
        while (true) {
            try {
                return delegate.isReadOnly();
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public FsEntry getEntry(FsEntryName name)
    throws IOException {
        while (true) {
            try {
                return delegate.getEntry(name);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public boolean isReadable(FsEntryName name) throws IOException {
        while (true) {
            try {
                return delegate.isReadable(name);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public boolean isWritable(FsEntryName name) throws IOException {
        while (true) {
            try {
                return delegate.isWritable(name);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public void setReadOnly(FsEntryName name) throws IOException {
        while (true) {
            try {
                delegate.setReadOnly(name);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public boolean setTime(
            FsEntryName name,
            Map<Access, Long> times,
            BitField<FsOutputOption> options)
    throws IOException {
        while (true) {
            try {
                return delegate.setTime(name, times, options);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public boolean setTime(
            FsEntryName name,
            BitField<Access> types,
            long value,
            BitField<FsOutputOption> options)
    throws IOException {
        while (true) {
            try {
                return delegate.setTime(name, types, value, options);
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public InputSocket<?> getInputSocket(   FsEntryName name,
                                            BitField<FsInputOption> options) {
        return SYNC_SOCKET_FACTORY.newInputSocket(this,
                delegate.getInputSocket(name, options));
    }

    @Override
    public OutputSocket<?> getOutputSocket( FsEntryName name,
                                            BitField<FsOutputOption> options,
                                            Entry template) {
        return SYNC_SOCKET_FACTORY.newOutputSocket(this,
                delegate.getOutputSocket(name, options, template));
    }

    @Override
    public void mknod(
            @NonNull FsEntryName name,
            @NonNull Type type,
            @NonNull BitField<FsOutputOption> options,
            @CheckForNull Entry template)
    throws IOException {
        while (true) {
            try {
                delegate.mknod(name, type, options, template);
                return;
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Override
    public void unlink(FsEntryName name, BitField<FsOutputOption> options)
    throws IOException {
        while (true) {
            try {
                delegate.unlink(name, options);
                return;
            } catch (FsNotSyncedException ex) {
                delegate.sync(SYNC_OPTIONS);
            }
        }
    }

    @Immutable
    private enum SyncSocketFactory {
        OIO() {
            @Override
            InputSocket<?> newInputSocket(
                    FsSyncController<?, ?> controller,
                    InputSocket<?> input) {
                return controller.new SyncInputSocket(input);
            }

            @Override
            OutputSocket<?> newOutputSocket(
                    FsSyncController<?, ?> controller,
                    OutputSocket<?> output) {
                return controller.new SyncOutputSocket(output);
            }
        },

        NIO2() {
            @Override
            InputSocket<?> newInputSocket(
                    FsSyncController<?, ?> controller,
                    InputSocket<?> input) {
                return controller.new Nio2SyncInputSocket(input);
            }

            @Override
            OutputSocket<?> newOutputSocket(
                    FsSyncController<?, ?> controller,
                    OutputSocket<?> output) {
                return controller.new Nio2SyncOutputSocket(output);
            }
        };

        abstract InputSocket<?> newInputSocket(
                FsSyncController<?, ?> controller,
                InputSocket <?> input);
        
        abstract OutputSocket<?> newOutputSocket(
                FsSyncController<?, ?> controller,
                OutputSocket <?> output);
    } // SyncSocketFactory

    private final class Nio2SyncInputSocket
    extends SyncInputSocket {
        Nio2SyncInputSocket(InputSocket<?> input) {
            super(input);
        }

        @Override
        public SeekableByteChannel newSeekableByteChannel() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().newSeekableByteChannel();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }
    } // Nio2SyncInputSocket

    private class SyncInputSocket
    extends DecoratingInputSocket<Entry> {
        SyncInputSocket(InputSocket<?> input) {
            super(input);
        }

        @Override
        public Entry getLocalTarget() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().getLocalTarget();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }

        @Override
        public Entry getPeerTarget() throws IOException {
            // Same implementation as super class, but makes stack trace nicer.
            return getBoundSocket().getPeerTarget();
        }

        @Override
        public ReadOnlyFile newReadOnlyFile() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().newReadOnlyFile();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }

        @Override
        public InputStream newInputStream() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().newInputStream();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }
    } // SyncInputSocket

    private final class Nio2SyncOutputSocket
    extends SyncOutputSocket {
        Nio2SyncOutputSocket(OutputSocket<?> output) {
            super(output);
        }

        @Override
        public SeekableByteChannel newSeekableByteChannel() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().newSeekableByteChannel();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }
    } // Nio2SyncOutputSocket

    private class SyncOutputSocket
    extends DecoratingOutputSocket<Entry> {
        SyncOutputSocket(OutputSocket<?> output) {
            super(output);
        }

        @Override
        public Entry getLocalTarget() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().getLocalTarget();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }

        @Override
        public Entry getPeerTarget() throws IOException {
            // Same implementation as super class, but makes stack trace nicer.
            return getBoundSocket().getPeerTarget();
        }

        @Override
        public OutputStream newOutputStream() throws IOException {
            while (true) {
                try {
                    return getBoundSocket().newOutputStream();
                } catch (FsNotSyncedException ex) {
                    delegate.sync(SYNC_OPTIONS);
                }
            }
        }
    } // SyncOutputSocket
}
