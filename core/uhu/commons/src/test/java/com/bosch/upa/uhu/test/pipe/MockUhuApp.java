package com.bosch.upa.uhu.test.pipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.application.IUhuApp;
import com.bosch.upa.uhu.pipe.core.IUhuPipeAPI;
import com.bosch.upa.uhu.pipe.core.PipeApprovalException;
import com.bosch.upa.uhu.pipe.core.PipeRequest;

public class MockUhuApp implements IUhuApp {
	
	private static final Logger log =  LoggerFactory.getLogger(MockUhuApp.class);
	
	private boolean approvePipeRequestCalled = false;
	
	private IUhuPipeAPI pipeAPI = null;
	
	public MockUhuApp() {
	}

	public void approvePipeRequest(String pipeRequestId) throws PipeApprovalException {
		
		log.info("  -- Aproving Pipe Request --");
		approvePipeRequestCalled = true;

		if (pipeAPI == null) {
			throw new PipeApprovalException("Uhu steup error: pipeAPI was not set");
		}
		
		PipeRequest request = pipeAPI.getPipeRequest(pipeRequestId);
		if (request == null) {
			throw new PipeApprovalException("PipeRequest was not found for id: " + pipeRequestId);
		}

		log.info("Approving request for pipe: " + request.getPipe());
		pipeAPI.pipeApproved(true, pipeRequestId, "pw", "sphere-id-42");
	}

	public boolean wasApprovePipeRequestCalled() {
		return approvePipeRequestCalled;
	}

	public IUhuPipeAPI getPipeAPI() {
		return pipeAPI;
	}

	public void setPipeAPI(IUhuPipeAPI pipeAPI) {
		log.info("Setting pipeAPI: " + pipeAPI);
		this.pipeAPI = pipeAPI;
	}
}
