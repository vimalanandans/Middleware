package com.bezirk.actions;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;
import java.io.FileNotFoundException;

public class SendFileStreamAction extends StreamAction {
    /*private final File file;*/

    public SendFileStreamAction(ZirkId zirkId, ZirkEndPoint recipient, /*StreamDescriptor descriptor,*/ short streamId, String streamActionName) {
        super(zirkId, recipient/*, descriptor*/, streamId, streamActionName);

        /*if (descriptor == null || descriptor.getFile() == null) {
            throw new IllegalArgumentException("Cannot send a null file");
        }

        if (!descriptor.getFile().exists()) {
            throw new IllegalArgumentException("Stream file not found", new FileNotFoundException(descriptor.getFile().getPath()));
        }

        this.file = descriptor.getFile();*/
    }

    /*public File getFile() {
        return file;
    }*/

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_BEZIRK_PUSH_UNICAST_STREAM;
    }
}
