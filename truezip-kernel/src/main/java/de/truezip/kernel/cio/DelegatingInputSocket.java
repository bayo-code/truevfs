/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.kernel.cio;

import de.truezip.kernel.rof.ReadOnlyFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Delegates all methods to another input socket.
 * 
 * @see    DelegatingOutputSocket
 * @param  <E> the type of the {@link #getLocalTarget() local target}.
 * @author Christian Schlichtherle
 */
@NotThreadSafe
public abstract class DelegatingInputSocket<E extends Entry>
extends InputSocket<E> {

    /**
     * Returns the delegate socket.
     * 
     * @return The delegate socket.
     * @throws IOException on any I/O failure. 
     */
    protected abstract InputSocket<? extends E> getDelegate()
    throws IOException;

    /**
     * Binds the delegate socket to this socket and returns it.
     *
     * @return The bound delegate socket.
     * @throws IOException on any I/O failure. 
     */
    protected final InputSocket<? extends E> getBoundDelegate()
    throws IOException {
        return getDelegate().bind(this);
    }

    @Override
    public E getLocalTarget() throws IOException {
        return getBoundDelegate().getLocalTarget();
    }

    @Override
    public ReadOnlyFile newReadOnlyFile() throws IOException {
        return getBoundDelegate().newReadOnlyFile();
    }

    @Override
    public SeekableByteChannel newSeekableByteChannel() throws IOException {
        return getBoundDelegate().newSeekableByteChannel();
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return getBoundDelegate().newInputStream();
    }
}