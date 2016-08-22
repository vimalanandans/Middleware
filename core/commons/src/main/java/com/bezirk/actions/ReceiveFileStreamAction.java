package com.bezirk.actions;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

public final class ReceiveFileStreamAction extends ZirkAction {
    private final String serializedStream;
    /**
     * Path to downloaded file.
     */
    private final File file;
    private final BezirkZirkEndPoint sender;

    public ReceiveFileStreamAction(ZirkId recipientId, String serializedStream,
                                   File file, BezirkZirkEndPoint sender) {
        super(recipientId);

        /*if (serializedStream == null) {
            throw new IllegalArgumentException("serializedStream cannot be null");
        }*/

        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }

        if (sender == null) {
            throw new IllegalArgumentException("sender cannot be null");
        }

        this.serializedStream = serializedStream;
        this.file = file;
        this.sender = sender;
    }

    public String getSerializedStream() {
        return serializedStream;
    }

    public File getFile() {
        return file;
    }

    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_ZIRK_RECEIVE_STREAM;
    }
}
