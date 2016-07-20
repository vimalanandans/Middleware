package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Bezirk bezirk = com.bezirk.middleware.proxy.Factory.registerZirk("XXX");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * The zirk that discovers and streams the file
     */
    private final class StreamLocalMockServiceA implements BezirkListener {
        private final String zirkName = "StreamLocalMockZirkA";
        private Bezirk bezirk = null;
        private StreamLocalDummyProtocolRole pRole;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            pRole = new StreamLocalDummyProtocolRole();
            bezirk.subscribe(pRole, this);

        }

        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }
    }

    /**
     * ProtocolRole used by MockServiceA.
     */
    private final class StreamLocalDummyProtocolRole extends ProtocolRole {
        private final String[] streams = {"DummyStream"};

        @Override
        public String getRoleName() {
            return StreamLocalDummyProtocolRole.class.getSimpleName();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return null;
        }

        @Override
        public String[] getStreamTopics() {
            return streams;
        }
    }

    /**
     * ProtocolRole used by MockServiceB
     */
    private final class StreamLocalMockServiceProtocolRole extends ProtocolRole {

        private final String[] streams = {"MockRequestStreamDescriptor"};

        @Override
        public String getRoleName() {
            return StreamLocalMockServiceProtocolRole.class.getSimpleName();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return null;
        }

        @Override
        public String[] getStreamTopics() {
            return streams;
        }

    }

    /**
     * StreamDescriptor Descriptor
     */
    private final class StreamLocalMockRequestStreamDescriptor extends StreamDescriptor {

        private StreamLocalMockRequestStreamDescriptor() {
            super(false, true);
        }


    }

    /**
     * Zirk that is consumer of StreamDescriptor
     */
    private final class StreamLocalMockServiceB implements BezirkListener {
        private final String zirkName = "StreamLocalMockServiceB";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            bezirk.subscribe(new StreamLocalMockServiceProtocolRole(), this);
        }

        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {

        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {

        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {
            logger.info("****** RECEIVED STREAM REQUEST ******");
            assertNotNull(topic);
            assertNotNull(streamDescriptor);
            assertNotNull(file);
            assertNotNull(sender);

            logger.info("topic-> " + topic);
            logger.info("streamDescriptor-> " + streamDescriptor);
            logger.info("streamId-> " + streamId);
            logger.info("filePath-> " + file);
            logger.info("sender-> " + sender);

            assertEquals("MockRequestStreamDescriptor", topic);
            assertEquals(sendFile, file);
            assertEquals(sendStreamId, streamId);
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

        @Override
        public void streamStatus(short streamId, StreamStates status) {
            assertEquals(sendStreamId, streamId);
            assertEquals(StreamStates.END_OF_DATA, status);
            logger.info("**** STREAM STATUS SUCCESSFUL FOR END_OF_DATA");
        }

    }
}
