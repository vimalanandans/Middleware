package com.bezirk.pipe;

import com.bezirk.pipe.core.PipeApprovalException;

/**
 * Platform independent interface offered by a bezirk sphere management application to bezirk
 */
public interface BezirkApp {
    /**
     * A request to the Bezirk application (e.g. a UI) to approve a zirk's
     * request for the specified pipe
     *
     * @param pipeRequestId
     */
    void approvePipeRequest(String pipeRequestId) throws PipeApprovalException;
}
