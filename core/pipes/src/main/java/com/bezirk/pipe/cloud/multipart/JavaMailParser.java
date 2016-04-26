package com.bezirk.pipe.cloud.multipart;

import com.bezirk.control.messages.pipes.CloudStreamResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;

public class JavaMailParser implements MultiPartParser {

    protected final Logger log = LoggerFactory.getLogger(JavaMailParser.class);

    /**
     * This looks easy, but it was hard to find the right way to do this!
     * See: http://stackoverflow.com/questions/15078347/handling-multipart-response-from-jersey-server-in-android-client
     *
     * @param headerMap
     * @param inStream
     * @return
     * @throws Exception
     */
    public CloudStreamResponse parse(Map<String, List<String>> headerMap, InputStream inStream) throws Exception {

		/* 
         * Create multipart object from the whole inputStream received from HTTP operation.
		 * 
		 * Implementation Note: There is a PERFORMANCE implication of using JavaMail's MimeMultipart
		 * object to parse the server response.  It is causing the inputstream to be read entirely 
		 * before passing it on to the receiving UhU zirk. This is true even if we use the
		 * IncrementalDataSource implementation that does not read the inputstream into a
		 * buffer immediately.  Both DataSources have the same performance for a given content
		 * file.
		 */

        log.info("Creating javax.activation.DataSource to hold multiparts");
        //ByteArrayDataSource dataSource = new ByteArrayDataSource(inStream, "mulitpart/mixed");
        IncrementalDataSource dataSource = new IncrementalDataSource(inStream, "mulitpart/mixed");
        log.info("Creating Multipart object from dataSource: " + dataSource.getName());
        MimeMultipart multipart = new MimeMultipart(dataSource);
		
		/*
		 * Parse and validate stream descriptor part
		 */

        log.info("Getting 1st body part: stream descriptor");
        BodyPart descriptorPart = multipart.getBodyPart(0);
        if (!descriptorPart.getContentType().contains("application/json")) {
            throw new Exception("Stream descriptor Part did not have expected type of application/json");
        }
        String descriptorContentId = descriptorPart.getHeader("Content-ID")[0];
        if (!descriptorContentId.contains("stream")) {
            throw new Exception("Stream descriptor Part did not have expected Content-ID of <stream>: " + descriptorContentId);
        }
        log.info("FOUND part: " + descriptorContentId);
		
		/*
		 * Parse and validate content part
		 */

        log.info("Getting 2nd body part: content stream");
        BodyPart contentPart = multipart.getBodyPart(1);
        if (!contentPart.getContentType().contains("application/octet-stream")) {
            throw new Exception("Part did not have expected type of application/octet-stream");
        }
        String contentContentId = contentPart.getHeader("Content-ID")[0];
        if (!contentContentId.contains("content")) {
            throw new Exception("Content Part did not have expected Content-ID of <content>: " + contentContentId);
        }
        log.info("FOUND part: " + contentContentId);
		
		/*
		 * Create and return a helper object to hold both parts
		 */

        CloudStreamResponse response = new CloudStreamResponse();
        log.info("Extracting streamDescriptor from multipart");
        response.setSerializedEvent(com.bezirk.pipe.cloud.StreamUtils.getStringFromInputStream(descriptorPart.getInputStream()));
        log.info("Extracting content inputStream from multipart");
        response.setStreamContent(contentPart.getInputStream());

        return response;
    }
}	



