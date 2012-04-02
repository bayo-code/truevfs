/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.kernel.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Christian Schlichtherle
 */
public class InputExceptionSource extends AbstractSource {

    private final Source source;

    public InputExceptionSource(final Source source) {
        if (null == (this.source = source))
            throw new NullPointerException();
    }

    @Override
    public InputStream newInputStream() throws IOException {
        try {
            return new InputExceptionStream(source.newInputStream());
        } catch (IOException ex) {
            throw new InputException(ex);
        }
    }
}
