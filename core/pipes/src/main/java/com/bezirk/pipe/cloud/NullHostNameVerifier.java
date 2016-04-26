package com.bezirk.pipe.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Verifies any hostname for any SSLSession. This is OK to use if you are
 * using self-signed certs and the target web zirk may be
 * deployed anywhere.
 * Adapted from: http://stackoverflow.com/questions/14619781/java-io-ioexception-hostname-was-not-verified
 */
public class NullHostNameVerifier implements HostnameVerifier {

    public static final Logger log = LoggerFactory.getLogger(NullHostNameVerifier.class);

    /**
     * Retursn true for any hostname and SSLSession
     */
    public boolean verify(String hostname, SSLSession session) {
        log.info("Approving certificate for " + hostname);
        return true;
    }
}
