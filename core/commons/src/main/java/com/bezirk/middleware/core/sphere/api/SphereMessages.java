/**
 *
 */
package com.bezirk.middleware.core.sphere.api;

import com.bezirk.middleware.core.sphere.messages.CatchRequest;
import com.bezirk.middleware.core.sphere.messages.CatchResponse;
import com.bezirk.middleware.core.sphere.messages.ShareRequest;
import com.bezirk.middleware.core.sphere.messages.ShareResponse;

/**
 * @author Rishabh Gulati
 */

public interface SphereMessages {

    boolean processCatchRequestExt(CatchRequest catchRequest);

    boolean processCatchResponse(CatchResponse catchResponse);

    void processShareResponse(ShareResponse shareResponse);

    void processShareRequest(ShareRequest shareRequest);

}
