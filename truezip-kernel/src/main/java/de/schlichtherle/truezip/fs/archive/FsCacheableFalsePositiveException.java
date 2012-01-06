/*
 * Copyright 2004-2012 Schlichtherle IT Services
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.schlichtherle.truezip.fs.archive;

import de.schlichtherle.truezip.fs.FsFalsePositiveException;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;

/**
 * A cacheable false positive exception.
 * 
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@DefaultAnnotation(NonNull.class)
final class FsCacheableFalsePositiveException extends FsFalsePositiveException {
    private static final long serialVersionUID = 5436924103910446876L;

    FsCacheableFalsePositiveException(IOException cause) {
        super(cause);
    }
}
