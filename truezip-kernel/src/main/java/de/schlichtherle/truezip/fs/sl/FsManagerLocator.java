/*
 * Copyright (C) 2011 Schlichtherle IT Services
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
package de.schlichtherle.truezip.fs.sl;

import de.schlichtherle.truezip.fs.FsDefaultManager;
import de.schlichtherle.truezip.fs.FsFailSafeManager;
import de.schlichtherle.truezip.fs.FsManager;
import de.schlichtherle.truezip.fs.FsManagerService;
import de.schlichtherle.truezip.fs.spi.FsManagerProvider;
import de.schlichtherle.truezip.util.ServiceLocator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jcip.annotations.Immutable;

/**
 * Locates a file system manager service of a class with a name which is
 * resolved by querying a system property or searching the class path,
 * whatever yields a result first.
 * <p>
 * First, the value of the {@link System#getProperty system property}
 * with the class name {@code "de.schlichtherle.truezip.fs.spi.FsManagerProvider"}
 * as the key is queried.
 * If this yields a value, the class with that name is then loaded and
 * instantiated by calling its no-arg constructor.
 * <p>
 * Otherwise, the class path is searched for any resource file with the name
 * {@code "META-INF/services/de.schlichtherle.truezip.fs.spi.FsManagerProvider"}.
 * If this yields a result, the class with the name in this file is then loaded
 * and instantiated by calling its no-arg constructor.
 * <p>
 * Otherwise, the expression
 * {@code new FsFailSafeManager(new FsDefaultManager())} is used to create the
 * file system manager in this container.
 *
 * @author Christian Schlichtherle
 * @version $Id$
 */
@Immutable
public final class FsManagerLocator implements FsManagerService {

    /** The singleton instance of this class. */
    public static final FsManagerLocator SINGLETON = new FsManagerLocator();

    private final FsManager manager;

    /** You cannot instantiate this class. */
    private FsManagerLocator() {
        final Logger
                logger = Logger.getLogger(  FsManagerLocator.class.getName(),
                                            FsManagerLocator.class.getName());
        final ServiceLocator locator = new ServiceLocator(
                FsManagerLocator.class.getClassLoader());
        FsManagerProvider
                provider = locator.getService(FsManagerProvider.class, null);
        if (null == provider) {
            final Iterator<FsManagerProvider>
                    i = locator.getServices(FsManagerProvider.class);
            if (i.hasNext())
                provider = i.next();
        }
        FsManager manager;
        if (null == provider) {
            manager = new FsFailSafeManager(new FsDefaultManager());
        } else {
            logger.log(Level.CONFIG, "located", provider);
            manager = provider.getFsManager();
        }
        this.manager = manager;
        logger.log(Level.CONFIG, null != provider ? "provided" : "default", manager);
    }

    @Override
    public FsManager getFsManager() {
        return manager;
    }
}
