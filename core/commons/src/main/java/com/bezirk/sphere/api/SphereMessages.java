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

public interface SphereMessages {

    boolean processCatchRequestExt(CatchRequest catchRequest);

    boolean processCatchResponse(CatchResponse catchResponse);

    void processShareResponse(ShareResponse shareResponse);

    void processShareRequest(ShareRequest shareRequest);

}
