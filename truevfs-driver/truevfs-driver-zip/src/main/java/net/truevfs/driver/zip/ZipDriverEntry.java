/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.truevfs.driver.zip;

import net.truevfs.comp.zip.driver.AbstractZipDriverEntry;
import net.truevfs.comp.zip.DateTimeConverter;
import net.truevfs.comp.zip.ZipEntry;

/**
 *
 * @author christian
 */
public class ZipDriverEntry extends AbstractZipDriverEntry {
    
    public ZipDriverEntry(String name) {
        super(name);
    }

    protected ZipDriverEntry(String name, ZipEntry template) {
        super(name, template);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link ZipDriverEntry} returns
     * {@link DateTimeConverter#ZIP}.
     *
     * @return {@link DateTimeConverter#ZIP}
     */
    @Override
    protected DateTimeConverter getDateTimeConverter() {
        return DateTimeConverter.ZIP;
    }
}
