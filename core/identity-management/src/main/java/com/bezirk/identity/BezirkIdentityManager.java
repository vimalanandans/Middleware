package com.bezirk.identity;

import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.messages.IdentifiedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

public class BezirkIdentityManager implements IdentityProvisioner, IdentityManager {
    private static final Logger logger = LoggerFactory.getLogger(BezirkIdentityManager.class);

    private static final String IDENTITY_HASH_ALGORITHM = "SHA-256";

    private Alias currentIdentity;

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
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("Identity hash algorithm does not exist", nsae);
            return null;
        }

        return (Alias) new BezirkAlias(name, aliasHash);
    }

    @Override
    public void setIdentity(Alias identity) {
        currentIdentity = identity;
    }

    @Override
    public void setMessageIdentity(IdentifiedEvent event) {
        event.setAlias(currentIdentity);
    }

    @Override
    public boolean isMiddlewareUser(Alias alias) {
        return alias.equals(currentIdentity);
    }
}
