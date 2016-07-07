package com.bezirk.common.pipe;

import com.bezirk.pipe.core.PipeApprovalException;

/**
 * Platform-independent interface offered by Bezirk to Bezirk management UI
 */
public interface BezirkPipeAPI {

    /**
     * Notify Bezirk that the request for the specified pipe was approved
     */
    void pipeApproved(boolean approved, String pipeRequestId,
                      String pipePassword, String sphereId) throws PipeApprovalException;

    PipeRequest getPipeRequest(String requestId);
}
