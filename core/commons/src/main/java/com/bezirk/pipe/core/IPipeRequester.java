package com.bezirk.pipe.core;


public interface IPipeRequester {
	
	void requestPipe(PipeRequest request) throws PipeApprovalException;

}
