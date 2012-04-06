/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package de.truezip.driver.zip.io;

import de.truezip.kernel.rof.ReadOnlyFile;
import de.truezip.key.param.AesKeyStrength;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipException;
import static org.junit.Assert.assertSame;

/**
 * @author Christian Schlichtherle
 */
public final class WinZipAesIT extends ZipTestSuite {

    @Override
    public ZipEntry newEntry(String name) {
        ZipEntry entry = new ZipEntry(name);
        entry.setEncrypted(true);
        return entry;
    }

    @Override
    protected ZipOutputStream newZipOutputStream(OutputStream out)
    throws IOException {
        ZipOutputStream res = new ZipOutputStream(out);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipOutputStream newZipOutputStream(
            OutputStream out, Charset charset)
    throws IOException {
        ZipOutputStream res = new ZipOutputStream(out, charset);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipOutputStream newZipOutputStream(
            OutputStream out,
            ZipFile appendee)
    throws ZipException {
        ZipOutputStream res = new ZipOutputStream(out, appendee);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(String name)
    throws IOException {
        ZipFile res = new ZipFile(name);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(
            String name, Charset charset)
    throws IOException {
        ZipFile res = new ZipFile(name, charset);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(File file)
    throws IOException {
        ZipFile res = new ZipFile(file);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(
            File file, Charset charset)
    throws IOException {
        ZipFile res = new ZipFile(file, charset);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(ReadOnlyFile file)
    throws IOException {
        ZipFile res = new ZipFile(file);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    @Override
    protected ZipFile newZipFile(
            ReadOnlyFile file, Charset charset)
    throws IOException {
        ZipFile res = new ZipFile(file, charset);
        res.setCryptoParameters(new WinZipAesCryptoParameters());
        return res;
    }

    /**
     * Skipped because this test is specified to a plain ZIP file.
     */
    @Override
    public void testBadGetCheckedInputStream() {
    }

    private static final class WinZipAesCryptoParameters
    implements WinZipAesParameters {
        @Override
        public byte[] getWritePassword(String name) throws ZipKeyException {
            return "top secret".getBytes();
        }

        @Override
        public byte[] getReadPassword(String name, boolean invalid) throws ZipKeyException {
            return "top secret".getBytes();
        }

        @Override
        public AesKeyStrength getKeyStrength(String name) throws ZipKeyException {
            return AesKeyStrength.BITS_128;
        }

        @Override
        public void setKeyStrength(String name, AesKeyStrength keyStrength) throws ZipKeyException {
            assertSame(AesKeyStrength.BITS_128, keyStrength);
        }
    } // WinZipAesCryptoParameters
}
