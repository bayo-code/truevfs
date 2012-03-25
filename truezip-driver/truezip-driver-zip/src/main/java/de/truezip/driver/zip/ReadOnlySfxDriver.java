/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.driver.zip;

import de.truezip.kernel.fs.FsModel;
import de.truezip.kernel.cio.IOPoolProvider;
import de.truezip.kernel.cio.OutputService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.concurrent.Immutable;

/**
 * An archive driver which reads Self Executable (SFX/EXE) ZIP files,
 * but doesn't support to create or update them because this would spoil the
 * SFX code in its preamble.
 * <p>
 * Subclasses must be thread-safe and should be immutable!
 * 
 * @author Christian Schlichtherle
 */
@Immutable
public class ReadOnlySfxDriver extends ZipDriver {

    /**
     * The character set used in SFX archives by default, which is determined
     * by calling {@code System.getProperty("file.encoding")}.
     */
    public static final Charset SFX_CHARSET
            = Charset.forName(System.getProperty("file.encoding"));

    public ReadOnlySfxDriver(IOPoolProvider provider) {
        super(provider, SFX_CHARSET);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link ZipDriver}
     * returns {@code true}.
     * 
     * @return {@code true}
     */
    @Override
    public final boolean getPreambled() {
        return true;
    }

    @Override
    protected final OutputService<ZipDriverEntry> newOutputService(
            final FsModel model,
            final OptionOutputSocket output,
            final ZipInputService source)
    throws IOException {
        assert null != model;
        assert null != output;
        throw new FileNotFoundException(
                "driver class does not support creating or modifying SFX archives");
    }
}