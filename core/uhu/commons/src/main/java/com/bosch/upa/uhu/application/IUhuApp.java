package com.bosch.upa.uhu.application;

import com.bosch.upa.uhu.pipe.core.PipeApprovalException;

/**
 * Platform independent interface offered by a uhu sphere management application to uhu
 */
public interface IUhuApp {
	
	/**
	 * A request to the Uhu application (e.g. a UI) to approve a service's
	 * request for the specified pipe
	 * @param pipeRequestId
	 */
	void approvePipeRequest(String pipeRequestId) throws PipeApprovalException;
}
