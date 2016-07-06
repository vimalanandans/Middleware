package com.bezirk.rest;

import com.bezirk.control.messages.Ledger;

/**
 * Rest callback interface.. this will be called by the RestHandler and response will be served at Handler implementation method.
 *
 * @author PIK6KOR
 */
public interface BezirkRestCallBack {

    public void callBackForResponse(Ledger ledger);

}
