package com.bezirk.pipe;

import com.bezirk.common.pipe.BezirkApp;
import com.bezirk.common.pipe.PipeRequest;
import com.bezirk.pipe.core.PipeApprovalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockBezirkApp implements BezirkApp {
    private static final Logger logger = LoggerFactory.getLogger(MockBezirkApp.class);

    private boolean approvePipeRequestCalled = false;

    private com.bezirk.common.pipe.BezirkPipeAPI pipeAPI = null;

    public MockBezirkApp() {
    }

    public void approvePipeRequest(String pipeRequestId) throws PipeApprovalException {

        logger.info("  -- Approving Pipe Request --");
        approvePipeRequestCalled = true;

        if (pipeAPI == null) {
            throw new PipeApprovalException("Bezirk setup error: pipeAPI was not set");
        }

        PipeRequest request = pipeAPI.getPipeRequest(pipeRequestId);
        if (request == null) {
            throw new PipeApprovalException("PipeRequest was not found for id: " + pipeRequestId);
        }

        logger.info("Approving request for pipe: " + request.getPipe());
        pipeAPI.pipeApproved(true, pipeRequestId, "pw", "sphere-id-42");
    }

    public boolean wasApprovePipeRequestCalled() {
        return approvePipeRequestCalled;
    }

    public com.bezirk.common.pipe.BezirkPipeAPI getPipeAPI() {
        return pipeAPI;
    }

    public void setPipeAPI(com.bezirk.common.pipe.BezirkPipeAPI pipeAPI) {
        logger.info("Setting pipeAPI: " + pipeAPI);
        this.pipeAPI = pipeAPI;
    }
}
