package com.bezirk.commons;
/**
 * Added by Vimal
 */

import com.bezirk.util.Hashids;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public class UhuId {

    //final String hashCharName = "0123456789abcdef";
    SecureRandom rand = new SecureRandom();

    /** get Unique ID from UUID */
    public String getId() {
        return UUID.randomUUID().toString();
    }

    /** get short id. *** it is not Not Unique**** */
    public String getShortId() {
        Hashids hashids = getHashId();

        String hash = hashids.encode(rand.nextInt(Integer.MAX_VALUE));

        return hash;

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
        return hashids.encodeHex(convertStringtoHex(name));
    }

    private long getUnsignedLong(int x) {
        return x & 0x00000000ffffffffL;
    }

    /* public String getShortIdByHash()
     {
         String uuid = toHex(UUID.randomUUID().toString());

         Hashids hashids = getHashId();

         String hash = hashids.encodeHex(uuid);

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
        Hashids hashids = getHashId();
        hashids.decode(id);
        return ;
    }*/

    /** convert string to hex string */
    //http://stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
    public String convertStringtoHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }


    /** get hash id*/
    private Hashids getHashId() {
        // here the salt is class name
        return new Hashids(UhuId.class.getName(), 7);
    }
}
