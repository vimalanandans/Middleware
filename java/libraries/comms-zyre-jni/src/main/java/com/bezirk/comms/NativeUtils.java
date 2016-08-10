package com.bezirk.comms;

import org.scijava.nativelib.NativeLibraryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * data must be packed in the below format
 * <p>
 * Native libraries should be packaged into a single jar file, with the
 * following directory and file structure:
 * </p>
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

    private static final String DELIMITER = "/";
    private static final String LIB_BIN = "lib-zeromq-bin";
    private static final String JAVA_TMPDIR = System.getProperty("java.io.tmpdir");
    private static final String RES_PATH = "META-INF" + DELIMITER + "lib" + DELIMITER;

    static NativeUtils utils = new NativeUtils();

    public static void loadLibs() {


        //loadLibsVer1();
        utils.loadLibsVer2();
    }

    /**
     * Sets the java library path to the specified path
     *
     * @param path the new library path
     * @throws Exception
     */
    public static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);

        //set sys_paths to null
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     */
    public static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    private static void loadLib(String name) {
        //name = NativeLibraryUtil.getPlatformLibraryName(name);
        try {
            System.loadLibrary(name);
        }catch (UnsatisfiedLinkError e){
            logger.error("Unable to load zyre libraries. \n" +
                    "Please refer http://developer.bezirk.com/documentation/installation_setup.php");
        }
    }

    /**
     * Puts library to temp dir and loads to memory
     * // refer http://stackoverflow.com/questions/1611357/how-to-make-a-jar-file-that-includes-dll-files
     */
   /* private static void loadLibJar(String path, String name) throws IOException {

        name = NativeLibraryUtil.getPlatformLibraryName(name);

        try {
            // have to use a stream
            //URL url = Zyre.class.getClassLoader().getResource(RES_WIN_PATH+name);
            URL url = Zyre.class.getClassLoader().getResource(path + name);
            InputStream in = url.openStream();
            // always write to different location
            File fileOut = new File(JAVA_TMPDIR + DELIMITER + LIB_BIN + DELIMITER + name);

            // if file exist after delete may be used by other process so leave it.
            if (fileOut.exists()) {
                logger.warn("using old libs, might be creating problems with old software ");
                System.load(fileOut.toString());
                return;
            }
            logger.info("Writing lib to: " + fileOut.getAbsolutePath());
            OutputStream out = FileUtils.openOutputStream(fileOut);

            try {
                IOUtils.copy(in, out);
            } finally {
                in.close();
                out.flush();
                out.close();
            }

            System.load(fileOut.toString());
        } catch (Exception e) {
            throw new IOException("Failed to load required DLL", e);
        }

    }*/

    private static void deleteOldFiles() {
        final File tmpDirectory = new File(JAVA_TMPDIR + LIB_BIN);
        final File[] files = tmpDirectory.listFiles();
        if (files == null) return;
        for (final File file : files) {
            // attempt to delete
            try {
                if (!file.delete()) {
                    logger.error("Failed to delete file: {}", file.getAbsolutePath());
                }
            } catch (final SecurityException e) {
                // not likely
                logger.error("unable to delete");
            }
        }
    }

    /**
     * refer http://fahdshariff.blogspot.com/2011/08/changing-java-library-path-at-runtime.html
     */

    public static void loadLibsVer1() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        logger.debug("java.library.path = " + System.getProperty("java.library.path"));
        String path = System.getProperty("java.library.path");
        logger.debug("LD_PRELOAD: " + System.getenv("LD_PRELOAD"));

        logger.debug("os.name: " + os);
        logger.debug("os.arch: " + arch);

        if (arch.equals("amd64") || arch.equals("x86_64")) {
            if (os.contains("windows")) {
                logger.debug("Loading native libs for x64windows");
                String[] libs = {"libsodium", "libzmq", "czmq", "zyre", "zyre-jni"};
                Zyre.loadSystemLibrary(libs);
            } else if (os.contains("linux")) {
                logger.info("Loading native libs for x64linux");
                String[] libs = {"sodium", "zmq", "czmq", "zyre", "zyre-jni"};
                Zyre.loadSystemLibrary(libs);
            } else if (os.contains("mac")) {
                logger.info("Loading native libs for osx");
                String[] libs = {"sodium", "zmq", "czmq", "zyre", "zyre-jni"};
                Zyre.loadSystemLibrary(libs);
                /*
    			System.load(path + "/libsodium.dylib");
    			System.load(path + "/libzmq.dylib");
    			System.load(path + "/libczmq.dylib");
    			System.load(path + "/libzyre.dylib");
    			System.load(path + "/libzyre-jni.dylib");
    			*/
            } else {
                throw new RuntimeException("x64 library for is not supported for: " + os);
            }
        } else {
            throw new RuntimeException("Architecture not supported: " + arch);
        }
    }

    public void loadLibsVer2() {

        // FIXME: the NativeLibraryUtil.loadVersionedNativeLibrary doesn't work for windows / linux
        // due to temp files are created with randam numbers other platforms not tested yet. test it and if it doesn't work migrate like the below


        // Copy the files to temp directory
        copyLibs();

        // set the load lib directory
        //setPath();


        if (NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.WINDOWS_64) {
            //   deleteOldFiles();

            try {
                loadLib("libsodium");
                loadLib("libzmq");
                loadLib("czmq");
                loadLib("zyre");
                loadLib("zyre-jni");
            } catch (Exception e) {
                String libPathProperty = System.getProperty("java.library.path");
                logger.info("java.library.path > " + libPathProperty);
                e.printStackTrace();
            }


        } else /*if (NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.LINUX_64)*/ {
            // mac and linux
            //   deleteOldFiles();

            try {
                loadLib("sodium");
                loadLib("zmq");
                loadLib("czmq");
                loadLib("zyre");
                loadLib("zyre-jni");
            } catch (Exception e) {
                String libPathProperty = System.getProperty("java.library.path");
                logger.info("java.library.path > " + libPathProperty);
                e.printStackTrace();
            }


        }// TODO : check for android


    }

    /**
     * Coping the library
     */
    private void copyLibs() {
        //String path = RES_PATH + NativeLibraryUtil.getArchitecture().name().toLowerCase() + DELIMITER;
        String path = RES_PATH + NativeLibraryUtil.getArchitecture().name().toLowerCase() + DELIMITER;

        URL url = Zyre.class.getClassLoader().getResource(path);

        if (url == null) {
            logger.warn("empty resource folder");
            return;
        }
        File fileOut = new File(JAVA_TMPDIR + DELIMITER + LIB_BIN + DELIMITER);

        if (fileOut.exists()) {
            logger.info("lib files already exits in " + fileOut.getAbsolutePath());
            logger.info("if new software installation, please delete libs at " + fileOut.getAbsolutePath());
            return;
        }

        try {
            ResourceUtils.copyResourcesRecursively(url, fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        File source = new File(url.getPath());
		File[] listOfFiles = source.listFiles();
		FileHelper.copyDirectory(template, output);

		File dest = new File(JAVA_TMPDIR + LIB_BIN);
		try {
			FileUtils.copyDirectory(source, listOfFiles[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

    }

    private File getFileFromURL(String path) {
        final URL url = Zyre.class.getClassLoader().getResource(path);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }

        return file;
    }

    private void setPath() {
        String property = System.getProperty("java.library.path");

        String TEMP_PATH = JAVA_TMPDIR + LIB_BIN + File.separator;
		/*try {
			setLibraryPath(TEMP_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
        // Doesn't work
        //System.setProperty("java.library.path",TEMP_PATH+ File.pathSeparator + property );

        property = System.getProperty("java.library.path");
        StringTokenizer parser = new StringTokenizer(property, ";");

        logger.info("path added > " + TEMP_PATH);

        while (parser.hasMoreTokens()) {
            logger.info(parser.nextToken());
        }
    }

}
