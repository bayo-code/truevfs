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
package de.schlichtherle.truezip.fs;

import java.io.IOException;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Indicates an exceptional condition when synchronizing the changes in a
 * federated file system to its parent file system.
 * An exception of this class implies that no or only insignificant parts
 * of the data of the federated file system have been lost.
 * 
 * @author Christian Schlichtherle
 * @version $Id$
 */
@ThreadSafe
public class FsSyncWarningException extends FsSyncException {
    private static final long serialVersionUID = 2302357394858347366L;

    public FsSyncWarningException(FsModel model, IOException cause) {
        super(model, cause, -1);
    }
}
