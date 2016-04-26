package com.bezirk.proxy.android;

import android.content.Intent;

import com.bezirk.actions.UhuActions;
import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.core.PipePolicyUtility;
import com.bezirk.pipe.core.PipeRequest;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static com.bezirk.util.UhuValidatorUtility.checkForString;
import static com.bezirk.util.UhuValidatorUtility.checkUhuServiceId;

/**
 * Created by wya1pi on 8/21/14.
 */
public class PipeActionParser {
    private static final Logger log = LoggerFactory.getLogger(PipeActionParser.class);

    public PipeRequest parsePipeRequest(Intent intent) {
        log.info("Validating intent: " + intent.getAction());

        /*
         * Get strings out of the intent
         */

        String pipeName = intent.getStringExtra(UhuActions.KEY_PIPE_NAME);
        String pipeId = intent.getStringExtra(UhuActions.KEY_PIPE_REQ_ID);
        String serviceIdAsString = intent.getStringExtra(UhuActions.KEY_SENDER_SERVICE_ID);
        String uriString = intent.getStringExtra(UhuActions.KEY_PIPE_URI);
        String pipeClassName = intent.getStringExtra(UhuActions.KEY_PIPE_CLASS);
        String policyIn = intent.getStringExtra(UhuActions.KEY_PIPE_POLICY_IN);
        String policyOut = intent.getStringExtra(UhuActions.KEY_PIPE_POLICY_OUT);

        UhuPipePolicy allowedIn = PipePolicy.fromJson(policyIn, UhuPipePolicy.class);
        UhuPipePolicy allowedOut = PipePolicy.fromJson(policyOut, UhuPipePolicy.class);
        PipePolicyUtility.policyInMap.put(pipeId, allowedIn);
        PipePolicyUtility.policyOutMap.put(pipeId, allowedOut);


        /*
         * Validate intent data
         */

        if (!stringsValid(serviceIdAsString, pipeName, uriString, pipeClassName)) {
            log.error("Intent not valid because intent extra strings not set correctly");
            return null;
        }

        URI pipeUri = null;
        try {
            pipeUri = new URI(uriString);
        } catch (URISyntaxException e) {
            log.error("Intent not valid because pipeUri could not be parsed", e);
            return null;
        }

        UhuServiceId serviceId = serviceIdFromString(serviceIdAsString);
        if (serviceId == null) {
            log.error("Intent not valid because there was a failure validating serviceId");
            return null;
        }

        /*
         * Create the PipeRequest object
         */

        CloudPipe pipe = null;

        if (pipeClassName.equals(CloudPipe.class.getCanonicalName())) {
            log.debug("Creating cloud pipe");
            pipe = new CloudPipe(pipeName, pipeUri);
        } else {
            log.error("Unknown pipe type: " + pipeClassName);
            return null;
        }

        PipeRequest pipeRequest = new PipeRequest(pipeId);
        pipeRequest.setPipe(pipe);
        pipeRequest.setRequestingService(serviceId);

        pipeRequest.setAllowedIn(allowedIn);
        pipeRequest.setAllowedOut(allowedOut);

        return pipeRequest;
    }

    private UhuServiceId serviceIdFromString(String serviceIdAsString) {
        Gson gson = new Gson();
        UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
        if (!checkUhuServiceId(serviceId)) {
            log.error("serviceId not valid: " + serviceId);
            return null;
        }

        return serviceId;
    }

    private boolean stringsValid(String serviceId, String pipeName, String uriString, String pipeClassName) {

        boolean stringsValid = true;
        String errorSuffix = "String is null or empty";

        if (!checkForString(serviceId)) {
            log.error("serviceId " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(pipeName)) {
            log.error("pipeName " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(uriString)) {
            log.error("uri " + errorSuffix);
            stringsValid = false;
        }
        if (!checkForString(pipeClassName)) {
            log.error("Pipe class " + errorSuffix);
            stringsValid = false;
        }

        return stringsValid;
    }
}
