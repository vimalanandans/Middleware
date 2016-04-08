package com.bosch.upa.uhu.pipe.core;

/**
 * Platform-independent interface offered by UhU to UhU management UI
 */
public interface IUhuPipeAPI {
	
	/**
	 * Notify UhU that the request for the specified pipe was approved
	 */
	void pipeApproved(boolean approved, String pipeRequestId, 
			String pipePassword, String sphereId) throws PipeApprovalException;

	PipeRequest getPipeRequest(String requestId);
}
