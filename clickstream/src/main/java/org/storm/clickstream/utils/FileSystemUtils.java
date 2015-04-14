package org.storm.clickstream.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 * Utility to search for files on the local filesystem.
 * </p>
 * 
 * @author Timothy Storm
 */
public class FileSystemUtils extends org.apache.commons.io.FileSystemUtils {

    /**
     * Discovers files based on the parent directory and the file filter. If no files are found then an empty array is
     * returned - never null.
     * 
     * @param parentDir
     *            - file to search from
     * @param fileFilter
     *            - filter to apply to files
     * @return files in the parentDir that are accepted by the filter
     * @throws IOException
     */
    public static File[] discover(File parentDir, FileFilter fileFilter) throws IOException {
        return discover(parentDir, fileFilter, false);
    }

    /**
     * Discovers files based on the parent directory and the file filter. If no files are found then an empty array is
     * returned - never null. If the recursive flag is 'true' then all directories under the parentDir are also
     * searched.
     * 
     * @param dir
     *            - file to search from
     * @param fileFilter
     *            - filter to apply to files
     * @param recursive
     *            - recursively discover files
     * @return files in the parentDir and optional descendants that are accepted by the filter
     * @throws IOException
     */
    public static File[] discover(File dir, FileFilter fileFilter, boolean recursive) throws IOException {
        if (!dir.exists()) throw new IOException("directory does not exist");
        if (!dir.isDirectory()) throw new IOException("not a directory");

        File[] files = new File[0];
        for (File f : dir.listFiles()) {
            if (isTraversable(f) && recursive) files = ArrayUtils.addAll(files, discover(f, fileFilter, recursive));
            else if (fileFilter.accept(f)) files = ArrayUtils.add(files, f);
        }
        return files;
    }

    /**
     * Discovers files based on the parent directory and the file name pattern. If no files are found then an empty
     * array is returned - never null.
     * 
     * @param parentDir
     *            - file to search from
     * @param name
     *            - to search
     * @return files in the parentDir, and sub directories, that match the name
     * @throws IOException
     */
    public static File discover(File parentDir, String name) throws IOException {
        return discover(parentDir, name, false);
    }

    /**
     * Discovers files based on the parent directory and the file name. If no files are found then an empty array is
     * returned - never null. If the recursive flag is 'true' then all directories under the parentDir are also
     * searched.
     * 
     * @param parentDir
     *            - file to search from
     * @param name
     *            - to search
     * @param recursive
     *            - recursively discover files
     * @return files in the parentDir, and optionally the sub directories, that match the name
     * @throws IOException
     */
    public static File discover(File parentDir, String name, boolean recursive) throws IOException {
        FileFilter nameFileFilter = new NameFileFilter(name);
        File[] files = discover(parentDir, nameFileFilter, recursive);
        if (files == null || files.length <= 0) return null;
        return files[0];
    }

    /**
     * Discovers files based on the parent directory and the file filter. If no files are found then an empty array is
     * returned - never null.
     * 
     * @param parentDir
     *            - path to search from
     * @param fileFilter
     *            - filter to apply to files
     * @return files in the parentDir that are accepted by the filter
     * @throws IOException
     */
    public static File[] discover(String parentDir, FileFilter fileFilter) throws IOException {
        return discover(new File(parentDir), fileFilter);
    }

    /**
     * Discovers files based on the parent directory and the file filter. If no files are found then an empty array is
     * returned - never null. If the recursive flag is 'true' then all directories are recursively searched searched.
     * 
     * @param parentDir
     *            - path to search from
     * @param fileFilter
     *            - filter to apply to files
     * @param recursive
     *            - recursively discover files
     * @return files in the parentDir and optional descendants that are accepted by the filter
     * @throws IOException
     */
    public static File[] discover(String parentDir, FileFilter fileFilter, boolean recursive) throws IOException {
        return discover(new File(parentDir), fileFilter, recursive);
    }

    /**
     * Discovers files based on the parent directory and the file name pattern. If no files are found then an empty
     * array is returned - never null.
     * 
     * @param parentDir
     *            - file to search from
     * @param name
     *            - to search
     * @return files in the parentDir, and sub directories, that match the name
     * @throws IOException
     */
    public static File discover(String parentPath, String name) throws IOException {
        return discover(parentPath, name, false);
    }

    /**
     * Discovers files based on the parent directory and the file name. If no files are found then an empty array is
     * returned - never null. If the recursive flag is 'true' then all directories under the parentDir are also
     * searched.
     * 
     * @param parentDir
     *            - file to search from
     * @param name
     *            - to search
     * @param recursive
     *            - recursively discover files
     * @return files in the parentDir, and optionally the sub directories, that match the name
     * @throws IOException
     */
    public static File discover(String parentPath, String name, boolean recursive) throws IOException {
        return discover(new File(parentPath), name, recursive);
    }

    /**
     * Determines if a directory is traversable by checking that its not null, IS a directory and is not a symlink
     * (a.k.a shortcut).
     * 
     * @param dir
     *            - to test
     * @return true if the directory is traversable
     * @throws IOException
     */
    public static boolean isTraversable(File dir) {
        try {
            return dir != null && dir.isDirectory() && !FileUtils.isSymlink(dir);
        } catch (IOException e) {}
        return false;
    }
}
