package com.bezirk.middleware.core.sphere.api;

import com.bezirk.middleware.objects.SphereVitals;
import com.bezirk.middleware.core.sphere.security.SphereKeys;

/**
 * @author Rishabh Gulati
 */
public interface CryptoInternals {

    /**
     * This method generates the keys for a sphere if the passed sphereId is not
     * already present
     *
     * @param sphereId sphereId for which keys need to be generated
     * @return <code>true</code> if the keys were generated and stored successfully
     */
    boolean generateKeys(String sphereId);

    /**
     * create secret key based on passcode
     */
    byte[] generateKey(String code);

    /**
     * Provides the key details which can be used for sharing a sphere with
     * other devices Eq. using QR code
     *
     * @return
     */

    /**
     * This method generates the keys for a sphere if the passed <code>sphereId</code> is not
     * already present. For the symmetric key the code is generated form short id of sphere id
     *
     * @param sphereId sphereId for which keys need to be generated
     * @return <code>true</code> if the keys were generated and stored successfully
     */
    boolean generateKeys(String sphereId, boolean fromSphereId);

    SphereVitals getSphereVitals(String sphereId);

    void addMemberKeys(String sphereId, SphereKeys sphereKeys);
}
