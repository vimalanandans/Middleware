package com.bezirk.pipe.android;

import com.bezirk.comms.UhuComms;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.pipe.core.PipeManagerImpl;
import com.bezirk.pipe.core.PipeRegistry;

import java.io.File;

/**
 * Created by wya1pi on 12/16/14.
 */
public final class PipeCommsFactory {

    private PipeCommsFactory(){
        //To hide the implicit public constructor.
    }

    public static PipeManager createPipeComms() {
        // Set up data members needed by PipeManager
        PipeRegistry pipeRegistry = PipeRegistryFactory.getPipeRegistry();
        LocalAndroidSender localSender = new LocalAndroidSender();

        // Where to write files retrieved from the pipe
        File outputDir = new File(UhuComms.getDOWNLOAD_PATH());

        // Set up and initialize pipe manager
        PipeManagerImpl pipeManagerImpl = new PipeManagerImpl();
        pipeManagerImpl.setPipeRegistry(pipeRegistry);
        pipeManagerImpl.setLocalSender(localSender);
        pipeManagerImpl.setOutputDir(outputDir);
        pipeManagerImpl.setCertFileName("assets/" + PipeManagerImpl.DEFAULT_CERT_FILENAME);
        pipeManagerImpl.init();

        return pipeManagerImpl;
    }
}


