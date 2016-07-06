package com.bezirk.pipe.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SelfSignedContextBuilder implements SSLContextBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SelfSignedContextBuilder.class);

    protected String certFileName = null;

    /*
     * Configure SSL Context so that we can send a HTTPS using a self-signed certificate
     * Adapted from sample code on developer.android.com:
     * https://developer.android.com/training/articles/security-ssl.html
     */
    public SSLContext build() throws Exception {
        if (certFileName == null || certFileName.isEmpty()) {
            throw new Exception("certFileName not set");
        }

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        /*
		 * To download a cert from, e.g., localhost use the openssl command: 
		 * sudo echo -n | openssl s_client -connect localhost:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ./cert.pem
		 * via: http://serverfault.com/questions/139728/how-to-download-the-ssl-certificate-from-a-website
		 */
        InputStream certInStream = getClass().getClassLoader().getResourceAsStream(certFileName);
        if (certInStream == null) {
            throw new Exception("Cert file could not be found: " + certFileName);
        }

        Certificate cert;
        try (InputStream buffCertInStream = new BufferedInputStream(certInStream)) {
            cert = cf.generateCertificate(buffCertInStream);
            logger.debug("cert=" + ((X509Certificate) cert).getSubjectDN());
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null); // initialize an empty keystore
        keyStore.setCertificateEntry("ca", cert); // add this certificate to our keystore

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        return context;
    }


    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }

}