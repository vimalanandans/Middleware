package com.bezirk.middleware.streaming;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.sun.istack.internal.NotNull;

import java.io.File;

/**
 * Created by PIK6KOR on 11/3/2016.
 */

public class FileStreamRequest extends StreamRequest{
    private File file;

    public FileStreamRequest(@NotNull ZirkId zirkId, @NotNull BezirkZirkEndPoint zirkEndPoint, @NotNull File file){
        super(zirkEndPoint, zirkId);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
