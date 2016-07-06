package com.bezirk.test.pipes;

import com.bezirk.control.messages.pipes.CloudResponse;
import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.pipe.cloud.CloudPipeClient;
import com.bezirk.pipe.cloud.CloudPipeClientImpl;
import com.bezirk.proxy.api.impl.BezirkServiceEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.registration.ServiceRegistration;
import com.bezirk.samples.protocols.EchoReply;
import com.bezirk.samples.protocols.EchoRequest;
import com.bezirk.samples.protocols.FileReply;
import com.bezirk.samples.protocols.FileRequest;
import com.bezirk.test.util.TestUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CloudPipeClientTest {

    private static final String TEMP_FILE_PREFIX = "testFile";
    private static final String TEMP_FILE_SUFFIX = ".txt";
    private static final Logger logger = LoggerFactory.getLogger(CloudPipeClientTest.class);
    private CloudPipeClient client;
    private EchoRequest echoRequest;
    private String tempFileName = TEMP_FILE_PREFIX + TEMP_FILE_SUFFIX;

    @BeforeClass
    public static void setUpTestSuite() throws Exception {
        if (!PipesTestSuite.isWebServerRunning()) {
            PipesTestSuite.setUpTestSuite();
        }
    }

    @Before
    public void beforeEachTest() throws Exception {
        URI uri = new URI(TestUtils.URL_BEZIRKCLOUD_LOCALHOST);
        client = new CloudPipeClientImpl(uri.toURL());

        // The request event to send
        echoRequest = new EchoRequest();
        echoRequest.setText("Hi mom");
    }

    @Test
    public void testSendEvent() throws Exception {
        // Make sure we created a correct request object
        assertNotNull(echoRequest);
        assertFalse(echoRequest.getText().isEmpty());

        // Send event and receive reply
        CloudResponse response = client.sendEvent(generateTestHeader(), echoRequest.serialize());
        String serializedResponse = response.getSerializedEvent();
        assertNotNull(serializedResponse);
        assertFalse(serializedResponse.trim().equals(""));

        validateHeaders(response);

        // Validate reply
        EchoReply echoReply = EchoReply.deserialize(serializedResponse);
        assertNotNull(echoReply);
        logger.info("Received reply... ");
        System.out.println(TestUtils.prettyPrintJson(serializedResponse));

        // Assert that the response wan an echo of the text we originally sent
        String replyMsg = echoReply.getText();
        assertTrue(replyMsg.contains(echoRequest.getText()));
        logger.info("Received echo: <" + echoReply.getText() + ">");
    }

    @Test
    public void testRetrieveContent() throws Exception {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setFileName(tempFileName);

        logger.info("retrieving content with request: ");
        System.out.println(TestUtils.prettyPrintJson(fileRequest.serialize()));

        CloudStreamResponse response = client.retrieveContent(generateTestHeader(), fileRequest.serialize());

        validateHeaders(response);

        String serializedStreamDesc = response.getStreamDescriptor();
        assertNotNull(serializedStreamDesc);
        logger.info("stream: ");
        System.out.println(TestUtils.prettyPrintJson(serializedStreamDesc));

        // Ensure that the streamDescriptor json string can be de-serialized as the expected Stream object
        FileReply fileReply = Stream.deserialize(serializedStreamDesc, FileReply.class);
        assertNotNull(fileReply);

        // Get content as input stream
        InputStream content = response.getStreamContent();
        assertNotNull(content);

        // Write content to a file and validate that the file exists
        logger.info("Writing content to temporary file");
        File outputFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        FileOutputStream fileOutStream = new FileOutputStream(outputFile);
        TestUtils.inputStreamToOutputStream(content, fileOutStream, 0);
        assertTrue(outputFile.exists());
        assertTrue(outputFile.isFile());
        logger.info("Temp file created: " + outputFile);
    }

	/*
	 *  Helper methods
	 */

    /**
     * Creates a bezirk multicast header
     *
     * @return
     */
    protected PipeMulticastHeader generateTestHeader() {
        // Set header for routing purposes
        BezirkZirkId serviceId = new BezirkZirkId(ServiceRegistration.generateUniqueServiceID());
        BezirkServiceEndPoint senderEndpoint = new BezirkServiceEndPoint("EchoSenderService", serviceId);
        PipeMulticastHeader pipeMulticastHeader = new PipeMulticastHeader();
        pipeMulticastHeader.setTopic(echoRequest.topic);
        pipeMulticastHeader.setSenderSEP(senderEndpoint);

        return pipeMulticastHeader;
    }

    protected void validateHeaders(CloudResponse response) throws Exception {
        // validate http header
        Map<String, List<String>> httpHeader = response.getHttpHeader();
        assertNotNull(httpHeader);
        logger.info("dumping header");
        for (String key : httpHeader.keySet()) {
            logger.info(key + " : " + httpHeader.get(key));
        }
        logger.info("end header");

        // Validate pipe header
        PipeHeader pipeHeader = response.getPipeHeader();
        assertNotNull(pipeHeader);
        if (pipeHeader instanceof PipeMulticastHeader) {
            logger.info("RECEIVED Multicast header");
        } else if (pipeHeader instanceof PipeUnicastHeader) {
            throw new Exception("Can't handle unicast header yet");
        } else {
            throw new Exception("Unknown header type: " + pipeHeader.getClass().getSimpleName());
        }
    }

}