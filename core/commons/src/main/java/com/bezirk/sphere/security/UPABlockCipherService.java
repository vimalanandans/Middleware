package com.bezirk.sphere.security;

import com.bezirk.devices.BezirkOsPlatform;

import org.apache.shiro.crypto.DefaultBlockCipherService;
import org.apache.shiro.crypto.OperationMode;
import org.apache.shiro.crypto.PaddingScheme;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * Class for thread-safe crypto functions, specifically providing a block cipher
 * that offers authenticated encryption (protects confidentiality and
 * integrity).
 */
public class UPABlockCipherService extends DefaultBlockCipherService {
    // We are using AES in GCM mode (needs no padding)
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final OperationMode operationMode = OperationMode.GCM;
    private static final PaddingScheme paddingScheme = PaddingScheme.NONE;
    // Bouncy Castle Provider registered?
    private static boolean bouncyRegistered = false;

    public UPABlockCipherService() {
        super(ENCRYPTION_ALGORITHM);
        if (!bouncyRegistered && BezirkOsPlatform.getCurrentOSPlatform().equals(BezirkOsPlatform.UPA_SERV__RUNTIME_ENV__JAVA))
            registerBouncy();
        setMode(operationMode);
        setPaddingScheme(paddingScheme);
    }

    private static synchronized void registerBouncy() {
        if (!bouncyRegistered)
            Security.addProvider(new BouncyCastleProvider());
        bouncyRegistered = true;
    }
}