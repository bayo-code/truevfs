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

import java.util.Iterator;
import de.schlichtherle.truezip.util.BitField;
import de.schlichtherle.truezip.util.ExceptionHandler;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import net.jcip.annotations.ThreadSafe;

import static de.schlichtherle.truezip.fs.FsSyncOption.*;

/**
 * A container which manages the lifecycle of controllers for federated file
 * systems. A file system is federated if and only if it's a member of a parent
 * file system.
 * <p>
 * Sub-classes must be thread-safe.
 *
 * @author Christian Schlichtherle
 * @version $Id$
 */
@ThreadSafe
public abstract class FsManager
implements Iterable<FsController<?>> {

    /**
     * Equivalent to
     * {@link #getController(FsMountPoint, FsFederatingDriver) getController(mountPoint, FsDefaultDriver.ALL)}.
     */
    public final @NonNull FsController<?>
    getController(@NonNull FsMountPoint mountPoint) {
        return getController(mountPoint, FsDefaultDriver.ALL);
    }

    /**
     * Returns a thread-safe file system controller for the given mount point.
     * If and only if the given mount point addresses a federated file system,
     * the returned file system controller is remembered for life cycle
     * management, i.e. future lookup and {@link #sync synchronization}
     * operations.
     *
     * @param  mountPoint the mount point of the file system.
     * @param  driver the file system driver which will be used to create a
     *         new file system controller if required.
     * @return A file system controller.
     */
    public abstract @NonNull FsController<?>
    getController(  @NonNull FsMountPoint mountPoint,
                    @NonNull FsFederatingDriver driver);

    /**
     * Returns the number of federated file systems managed by this instance.
     *
     * @return The number of federated file systems managed by this instance.
     */
    public abstract int getSize();

    /**
     * Returns an iterator for the controller of all federated file systems
     * managed by this instance.
     * <p>
     * <strong>Important:</strong> The iterated file system controllers must be
     * ordered so that all file systems appear before any of their parent file
     * systems.
     *
     * @return An iterator for the controller of all federated file systems
     *         managed by this instance.
     */
    @Override public abstract Iterator<FsController<?>>
    iterator();

    /**
     * Writes all changes to the contents of the federated file systems managed
     * by this instance to their respective parent file system.
     * This will reset the state of the respective file system controllers.
     *
     * @param  options the synchronization options.
     * @param  handler the exception handling strategy for dealing with one or
     *         more input {@code SyncException}s which may trigger an {@code X}.
     * @param  <X> the type of the {@code IOException} to throw at the
     *         discretion of the exception {@code handler}.
     * @throws IOException at the discretion of the exception {@code handler}.
     * @throws IllegalArgumentException if the combination of synchronization
     *         options is illegal, e.g. if {@code FORCE_CLOSE_INPUT} is cleared
     *         and {@code FORCE_CLOSE_OUTPUT} is set or if the synchronization
     *         option {@code ABORT_CHANGES} is set.
     */
    public <X extends IOException> void
    sync(   final @NonNull BitField<FsSyncOption> options,
            final @NonNull ExceptionHandler<? super IOException, X> handler)
    throws X {
        if (options.get(FORCE_CLOSE_OUTPUT) && !options.get(FORCE_CLOSE_INPUT)
                || options.get(ABORT_CHANGES))
            throw new IllegalArgumentException();

        class Sync implements Visitor {
            @Override public void
            visit(FsController<?> controller) throws IOException {
                controller.sync(options, handler);
            }
        } // class Sync

        visit(new Sync(), handler);
    }

    /**
     * Visits the controller of all federated file systems managed by this
     * instance.
     *
     * @param  visitor the visitor.
     * @param  handler the exception handling strategy for dealing with one or
     *         more input {@code SyncException}s which may trigger an {@code X}.
     * @param  <X> the type of the {@code IOException} to throw at the
     *         discretion of the exception {@code handler}.
     * @throws IOException at the discretion of the exception {@code handler}.
     */
    private <X extends IOException> void
    visit(  @NonNull Visitor visitor,
            @NonNull ExceptionHandler<? super IOException, X> handler)
    throws X {
        for (FsController<?> controller : this) {
            try {
                visitor.visit(controller);
            } catch (IOException ex) {
                handler.warn(ex);
            }
        }
    }

    /**
     * A visitor for file system controllers.
     *
     * @see #visit(Visitor, ExceptionHandler)
     */
    private interface Visitor {
        void visit(@NonNull FsController<?> controller)
        throws IOException;
    }

    /**
     * Two file system managers are considered equal if and only if they are
     * identical. This can't get overriden.
     */
    @SuppressWarnings(value = "EqualsWhichDoesntCheckParameterClass")
    @Override public final boolean
    equals(Object that) {
        return this == that;
    }

    /**
     * Returns a hash code which is consistent with {@link #equals}.
     * This can't get overriden.
     */
    @Override public final int
    hashCode() {
        return super.hashCode();
    }
}
