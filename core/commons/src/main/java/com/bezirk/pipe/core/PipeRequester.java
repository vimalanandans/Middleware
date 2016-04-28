package com.bezirk.pipe.core;

import com.bezirk.application.BezirkApp;
import com.bezirk.commons.BezirkCompManager;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Used by a platform to request a pipe on behalf of a zirk
 */
public class PipeRequester implements IPipeRequester, BezirkPipeAPI {
    private static final Logger logger = LoggerFactory.getLogger(PipeRequester.class);

    private final Map<String, PipeRequest> outstandingRequests = new HashMap<String, PipeRequest>();

    // Holds information about uhu state
    private PipeRegistry registry = null;

    // Interface to interact with the user-facing bezirk app
    private BezirkApp app = null;

    public PipeRequester() {
        // no-arg constructor used for flexibility (e.g. calling it as a bean)
    }

	/*
	 * Methods that implement IPipeRequest
	 */

    /**
     * Request that a pipe be added to the system
     */
    public void requestPipe(PipeRequest request) throws PipeApprovalException {

        logger.info("requestPipeAuthorization() called for: " + request.getPipe());

        Pipe pipe = request.getPipe();
        PipeRecord existingRecord = registry.lookup(pipe);

        logger.info("in requestPipeAuthorization(). registry has items: " + registry.allPipes().size());
        for (PipeRecord r : registry.allPipes()) {
            logger.info("   pipe item: " + r.getPipe());
        }
    	
    	/*
    	 * Case 1: Request for a new pipe
    	 */
        if (existingRecord == null) {
            logger.info("Requesting approval for new pipe: " + pipe);
            outstandingRequests.put(request.getId(), request);
            // Ask platform-specific app to approve the request
            app.approvePipeRequest(request.getId());

            return;
        }
    	
    	/*
    	 * Case 2: Request for existing pipe with exactly the same policies
    	 */

        boolean inMatches = policiesMatch(existingRecord.getAllowedIn(), request.getAllowedIn());
        boolean outMatches = policiesMatch(existingRecord.getAllowedOut(), request.getAllowedOut());

        if (inMatches && outMatches) {
            logger.info("Pipe exists but identical policies are being reqeusted.  No need to prompt user");
            outstandingRequests.put(request.getId(), request);

            // We don't need to ask the user to approve since this pipe/policy combination has already been approved
            this.pipeApproved(true, request.getId(), existingRecord.getPassword(), existingRecord.getSphereId());

            return;
        }

    	/*
    	 *  Case 3: Request for existing pipe with additional inbound or outbound protocols
    	 *  FOR NOW, we just send all policies being requested to the user. 
    	 *  TODO: in the future, we should only send the additional ones
    	 */

        logger.info("Pipe exists and new policies are being requested");
        outstandingRequests.put(request.getId(), request);
        // Ask platform-specific app to approve the request
        app.approvePipeRequest(request.getId());
    }

    /**
     * Two policies match if (1) they are both null or (2) policy1.equals(policy2)
     *
     * @param policy1
     * @param policy2
     * @return
     */
    private boolean policiesMatch(PipePolicy policy1, PipePolicy policy2) {

        if (policy1 == null && policy2 == null) {
            return true;
        } else {
            try {
                return policy1.equals(policy2);
            } catch (NullPointerException e) {
                logger.error("Error in evaluating pipe policy " + e);

                return false;
            }
        }
    }
    
    /*
     * Methods that implement BezirkPipeAPI
     */

    /**
     * Called to notify the PipeManager whether the user has granted access to the pipe and
     * which policies are allowed to pass in and out of the pipe.
     *
     * @param approved     True if the pipe was granted
     * @param pipe        The Pipe originally requested by the zirk
     * @param allowedIn   This PipePolicy contains the collection of Protocols allowed into the local sphere
     * @param allowedOut  This PipePolicy contains the collection of Protocols allowed to pass out of the local sphere
     * @param sphereId    The sphere the pipe has been added to
     * @param uhuListener The bezirk zirk to notify of the status of the pipe request
     */
    public void pipeApproved(boolean approved, String pipeRequestId, String pipePassword, String sphereId) throws PipeApprovalException {
        PipeRequest request = outstandingRequests.get(pipeRequestId);

        Pipe pipe = request.getPipe();
        BezirkPipePolicy allowedIn = PipePolicyUtility.policyInMap.get(request.getId());
        BezirkPipePolicy allowedOut = PipePolicyUtility.policyOutMap.get(request.getId());

        // For now, we are ignoring the approved boolean and using the pipePolicy.isApproved() to indicate approval
        if (registry.isRegistered(pipe)) {
            logger.debug("Updating pipe registration");
            registry.update(pipe, allowedIn, allowedOut, sphereId, pipePassword);
        } else {
            logger.debug("Pipe is not yet registered, so registering it now");
            registry.add(pipe, allowedIn, allowedOut, sphereId, pipePassword);
        }

        //CLEAR PENDING
        outstandingRequests.remove(pipeRequestId);

        //INVOKE CALL BACK
        final PipeRequestIncomingMessage pipeMsg = new PipeRequestIncomingMessage(pipe,
                pipeRequestId, allowedIn, allowedOut, (BezirkZirkId) request.getRequestingService());
        BezirkCompManager.getplatformSpecificCallback().onPipeApprovedMessage(pipeMsg);
        logger.info("pipe approved: " + approved);

        // Clear request
        PipePolicyUtility.removeId(request.getId());
    }

    public PipeRequest getPipeRequest(String requestId) {
        return outstandingRequests.get(requestId);
    }
    
    /*
     * Getters and setters
     */

    public void setRegistry(PipeRegistry registry) {
        this.registry = registry;
    }

    public void setApp(BezirkApp app) {
        this.app = app;
    }


}
