package com.bezirk.middleware.core.identity;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

public class BezirkIdentityManager implements com.bezirk.middleware.core.identity.IdentityProvisioner, IdentityManager {
    private static final Logger logger = LoggerFactory.getLogger(BezirkIdentityManager.class);

    private static final String IDENTITY_HASH_ALGORITHM = "SHA-256";
    private static final String DEFAULT_USER_ID = "BezirkUser";
    private final Gson gson = new Gson();
    private Alias currentIdentity;

    public boolean createAndSetIdentity(String serializedAlias) {
        final Alias alias;

        if (serializedAlias == null) {
            alias = createIdentity(DEFAULT_USER_ID);
        } else {
            alias = gson.fromJson(serializedAlias, Alias.class);
        }

        if (alias != null) {
            setIdentity(alias);
            return true;
        }
        return false;
    }

    /**
     * Get the deserialized string
     */
    public String getAliasString() {
        if (currentIdentity != null)
            return gson.toJson(currentIdentity);
        return null;
    }

    @Override
    public Alias createIdentity(String name) {
        final SecureRandom rng = new SecureRandom();

        final UUID uuid = UUID.randomUUID();

        final byte[] bezirkId = new byte[16];
        rng.nextBytes(bezirkId);

        final ByteBuffer aliasBuffer = ByteBuffer.wrap(new byte[16 + bezirkId.length]);
        aliasBuffer.putLong(uuid.getMostSignificantBits());
        aliasBuffer.putLong(uuid.getLeastSignificantBits());
        aliasBuffer.put(bezirkId);

        byte[] aliasHash;

        try {
            MessageDigest md = MessageDigest.getInstance(IDENTITY_HASH_ALGORITHM);
            aliasHash = md.digest(aliasBuffer.array());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Identity hash algorithm does not exist", e);
            return null;
        }

        return new Alias(name, aliasHash);
    }

    @Override
    public void setIdentity(Alias identity) {
        currentIdentity = identity;
    }

    @Override
    public Alias getAlias() {
        return new Alias(currentIdentity.getName(), currentIdentity.getHash());
    }

    @Override
    public boolean isMiddlewareUser(Alias alias) {
        return currentIdentity != null && currentIdentity.equals(alias);
    }
}
