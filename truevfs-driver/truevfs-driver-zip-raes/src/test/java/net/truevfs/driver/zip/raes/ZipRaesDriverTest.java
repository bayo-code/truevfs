/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truevfs.driver.zip.raes;

import net.truevfs.comp.zip.driver.AbstractZipDriverEntry;
import net.truevfs.kernel.spec.FsArchiveDriverTestSuite;

/**
 * @author  Christian Schlichtherle
 */
public final class ZipRaesDriverTest
extends FsArchiveDriverTestSuite<AbstractZipDriverEntry, ZipRaesDriver> {

    @Override
    protected TestZipRaesDriver newArchiveDriver() {
        return new TestZipRaesDriver();
    }

    @Override
    protected String getUnencodableName() {
        return null;
    }
}
