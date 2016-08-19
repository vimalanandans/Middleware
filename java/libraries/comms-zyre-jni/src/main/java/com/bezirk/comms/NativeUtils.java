package com.bezirk.comms;

import org.scijava.nativelib.NativeLibraryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Native libraries should be packaged into a single jar file, with the
 * following directory and file structure:
 * <pre>
 * META_INF
 *   lib
 *     linux_32
 *       libxxx[-vvv].so
 *     linux_64
 *       libxxx[-vvv].so
 *     osx_32
 *       libxxx[-vvv].dylib
 *     osx_64
 *       libxxx[-vvv].dylib
 *     windows_32
 *       xxx[-vvv].dll
 *     windows_64
 *       xxx[-vvv].dll
 * </pre>
 */

public class NativeUtils {
    public static final Logger logger = LoggerFactory.getLogger(NativeUtils.class);

    private static final String LIB_BIN = "lib-zeromq-bin";
    private static final String TMPDIR = System.getProperty("java.io.tmpdir");
    private static final String RES_PATH = "META-INF/lib/";

    public static void loadNativeBinaries() {
        loadLibraries();
    }

    private static void loadLibrary(String library) {
        try {
            System.load(library);
        } catch (UnsatisfiedLinkError e) {
            logger.error("Unable to load zyre libraries. \n" +
                    "Please refer http://developer.bezirk.com/documentation/installation_setup.php");
        }
    }

    private static void deleteOldFiles() {
        final File tmpDirectory = new File(TMPDIR + LIB_BIN);
        final File[] files = tmpDirectory.listFiles();
        if (files == null) return;
        for (final File file : files) {
            if (!file.delete()) {
                logger.error("Failed to delete file: {}", file.getAbsolutePath());
            }
        }
    }

    private static void loadLibraries() {
        copyLibs();

        // Must be ArrayList because the order the libraries are loaded in matters
        final List<String> libraries = new ArrayList<>(
                Arrays.asList("libsodium", "libzmq", "czmq", "zyre", "zyre-jni"));

        final String prefix;
        final String extension;

        if (NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.WINDOWS_64 ||
                NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.WINDOWS_32) {
            prefix = "";
            extension = ".dll";
        } else if (NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.OSX_64 ||
                NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.OSX_32) {
            prefix = "lib";
            extension = ".dylib";
        } else {
            prefix = "lib";
            extension = ".so";
        }
        try {
            for (String library : libraries) {
                if (!prefix.isEmpty() && !library.startsWith(prefix)) library = prefix + library;
                loadLibrary(TMPDIR + File.separator + LIB_BIN + File.separator + library + extension);
            }
        } catch (UnsatisfiedLinkError e) {
            logger.info("java.library.path > " + System.getProperty("java.library.path"), e);
        }
    }

    private static void copyLibs() {
        final String path = RES_PATH + NativeLibraryUtil.getArchitecture().name().toLowerCase();
        final URL url = Zyre.class.getClassLoader().getResource(path);

        if (url == null) {
            logger.error("Zyre binaries do not exist in the expected resource directory {}", path);
            return;
        }

        final File fileOut = new File(TMPDIR + File.separator + LIB_BIN + File.separator);

        if (fileOut.exists()) {
            logger.info("Zyre binaries already exits in {}, delete this folder for new Bezirk " +
                    "installations", fileOut.getAbsolutePath());
        } else {
            try {
                ResourceUtils.copyResourcesRecursively(url, fileOut);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
