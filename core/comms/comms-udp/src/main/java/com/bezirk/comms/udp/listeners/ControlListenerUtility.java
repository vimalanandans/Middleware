package com.bezirk.comms.udp.listeners;

import com.bezirk.middleware.core.checksum.DuplicateMessageManager;
import com.bezirk.commons.BezirkVersion;
import com.bezirk.comms.CommsNotification;
import com.bezirk.control.messages.ControlLedger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * This class is used a utility function for common methods used by the Control Listeners
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * if version mismatch occures
 */
public final class ControlListenerUtility {
    private static final Logger logger = LoggerFactory.getLogger(ControlListenerUtility.class);

    private final static byte SEPERATOR = (byte)',';

    private ControlListenerUtility() {
        // This a utility class
    }

    public static boolean constructMsg(ControlLedger receivedMessage, DatagramPacket receivePacket, CommsNotification notification) {
        //Reconstruct the message SphereName, EncryptedBytes
        boolean isMsgDuplicate = false;
        final byte[] received = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), received, 0, receivePacket.getLength());
        // Message format is bytes --> CS,SphereName,EncryptedMessage
        int countSeperator = -1;
        int firstSEP = 0;
        int secSEP = 0;
        for (int i = 0; i < received.length; i++) {
            if (received[i] == SEPERATOR) {
                countSeperator++;
                switch (countSeperator) {
                    case 0:
                        String tempString = new String(Arrays.copyOfRange(received, 0, i));
                        if (!BezirkVersion.BEZIRK_VERSION.equals(tempString)) {
                            logger.error("UPGRADE BEZIRK. BEZIRK VERSION MISMATCH. device version > " + BezirkVersion.BEZIRK_VERSION + " Received msg version " + tempString);
                            if (null != notification) {
                                notification.versionMismatch(tempString);
                            }
                            return false;
                        }
                        firstSEP = i;
                        break;
                    case 1:
                        byte[] receivedChecksum = Arrays.copyOfRange(received, firstSEP + 1, i);
                        secSEP = i;
                        //do the handling
                        isMsgDuplicate = DuplicateMessageManager.checkDuplicateEvent(receivedChecksum);
                        receivedMessage.setChecksum(receivedChecksum);
                        break;
                    case 2:
                        final byte[] reconSphere = Arrays.copyOfRange(received, secSEP + 1, i);
                        String sphereName = new String(reconSphere, 0, reconSphere.length);
                        receivedMessage.setSphereId(sphereName);
                        final byte[] encPayload = Arrays.copyOfRange(received, i + 1, receivePacket.getLength());
                        receivedMessage.setEncryptedMessage(encPayload);
                        break;

                }

                if (isMsgDuplicate) {
                    //logger.info("Duplicate Msg Received DROPPING PACKET; CHECKSUM = " + CheckSumUtil.bytesToHex(receivedMessage.getChecksum()) );
                    //logger.info("Duplicate Msg Received DROPPING PACKET");
                    return false;
                }
            }
        }
        return true;
    }
}
