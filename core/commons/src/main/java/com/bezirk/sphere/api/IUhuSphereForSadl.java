/**
 *
 */
package com.bezirk.sphere.api;

import java.io.InputStream;
import java.io.OutputStream;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author Rishabh Gulati
 * 
 */
public interface IUhuSphereForSadl {

    /**
     * Encrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     * 
     * @param sphereId
     *            sphereId of the sphere for which serializedContent needs to be
     *            encrypted
     * @param serializedContent
     *            content to be encrypted
     * @return encrypted byte array if 1. sphereId is not null & has a sphereKey
     *         associated with it 2. serializedContent is not null
     * 
     *         null otherwise
     */
    public byte[] encryptSphereContent(String sphereId, String serializedContent);

    /**
     * Decrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     * 
     * @param sphereId
     *            sphereId of the sphere for which serializedContent needs to be
     *            decrypted
     * @param serializedContent
     *            content to be decrypted
     * @return Decrypted serialized content String if 1. sphereId is not null &
     *         has a sphereKey associated with it 2. serializedContent is not
     *         null
     * 
     *         null otherwise
     */
    public String decryptSphereContent(String sphereId, byte[] serializedContent);

    /**
     * Encrypts a stream into another stream
     * 
     * @param in
     *            Input stream for incoming un-encrypted information
     * @param out
     *            Output stream for outgoing encrypted information
     * @param sphereId
     *            sphereId of the sphere for which input stream needs to be
     *            encrypted
     * 
     *            <pre>
     * NOTE: This method does NOT flush or close either stream prior to returning - the caller must do so when they are finished with the streams. For example:
     * {@code try {
     *       InputStream in = ...
     *       OutputStream out = ...
     *       uhuSphere.encryptSphereContent(in, out, sphereId);
     *   } finally {
     *       if (in != null) {
     *           try {
     *               in.close();
     *           } catch (IOException ioe1) { ... log, trigger event, etc }
     *       }
     *       if (out != null) {
     *           try {
     *               out.close();
     *           } catch (IOException ioe2) { ... log, trigger event, etc }
     *       }
     *   }}
     *            </pre>
     */
    public void encryptSphereContent(InputStream in, OutputStream out, String sphereId);

    /**
     * Decrypts a stream into another stream
     * 
     * @param in
     *            Input stream for incoming encrypted information
     * @param out
     *            Output stream for outgoing decrypted information
     * @param sphereId
     *            sphereId of the sphere for which input stream needs to be
     *            decrypted
     * 
     *            <pre>
     * NOTE: This method does NOT flush or close either stream prior to returning - the caller must do so when they are finished with the streams. For example:	
     * {@code try {
     *       InputStream in = ...
     *       OutputStream out = ...
     *       uhuSphere.decryptSphereContent(in, out, sphereId);
     *   } finally {
     *       if (in != null) {
     *           try {
     *               in.close();
     *           } catch (IOException ioe1) { ... log, trigger event, etc }
     *       }
     *       if (out != null) {
     *           try {
     *               out.close();
     *           } catch (IOException ioe2) { ... log, trigger event, etc }
     *       }
     *   }}
     *            </pre>
     */
    public void decryptSphereContent(InputStream in, OutputStream out, String sphereId);

    /**
     * Provides iterable collection of sphereIds associated with passed
     * UhuServiceId
     * 
     * @param serviceId
     *            UhuServiceId for retrieving stored membership information
     * @return Iterable Collection of sphereIds for the passed ServiceId
     * 
     *         null in case the serviceId passed is null or not registered
     */
    public Iterable<String> getSphereMembership(UhuServiceId serviceId);

    // TODO add to wiki : found while refactoring to the new API
    /**
     * Checks if the service is a part of the sphere
     * 
     * @param service
     *            UhuServiceId for finding existence in a sphere
     * @param sphereId
     *            Sphere to be tested
     * @return true if the service exist in the sphere false otherwise
     */
    public boolean isServiceInSphere(UhuServiceId service, String sphereId);

    /**
     * Gets the service name of the passed UhuServiceId
     * 
     * @param serviceId
     *            UhuServiceId for retrieving the service name
     * @return Service name if the service id is valid and not null null
     *         otherwise
     */
    public String getServiceName(UhuServiceId serviceId);

    /**
     * This method handles processing the sphere related discovery request
     * 
     * @param discoveryRequest
     */
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest);

    /**
     * @author vijet
     * @date 25-2-2015 Returns the Device Name associated with the DeviceId
     * @param deviceId
     *            the deviceId whose Device Name needs to be known
     * @return Device Name if exists, null otherwise
     */
    public String getDeviceNameFromSphere(String deviceId);

}