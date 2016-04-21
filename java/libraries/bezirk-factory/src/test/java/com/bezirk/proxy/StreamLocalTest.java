package com.bezirk.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.UnicastStream;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * @author VBD4KOR
 *         This testcase is used to test the Streaming locally. Two MockServices are used for testing.
 *         MockService-B registers and subscribes for a Stream.
 *         MockService-A registers and subscribes for dummy Stream. MS-A discovers the services and streams the file to the MS-B.
 *         MS-B receives the file.
 */
public class StreamLocalTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamLocalTest.class);
    private static boolean isStreamSuccess = false;
    private short sendStreamId = -1;
    private String sendfilePath = null;
    private StreamLocalMockRequestStream request = null;
    private StreamLocalMockServiceA mockA;
    private StreamLocalMockServiceB mockB;

    @BeforeClass
    public static void setup() {
        logger.info(" ****************** Setting up Stream Local Testcase *******************");

    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** Shutting down Stream Local Testcase ****************************");

    }

    @Before
    public void setUpServices() {
        StreamLocalTest streamLocalTest = new StreamLocalTest();
        mockB = streamLocalTest.new StreamLocalMockServiceB();
        mockB.setupMockService();

        mockA = streamLocalTest.new StreamLocalMockServiceA();
        mockA.setupMockService();
    }

    // TODO: This test fails sporadically
    //@Test(timeout=60000)
    public void testForLocalStreaming() {


        mockA.discoverMockService();
        while (!isStreamSuccess) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void destroySetUp() {
        Bezirk uhu = Factory.getInstance();
        uhu.unregisterService(mockA.myId);
        uhu.unregisterService(mockB.myId);
    }

    /**
     * The service that discovers and streams the file
     */
    private final class StreamLocalMockServiceA implements BezirkListener {
        private final String serviceName = "StreamLocalMockServiceA";
        private Bezirk uhu = null;
        private ServiceId myId = null;
        private StreamLocalDummyProtocolRole pRole;

        /**
         * Setup the service
         */
        private final void setupMockService() {
            uhu = Factory.getInstance();
            myId = uhu.registerService(serviceName);
            logger.info("StreamLocalMockServiceA - regId : " + ((UhuServiceId) myId).getUhuServiceId());
            pRole = new StreamLocalDummyProtocolRole();
            uhu.subscribe(myId, pRole, this);

        }

        /**
         * Discover the services to stream unicastly
         */
        private final void discoverMockService() {
            StreamLocalMockServiceProtocolRole pRole = new StreamLocalMockServiceProtocolRole();
            uhu.discover(myId, null, pRole, 10000, 1, this);

        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamConditions status) {
        }

        @Override
        public void pipeStatus(Pipe p, PipeConditions status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
            logger.info("Received Discovery Response");
            if (serviceSet == null) {
                fail("Service Set of Discovered Services in Null");
                return;
            }
            if (serviceSet.isEmpty()) {
                fail("Service Set is Empty");
                return;
            }

            assertEquals(1, serviceSet.size());
            UhuDiscoveredService dService = null;

            Iterator<DiscoveredService> iterator = serviceSet.iterator();
            dService = (UhuDiscoveredService) iterator.next();
            logger.info("DiscoveredServiceName : " + dService.name + "\n" +
                    "Discovered Role : " + dService.pRole + "\n" +
                    "Discovered SEP" + dService.service + "\n");

            request = new StreamLocalMockRequestStream(Message.Flag.REQUEST, "MockRequestStream", dService.service);

            sendfilePath = StreamLocalTest.class.getClassLoader().getResource("streamingTestFile.txt").getPath();
            sendStreamId = uhu.sendStream(myId, dService.service, request, sendfilePath);
        }

        @Override
        public void pipeGranted(Pipe p, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }

    }

    /**
     * ProtocolRole used by MockServiceA.
     */
    private final class StreamLocalDummyProtocolRole extends ProtocolRole {
        private final String[] streams = {"DummyStream"};

        @Override
        public String getProtocolName() {
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

        private final String[] streams = {"MockRequestStream"};

        @Override
        public String getProtocolName() {
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
     * Stream Descriptor
     */
    private final class StreamLocalMockRequestStream extends UnicastStream {

        private StreamLocalMockRequestStream(Flag flag, String topic,
                                             ServiceEndPoint recipient) {
            super(flag, topic, recipient);
        }


    }

    /**
     * Service that is consumer of Stream
     */
    private final class StreamLocalMockServiceB implements BezirkListener {
        private final String serviceName = "StreamLocalMockServiceB";
        private Bezirk uhu = null;
        private ServiceId myId = null;

        /**
         * Setup the service
         */
        private final void setupMockService() {
            uhu = Factory.getInstance();
            myId = uhu.registerService(serviceName);
            logger.info("StreamLocalMockServiceB - regId : " + ((UhuServiceId) myId).getUhuServiceId());
            uhu.subscribe(myId, new StreamLocalMockServiceProtocolRole(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {

        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender) {

        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender) {
            logger.info("****** RECEIVED STREAM REQUEST ******");
            assertNotNull(topic);
            assertNotNull(stream);
            assertNotNull(filePath);
            assertNotNull(sender);

            logger.info("topic-> " + topic);
            logger.info("stream-> " + stream);
            logger.info("streamId-> " + streamId);
            logger.info("filePath-> " + filePath);
            logger.info("sender-> " + sender);

            assertEquals("MockRequestStream", topic);
            assertEquals(sendfilePath, filePath);
            assertEquals(sendStreamId, streamId);
            assertEquals(request.serialize(), stream);
            // Read and verify the stream
            FileInputStream fileInputStream = null;
            BufferedReader reader = null;
            try {
                fileInputStream = new FileInputStream(filePath);
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
        public void streamStatus(short streamId, StreamConditions status) {
            assertEquals(sendStreamId, streamId);
            assertEquals(StreamConditions.END_OF_DATA, status);
            logger.info("**** STREAM STATUS SUCCESSFUL FOR END_OF_DATA");
        }


        @Override
        public void pipeStatus(Pipe p, PipeConditions status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
        }

        @Override
        public void pipeGranted(Pipe p, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }


    }
}
