/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.extension.jmxjul;

import de.truezip.kernel.fs.FsCompositeDriver;
import de.truezip.kernel.fs.FsController;
import de.truezip.kernel.fs.FsModel;
import javax.annotation.concurrent.Immutable;

/**
 * @author  Christian Schlichtherle
 */
@Immutable
public class InstrumentingCompositeDriver implements FsCompositeDriver {

    protected final FsCompositeDriver driver;
    protected final InstrumentingDirector<?> director;

    public InstrumentingCompositeDriver(final FsCompositeDriver driver,
                                        final InstrumentingDirector<?> director) {
        if (null == (this.driver = driver))
            throw new NullPointerException();
        if (null == (this.director = director))
            throw new NullPointerException();
    }

    @Override
    public FsController<?> newController(   final FsModel model,
                                            final FsController<?> parent) {
        return director.instrument(driver.newController(director.instrument(model, this), parent), this);
    }
}