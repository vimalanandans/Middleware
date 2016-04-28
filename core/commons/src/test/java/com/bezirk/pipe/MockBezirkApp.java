package com.bezirk.pipe;

import com.bezirk.application.BezirkApp;
import com.bezirk.pipe.core.BezirkPipeAPI;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockBezirkApp implements BezirkApp {
    private static final Logger logger = LoggerFactory.getLogger(MockBezirkApp.class);

    private boolean approvePipeRequestCalled = false;

    private BezirkPipeAPI pipeAPI = null;

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

    public BezirkPipeAPI getPipeAPI() {
        return pipeAPI;
    }

    public void setPipeAPI(BezirkPipeAPI pipeAPI) {
        logger.info("Setting pipeAPI: " + pipeAPI);
        this.pipeAPI = pipeAPI;
    }
}
