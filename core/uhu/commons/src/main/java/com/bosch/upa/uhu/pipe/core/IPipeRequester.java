package com.bosch.upa.uhu.pipe.core;


public interface IPipeRequester {
	
	void requestPipe(PipeRequest request) throws PipeApprovalException;

}
