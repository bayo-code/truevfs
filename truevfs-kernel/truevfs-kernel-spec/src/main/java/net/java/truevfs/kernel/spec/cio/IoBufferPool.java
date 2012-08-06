/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truevfs.kernel.spec.cio;

import java.io.IOException;
import javax.annotation.concurrent.ThreadSafe;
import net.java.truecommons.shed.Pool;

/**
 * An abstract pool for allocating I/O buffers, which can get used as a
 * volatile storage for bulk I/O.
 * Typical implementations may use temporary files for big data or byte arrays
 * for small data.
 * <p>
 * Implementations must be thread-safe.
 * However, this does not necessarily apply to its managed I/O buffers.
 * 
 * @author Christian Schlichtherle
 */
@ThreadSafe
public abstract class IoBufferPool
implements Pool<IoBuffer, IOException> {

    @Override
    public final void release(IoBuffer buffer) throws IOException {
        buffer.release();
    }
}
