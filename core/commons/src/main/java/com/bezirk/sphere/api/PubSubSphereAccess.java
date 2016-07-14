/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Rishabh Gulati
 */
public interface PubSubSphereAccess {

    /**
     * Encrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     *
     * @param sphereId          sphereId of the sphere for which serializedContent needs to be
     *                          encrypted
     * @param serializedContent content to be encrypted
     * @return encrypted byte array if 1. <code>sphereId</code> is not <code>null</code> and has a
     * sphereKey associated with it 2. <code>serializedContent</code> is not <code>null</code>,
     * <code>null</code> otherwise
     */
    public byte[] encryptSphereContent(String sphereId, String serializedContent);

    /**
     * Decrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     *
     * @param sphereId          sphereId of the sphere for which serializedContent needs to be
     *                          decrypted
     * @param serializedContent content to be decrypted
     * @return Decrypted serialized content String if 1. <code>sphereId</code> is not
     * <code>null</code> and has a sphereKey associated with it 2. <code>serializedContent</code>
     * is not <code>null</code>, <code>null</code> otherwise
     */
    public String decryptSphereContent(String sphereId, byte[] serializedContent);

    /**
     * Encrypts a stream into another stream. This method does NOT flush or close either stream
     * prior to returning - the caller must do so when they are finished with the streams. For example:
     * <pre>
     *     {@code try {
     *         InputStream in = ...
     *         OutputStream out = ...
     *         bezirkSphere.encryptSphereContent(in, out, sphereId);
     *     } finally {
     *         if (in != null) {
     *             try {
     *                 in.close();
     *             } catch (IOException ioe1) { ... logger, trigger event, etc }
     *         }
     *         if (out != null) {
     *             try {
     *                 out.close();
     *             } catch (IOException ioe2) { ... logger, trigger event, etc }
     *         }
     *    }}
     * </pre>
     *
     * @param in       Input stream for incoming un-encrypted information
     * @param out      Output stream for outgoing encrypted information
     * @param sphereId sphereId of the sphere for which input stream needs to be
     *                 encrypted
     */
    public void encryptSphereContent(InputStream in, OutputStream out, String sphereId);

    /**
     * Decrypts a stream into another stream
     *
     * @param in       Input stream for incoming encrypted information
     * @param out      Output stream for outgoing decrypted information
     * @param sphereId sphereId of the sphere for which input stream needs to be
     *                 decrypted
     *                 <pre>
     *                 NOTE: This method does NOT flush or close either stream prior to returning - the caller must do so when they are finished with the streams. For example:
     *                 {@code try {
     *                       InputStream in = ...
     *                       OutputStream out = ...
     *                       bezirkSphere.decryptSphereContent(in, out, sphereId);
     *                   } finally {
     *                       if (in != null) {
     *                           try {
     *                               in.close();
     *                           } catch (IOException ioe1) { ... logger, trigger event, etc }
     *                       }
     *                       if (out != null) {
     *                           try {
     *                               out.close();
     *                           } catch (IOException ioe2) { ... logger, trigger event, etc }
     *                       }
     *                   }}
     *                            </pre>
     */
    public void decryptSphereContent(InputStream in, OutputStream out, String sphereId);

    /**
     * Provides iterable collection of sphereIds associated with passed
     * ZirkId
     *
     * @param zirkId ZirkId for retrieving stored membership information
     * @return iterable Collection of sphereIds for the passed ZirkId, <code>null</code> in case
     * the <code>zirkId</code> passed is <code>null</code> or not registered
     */
    public Iterable<String> getSphereMembership(ZirkId zirkId);

    // TODO add to wiki : found while refactoring to the new API

    /**
     * Checks if the zirk is a part of the sphere
     *
     * @param service  ZirkId for finding existence in a sphere
     * @param sphereId sphere to be tested
     * @return true if the zirk exist in the sphere false otherwise
     */
    public boolean isZirkInSphere(ZirkId service, String sphereId);

    /**
     * Gets the zirk name of the passed ZirkId
     *
     * @param serviceId ZirkId for retrieving the zirk name
     * @return Zirk name if the zirk id is valid and not null null
     * otherwise
     */
    public String getZirkName(ZirkId serviceId);

    /**
     * This method handles processing the sphere related discovery request
     *
     * @param discoveryRequest
     */
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest);

    /**
     * @param deviceId the deviceId whose Device Name needs to be known
     * @return Device Name if exists, null otherwise
     */
    public String getDeviceNameFromSphere(String deviceId);

}