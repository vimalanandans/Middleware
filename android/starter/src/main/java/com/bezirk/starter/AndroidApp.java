package com.bezirk.starter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.bezirk.actions.BezirkActions;
import com.bezirk.application.BezirkApp;
import com.bezirk.pipe.core.BezirkPipeAPI;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipeRequest;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidApp implements BezirkApp {
    private static final Logger logger = LoggerFactory.getLogger(AndroidApp.class);

    private static final String COMPONENT_NAME = "com.bosch.upa.uhu.controlui";
    private final BezirkPipeAPI pipeAPI;
    private Context context;

    public AndroidApp(Context context, BezirkPipeAPI pipeApi) {
        this.context = context;
        this.pipeAPI = pipeApi;
    }

    @Override
    public void approvePipeRequest(String pipeRequestId) throws PipeApprovalException {
        if (pipeAPI == null) {
            throw new PipeApprovalException("Cannot lookup pipe request because pipeAPI is null");
        }

        logger.info("  -- Approving Pipe Request --");

        PipeRequest pipeRequest = pipeAPI.getPipeRequest(pipeRequestId);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(COMPONENT_NAME, "com.bezirk.spheremanager.PipeActivity"));
        intent.putExtra(BezirkActions.KEY_PIPE_REQ_ID, pipeRequestId);
        intent.putExtra(BezirkActions.KEY_SENDER_ZIRK_ID, new Gson().toJson(pipeRequest.getRequestingService()));

        // TODO remove the above and send the below serialized object to UI
        //Only to test
        String data = new Gson().toJson(pipeRequest);
        logger.info("pipe request : toJson > " + data);

        context.startActivity(intent);

    }

    public void setContext(Context context) {
        this.context = context;
    }


}
