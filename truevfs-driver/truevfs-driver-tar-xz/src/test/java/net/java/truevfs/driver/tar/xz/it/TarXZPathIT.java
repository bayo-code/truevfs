/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truevfs.driver.tar.xz.it;

import net.java.truevfs.comp.tar.driver.it.TarPathITSuite;
import net.java.truevfs.driver.tar.xz.TarXZDriver;
import net.java.truevfs.driver.tar.xz.TestTarXZDriver;

/**
 * @author Christian Schlichtherle
 */
public final class TarXZPathIT extends TarPathITSuite<TarXZDriver> {
    @Override
    protected String getExtensionList() {
        return "tar.xz";
    }

    @Override
    protected TarXZDriver newArchiveDriver() {
        return new TestTarXZDriver();
    }
}
