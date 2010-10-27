/*
 * Copyright (C) 2005-2010 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schlichtherle.truezip.io.zip;

import de.schlichtherle.truezip.crypto.io.raes.RaesOutputStream;
import de.schlichtherle.truezip.crypto.io.raes.RaesParameters;
import de.schlichtherle.truezip.crypto.io.raes.Type0RaesParameters;
import de.schlichtherle.truezip.crypto.io.raes.RaesReadOnlyFile;
import de.schlichtherle.truezip.io.rof.ReadOnlyFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tests compression and encryption of data.
 * Subclasses must override {@link #setUp}.
 * 
 * @author Christian Schlichtherle
 * @version $Id$
 */
public abstract class RaesZipTestCase extends ZipTestCase {

    private static final Logger logger
            = Logger.getLogger(RaesZipTestCase.class.getName());

    /** Cipher text shorter than this gets authenticated. */
    private static int AUTHENTICATION_TRIGGER = 512 * 1024;
    
    private static final String PASSWD = "passwd";

    private static final int[] keyStrengths = {
        Type0RaesParameters.KEY_STRENGTH_128,
        Type0RaesParameters.KEY_STRENGTH_192,
        Type0RaesParameters.KEY_STRENGTH_256,
    };

    private static int createKeyStrength() {
        final int keyStrength = keyStrengths[rnd.nextInt(keyStrengths.length)];
        //final int keyStrength = KEY_STRENGTH_ULTRA;
        logger.log(Level.FINE, "Using {0} bits cipher key.", (128 + keyStrength * 64));
        return keyStrength;
    }

    private static final RaesParameters raesParameters = new Type0RaesParameters() {
        @Override
		public char[] getOpenPasswd() {
            return PASSWD.toCharArray();
        }

        @Override
		public void invalidOpenPasswd() {
            throw new AssertionError();
        }

        @Override
		public char[] getCreatePasswd() {
            return PASSWD.toCharArray();
        }

        @Override
		public int getKeyStrength() {
            return createKeyStrength();
        }
        
        @Override
		public void setKeyStrength(int keyStrength) {
        }
    };
    
    public RaesZipTestCase(String testName) {
        super(testName);
    }

    @Override
    protected ZipOutputStream newZipOutputStream(final OutputStream out)
    throws IOException {
        final RaesOutputStream ros = RaesOutputStream.getInstance(
                out, raesParameters);
        try {
            return new ZipOutputStream(ros);
        } catch (RuntimeException exc) {
            ros.close();
            throw exc;
        }
    }

    @Override
    protected ZipOutputStream newZipOutputStream(
            final OutputStream out, final String charset)
    throws IOException {
        final RaesOutputStream ros = RaesOutputStream.getInstance(
                out, raesParameters);
        try {
            return new ZipOutputStream(ros, charset);
        } catch (RuntimeException exc) {
            ros.close();
            throw exc;
        }
    }

    @Override
    protected ZipOutputStream newZipOutputStream(
            final OutputStream out,
            final ZipFile appendee)
    throws IOException {
        final RaesOutputStream ros = RaesOutputStream.getInstance(
                out, raesParameters);
        try {
            return new ZipOutputStream(ros, ZIP.UTF8, appendee);
        } catch (RuntimeException exc) {
            ros.close();
            throw exc;
        }
    }

    @Override
    protected ZipFile newZipFile(final String name)
    throws IOException {
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(new File(name), raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }

    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    protected ZipFile newZipFile(
            final String name, final String charset)
    throws IOException {
        if (charset == null)
            throw new NullPointerException();
        new String(new byte[0], charset); // may throw UnsupportedEncodingExceoption!
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(new File(name), raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof, charset);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }

    @Override
    protected ZipFile newZipFile(final File file)
    throws IOException {
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(file, raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }

    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    protected ZipFile newZipFile(
            final File file, final String charset)
    throws IOException {
        if (charset == null)
            throw new NullPointerException();
        new String(new byte[0], charset); // may throw UnsupportedEncodingExceoption!
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(file, raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof, charset);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }

    @Override
    protected ZipFile newZipFile(final ReadOnlyFile file)
    throws IOException {
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(file, raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }

    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    protected ZipFile newZipFile(
            final ReadOnlyFile file, final String charset)
    throws IOException {
        if (charset == null)
            throw new NullPointerException();
        new String(new byte[0], charset); // may throw UnsupportedEncodingExceoption!
        final RaesReadOnlyFile rof
                = RaesReadOnlyFile.getInstance(file, raesParameters);
        try {
            if (rof.length() < AUTHENTICATION_TRIGGER) // heuristic
                rof.authenticate();
            return new ZipFile(rof, charset);
        } catch (RuntimeException exc) {
            rof.close();
            throw exc;
        } catch (IOException exc) {
            rof.close();
            throw exc;
        }
    }
}
