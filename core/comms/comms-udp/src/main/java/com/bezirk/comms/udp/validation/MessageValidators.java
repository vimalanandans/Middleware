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
    private static final Logger log = LoggerFactory.getLogger(MessageValidators.class);

    private Ledger ledger;
    private String computedDevId;
    private BezirkCommsLegacy uhuComms = null;

    public MessageValidators(String devId, Ledger l, BezirkCommsLegacy uhuComms) {
        this.ledger = l;
        this.computedDevId = devId;
        this.uhuComms = uhuComms;
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
            log.error(" Null Header received, Removed the msg");
            return;
        }

        //Decrypt Header
        BezirkSphereForSadl sphereIf = BezirkCompManager.getSphereForSadl();

        if (sphereIf == null) {
            log.error("sphere object for the sadl is invalid. msg not decrypted");
            return;
        }


        final String encryptedSerialzedHeader = sphereIf.decryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getEncryptedHeader());
        if (!BezirkValidatorUtility.checkForString(encryptedSerialzedHeader)) {
            log.error(" Serialized Decrypted Header is null");
            return;
        }
        eLedger.setSerializedHeader(encryptedSerialzedHeader);

        //Set Header
        if (!setHeader(eLedger, encryptedSerialzedHeader)) {
            log.error("Dropping Msg setHeader failed");
            return;
        }

        // Check Integrity
        boolean success = this.computedDevId.equals(eLedger.getHeader().getSenderSEP().device);
        if (!success) {
            log.error("Dropping Msg Integerity failed");
            return;
        }

        //Clarify Add received message to receiverMessagerQueue
        //MessageQueueManager.getReceiverMessageQueue().addToQueue(eLedger);
        uhuComms.addToQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);

    }

    private Boolean setHeader(EventLedger eLedger, String encryptedSerialzedHeader) {
        if (eLedger.getIsMulticast()) {
            MulticastHeader mHeader = new Gson().fromJson(encryptedSerialzedHeader, MulticastHeader.class);
            if (!BezirkValidatorUtility.checkHeader(mHeader) || null == eLedger.getEncryptedMessage()) {
                log.error(" Serialized Decrypted Header (Multicast) is not having all the feilds defined");
                //MessageQueueManager.getReceiverMessageQueue().removeFromQueue(eLedger);
                uhuComms.removeFromQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);
                return false;
            }
            eLedger.setHeader(mHeader);
        } else {
            UnicastHeader uHeader = new Gson().fromJson(encryptedSerialzedHeader, UnicastHeader.class);
            if (!BezirkValidatorUtility.checkHeader(uHeader)) {
                log.error(" Serialized Decrypted Header ( Unicast ) is not having all the feilds defined");
                //MessageQueueManager.getReceiverMessageQueue().removeFromQueue(eLedger);
                uhuComms.removeFromQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eLedger);
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
            log.debug("Decryption failed");
            return;
        }


        // Check Integrity
        final Boolean success = this.computedDevId.equals(cLedger.getMessage().getSender().device);
        if (!success) {
            log.debug("Dropping Msg Integerity failed");
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
        uhuComms.addToQueue(BezirkCommsLegacy.COMM_QUEUE_TYPE.CONTROL_RECEIVE_QUEUE, cLedger);
    }

    // Clarify and change the Message
    private Boolean decryptMsg(ControlLedger cLedger) {
        final String sphereid = cLedger.getSphereId();
        final byte[] encMsg = cLedger.getEncryptedMessage();

        if (!BezirkValidatorUtility.checkForString(sphereid) || encMsg == null) {
            log.error("sphere or encrypted message is null");
            return false;
        } else {
            BezirkSphereForSadl sphereIf = BezirkCompManager.getSphereForSadl();

            if (sphereIf == null) {
                log.error("sphere object for the sadl is invalid. msg not decrypted");
                return false;
            }

            final String decryptMsg = sphereIf.decryptSphereContent(sphereid, encMsg);
            if (BezirkValidatorUtility.checkForString(decryptMsg)) {
                log.debug("Ctrl Msg decrypted: " + decryptMsg);
                cLedger.setSerializedMessage(decryptMsg);
                return true;
            } else {
                return false;
            }
        }
    }
}
