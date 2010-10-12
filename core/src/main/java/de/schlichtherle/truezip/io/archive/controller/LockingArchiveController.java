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
package de.schlichtherle.truezip.io.archive.controller;

import de.schlichtherle.truezip.io.socket.OutputOption;
import de.schlichtherle.truezip.io.socket.InputOption;
import de.schlichtherle.truezip.io.socket.CommonEntry;
import de.schlichtherle.truezip.io.socket.CommonEntry.Type;
import de.schlichtherle.truezip.io.socket.CommonEntry.Access;
import de.schlichtherle.truezip.io.socket.OutputSocket;
import de.schlichtherle.truezip.io.socket.InputSocket;
import de.schlichtherle.truezip.io.socket.FileSystemEntry;
import de.schlichtherle.truezip.io.rof.ReadOnlyFile;
import de.schlichtherle.truezip.io.socket.FilterInputSocket;
import de.schlichtherle.truezip.io.socket.FilterOutputSocket;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.ExceptionBuilder;
import de.schlichtherle.truezip.util.concurrent.lock.ReentrantLock;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.Icon;

/**
 * @author Christian Schlichtherle
 * @version $Id$
 */
final class LockingArchiveController extends FilterArchiveController {

    LockingArchiveController(final ArchiveController controller) {
        super(controller);
    }

    ReentrantLock readLock() {
        return getModel().readLock();
    }

    ReentrantLock writeLock() {
        return getModel().writeLock();
    }

    void ensureNotReadLockedByCurrentThread(NotWriteLockedException ex) {
        getModel().ensureNotReadLockedByCurrentThread(ex);
    }

    @Override
    public Icon getOpenIcon() {
        try {
            readLock().lock();
            try {
                return getController().getOpenIcon();
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().getOpenIcon();
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public Icon getClosedIcon()  {
        try {
            readLock().lock();
            try {
                return getController().getClosedIcon();
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().getClosedIcon();
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        try {
            readLock().lock();
            try {
                return getController().isReadOnly();
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().isReadOnly();
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public FileSystemEntry getEntry(String path) {
        try {
            readLock().lock();
            try {
                return getController().getEntry(path);
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().getEntry(path);
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public boolean isReadable(String path) {
        try {
            readLock().lock();
            try {
                return getController().isReadable(path);
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().isReadable(path);
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public boolean isWritable(String path) {
        try {
            readLock().lock();
            try {
                return getController().isWritable(path);
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return getController().isWritable(path);
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public void setReadOnly(String path)
    throws IOException {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            getController().setReadOnly(path);
        } finally {
            writeLock().unlock();
        }
    }

    @Override
    public boolean setTime( String path, BitField<Access> types, long value)
    throws IOException {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            return getController().setTime(path, types, value);
        } finally {
            writeLock().unlock();
        }
    }

    @Override
    public InputSocket<?> getInputSocket(   String path,
                                            BitField<InputOption> options)
    throws IOException {
        try {
            readLock().lock();
            try {
                return new Input(getController().getInputSocket(path, options));
            } finally {
                readLock().unlock();
            }
        } catch (NotWriteLockedException ex) {
            ensureNotReadLockedByCurrentThread(ex);
            writeLock().lock();
            try {
                return new Input(getController().getInputSocket(path, options));
            } finally {
                writeLock().unlock();
            }
        }
    }

    private class Input extends FilterInputSocket<CommonEntry> {

        protected Input(InputSocket<? extends CommonEntry> target) {
            super(target);
        }

        @Override
        public CommonEntry getLocalTarget() throws IOException {
            try {
                readLock().lock();
                try {
                    return getInputSocket().getLocalTarget();
                } finally {
                    readLock().unlock();
                }
            } catch (NotWriteLockedException ex) {
                ensureNotReadLockedByCurrentThread(ex);
                writeLock().lock();
                try {
                    return getInputSocket().getLocalTarget();
                } finally {
                    writeLock().unlock();
                }
            }
        }

        @Override
        public InputStream newInputStream() throws IOException {
            try {
                readLock().lock();
                try {
                    return getInputSocket().newInputStream();
                } finally {
                    readLock().unlock();
                }
            } catch (NotWriteLockedException ex) {
                ensureNotReadLockedByCurrentThread(ex);
                writeLock().lock();
                try {
                    return getInputSocket().newInputStream();
                } finally {
                    writeLock().unlock();
                }
            }
        }

        @Override
        public ReadOnlyFile newReadOnlyFile() throws IOException {
            try {
                readLock().lock();
                try {
                    return getInputSocket().newReadOnlyFile();
                } finally {
                    readLock().unlock();
                }
            } catch (NotWriteLockedException ex) {
                ensureNotReadLockedByCurrentThread(ex);
                writeLock().lock();
                try {
                    return getInputSocket().newReadOnlyFile();
                } finally {
                    writeLock().unlock();
                }
            }
        }
    }

    @Override
    public OutputSocket<?> getOutputSocket( String path,
                                            BitField<OutputOption> options)
    throws IOException {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            return new Output(getController().getOutputSocket(path, options));
        } finally {
            writeLock().unlock();
        }
    }

    private class Output extends FilterOutputSocket<CommonEntry> {

        protected Output(OutputSocket<? extends CommonEntry> target) {
            super(target);
        }

        @Override
        public CommonEntry getLocalTarget() throws IOException {
            ensureNotReadLockedByCurrentThread(null);
            writeLock().lock();
            try {
                return getOutputSocket().getLocalTarget();
            } finally {
                writeLock().unlock();
            }
        }

        @Override
        public OutputStream newOutputStream() throws IOException {
            ensureNotReadLockedByCurrentThread(null);
            writeLock().lock();
            try {
                return getOutputSocket().newOutputStream();
            } finally {
                writeLock().unlock();
            }
        }
    }

    @Override
    public boolean mknod(   String path,
                            Type type,
                            CommonEntry template,
                            BitField<OutputOption> options)
    throws IOException {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            return getController().mknod(path, type, template, options);
        } finally {
            writeLock().unlock();
        }
    }

    @Override
    public void unlink(String path)
    throws IOException {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            getController().unlink(path);
        } finally {
            writeLock().unlock();
        }
    }

    @Override
    public <E extends IOException>
    void sync(ExceptionBuilder<? super SyncException, E> builder, BitField<SyncOption> options)
    throws E {
        ensureNotReadLockedByCurrentThread(null);
        writeLock().lock();
        try {
            getController().sync(builder, options);
        } finally {
            writeLock().unlock();
        }
    }
}
