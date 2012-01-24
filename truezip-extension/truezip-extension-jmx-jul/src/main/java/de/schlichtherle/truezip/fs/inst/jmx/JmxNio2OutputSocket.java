/*
 * Copyright 2004-2012 Schlichtherle IT Services
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.schlichtherle.truezip.fs.inst.jmx;

import de.schlichtherle.truezip.entry.Entry;
import de.schlichtherle.truezip.socket.OutputSocket;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import net.jcip.annotations.Immutable;

/**
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@Immutable
@DefaultAnnotation(NonNull.class)
final class JmxNio2OutputSocket<E extends Entry>
extends JmxOutputSocket<E> {

    JmxNio2OutputSocket(OutputSocket<? extends E> model, JmxDirector director, JmxIOStatistics stats) {
        super(model, director, stats);
    }

    @Override
    public final SeekableByteChannel newSeekableByteChannel() throws IOException {
        return new JmxSeekableByteChannel(getBoundSocket().newSeekableByteChannel(), stats);
    }
}
