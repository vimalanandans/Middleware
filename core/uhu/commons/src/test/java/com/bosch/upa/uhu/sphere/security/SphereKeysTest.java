package com.bosch.upa.uhu.sphere.security;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Test;

public class SphereKeysTest {
	
	byte[] sphereKey="TestSphereKey".getBytes();
	byte[] ownerPrivateKeyBytes="OwnerPrivateKey".getBytes();
	byte[] ownerPublicKeyBytes="OwnerPublicKey".getBytes();
	SphereKeys sphereKeys = new SphereKeys();

	@Test
	public void test() {

		sphereKeys = new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
	
		assertArrayEquals("SphereKey is not equal to the set value.", sphereKey,sphereKeys.getSphereKey());
		assertArrayEquals("OwnerPrivateKey is not equal to the set value.", ownerPrivateKeyBytes,sphereKeys.getOwnerPrivateKeyBytes());
		assertArrayEquals("OwnerPublicKey is not equal to the set value.", ownerPublicKeyBytes,sphereKeys.getOwnerPublicKeyBytes());
		

		sphereKey="TempSphereKey".getBytes();
		ownerPublicKeyBytes="TempOwnerPublicKey".getBytes();
		sphereKeys = new SphereKeys(sphereKey, ownerPublicKeyBytes) ;
		
		assertArrayEquals("SphereKey is not equal to the set value.", sphereKey,sphereKeys.getSphereKey());
		assertArrayEquals("OwnerPublicKey is not equal to the set value.", ownerPublicKeyBytes,sphereKeys.getOwnerPublicKeyBytes());
		
		sphereKey="Test2SphereKey".getBytes();
		KeyPair pair= getKeyPair();
		sphereKeys = new SphereKeys(sphereKey, pair);
		
		assertArrayEquals("SphereKey is not equal to the set value.", sphereKey,sphereKeys.getSphereKey());
		assertArrayEquals("OwnerPublicKey is not equal to the set value.", pair.getPublic().getEncoded(),sphereKeys.getOwnerPublicKeyBytes());
		assertArrayEquals("OwnerPrivateKey is not equal to the set value.", pair.getPrivate().getEncoded(),sphereKeys.getOwnerPrivateKeyBytes());
		
		testHashCodeAndEquals();

	}

	
	private void testHashCodeAndEquals() {
		
		 sphereKeys= new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
		
		SphereKeys testSphereKeys = new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
		
		assertEquals("Similar SphereKeys have different hashcode.",sphereKeys.hashCode(),testSphereKeys.hashCode());
		assertTrue("Similar SphereKeys are considered unequal.",sphereKeys.equals(testSphereKeys));
		assertTrue("SphereKeys is not considered equal to itself.",sphereKeys.equals(sphereKeys));

		
		assertFalse("SphereKeys is considered equal to ownerPrivateKeys",sphereKeys.equals(ownerPrivateKeyBytes));
		assertFalse("SphereKeys is considered equal to null",sphereKeys.equals(null));

		ownerPrivateKeyBytes ="DiffPrivateKey".getBytes();
		testSphereKeys= new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
		
		assertNotEquals("Different SphereKeys have same hashcode.",sphereKeys.hashCode(),testSphereKeys.hashCode());
		assertFalse("SphereKeys with different privatekeys are considered equal.",sphereKeys.equals(testSphereKeys));
		
		ownerPrivateKeyBytes =sphereKeys.getOwnerPrivateKeyBytes();
		ownerPublicKeyBytes = "DiffPublicKey".getBytes();
		testSphereKeys= new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
		
		assertNotEquals("Different SphereKeys have same hashcode.",sphereKeys.hashCode(),testSphereKeys.hashCode());
		assertFalse("SphereKeys with different publicKeys are considered equal.",sphereKeys.equals(testSphereKeys));
		
		ownerPrivateKeyBytes =sphereKeys.getOwnerPrivateKeyBytes();
		ownerPublicKeyBytes = sphereKeys.getOwnerPublicKeyBytes();
		sphereKey ="DiffSphereKey".getBytes();
		testSphereKeys= new SphereKeys(sphereKey, ownerPrivateKeyBytes, ownerPublicKeyBytes);
		
		assertNotEquals("Different SphereKeys have same hashcode.",sphereKeys.hashCode(),testSphereKeys.hashCode());
		assertFalse("SphereKeys with different sphereKey are considered equal.",sphereKeys.equals(testSphereKeys));

		
		
	}


	private KeyPair getKeyPair(){
        KeyPair pair = null;
        try {
        	
        	String KEY_FACTORY_ALGORITHM = "DSA";
        	String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
            
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);

            SecureRandom random = SecureRandom
                    .getInstance(SECURE_RANDOM_ALGORITHM);

            keyGen.initialize(1024, random);

            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            fail("Unable to generate keypair.");
        }
        return pair;
    }
}
