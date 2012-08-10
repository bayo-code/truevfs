/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truevfs.ext.jmx;

import javax.annotation.concurrent.ThreadSafe;
import net.java.truevfs.ext.jmx.stats.SyncStatistics;

/**
 * The MXBean interface for {@linkplain SyncStatistics sync statistics}.
 *
 * @author Christian Schlichtherle
 */
@ThreadSafe
public interface JmxSyncStatisticsMXBean {
    String getSubject();
    long   getSyncNanosecondsPerOperation();
    long   getSyncNanosecondsTotal();
    int    getSyncOperations();
    long   getTimeCreatedMillis();
    String getTimeCreatedString();
    long   getTimeUpdatedMillis();
    String getTimeUpdatedString();
}
