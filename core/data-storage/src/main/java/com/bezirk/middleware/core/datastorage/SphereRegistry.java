/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com) & Rishabh
 */
package com.bezirk.middleware.core.datastorage;

import com.bezirk.middleware.core.sphere.impl.DeviceInformation;
import com.bezirk.middleware.core.sphere.impl.Zirk;
import com.bezirk.middleware.core.sphere.impl.Sphere;
import com.bezirk.middleware.core.sphere.security.SphereKeys;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * sphere Registry that holds all the sphere Maps
 */
public class SphereRegistry implements Serializable {
    private static final long serialVersionUID = 682210165778262550L;
    public LinkedHashMap<String, Sphere> spheres;
    public LinkedHashMap<String, Zirk> sphereMembership;
    public LinkedHashMap<String, DeviceInformation> devices;
    public HashMap<String, SphereKeys> sphereKeyMap;
    public HashMap<String, HashKey> sphereHashKeyMap;

    public SphereRegistry() {
        super();
        this.spheres = new LinkedHashMap<>();
        this.sphereMembership = new LinkedHashMap<>();
        this.devices = new LinkedHashMap<>();
        this.sphereKeyMap = new HashMap<>();
        this.sphereHashKeyMap = new HashMap<>();
    }

    /**
     * put sphere keys
     */
    public boolean isKeymapExist(String sphereId) {
        boolean exists = false;
        if (sphereKeyMap.containsKey(sphereId)) {
            exists = true;
        } else if (sphereHashKeyMap.containsKey(sphereId)) { // else check the
            // secondary map
            exists = true;
        }
        return exists;
    }

    /**
     * put sphere keys
     */
    public void putSphereKeys(String sphereId, SphereKeys sphereKeys) {
        sphereKeyMap.put(sphereId, sphereKeys);
    }

    public void putSphereKey(String passCode, byte[] sphereKeys, String sphereId) {
        sphereHashKeyMap.put(passCode, new HashKey(sphereKeys, sphereId));
    }

    /**
     * get sphere keys
     */
    public SphereKeys getSphereKeys(String sphereId) {
        return sphereKeyMap.get(sphereId);
    }

    /**
     * get sphere keys
     */
    public HashKey getSphereHashKeys(String hashId) {
        return sphereHashKeyMap.get(hashId);
    }

    public String getSphereIdFromPasscode(String passCode) {
        if (sphereHashKeyMap.containsKey(passCode)) {
            return sphereHashKeyMap.get(passCode).getSphereId();
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((devices == null) ? 0 : devices.hashCode());
        result = prime * result + ((sphereHashKeyMap == null) ? 0 : sphereHashKeyMap.hashCode());
        result = prime * result + ((sphereKeyMap == null) ? 0 : sphereKeyMap.hashCode());
        result = prime * result + ((sphereMembership == null) ? 0 : sphereMembership.hashCode());
        result = prime * result + ((spheres == null) ? 0 : spheres.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SphereRegistry other = (SphereRegistry) obj;
        if (devices == null) {
            if (other.devices != null)
                return false;
        } else if (!devices.equals(other.devices))
            return false;
        if (sphereHashKeyMap == null) {
            if (other.sphereHashKeyMap != null)
                return false;
        } else if (!sphereHashKeyMap.equals(other.sphereHashKeyMap))
            return false;
        if (sphereKeyMap == null) {
            if (other.sphereKeyMap != null)
                return false;
        } else if (!sphereKeyMap.equals(other.sphereKeyMap))
            return false;
        if (sphereMembership == null) {
            if (other.sphereMembership != null)
                return false;
        } else if (!sphereMembership.equals(other.sphereMembership))
            return false;
        if (spheres == null) {
            if (other.spheres != null)
                return false;
        } else if (!spheres.equals(other.spheres))
            return false;
        return true;
    }

    public void clearRegistry() {
        this.spheres.clear();
        this.sphereMembership.clear();
        this.devices.clear();
        this.sphereKeyMap.clear();
        this.sphereHashKeyMap.clear();
    }

    /**
     * holds the hash key against the sphere key
     */
    public class HashKey implements Serializable {
        private static final long serialVersionUID = -3621416938085121516L;
        private byte[] hashKey;
        private String sphereId;

        public HashKey(byte[] sphereKey, String sphereId) {
            this.hashKey = sphereKey == null ? null : sphereKey.clone();
            this.sphereId = sphereId;
        }

        public String getSphereId() {
            return sphereId;
        }

        public byte[] getHashKey() {
            return hashKey == null ? null : hashKey.clone();
        }
    }

}
