/*
 * Copyright (C) 2005-2011 Schlichtherle IT Services
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
package de.schlichtherle.truezip.crypto.raes;

import de.schlichtherle.truezip.crypto.CipherOutputStream;
import de.schlichtherle.truezip.crypto.param.KeyStrength;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;
import java.io.OutputStream;
import net.jcip.annotations.NotThreadSafe;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.Mac;

/**
 * An {@link OutputStream} to produce a file with data ecnrypted according
 * to the Random Access Encryption Specification (RAES).
 *
 * @see     RaesReadOnlyFile
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@NotThreadSafe
@DefaultAnnotation(NonNull.class)
public abstract class RaesOutputStream extends CipherOutputStream {

    /**
     * Update the given KLAC with the given file {@code length} in
     * little endian order and finalize it, writing the result to {@code buf}.
     * The KLAC must already have been initialized and updated with the
     * password bytes as retrieved according to PKCS #12.
     * The result is stored in {@code buf}, which must match the given
     * KLAC's output size.
     */
    static void klac(final Mac klac, long length, final byte[] buf) {
        for (int i = 0; i < 8; i++) {
            klac.update((byte) length);
            length >>= 8;
        }
        final int bufLen = klac.doFinal(buf, 0);
        assert bufLen == buf.length;
    }

    /**
     * Returns a new {@code RaesOutputStream}.
     *
     * @param  out The underlying output stream to use for the encrypted data.
     * @param  param The {@link RaesParameters} used to determine and
     *         configure the type of RAES file created.
     *         If the run time class of this parameter matches multiple
     *         parameter interfaces, it is at the discretion of this
     *         implementation which one is picked and hence which type of
     *         RAES file is created.
     *         If you need more control over this, pass in an instance which's
     *         run time class just implements the
     *         {@link RaesParametersProvider} interface.
     *         Instances of this interface are queried to find RAES parameters
     *         which match a known RAES type.
     *         This algorithm is recursively applied.
     * @return A new {@code RaesOutputStream}.
     * @throws RaesParametersException If {@code param} is {@code null} or
     *         no suitable RAES parameters can be found.
     * @throws IOException On any I/O error.
     */
    public static RaesOutputStream getInstance(
            final OutputStream out,
            final @CheckForNull RaesParameters param)
    throws IOException {
        if (null == out)
            throw new NullPointerException();
        // Order is important here to support multiple interface implementations!
        if (param == null) {
            throw new RaesParametersException("No RAES parameters available!");
        } else if (param instanceof Type0RaesParameters) {
            return new Type0RaesOutputStream(out,
                    (Type0RaesParameters) param);
        } else if (param instanceof RaesParametersProvider) {
            return getInstance(out,
                    ((RaesParametersProvider) param).get(RaesParameters.class));
        } else {
            throw new RaesParametersException();
        }
    }

    RaesOutputStream(   @CheckForNull OutputStream out,
                        @CheckForNull BufferedBlockCipher cipher) {
        super(out, cipher);
    }

    /**
     * Returns the key strength which is actually used to encrypt the data of
     * the RAES file.
     */
    public abstract KeyStrength getKeyStrength();
}
