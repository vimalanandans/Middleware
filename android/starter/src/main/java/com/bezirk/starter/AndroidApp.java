package com.bezirk.starter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.UhuActions;
import com.bezirk.application.IUhuApp;
import com.bezirk.pipe.core.IUhuPipeAPI;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipeRequest;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wya1pi on 12/16/14.
 */
public class AndroidApp implements IUhuApp {

    private static final Logger log = LoggerFactory.getLogger(AndroidApp.class);
    private static final String COMPONENT_NAME = "com.bosch.upa.uhu.controlui";
    private final IUhuPipeAPI pipeAPI;
    private Context context;

    public AndroidApp(Context context, IUhuPipeAPI pipeApi) {
        this.context = context;
        this.pipeAPI = pipeApi;
    }

    @Override
    public void approvePipeRequest(String pipeRequestId) throws PipeApprovalException {

        if (pipeAPI == null) {
            throw new PipeApprovalException("Cannot lookup pipe request because pipeAPI is null");
        }

        log.info("  -- Aproving Pipe Request --");

        PipeRequest pipeRequest = pipeAPI.getPipeRequest(pipeRequestId);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(COMPONENT_NAME, "com.bosch.upa.spheremanager.PipeActivity"));
        intent.putExtra(UhuActions.KEY_PIPE_REQ_ID, pipeRequestId);
        intent.putExtra(UhuActions.KEY_PIPE_NAME, pipeRequest.getPipe().getName());
        intent.putExtra(UhuActions.KEY_SENDER_SERVICE_ID, new Gson().toJson((UhuServiceId) pipeRequest.getRequestingService()));

        // TODO remove the above and send the below serialized object to UI
        //Only to test
        String data = new Gson().toJson(pipeRequest);
        log.info("pipe request : toJson > " + data);

        context.startActivity(intent);

    }

    public void setContext(Context context) {
        this.context = context;
    }


}
