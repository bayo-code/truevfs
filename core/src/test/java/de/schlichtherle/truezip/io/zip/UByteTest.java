/*
 * Copyright (C) 2007-2010 Schlichtherle IT Services
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

import junit.framework.TestCase;

/**
 * A simple round trip test of the static utility methods for unsigned byte
 * integers.
 * 
 * @author Christian Schlichtherle
 * @version $Id$
 */
public class UByteTest extends TestCase {

    public UByteTest(String testName) {
        super(testName);
    }

    public void testCheck() {
        try {
            UByte.check(UByte.MIN_VALUE - 1);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException expected) {
        }

        UByte.check(UByte.MIN_VALUE);
        UByte.check(UByte.MAX_VALUE);

        try {
            UByte.check(UByte.MAX_VALUE + 1);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException expected) {
        }
    }
}
