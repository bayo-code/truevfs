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
package de.schlichtherle.truezip.key;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

/**
 * Uses a map to hold the safe key providers managed by this instance.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
@ThreadSafe
@DefaultAnnotation(NonNull.class)
public abstract class SafeKeyManager<K extends SafeKey<K>, P extends SafeKeyProvider<K>>
implements KeyManager<K> {

    private final Map<URI, P> providers = new HashMap<URI, P>();

    /**
     * Constructs a new safe key manager.
     *
     * @since TrueZIP 7.2
     */
    protected SafeKeyManager() {
    }

    /**
     * Returns a new key provider.
     * 
     * @return A new key provider.
     * @since  TrueZIP 7.2
     */
    protected abstract P newKeyProvider();

    @Override
    public synchronized P getKeyProvider(final URI resource) {
        if (null == resource)
            throw new NullPointerException();
        P provider = providers.get(resource);
        if (null == provider) {
            provider = newKeyProvider();
            providers.put(resource, provider);
        }
        return provider;
    }

    @Override
    public synchronized P moveKeyProvider(final URI oldResource, final URI newResource) {
        if (null == newResource)
            throw new NullPointerException();
        if (oldResource.equals(newResource))
            throw new IllegalArgumentException();
        final P provider = removeKeyProvider0(oldResource);
        //provider.setKey(null);
        return providers.put(newResource, provider);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned key provider is invalidated and will behave as if prompting
     * for the secret key had been disabled or cancelled by the user.
     */
    @Override
    public synchronized P removeKeyProvider(final URI resource) {
        if (null == resource)
            throw new NullPointerException();
        final P provider = removeKeyProvider0(resource);
        provider.setKey(null);
        return provider;
    }

    private P removeKeyProvider0(final URI resource) {
        final P provider = providers.remove(resource);
        if (null == provider)
            throw new IllegalArgumentException();
        return provider;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * Returns a string representation of this object for debugging and logging
     * purposes.
     */
    @Override
    public String toString() {
        return getClass().getName();
    }
}
