package com.bezirk.pipe.core;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Component that runs as a thread to process outgoing events in a loop:
 * <li> check to see if an event should be sent to a pipe
 * <li> send the event to the pipe via Pipe.pipeClient().sendEvent()
 */
public class PipeManagerImpl implements PipeManager {

	/*
     * Constants
	 */

    // This the lower bound for the size of the thread pool
    public static final int DEFAULT_CORE_POOL_SZ = 5;

    // Max size of pool that sends http requests to the remote web zirk
    public static final int DEFAULT_MAX_POOL_SZ_SENDING = 5;

    // Max size of the pool used for writing streams
    public static final int DEFAULT_MAX_POOL_SZ_WRITING = 10;

    // timeout before extra threads are destroyed
    public static final long DEFAULT_THREAD_KEEP_ALIVE_SECS = 5;

    public static final String DEFAULT_CERT_FILENAME = "upa.crt";

    /*
     * Private data members
     */
    private static final Logger log = LoggerFactory.getLogger(PipeManagerImpl.class);
    // Thread pool used to send http requests
    private ThreadPoolExecutor sendingThreadPool = null;
    // Thread pool for writing streams to disk
    private ThreadPoolExecutor writingThreadPool = null;
    /**
     * Reference to the pipe registry so that pipes can be searched for
     */
    private PipeRegistry pipeRegistry;
    /**
     * Whether the PipeManager was successfully initialized and thus able to process messages.
     * Interface methods won't execute if this is set to false.
     */
    private boolean initialized = false;
    private LocalBezirkSender localBezirkSender = null;

    private File outputDir = null;

    private String certFileName = DEFAULT_CERT_FILENAME;
	
	/*
	 *  Publicly accessible methods
	 */

    public PipeManagerImpl() {
        // no-arg constructor needed so that object can be treated as a java bean
    }

    public void init() {
        log.info("initializing ... ");
        if (sendingThreadPool == null) {
            // Thread pool used to send http requests
            sendingThreadPool = defaultRemoteSendingPool();
        }
        if (writingThreadPool == null) {
            // Thread pool for writing streams to disk
            writingThreadPool = defaultLocalWritingPool();
        }

        if (validateDataMembers()) {
            initialized = true;
            log.info("successfully initialized");
        } else {
            log.error("PipeMonitor not initialized correctly. Pipes disabled.");
            initialized = false;
        }
    }
	
	/*
	 * Public methods
	 */

    /**
     * Called by the bezirk SenderThread to enqueue an event for sending to the remote location
     *
     * @param event
     */
    //public void enqueueForRemoteSending(EventControlMessage eventRecord) {
    public void processRemoteSend(Header uhuHeader, String serializedEvent) {
        if (!initialized) {
            log.warn("PipeManager not initialized.  Can't execute processRemoteSend.");
            return;
        }
        log.info("beginning processRemoteSend() ...");

        if (uhuHeader == null) {
            log.error("Bezirk Header is null.  Not sending via cloudpipe: " + serializedEvent);
            return;
        }
        if (uhuHeader instanceof UnicastHeader) {
            log.error("Can't yet send Unicast messages via a pipe");
            return;
        }

        // Services shouldn't send multicast with null address, but hey, we're defensive
        Address address = ((MulticastHeader) uhuHeader).getAddress();
        if (address == null) {
            log.error("Address is null. Not sending via cloudpipe: " + serializedEvent);
            return;
        }

        // Only add to queue if the location refers to a pipe
        if (addressIsPipe(address)) {
            //logger.info("Adding event to PipeSender queue: " + eventRecord.getSerializedMessage());
            log.info("Address is pipe; adding event to PipeSender queue for pipe: " + address.getPipe());
            executeRemoteMulticastSend((MulticastHeader) uhuHeader, serializedEvent);
        } else {
            log.info("address does not contain a pipe: " + address.getPipe());
        }
    }

    /**
     * Called by the remote sending thread when it has received content to be written
     *
     * @param job
     */
    public void processLocalWrite(WriteJob writeJob) {
        if (!initialized) {
            log.warn("PipeManager not initialized.  Can't execute processLocalWrite.");
            return;
        }
        executeWrite(writeJob);
    }

    /**
     * Called by the writing thread once it has started writing the content to disk
     *
     * @param job
     */
    public void processLocalSend(LocalStreamSendJob job) {
        if (!initialized) {
            log.warn("PipeManager not initialized.  Can't execute processLocalSend.");
            return;
        }

        log.info("Executing local stream send: " + job);
        String serializedStreamDesc = job.getStreamDescriptor();

        localBezirkSender.invokeIncoming(job.getPipeHeader(), serializedStreamDesc, job.getFilePath());
    }

    public void processLocalSend(PipeHeader pipeHeader, String serializedEvent) {
        if (!initialized) {
            log.warn("PipeManager not initialized.  Can't execute processLocalSend.");
            return;
        }
        log.info("Executing local event send: " + serializedEvent);
        localBezirkSender.invokeReceive(pipeHeader, serializedEvent);
    }

    @Override
    public boolean isRegistered(Pipe pipe) {
        return pipeRegistry.isRegistered(pipe);
    }

    @Override
    public PipeRecord getPipeRecord(Pipe pipe) {
        log.warn("getPipeRecord() not implemented yet");
        // TODO return actual pipe record here or PipeInfo object
        return new PipeRecord(pipe);
    }

    @Override
    public void pipeGranted(boolean granted, Pipe pipe, PipePolicy allowedIn,
                            PipePolicy allowedOut, String sphereId, BezirkListener uhuListener) {

        // TODO call uhuListener directly or via a proxy?  is this done differently on each platform?
        // uhuListener.pipeGranted(...)
    }
	
	/*
	 * Helper methods
	 */

    /**
     * Submit a remote sending task by creating an instance of RemoteSender and passing
     * it to the sendingPool's execute method.  The thread pool calls RemoteSender.run
     * when it has a thread available.
     *
     * @param eventRecord
     */
    private void executeRemoteMulticastSend(MulticastHeader uhuMulticastHeader, String serializedEvent) {
        //String serializedEvent = eventRecord.getSerializedMessage();
        log.info("Executing remote send: " + serializedEvent);

        RemoteSender remoteSender = new RemoteSender();
        remoteSender.setPipeRegistry(pipeRegistry);
        remoteSender.setSerializedEvent(serializedEvent);
        remoteSender.setBezirkHeader(uhuMulticastHeader);
        remoteSender.setPipeMonitor(this);
        remoteSender.setCertFileName(certFileName);

        sendingThreadPool.execute(remoteSender);
    }

    /**
     * Submit a write task by creating an instance of StreamWriter and passing it to the
     * writerPool's execute method.  The write is started by calling StreamWriter.run
     *
     * @param job
     */
    private void executeWrite(WriteJob job) {
        log.info("Executing write: " + job);
        // Create a Runnable StreamWriter that will write to the file in own thread
        StreamWriter writer = new StreamWriter();
        writer.setWriteJob(job);
        writer.setPipeMonitor(this);
        writer.setOutputDir(outputDir);
        writer.setPipeHeader(job.getPipeHeader());

        writingThreadPool.execute(writer);
    }

    private ThreadPoolExecutor defaultRemoteSendingPool() {
        return new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SZ,
                DEFAULT_MAX_POOL_SZ_SENDING,
                DEFAULT_THREAD_KEEP_ALIVE_SECS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }

    private ThreadPoolExecutor defaultLocalWritingPool() {
        return new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SZ,
                DEFAULT_MAX_POOL_SZ_WRITING,
                DEFAULT_THREAD_KEEP_ALIVE_SECS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }

    /**
     * Ensure that data members were initialized correctly
     *
     * @return
     */
    protected boolean validateDataMembers() {
        boolean valid = true;

        if (localBezirkSender == null) {
            log.error("local bezirk sender is null.");
            valid = false;
        }
        if (pipeRegistry == null) {
            log.error("PipeRegistry is null.");
            valid = false;
        }
        if (sendingThreadPool == null) {
            log.error("sendingPool is null.");
            valid = false;
        }
        if (writingThreadPool == null) {
            log.error("writingPool is null.");
            valid = false;
        }
        if (outputDir == null) {
            log.error("outputDir is null.");
            valid = false;
        }
        if (certFileName == null) {
            log.error("certFileName is null.");
            valid = false;
        } else {
            InputStream certInStream = getClass().getClassLoader().getResourceAsStream(certFileName);
            if (certInStream == null) {
                log.error("Cert file could not be found on classpath: " + certFileName);
                valid = false;
            }
        }

        return valid;
    }


    /**
     * Check if this address represents a registered pipe
     *
     * @param address
     * @return
     */
    private boolean addressIsPipe(Address address) {
        return pipeRegistry.isRegistered(address.getPipe());
    }

	/*
	 * Getter and Setter methods - these are set by UhuProxy when the PipeSender is created
	 */

    public void setPipeRegistry(PipeRegistry pipeRegistry) {
        this.pipeRegistry = pipeRegistry;
    }

    public void setLocalSender(LocalBezirkSender localSender) {
        this.localBezirkSender = localSender;
    }

    public void stop() {
        this.initialized = false;
    }

    public void setWritingThreadPool(ThreadPoolExecutor writingThreadPool) {
        this.writingThreadPool = writingThreadPool;
    }

    public void setSendingThreadPool(ThreadPoolExecutor sendingThreadPool) {
        this.sendingThreadPool = sendingThreadPool;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }

}
