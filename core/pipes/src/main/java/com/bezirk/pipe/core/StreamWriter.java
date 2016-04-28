package com.bezirk.pipe.core;

import com.bezirk.control.messages.pipes.PipeHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class used to write a stream to disk so that it can be passed to a
 * local bezirk zirk
 */
public class StreamWriter implements Runnable {
    private static int numThreads = 0;
    private static final Logger logger = LoggerFactory.getLogger(StreamWriter.class);
    private final int id;

    private WriteJob writeJob;

    // Used to call back to the pipemonitor with the result from run(),
    // which is the name of the file to pass to the local bezirk zirk
    private PipeManagerImpl pipeMonitor;


    // TODO: we need to set this somehow when the pipe manager gets created by the platform
    //File outputDir = new File(Environment.getExternalStorageDirectory() + "/UhuDownloads");
    private File outputDir = null;

    private PipeHeader pipeHeader;


    public StreamWriter() {
        id = ++numThreads;
    }

    /**
     * Write the stream to disk
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (!validateDataMembers()) {
            logger.error("Data members not specified correctly");
            return;
        }

        try {
            writeFile();
        } catch (IOException e) {
            logger.error("Error writing to file: " + e.getClass().getSimpleName() + ": \n" + e);
        }
    }

	/*
	 * Helper methods
	 */

    protected boolean validateDataMembers() {
        String err = "Cannot write file. ";
        boolean valid = true;

        if (pipeMonitor == null) {
            logger.error(err + "pipeMonitor is null");
            valid = false;
        }
        if (writeJob == null) {
            logger.error(err + "writeJob is null");
            valid = false;
        }
        if (writeJob.getInputStream() == null) {
            logger.error(err + "writeJob.inputStream is null");
            valid = false;
        }
        if (writeJob.getStreamDescriptor() == null) {
            logger.error(err + "writeJob.streamDescriptor is null");
            valid = false;
        }
        if (writeJob.getShortFileName() == null) {
            logger.error(err + "writeJob.shortFileName is null");
            valid = false;
        }
        if (outputDir == null) {
            logger.error(err + "outputDir is null");
            valid = false;
        }
        if (pipeHeader == null) {
            logger.error(err + "pipeHeader is null");
            valid = false;
        }

        return valid;
    }

    /**
     * Write the file to disk and pass the name of the file back to the pipeMonitor once the write
     * has started
     *
     * @throws IOException
     */
    protected void writeFile() throws IOException {
        File outputFile = createFile();

        InputStream inStream = writeJob.getInputStream();
        String serializedStreamDesc = writeJob.getStreamDescriptor();

        int counter = 0;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            try {
                final byte[] buffer = new byte[1024];
                int read;

                // Ensure data is available to be read before proceeding
                while (inStream.available() < 1) {
                    logger.warn("Data not available to be read. Sleeping for 200ms.");
                    Thread.sleep(200);
                }
                // Data is available to be read, so we can send fileName to local services
                logger.info("*** enqueue-ing for local sending *** " + outputFile.getAbsolutePath());
                LocalStreamSendJob localSendJob = new LocalStreamSendJob();
                localSendJob.setStreamDescriptor(serializedStreamDesc);
                localSendJob.setFilePath(outputFile.getAbsolutePath());
                logger.info("** setting pipeheader: " + pipeHeader.serialize());
                localSendJob.setPipeHeader(pipeHeader);
                pipeMonitor.processLocalSend(localSendJob);

                logger.info("(thread: " + id + ") Streaming file write started");

                // Write the file
                while ((read = inStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    fileOutputStream.flush();

                    counter++;
                    if (counter % 500 == 0) {
                        logger.info("(thread: " + id + ") streaming file written in KBytes: " + counter);
                    }
                }
                fileOutputStream.flush();
            } catch (Exception t) {
                logger.error("(thread: " + id + ") Problem writing file: \n", t);
            } finally {
                fileOutputStream.close();
            }
        } finally {
            logger.info("(thread: " + id + ") streaming file written completely: " + outputFile);
            inStream.close();
        }

        if (!outputFile.exists()) {
            throw new IOException("(thread: " + id + ") File was not created: " + outputFile);
        }
    }


    /**
     * Create a file in the bezirk portion of the externalStorageDirectory
     *
     * @param fileName
     * @param retainFile True if the file shoudl be kept on disk and managed by bezirk services.
     *                   False to instruct bezirk to create a temp file which may be deleted by the system
     * @return
     * @throws IOException
     */
    protected File createFile() throws IOException {
        // Create output directory if it doesn't exist
        if (outputDir.exists()) {
            logger.info("Output directory already exists: " + outputDir);
        } else {
            boolean success = outputDir.mkdirs();
            if (!success) {
                throw new IOException("Temp directory does not exist and it could not be created");
            }
            logger.info("Output directory created: " + outputDir);
        }

        final File outputFile;

        if (writeJob.retainFile) {
            outputFile = new File(outputDir, writeJob.getShortFileName());
            logger.info("Created regular file: " + outputFile);
        } else {
            outputFile = File.createTempFile(writeJob.getShortFileName(), "", outputDir);
            logger.info("Created temp file: " + outputFile);
        }

        return outputFile;
    }
	
	/*
	 * Getters/setters
	 */

    public WriteJob getWriteJob() {
        return writeJob;
    }

    public void setWriteJob(WriteJob writeJob) {
        this.writeJob = writeJob;
    }

    public void setPipeMonitor(PipeManagerImpl pipeMonitor) {
        this.pipeMonitor = pipeMonitor;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public PipeHeader getPipeHeader() {
        return pipeHeader;
    }

    public void setPipeHeader(PipeHeader pipeHeader) {
        this.pipeHeader = pipeHeader;
    }
}
