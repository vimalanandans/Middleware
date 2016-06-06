package com.bezirk.proxy.android;

import android.content.Intent;

import com.bezirk.actions.BezirkActions;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.core.PipePolicyUtility;
import com.bezirk.pipe.core.PipeRequest;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static com.bezirk.util.BezirkValidatorUtility.checkForString;
import static com.bezirk.util.BezirkValidatorUtility.checkBezirkZirkId;

public class PipeActionParser {
    private static final Logger logger = LoggerFactory.getLogger(PipeActionParser.class);

    public PipeRequest parsePipeRequest(Intent intent) {
        logger.info("Validating intent: {}", intent.getAction());

        /*
         * Get strings out of the intent
         */

        String pipeName = intent.getStringExtra(BezirkActions.KEY_PIPE_NAME);
        String pipeId = intent.getStringExtra(BezirkActions.KEY_PIPE_REQ_ID);
        String serviceIdAsString = intent.getStringExtra(BezirkActions.KEY_SENDER_ZIRK_ID);
        String uriString = intent.getStringExtra(BezirkActions.KEY_PIPE_URI);
        String pipeClassName = intent.getStringExtra(BezirkActions.KEY_PIPE_CLASS);
        String policyIn = intent.getStringExtra(BezirkActions.KEY_PIPE_POLICY_IN);
        String policyOut = intent.getStringExtra(BezirkActions.KEY_PIPE_POLICY_OUT);

        BezirkPipePolicy allowedIn = PipePolicy.fromJson(policyIn, BezirkPipePolicy.class);
        BezirkPipePolicy allowedOut = PipePolicy.fromJson(policyOut, BezirkPipePolicy.class);
        PipePolicyUtility.policyInMap.put(pipeId, allowedIn);
        PipePolicyUtility.policyOutMap.put(pipeId, allowedOut);


        /*
         * Validate intent data
         */

        if (!stringsValid(serviceIdAsString, pipeName, uriString, pipeClassName)) {
            logger.error("Intent not valid because intent extra strings not set correctly");
            return null;
        }

        URI pipeUri = null;
        try {
            pipeUri = new URI(uriString);
        } catch (URISyntaxException e) {
            logger.error("Intent not valid because pipeUri could not be parsed", e);
            return null;
        }

        ZirkId serviceId = serviceIdFromString(serviceIdAsString);
        if (serviceId == null) {
            logger.error("Intent not valid because there was a failure validating zirkId");
            return null;
        }

        /*
         * Create the PipeRequest object
         */

        /*CloudPipe pipe = null;

        if (pipeClassName.equals(CloudPipe.class.getCanonicalName())) {
            logger.debug("Creating cloud pipe");
            pipe = new CloudPipe(pipeName, pipeUri);
        } else {
            logger.error("Unknown pipe type: " + pipeClassName);
            return null;
        }*/

        PipeRequest pipeRequest = new PipeRequest(pipeId);
        //pipeRequest.setPipe(pipe);
        pipeRequest.setRequestingService(serviceId);

        pipeRequest.setAllowedIn(allowedIn);
        pipeRequest.setAllowedOut(allowedOut);

        return pipeRequest;
    }

    private ZirkId serviceIdFromString(String serviceIdAsString) {
        Gson gson = new Gson();
        ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
        if (!checkBezirkZirkId(serviceId)) {
            logger.error("zirkId not valid: " + serviceId);
            return null;
        }

        return serviceId;
    }

    private boolean stringsValid(String serviceId, String pipeName, String uriString, String pipeClassName) {

        boolean stringsValid = true;
        String errorSuffix = "String is null or empty";

        if (!checkForString(serviceId)) {
            logger.error("zirkId " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(pipeName)) {
            logger.error("pipeName " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(uriString)) {
            logger.error("uri " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(pipeClassName)) {
            logger.error("Pipe class " + errorSuffix);
            stringsValid = false;
        }

        return stringsValid;
    }
}
