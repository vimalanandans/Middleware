package com.bezirk.pipe.cloud;

import com.bezirk.control.messages.pipes.CloudResponse;
import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class CloudPipeClientImpl implements CloudPipeClient {

    // TODO: should these constants live in the CloudPipe library class?
    public static final String EVENT_PATH = "/cloudpipe/sendevent";
    public static final String CONTENT_PATH = "/cloudpipe/getcontent";

	/*
	 * Constants that represent REST service paths relative to baseURL, which is 
	 * the root webserver service path
	 */
    public static final String CONTENT_MULTIPART_PATH = "/cloudpipe/content";
    public static final String CERT_FILENAME_DEFAULT = "upa.crt";
    public static final String KEY_CONTENT_TYPE = "Content-Type";
    public static final String KEY_UHU_HEADER = "Uhu-Header";
	
	/*
	 * Constants related to expected HTTP header keys/values
	 */
    public static final String VAL_CONTENT_TYPE_APP_JSON = "application/json";
    public static final String VAL_CONTENT_TYPE_MULTIPART_MIXED = "multipart/mixed";
    /**
     * The root web server path that hosts the Uhu cloudpipe service , i.e.,
     * something like: http://some-host:some-port/services/uhu
     */
    protected URL baseURL = null;
    protected Logger log = LoggerFactory.getLogger(CloudPipeClientImpl.class);
    protected URL eventURL = null;
    protected URL contentURL = null;
    protected URL contentMultipartURL = null;

    protected com.bezirk.pipe.cloud.multipart.MultiPartParser multiPartParser = new com.bezirk.pipe.cloud.multipart.IncrementalParser();

    protected SSLContext sslContext = null;
    protected SelfSignedContextBuilder ssCertBuilder = new SelfSignedContextBuilder();
    protected String certFileName = CERT_FILENAME_DEFAULT;

    public CloudPipeClientImpl(URL baseURL, String certFileName) {
        this.baseURL = baseURL;
        this.certFileName = certFileName;

        try {
            initUrls();
            log.info("configuring SSL Context");
            ssCertBuilder.setCertFileName(certFileName);
            sslContext = ssCertBuilder.build();
        } catch (MalformedURLException e) {
            log.error("URL not valid: ", e);
        } catch (Exception e) {
            log.error("SSL could not be configured: ", e);

        }
    }

    private void initUrls() throws MalformedURLException {
        this.eventURL = new URL(baseURL.toString() + EVENT_PATH);
        this.contentURL = new URL(baseURL.toString() + CONTENT_PATH);
        this.contentMultipartURL = new URL(baseURL.toString() + CONTENT_MULTIPART_PATH);

        if (!isHttpsUrl(baseURL)) {
            throw new MalformedURLException("Base url is not https (" + baseURL + ")");
        }
    }

    private boolean isHttpsUrl(URL... urls) {
        for (URL url : urls) {
            if (!url.getProtocol().equals("https")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CloudResponse sendEvent(PipeHeader pipeHeader, String serializedEvent) {
        if (eventURL == null) {
            log.error("Cannot sendEvent. eventURL is null");
            return null;
        }
        if (pipeHeader == null) {
            log.error("Cannot sendEvent. pipeHeader is null");
            return null;
        }
        if (serializedEvent == null) {
            log.error("Cannot sendEvent. serializedEvent is null");
            return null;
        }

        HttpsURLConnection conn = null;
        CloudResponse response = null;

        try {
            conn = (HttpsURLConnection) eventURL.openConnection();
            response = postJson(conn, pipeHeader, serializedEvent);
        } catch (Exception e) {
            log.error("problem opening http connection", e);
        } finally {
            conn.disconnect();
        }

        return response;
    }

    @Override
    public CloudStreamResponse retrieveContent(PipeHeader pipeHeader, String serializedEvent) {
        CloudStreamResponse response = new CloudStreamResponse();

        if (contentMultipartURL == null) {
            log.error("Cannot retrieveContent(). contentMultipartURL is null");
            return null;
        }
        if (pipeHeader == null) {
            log.error("Cannot sendEvent. pipeHeader is null");
            return null;
        }
        if (serializedEvent == null) {
            log.error("Cannot sendEvent. serializedEvent is null");
            return null;
        }

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) contentMultipartURL.openConnection();
            response = (CloudStreamResponse) postJson(conn, pipeHeader, serializedEvent);
        } catch (Exception e) {
            log.error("problem opening http connection: ", e);

        }
        // TODO: how to disconnect this connection object, since it should live after this method returns?
		/*
		finally {
			conn.disconnect();
		}
		*/

        return response;
    }

    /**
     * General helper method to post a json uhu event -- can receive multipart/mixed as well as single part responses
     *
     * @param body
     * @return
     */
    protected CloudResponse postJson(HttpsURLConnection conn, PipeHeader pipeHeader, String body) throws Exception {
        // Enable use of our self-signed cert if we found one
        if (sslContext == null) {
            throw new Exception("SSL Context was not configured");
        }
        conn.setHostnameVerifier(new NullHostNameVerifier());
        conn.setSSLSocketFactory(sslContext.getSocketFactory());

        // Create a POST request
        log.debug("about to post event: " + body);
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0); // setting this to 0 gives us the system default  request buffer size
        conn.setRequestMethod("POST");
        conn.setRequestProperty(KEY_CONTENT_TYPE, VAL_CONTENT_TYPE_APP_JSON);
        conn.setRequestProperty(PipeHeader.KEY_UHU_HEADER, pipeHeader.serialize());

        // Send the request
        PrintWriter writer = new PrintWriter(conn.getOutputStream());
        log.info("sending event: " + body);
        writer.print(body);
        writer.flush();

        // Get the response
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            conn.disconnect();
            throw new Exception("POST failed with code: " + responseCode);
        }

        // Put entire response header into a Map
        Map<String, List<String>> httpHeader = conn.getHeaderFields();

        // Dump header for debugging
        if (log.isInfoEnabled()) {
            //if (logger.isDebugEnabled()) {
            log.info("Dumping header ...");
            System.out.println("*** BEGIN header ***");
            for (String key : httpHeader.keySet()) {
                System.out.println("  " + key + " : " + httpHeader.get(key) + " (list items: " + httpHeader.get(key).size() + ")");
            }
            System.out.println("*** END header ***");
        }

        // Extract selected HTTP header keys/values that we need
        List<String> contentTypeList = httpHeader.get(KEY_CONTENT_TYPE);
        if (contentTypeList == null) {
            throw new Exception("Couldn't find value for header: " + KEY_CONTENT_TYPE);
        }
        String contentType = contentTypeList.get(0);
        InputStream inStream = conn.getInputStream();
        log.info("Received a response with Content-Type: " + contentType);

        // Return this response object
        CloudResponse response;

        // Multipart response containing a stream descriptor + stream content
        if (contentType.contains(VAL_CONTENT_TYPE_MULTIPART_MIXED)) {
            log.info("Parsing multiparts");
            response = multiPartParser.parse(httpHeader, inStream);
        } else if (contentType.contains(VAL_CONTENT_TYPE_APP_JSON)) {
            // Single part response containing a regular uhu event
            final String serializedReply = StreamUtils.getStringFromInputStream(inStream);
            response = new CloudResponse();
            response.setSerializedEvent(serializedReply);
            response.setHttpHeader(httpHeader);
            // For event responses, the uhu header is returned in the httpHeader
            response.setPipeHeader(extractPipeHeaderFromHttpHeader(httpHeader));
        } else {
            String err = "Unexpected content-type: " + contentType;
            log.error(err);
            throw new Exception(err);
        }

        return response;
    }

    private PipeHeader extractPipeHeaderFromHttpHeader(Map<String, List<String>> httpHeader) throws Exception {
        // Pull the uhu PipeHeader out of the response header, because t
        List<String> headerValue = httpHeader.get(KEY_UHU_HEADER);
        if (headerValue == null) {
            throw new Exception("Uhu header was not returned in http header in the response to sendEvent()");
        }
        String seralizedUhuHeader = headerValue.get(0);
        PipeMulticastHeader multicastHeader = PipeMulticastHeader.deserialize(seralizedUhuHeader, PipeMulticastHeader.class);
        if (multicastHeader == null) {
            throw new Exception("Uhu pipe header could not be deserialized: " + multicastHeader);
        }

        return multicastHeader;
    }
}
