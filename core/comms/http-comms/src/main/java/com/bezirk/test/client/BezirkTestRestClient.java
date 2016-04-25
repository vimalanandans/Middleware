package com.bezirk.test.client;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * This class will call the rest controller to test for a service response.
 *
 * @author PIK6KOR
 */
public class BezirkTestRestClient {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) {

        BezirkTestRestClient httpClient = new BezirkTestRestClient();
        httpClient.sendPost();
    }


    /**
     * send a post data to Party Application..
     */
    private void sendPost() {

        try {
            String url = "http://192.168.1.3:8080/bezirk/service";

			
			/*
			 * 
			 */
            String xmlString = "</xml>";
            HttpPost httpRequest = new HttpPost(url);
            httpRequest.setHeader("Content-Type", "application/xml");
            StringEntity xmlEntity = new StringEntity(xmlString);
            httpRequest.setEntity(xmlEntity);


            // ******************
			
			/*
			HttpResponse httpresponse = httpClient.execute(httpRequest);
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			// add header
			httpPost.setHeader("User-Agent", USER_AGENT);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("BEZIRK_EVENT_MSG", "{\"flag\":\"REQUEST\",\"topic\":\"HostIdentityEvent\"}"));
			urlParameters.add(new BasicNameValuePair("BEZIRK_EVENT_TOPIC", "HostIdentityEvent"));
			urlParameters.add(new BasicNameValuePair("BEZIRK_EXPECTED_RESPONSE_TYPE", "HostEvent"));
			
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(httpPost);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + httpPost.getEntity());
			System.out.println("Response Code : " + 
					response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.toString());*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
