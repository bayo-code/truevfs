/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truevfs.kernel.spec.spi;

import net.java.truecommons.services.DecoratorService;
import javax.annotation.concurrent.ThreadSafe;
import net.truevfs.kernel.spec.cio.IoBuffer;
import net.truevfs.kernel.spec.cio.IoBufferPool;
import net.truevfs.kernel.spec.sl.IoBufferPoolLocator;

/**
 * An abstract service for decorating I/O buffer pools.
 * Decorator services are subject to service location by the
 * {@link IoBufferPoolLocator#SINGLETON}.
 * If multiple decorator services are locatable on the class path at run time,
 * they are applied in ascending order of their
 * {@linkplain #getPriority() priority} so that the product of the decorator
 * service with the greatest number becomes the head of the resulting product
 * chain.
 * <p>
 * Implementations should be thread-safe.
 */
@ThreadSafe
public abstract class IoBufferPoolDecorator
extends DecoratorService<IoBufferPool<? extends IoBuffer<?>>> {
}
