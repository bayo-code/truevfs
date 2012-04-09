/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.kernel.io;

import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;

/**
 * @see    OneTimeSink
 * @author Christian Schlichtherle
 */
public final class OneTimeSource
extends OneTimeResource<InputStream>
implements Source {

    public OneTimeSource(InputStream in) {
        super(in);
    }

    public OneTimeSource(SeekableByteChannel channel) {
        super(channel);
    }
}
