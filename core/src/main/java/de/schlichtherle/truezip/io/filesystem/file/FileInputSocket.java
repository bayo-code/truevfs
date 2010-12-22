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
package de.schlichtherle.truezip.io.filesystem.file;

import de.schlichtherle.truezip.io.socket.DefaultCache;
import de.schlichtherle.truezip.io.rof.ReadOnlyFile;
import de.schlichtherle.truezip.io.rof.SimpleReadOnlyFile;
import de.schlichtherle.truezip.io.filesystem.InputOption;
import de.schlichtherle.truezip.io.socket.InputSocket;
import de.schlichtherle.truezip.util.BitField;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @see     FileOutputSocket
 * @author  Christian Schlichtherle
 * @version $Id$
 */
public final class FileInputSocket extends InputSocket<FileEntry> {
    private final FileEntry file;

    private static final BitField<InputOption> NO_INPUT_OPTIONS
            = BitField.noneOf(InputOption.class);

    public static InputSocket<FileEntry> get(FileEntry file) {
        return get(file, NO_INPUT_OPTIONS);
    }

    public static InputSocket<FileEntry> get(   FileEntry file,
                                                BitField<InputOption> options) {
        InputSocket<FileEntry> input = new FileInputSocket(file);
        if (options.get(InputOption.CACHE))
            input = DefaultCache.Strategy.READ_ONLY
                    .newCache(FileEntry.class, TempFilePool.get())
                    .configure(input)
                    .getInputSocket();
        return input;
    }

    private FileInputSocket(final FileEntry file) {
        if (null == file)
            throw new NullPointerException();
        this.file = file;
    }

    @Override
    public FileEntry getLocalTarget() {
        return file;
    }

    @Override
    public ReadOnlyFile newReadOnlyFile() throws IOException {
        return new SimpleReadOnlyFile(file.getFile());
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return new FileInputStream(file.getFile());
    }
}
