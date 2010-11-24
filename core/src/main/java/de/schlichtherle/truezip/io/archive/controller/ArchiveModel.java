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

import de.schlichtherle.truezip.io.filesystem.FileSystemModel;
import de.schlichtherle.truezip.util.concurrent.lock.ReentrantLock;
import de.schlichtherle.truezip.util.concurrent.lock.ReentrantReadWriteLock;
import java.net.URI;

/**
 * Defines the common properties of any archive file system.
 *
 * @author Christian Schlichtherle
 * @version $Id$
 */
public class ArchiveModel extends FileSystemModel {
    private final ReentrantLock readLock;
    private final ReentrantLock writeLock;

    public ArchiveModel(final URI mountPoint,
                        final FileSystemModel parent) {
        super(mountPoint, parent);
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }


    final ReentrantLock readLock() {
        return readLock;
    }

    final ReentrantLock writeLock() {
        return writeLock;
    }
}
