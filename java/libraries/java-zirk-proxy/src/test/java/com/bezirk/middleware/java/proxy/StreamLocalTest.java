package com.bezirk.middleware.java.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * @author VBD4KOR
 *         This testcase is used to test the Streaming locally. Two MockServices are used for testing.
 *         MockService-B registers and subscribes for a StreamDescriptor.
 *         MockService-A registers and subscribes for dummy StreamDescriptor. MS-A discovers the services and streams the file to the MS-B.
 *         MS-B receives the file.
 */
public class StreamLocalTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamLocalTest.class);
    private static boolean isStreamSuccess = false;
    private short sendStreamId = -1;
    private File sendFile = new File(StreamLocalTest.class.getClassLoader().getResource("streamingTestFile.txt").getPath());
    private StreamLocalMockRequestStreamDescriptor request = null;
    private StreamLocalMockServiceA mockA;
    private StreamLocalMockServiceB mockB;

    @Before
    public void setUpServices() {
        StreamLocalTest streamLocalTest = new StreamLocalTest();
        mockB = streamLocalTest.new StreamLocalMockServiceB();
        mockB.setupMockService();

        mockA = streamLocalTest.new StreamLocalMockServiceA();
        mockA.setupMockService();
    }

    @After
    public void destroySetUp() {
        Bezirk bezirk = com.bezirk.middleware.java.proxy.BezirkMiddleware.registerZirk("XXX");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * The zirk that discovers and streams the file
     */
    private final class StreamLocalMockServiceA {
        private final String zirkName = "StreamLocalMockZirkA";
        private Bezirk bezirk = null;
        private StreamLocalDummyMessageSet messagetSet;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.java.proxy.BezirkMiddleware.registerZirk(zirkName);
            messagetSet = new StreamLocalDummyMessageSet();
            bezirk.subscribe(messagetSet);

        }
    }

    private final class StreamLocalDummyMessageSet extends StreamSet {
        public StreamLocalDummyMessageSet() {
            super(StreamLocalMockRequestStreamDescriptor.class);
        }
    }

    private final class StreamLocalMockServiceMessageSet extends StreamSet {
        public StreamLocalMockServiceMessageSet() {
            super(StreamLocalMockRequestStreamDescriptor.class);
        }
    }

    /**
     * StreamDescriptor Descriptor
     */
    private final class StreamLocalMockRequestStreamDescriptor extends StreamDescriptor {

        private StreamLocalMockRequestStreamDescriptor() {
            super(false, true, sendFile,"Test");
        }


    }

    /**
     * Zirk that is consumer of StreamDescriptor
     */
    private final class StreamLocalMockServiceB {
        private final String zirkName = "StreamLocalMockServiceB";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.java.proxy.BezirkMiddleware.registerZirk(zirkName);
            StreamLocalMockServiceMessageSet streams = new StreamLocalMockServiceMessageSet();

            streams.setStreamReceiver(new StreamSet.StreamReceiver<File>() {
                @Override
                public void receiveStream(StreamDescriptor streamDescriptor, File file, ZirkEndPoint sender) {
                    logger.info("****** RECEIVED STREAM REQUEST ******");
                    assertNotNull(streamDescriptor);
                    assertNotNull(file);
                    assertNotNull(sender);

                    logger.info("streamDescriptor-> " + streamDescriptor);
                    logger.info("filePath-> " + file);
                    logger.info("sender-> " + sender);

                    assertEquals(sendFile, file);
                    assertEquals(request.toJson(), streamDescriptor);
                    // Read and verify the streamDescriptor
                    FileInputStream fileInputStream = null;
                    BufferedReader reader = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        reader = new BufferedReader(new InputStreamReader(fileInputStream));
                        String readData = reader.readLine();
                        assertEquals("Streaming test file", readData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail();
                    } finally {

                        try {
                            if (reader != null)
                                reader.close();
                            if (fileInputStream != null)
                                fileInputStream.close();
                        } catch (IOException e) {

                            logger.error("Error in closing resources.");
                        }
                    }
                    isStreamSuccess = true;
                }
            });

            bezirk.subscribe(streams);
        }
    }
}
