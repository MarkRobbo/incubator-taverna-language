package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBundleFileSystem {

    private BundleFileSystem fs;

    @Before
    public void newFS() throws Exception {
        fs = BundleFileSystemProvider.newFileSystemFromTemporary();
        // System.out.println(fs.getSource());
    }

    @After
    public void closeFS() throws IOException {
        fs.close();
        Files.deleteIfExists(fs.getSource());
    }

    /**
     * Test that BundleFileSystem does not allow a ZIP file to also become a
     * directory. See http://stackoverflow.com/questions/16588321/ as Java 7'z
     * ZIPFS normally allows this (!)
     * 
     * @throws Exception
     */
    @Test
    public void fileAndDirectory() throws Exception {
        Path folder = fs.getPath("folder");

        // To test on local file system, uncomment next 2 lines:
//        Path test = Files.createTempDirectory("test");
//        folder = test.resolve("folder");
        
        
        assertFalse(Files.exists(folder));
        Files.createFile(folder);
        assertTrue(Files.exists(folder));
        assertTrue(Files.isRegularFile(folder));
        assertFalse(Files.isDirectory(folder));

        try {
            Files.createDirectory(folder);
            fail("Should have thrown FileAlreadyExistsException");
        } catch (FileAlreadyExistsException ex) {
        }
        assertFalse(Files.isDirectory(folder));

        try {
            Files.createDirectories(folder);
            fail("Should have thrown FileAlreadyExistsException");
        } catch (FileAlreadyExistsException ex) {
        }
        assertFalse(Files.isDirectory(folder));

        Path child = folder.resolve("child");

        try {
            Files.createFile(child);
            fail("Should have thrown NoSuchFileException");
        } catch (NoSuchFileException ex) {
        }
        assertFalse(Files.exists(child));

        assertTrue(Files.isRegularFile(folder));
        assertFalse(Files.isDirectory(folder));
        assertFalse(Files.isDirectory(child.getParent()));
        assertFalse(Files.isDirectory(fs.getPath("folder/")));
    }

    /**
     * Test that BundleFileSystem does not allow a ZIP directory to also become
     * a file. See http://stackoverflow.com/questions/16588321/ as Java 7'z
     * ZIPFS normally allows this (!)
     * 
     * @throws Exception
     */
    @Test
    public void directoryAndFile() throws Exception {
        Path folderSlash = fs.getPath("folder/");
        Path folder = fs.getPath("folder");

        
        // Uncomment next 3 lines to test on local FS
//        Path test = Files.createTempDirectory("test");
//        folderSlash = test.resolve("folder/");
//        folder = test.resolve("folder");
        
        assertFalse(Files.exists(folderSlash));

        Files.createDirectory(folderSlash);
        assertTrue(Files.exists(folderSlash));
        assertFalse(Files.isRegularFile(folderSlash));
        assertTrue(Files.isDirectory(folderSlash));

        try {
            Files.createDirectory(folderSlash);
            fail("Should have thrown FileAlreadyExistsException");
        } catch (FileAlreadyExistsException ex) {
        }

        try {
            Files.createFile(folderSlash);
            fail("Should have thrown IOException");
        } catch (IOException ex) {
        }

        try {
            Files.createFile(folder);
            fail("Should have thrown IOException");
        } catch (IOException ex) {
        }
                
        Path child = folderSlash.resolve("child");
        Files.createFile(child);

        assertTrue(Files.exists(folder));
        assertTrue(Files.exists(folderSlash));        
        
        assertFalse(Files.isRegularFile(folder));
        assertFalse(Files.isRegularFile(folderSlash));

        assertTrue(Files.isDirectory(folder));
        assertTrue(Files.isDirectory(folderSlash));

        
    }

}