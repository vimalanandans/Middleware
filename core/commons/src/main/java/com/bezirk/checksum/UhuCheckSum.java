package com.bezirk.checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;


public final class UhuCheckSum {
    private static final Logger logger = LoggerFactory.getLogger(UhuCheckSum.class);

    private final static String ALGORITHM = "SHA";

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private UhuCheckSum() {

    }

    /**
     * This method can be used to compute CRC based on SHA algorithm
     *
     * @param dataToBeComputed
     * @return
     */
    public static byte[] computeCheckSum(final byte[] dataToBeComputed) {
        byte[] computedCheckSum = null;
        try {
            MessageDigest msgdgst = MessageDigest.getInstance(ALGORITHM);
            msgdgst.update(dataToBeComputed);
            computedCheckSum = msgdgst.digest();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could not find any algorithm to compute the checksum", e);
        }
        return computedCheckSum;
    }


    /**
     * This algorithm is used to compute the Checksum
     *
     * @param dataToBeComputed
     * @return
     */
    public static byte[] computeCRC(final byte[] dataToBeComputed) {
        byte[] computedCheckSum = null;
        try {
            CRC32 crc32 = new CRC32();
            crc32.update(dataToBeComputed);
            Long crcValue = crc32.getValue();
            computedCheckSum = String.valueOf(crcValue).getBytes();
        } catch (Exception e) {
            logger.error("Error occured while computing checksum using crc32", e);
        }
        return computedCheckSum;
    }

}
