/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.driver.zip.raes.rof;

import de.truezip.driver.zip.raes.crypto.*;
import de.truezip.kernel.io.AbstractSink;
import de.truezip.kernel.io.Streams;
import de.truezip.kernel.rof.ReadOnlyFile;
import de.truezip.kernel.rof.ReadOnlyFileTestSuite;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Christian Schlichtherle
 */
public final class RaesRofIT extends ReadOnlyFileTestSuite {

    private static final Logger logger = Logger.getLogger(
            RaesRofIT.class.getName());

    private static RaesParameters newRaesParameters() {
        return new MockType0RaesParameters();
    }

    private File cipherFile;

    @Override
    protected ReadOnlyFile newReadOnlyFile(final File plainFile)
    throws IOException {
        try (final InputStream in = new FileInputStream(plainFile)) {
            cipherFile = File.createTempFile(TEMP_FILE_PREFIX, null);
            try {
                final RaesOutputStream out = new RaesSink(
                        new AbstractSink() {
                            @Override
                            public OutputStream newOutputStream() throws IOException {
                                return new FileOutputStream(cipherFile);
                            }
                        },
                        newRaesParameters()).newOutputStream();
                Streams.copy(in, out);
                logger.log(Level.FINEST,
                        "Encrypted {0} bytes of random data using AES-{1}/CTR/Hmac-SHA-256/PKCS#12v1",
                        new Object[]{ plainFile.length(), out.getKeyStrength().getBits() });
                // Open cipherFile for random access decryption.
            } catch (IOException ex) {
                final File cipherFile = this.cipherFile;
                this.cipherFile = null;
                if (!cipherFile.delete())
                    throw new IOException(cipherFile + " (could not delete)", ex);
                throw ex;
            }
            return RaesReadOnlyFile.getInstance(cipherFile, newRaesParameters());
        }
    }

    @Override
    public void tearDown() {
        try {
            try {
                super.tearDown();
            } finally {
                final File cipherFile = this.cipherFile;
                this.cipherFile = null;
                if (null != cipherFile && cipherFile.exists() && !cipherFile.delete())
                    throw new IOException(cipherFile + " (could not delete)");
            }
        } catch (IOException ex) {
            logger.log(Level.FINEST,
                    "Failed to clean up test file (this may be just an aftermath):",
                    ex);
        }
    }
}