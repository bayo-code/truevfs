/*
 * Copyright (C) 2005-2012 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truevfs.ext.jmx;

import java.util.Date;
import java.util.Objects;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.StandardMBean;
import net.java.truevfs.ext.jmx.stats.FsStatistics;
import net.java.truevfs.ext.jmx.stats.IoStatistics;
import net.java.truevfs.ext.jmx.stats.SyncStatistics;

/**
 * The combined MXBean view for an {@linkplain FsStatistics I/O logger}
 * and its {@linkplain IoStatistics I/O statistics}.
 *
 * @author Christian Schlichtherle
 */
@ThreadSafe
public class JmxStatisticsView
extends StandardMBean implements JmxStatisticsMXBean {
    protected final JmxStatistics stats;

    public JmxStatisticsView(final JmxStatistics stats) {
        super(JmxStatisticsMXBean.class, true);
        this.stats = Objects.requireNonNull(stats);
    }

    /**
     * Override customization hook:
     * You can supply a customized description for MBeanInfo.getDescription()
     */
    @Override
    protected String getDescription(MBeanInfo info) {
        return "A log of file system statistics.";
    }

    /**
     * Override customization hook:
     * You can supply a customized description for MBeanAttributeInfo.getDescription()
     */
    @Override
    protected String getDescription(MBeanAttributeInfo info) {
        switch (info.getName()) {
        case "ReadBytesPerOperation":
            return "The average number of bytes per read operation.";
        case "ReadBytesTotal":
            return "The total number of bytes read.";
        case "ReadKilobytesPerSecond":
            return "The average throughput for read operations.";
        case "ReadNanosecondsPerOperation":
            return "The average execution time per read operation.";
        case "ReadNanosecondsTotal":
            return "The total execution time for read operations.";
        case "ReadOperations":
            return "The total number of read operations.";
        case "Subject":
            return "The subject of this log.";
        case "SyncNanosecondsPerOperation":
            return "The average execution time per sync operation.";
        case "SyncNanosecondsTotal":
            return "The total execution time for sync operations.";
        case "SyncOperations":
            return "The total number of sync operations.";
        case "TimeCreatedMillis":
            return "The time this log has been created in milliseconds.";
        case "TimeCreatedString":
            return "The time this log has been created.";
        case "TimeUpdatedMillis":
            return "The last time this log has been updated in milliseconds.";
        case "TimeUpdatedString":
            return "The last time this log has been updated.";
        case "WriteBytesPerOperation":
            return "The average number of bytes per write operation.";
        case "WriteBytesTotal":
            return "The total number of bytes written.";
        case "WriteKilobytesPerSecond":
            return "The average throughput for write operations.";
        case "WriteNanosecondsPerOperation":
            return "The average execution time per write operation.";
        case "WriteNanosecondsTotal":
            return "The total execution time for write operations.";
        case "WriteOperations":
            return "The total number of write operations.";
        default:
            return null;
        }
    }

    private IoStatistics getInputStats() {
        return stats.getInputStats();
    }

    private IoStatistics getOutputStats() {
        return stats.getOutputStats();
    }

    private SyncStatistics getSyncStats() {
        return stats.getSyncStats();
    }

    @Override
    public int getReadBytesPerOperation() {
        return getInputStats().getBytesPerOperation();
    }

    @Override
    public long getReadBytesTotal() {
        return getInputStats().getBytesTotal();
    }

    @Override
    public long getReadKilobytesPerSecond() {
        return getInputStats().getKilobytesPerSecond();
    }

    @Override
    public long getReadNanosecondsPerOperation() {
        return getInputStats().getNanosecondsPerOperation();
    }

    @Override
    public long getReadNanosecondsTotal() {
        return getInputStats().getNanosecondsTotal();
    }

    @Override
    public int getReadOperations() {
        return getInputStats().getSequenceNumber();
    }

    @Override
    public String getSubject() {
        return stats.getSubject();
    }

    @Override
    public long getSyncNanosecondsPerOperation() {
        return getSyncStats().getNanosecondsPerOperation();
    }

    @Override
    public long getSyncNanosecondsTotal() {
        return getSyncStats().getNanosecondsTotal();
    }

    @Override
    public int getSyncOperations() {
        return getSyncStats().getSequenceNumber();
    }

    @Override
    public long getTimeCreatedMillis() {
        return stats.getTimeCreatedMillis();
    }

    @Override
    public String getTimeCreatedString() {
        return new Date(getTimeCreatedMillis()).toString();
    }

    @Override
    public long getTimeUpdatedMillis() {
        return Math.max(
                Math.max(getInputStats().getTimeMillis(), getOutputStats().getTimeMillis()),
                getSyncStats().getTimeMillis());
    }

    @Override
    public String getTimeUpdatedString() {
        return new Date(getTimeUpdatedMillis()).toString();
    }

    @Override
    public int getWriteBytesPerOperation() {
        return getOutputStats().getBytesPerOperation();
    }

    @Override
    public long getWriteBytesTotal() {
        return getOutputStats().getBytesTotal();
    }

    @Override
    public long getWriteKilobytesPerSecond() {
        return getOutputStats().getKilobytesPerSecond();
    }

    @Override
    public long getWriteNanosecondsPerOperation() {
        return getOutputStats().getNanosecondsPerOperation();
    }

    @Override
    public long getWriteNanosecondsTotal() {
        return getOutputStats().getNanosecondsTotal();
    }

    @Override
    public int getWriteOperations() {
        return getOutputStats().getSequenceNumber();
    }
}
