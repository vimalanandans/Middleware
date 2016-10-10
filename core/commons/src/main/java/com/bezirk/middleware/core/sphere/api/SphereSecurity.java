/**
 *
 */
package com.bezirk.middleware.core.sphere.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Rishabh Gulati
 */
public interface SphereSecurity {


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
    byte[] encryptSphereContent(String sphereId, String serializedContent);

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
    String decryptSphereContent(String sphereId, byte[] serializedContent);

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
    void encryptSphereContent(InputStream in, OutputStream out, String sphereId);

    /**
     * Decrypts a stream into another stream
     *
     * @param in       Input stream for incoming encrypted information
     * @param out      Output stream for outgoing decrypted information
     * @param sphereId sphereId of the sphere for which input stream needs to be
     *                 decrypted
     *                 <pre>
     *                 NOTE: This method does NOT flush or close either stream prior to returning -
     *                 the caller must do so when they are finished with the streams. For example:
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
    void decryptSphereContent(InputStream in, OutputStream out, String sphereId);
}
