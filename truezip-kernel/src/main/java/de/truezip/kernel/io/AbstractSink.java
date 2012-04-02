/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.kernel.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;

/**
 * @author Christian Schlichtherle
 */
public abstract class AbstractSink implements Sink {
    @Override
    public OutputStream newOutputStream() throws IOException {
        return new SeekableByteChannelOutputStream(newSeekableByteChannel());
    }

    @Override
    public SeekableByteChannel newSeekableByteChannel() throws IOException {
        throw new UnsupportedOperationException();
    }
}
