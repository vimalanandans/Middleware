/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;

/**
 * @author Rishabh Gulati
 */

public interface IUhuSphereMessages {

    public boolean processCatchRequestExt(CatchRequest catchRequest);

    public boolean processCatchResponse(CatchResponse catchResponse);

    public void processShareResponse(ShareResponse shareResponse);

    public void processShareRequest(ShareRequest shareRequest);

}
