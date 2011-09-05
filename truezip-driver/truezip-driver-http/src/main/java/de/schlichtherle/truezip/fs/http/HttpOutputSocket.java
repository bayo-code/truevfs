/*
 * Copyright (C) 2011 Schlichtherle IT Services
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.schlichtherle.truezip.fs.http;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.socket.OutputSocket;
import de.schlichtherle.truezip.util.BitField;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.OutputStream;
import net.jcip.annotations.ThreadSafe;

/**
 * An output socket for HTTP(S) entries.
 * 
 * @see     HttpInputSocket
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@ThreadSafe
@DefaultAnnotation(NonNull.class)
final class HttpOutputSocket extends OutputSocket<HttpEntry> {

    private final HttpEntry entry;

    HttpOutputSocket(   final               HttpEntry                entry,
                        final               BitField<FsOutputOption> options,
                        final @CheckForNull Entry                    template) {
        assert null != entry;
        assert null != options;
        this.entry    = entry;
    }

    @Override
    public HttpEntry getLocalTarget() {
        return entry;
    }

    @Override
    public OutputStream newOutputStream() throws IOException {
        return entry.getConnection().getOutputStream();
    }
}
