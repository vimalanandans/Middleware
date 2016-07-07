package com.bezirk.common.pipe;


import com.bezirk.pipe.core.PipeApprovalException;

public interface IPipeRequester {

    void requestPipe(PipeRequest request) throws PipeApprovalException;

}
