package com.bezirk.pipe;


import com.bezirk.pipe.core.PipeApprovalException;

public interface IPipeRequester {

    void requestPipe(PipeRequest request) throws PipeApprovalException;

}
