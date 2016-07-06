package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.SphereVitals;
import com.bezirk.sphere.security.SphereKeys;

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
    public boolean generateKeys(String sphereId);

    /**
     * create secret key basedon passcode
     */
    public byte[] generateKey(String code);

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
    public boolean generateKeys(String sphereId, boolean fromSphereId);

    public SphereVitals getSphereVitals(String sphereId);

    public void addMemberKeys(String sphereId, SphereKeys sphereKeys);
}
