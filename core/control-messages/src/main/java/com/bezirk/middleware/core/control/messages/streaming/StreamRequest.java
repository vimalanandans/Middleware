/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.control.messages.streaming;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.core.streaming.control.Objects.StreamRecord;

/**
 * This Message is internally sent by the Bezirk for hand shaking with the recipient.
 */
public class StreamRequest extends UnicastControlMessage {
    /**
     * Discriminator that uniquely defines the Control message!
     */
    private static final Discriminator discriminator = ControlMessage.Discriminator.STREAM_REQUEST;
    /**
     * Location that is used for MulticastStreamDescriptor
     */
    public final Location location;
    /**
     * Contains the serialized String of the StreamDescriptor Descriptor pushed by the StreamDescriptor
     */
    public final String serializedString;
    /**
     * StreamDescriptor Topic of the StreamDescriptor Descriptor
     */
    public final String streamLabel = null;
    /**
     * Name of the file that needs to be pushed on the recipient
     */
    public final String fileName;
    /**
     * Flag indicating secure communication. If true file transfer will be encrypted.
     */
    public final boolean isEncrypted;
    /**
     * Flag indicating the communication is quantized. If <code>true</code> data  will be quantized
     * and sent
     */
    public boolean isIncremental = false;

    public StreamRequest(String sphereId, StreamRecord streamRecord, Location location) {
        super(streamRecord.getSender(), streamRecord.getRecipient(), sphereId, discriminator, false,
                streamRecord.getUniqueKey());
        this.location = location;
        this.serializedString = streamRecord.getSerializedStream();
        this.fileName = streamRecord.getFile().getName();
        this.isEncrypted = streamRecord.isEncryptedStream();
    }
}
