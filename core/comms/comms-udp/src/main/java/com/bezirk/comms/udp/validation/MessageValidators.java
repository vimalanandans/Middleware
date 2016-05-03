package com.bezirk.comms.udp.validation;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkCommsLegacy;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.util.BezirkValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the worker threads which validate the message before populating
 * the receiverQueue
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class MessageValidators implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageValidators.class);

    private Ledger ledger;
    private String computedDevId;
    private BezirkCommsLegacy bezirkComms = null;

    public MessageValidators(String devId, Ledger l, BezirkCommsLegacy bezirkComms) {
        this.ledger = l;
        this.computedDevId = devId;
        this.bezirkComms = bezirkComms;
    }

    @Override
    public void run() {
        if (this.ledger instanceof ControlLedger) {
            processControl((ControlLedger) this.ledger);
        } else if (this.ledger instanceof EventLedger) {
            processEvent((EventLedger) this.ledger);
        }
    }

    private void processEvent(EventLedger eLedger) {
        //Check Encrypted Header and SphereId
        if (null == eLedger.getEncryptedHeader() || !BezirkValidatorUtility.checkForString(eLedger.getHeader().getSphereName())) {
            logger.error(" Null Header received, Removed the msg");
            return;
        }

        //Decrypt Header
        BezirkSphereForSadl sphereIf = BezirkCompManager.getSphereForSadl();

        if (sphereIf == null) {
            logger.error("sphere object for the sadl is invalid. msg not decrypted");
            return;
        }


        final String encryptedSerializedHeader = sphereIf.decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedHeader());
        if (!BezirkValidatorUtility.checkForString(encryptedSerializedHeader)) {
            logger.error(" Serialized Decrypted Header is null");
            return;
        }
        eLedger.setSerializedHeader(encryptedSerializedHeader);

        //Set Header
        if (!setHeader(eLedger, encryptedSerializedHeader)) {
            logger.error("Dropping Msg setHeader failed");
            return;
        }

        // Check Integrity
        boolean success = this.computedDevId.equals(eLedger.getHeader().getSenderSEP().device);
        if (!success) {
            logger.error("Dropping Msg Integrity failed");
            return;
        }

        //Clarify Add received message to receiverMessengerQueue
        //MessageQueueManager.getReceiverMessageQueue().addToQueue(eLedger);
        bezirkComms.addToQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);

    }

    private Boolean setHeader(EventLedger eLedger, String encryptedSerializedHeader) {
        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = new Gson().fromJson(encryptedSerializedHeader, MulticastHeader.class);
            if (!BezirkValidatorUtility.checkHeader(mHeader) || null == eLedger.getEncryptedMessage()) {
                logger.error(" Serialized Decrypted Header (Multicast) is not having all the fields defined");
                //MessageQueueManager.getReceiverMessageQueue().removeFromQueue(eLedger);
                bezirkComms.removeFromQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);
                return false;
            }
            eLedger.setHeader(mHeader);
        } else {
            UnicastHeader uHeader = new Gson().fromJson(encryptedSerializedHeader, UnicastHeader.class);
            if (!BezirkValidatorUtility.checkHeader(uHeader)) {
                logger.error(" Serialized Decrypted Header ( Unicast ) is not having all the fields defined");
                //MessageQueueManager.getReceiverMessageQueue().removeFromQueue(eLedger);
                bezirkComms.removeFromQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);
                return false;
            }
            eLedger.setHeader(uHeader);
        }
        return true;
    }

    private void processControl(ControlLedger cLedger) {
        //decrypt
        if (decryptMsg(cLedger)) {
            ControlMessage cMsg = ControlMessage.deserialize(cLedger.getSerializedMessage(), ControlMessage.class);
            cLedger.setMessage(cMsg);
        } else {
            logger.debug("Decryption failed");
            return;
        }


        // Check Integrity
        final Boolean success = this.computedDevId.equals(cLedger.getMessage().getSender().device);
        if (!success) {
            logger.debug("Dropping Msg Integrity failed");
            return;
        }

        //Check duplicate
        /*
		This module is no longer needed. because no one calls addmessage
         * and only the checking the record present in the message validator
         * - Vimal
		success = BezirkCompManager.getMsgBookKeeper().processMsg(cLedger);
		if(!success){
			logger.debug("Dropping Duplicate Msg: "+ cLedger.getMessage().getUniqueKey());
			return;
		}
		*/

        //populate Receiver Queue
        //MessageQueueManager.getControlReceiverQueue().addToQueue(cLedger);
        bezirkComms.addToQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.CONTROL_RECEIVE_QUEUE, cLedger);
    }

    // Clarify and change the Message
    private Boolean decryptMsg(ControlLedger cLedger) {
        final String sphereid = cLedger.getSphereId();
        final byte[] encMsg = cLedger.getEncryptedMessage();

        if (!BezirkValidatorUtility.checkForString(sphereid) || encMsg == null) {
            logger.error("sphere or encrypted message is null");
            return false;
        } else {
            BezirkSphereForSadl sphereIf = BezirkCompManager.getSphereForSadl();

            if (sphereIf == null) {
                logger.error("sphere object for the sadl is invalid. msg not decrypted");
                return false;
            }

            final String decryptMsg = sphereIf.decryptSphereContent(sphereid, encMsg);
            if (BezirkValidatorUtility.checkForString(decryptMsg)) {
                logger.debug("Ctrl Msg decrypted: " + decryptMsg);
                cLedger.setSerializedMessage(decryptMsg);
                return true;
            } else {
                return false;
            }
        }
    }
}
