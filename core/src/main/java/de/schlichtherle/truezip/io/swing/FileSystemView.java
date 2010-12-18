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
package de.schlichtherle.truezip.io.swing;

import de.schlichtherle.truezip.io.file.ArchiveDetector;
import de.schlichtherle.truezip.io.file.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * A custom file system view required to browse archive files like (virtual)
 * directories with a JFileChooser.
 * This class is also used by
 * {@link de.schlichtherle.truezip.io.swing.tree.FileTreeCellRenderer}
 * to render files and directories in a
 * {@link de.schlichtherle.truezip.io.swing.JFileTree}.
 *
 * @author  Christian Schlichtherle
 * @version $Id$
 */
//
// Unfortunately this is a pretty ugly piece of code.
// The reason for this is the completely broken design of the genuine
// JFileChooser, FileSystemView, FileView, ShellFolder and BasicFileChooserUI
// classes.
// The FileSystemView uses a lot of "instanceof" runtime type detections
// in conjunction with Sun's proprietory (and platform dependent) ShellFolder
// class, which subclasses java.io.File.
// Other classes like BasicFileChooserUI also rely on the use of the
// ShellFolder class, which they really shouldn't.
// The use of the ShellFolder class is also the sole reason for the existence
// of the file delegate property in de.schlichtherle.truezip.io.File.
// For many methods in this class, we need to pass in the delegate to the
// superclass implementation in order for the JFileChooser to work as expected.
//
// Dear Sun: Please enhance the JFileChooser, FileSystemView, FileView and
// ShellFolder classes.
// My primary recommendation would be to define clear responsibilities for
// each of the redesigned classes: Most importantly, all (meta) properties of
// a file (like its name, icon, description, etc.) should be clearly located
// in ONE class and whoever uses this should rely on polymorphism rather than
// instanceof conditionals.
// Finally, please put your new design to test with browsing a virtual file
// system (like TrueZIP provides) - the current JFileChooser is just not able
// to do this right.
//
public class FileSystemView extends DecoratingFileSystemView {

    private static FileSystemView defaultView = new FileSystemView(
            javax.swing.filechooser.FileSystemView.getFileSystemView(),
            null);

    /** Maybe null - uses default then. **/
    private ArchiveDetector archiveDetector;

    private FileSystemView(
            javax.swing.filechooser.FileSystemView delegate,
            ArchiveDetector archiveDetector) {
        super(delegate);
        this.archiveDetector = archiveDetector;
    }

    public static javax.swing.filechooser.FileSystemView getFileSystemView() {
        return getFileSystemView(null);
    }

    public static javax.swing.filechooser.FileSystemView getFileSystemView(
            ArchiveDetector archiveDetector) {
        return archiveDetector != null
            ? new FileSystemView(
                javax.swing.filechooser.FileSystemView.getFileSystemView(),
                archiveDetector)
            : defaultView;
    }

    /**
     * Returns a valid archive detector to use with this class.
     * If no archive detector has been explicitly set for this file system
     * view or the archive detector has been set to {@code null},
     * then {@link de.schlichtherle.truezip.io.file.File#getDefaultArchiveDetector} is
     * returned.
     */
    public ArchiveDetector getArchiveDetector() {
        return archiveDetector != null
            ? archiveDetector
            : File.getDefaultArchiveDetector();
    }

    /**
     * Sets the archive detector to use within this class.
     *
     * @param archiveDetector The archive detector to use.
     *        May be {@code null} to indicate that
     *        {@link de.schlichtherle.truezip.io.file.File#getDefaultArchiveDetector}
     *        should be used.
     */
    public void setArchiveDetector(ArchiveDetector archiveDetector) {
        this.archiveDetector = archiveDetector;
    }

    /** Wraps the given file in an archive enabled file. */
    protected File wrap(final java.io.File file) {
        if (file == null)
            return null;
        return file instanceof File
                ? (File) file
                : getArchiveDetector().newFile(file);
    }

    /** Unwraps the delegate of a possibly archive enabled file. */
    protected java.io.File unwrap(final java.io.File file) {
        return file instanceof File
                ? ((File) file).getDelegate()
                : file;
    }

    /**
     * Creates a ZIP enabled file where necessary only,
     * otherwise the blueprint is simply returned.
     */
    public java.io.File createFileObject(final java.io.File file) {
        if (file == null)
            return null;
        final File wFile = wrap(file);
        return wFile.isArchive() || wFile.isEntry()
                ? wFile
                : unwrap(file);
    }

    //
    // Overridden methods:
    //

    @Override
    public boolean isRoot(java.io.File file) {
        return super.isRoot(unwrap(file));
    }

    @Override
    public Boolean isTraversable(java.io.File file) {
        final File wFile = wrap(file);
        return null != wFile
                ? Boolean.valueOf(wFile.isDirectory())
                : super.isTraversable(unwrap(file));
    }

    @Override
    public String getSystemDisplayName(java.io.File file) {
        final File wFile = wrap(file);
        if (wFile.isArchive() || wFile.isEntry())
            return wFile.getName();
        return super.getSystemDisplayName(unwrap(file));
    }

    @Override
    public String getSystemTypeDescription(java.io.File file) {
        final File wFile = wrap(file);
        final String typeDescription = FileView.typeDescription(wFile);
        if (typeDescription != null)
            return typeDescription;
        return super.getSystemTypeDescription(unwrap(file));
    }

    @Override
    public Icon getSystemIcon(java.io.File file) {
        final File wFile = wrap(file);
        final Icon icon = FileView.closedIcon(wFile);
        if (icon != null)
            return icon;
        final java.io.File uFile = unwrap(file);
        return uFile.exists()
            ? super.getSystemIcon(uFile)
            : null;
    }

    @Override
    public boolean isParent(java.io.File folder, java.io.File file) {
        return super.isParent(wrap(folder), wrap(file))
            || super.isParent(unwrap(folder), unwrap(file));
    }

    @Override
    public java.io.File getChild(java.io.File parent, String child) {
        final File wParent = wrap(parent);
        if (wParent.isArchive() || wParent.isEntry())
            return createFileObject(super.getChild(wParent, child));
        return createFileObject(super.getChild(unwrap(parent), child));
    }

    @Override
    public boolean isFileSystem(java.io.File file) {
        return super.isFileSystem(unwrap(file));
    }

    @Override
    public java.io.File createNewFolder(final java.io.File parent)
    throws IOException {
        final File wParent = wrap(parent);
        if (wParent.isArchive() || wParent.isEntry()) {
            File folder = getArchiveDetector().newFile(
                    wParent,
                    UIManager.getString(File.separatorChar == '\\'
                            ? "FileChooser.win32.newFolder"
                            : "FileChooser.other.newFolder"));

            for (int i = 2; !folder.mkdirs(); i++) {
                if (i > 100)
                    throw new IOException(wParent + ": Could not create new directory entry!");
                folder = getArchiveDetector().newFile(
                        wParent,
                        MessageFormat.format(
                            UIManager.getString(File.separatorChar == '\\'
                                ? "FileChooser.win32.newFolder.subsequent"
                                : "FileChooser.other.newFolder.subsequent"),
                            new Object[] { Integer.valueOf(i) }));
            }

            return folder;
        }
        return createFileObject(super.createNewFolder(unwrap(parent)));
    }

    @Override
    public boolean isHiddenFile(java.io.File file) {
        return super.isHiddenFile(unwrap(file));
    }

    @Override
    public boolean isFileSystemRoot(java.io.File file) {
        return super.isFileSystemRoot(unwrap(file));
    }

    @Override
    public boolean isDrive(java.io.File file) {
        return super.isDrive(unwrap(file));
    }

    @Override
    public boolean isFloppyDrive(java.io.File file) {
        return super.isFloppyDrive(unwrap(file));
    }

    @Override
    public boolean isComputerNode(java.io.File file) {
        return super.isComputerNode(unwrap(file));
    }

    /**
     * Creates a ZIP enabled file where necessary only,
     * otherwise the file system view delegate is used to create the file.
     */
    @Override
    public java.io.File createFileObject(java.io.File dir, String str) {
        return createFileObject(super.createFileObject(dir, str));
    }

    /**
     * Creates a ZIP enabled file where necessary only,
     * otherwise the file system view delegate is used to create the file.
     */
    @Override
    public java.io.File createFileObject(String str) {
        return createFileObject(super.createFileObject(str));
    }

    @Override
    public java.io.File[] getFiles(
            final java.io.File dir,
            final boolean useFileHiding) {
        final File smartDir = wrap(dir);
        if (smartDir.isArchive() || smartDir.isEntry()) {
            // dir is a ZIP file or an entry in a ZIP file.
            return smartDir.listFiles(new FileFilter() {
                @Override
				public boolean accept(java.io.File file) {
                    return !useFileHiding || !isHiddenFile(file);
                }
            });
        } else {
            final java.io.File files[] = super.getFiles(unwrap(dir), useFileHiding);
            if (files != null)
                for (int i = files.length; --i >= 0; )
                    files[i] = createFileObject(files[i]);

            return files;
        }
    }

    @Override
    public java.io.File getParentDirectory(java.io.File file) {
        final File wFile = wrap(file);
        if (wFile.isEntry())
            return createFileObject(wFile.getParentFile());
        return createFileObject(super.getParentDirectory(unwrap(file)));
    }

    /*protected java.io.File createFileSystemRoot(java.io.File file) {
        // As an exception to the rule, we will not delegate this call as this
        // method has protected access.
        // Instead, we will delegate it to our superclass and unwrap the plain
        // file object from it.
        return super.createFileSystemRoot(unwrap(file));
    }*/
}
