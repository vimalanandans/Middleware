package com.bezirk.middleware.core.sphere.impl;

import com.bezirk.middleware.core.util.Hashids;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class BezirkId {
    private final SecureRandom rand = new SecureRandom();

    /** get Unique ID from UUID */
    public String getId() {
        return UUID.randomUUID().toString();
    }

    /** get short id. *** it is not Not Unique**** */
    public String getShortId() {
        return getHashId().encode(rand.nextInt(Integer.MAX_VALUE));
    }

    /** get short id - by strings hash code (uuid). not unique id*/
    public String getShortIdByHash(String name) {
        Hashids hashids = getHashId();
        // encode the hash code of the string and create the short id
        return hashids.encode(getUnsignedLong(name.hashCode()));
    }

    /** get short id - by name*/
    public String getShortIdByName(String name) {
        Hashids hashids = getHashId();
        // encode the hash code of the string and create the short id
        return hashids.encodeHex(toHex(name));
    }

    private long getUnsignedLong(int x) {
        return x & 0x00000000ffffffffL;
    }

    /* public String getShortIdByHash()
     {
         String uuid = toHex(UUID.randomUUID().toString());

         Hashids com.bezirk.middleware.core.hashids = getHashId();

         String hash = com.bezirk.middleware.core.hashids.encodeHex(uuid);

         return hash;

     }*/
    public String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, i + 2);
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

    /** get short id (not unique) - by name*/
    /*public String getNameFromShortId(String id)
    {
        Hashids com.bezirk.middleware.core.hashids = getHashId();
        com.bezirk.middleware.core.hashids.decode(id);
        return ;
    }*/

    private String toHex(String arg) {
        try {
            return String.format("%x", new BigInteger(1, arg.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
        }
    }


    /** get hash id*/
    private Hashids getHashId() {
        // here the salt is class name
        return new Hashids(BezirkId.class.getName(), 7);
    }
}
