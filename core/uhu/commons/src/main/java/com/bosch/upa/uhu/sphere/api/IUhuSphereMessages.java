/**
 * 
 */
package com.bosch.upa.uhu.sphere.api;

import com.bosch.upa.uhu.sphere.messages.CatchRequest;
import com.bosch.upa.uhu.sphere.messages.CatchResponse;
import com.bosch.upa.uhu.sphere.messages.ShareRequest;
import com.bosch.upa.uhu.sphere.messages.ShareResponse;

/**
 * @author Rishabh Gulati
 *
 */

public interface IUhuSphereMessages {

    public boolean processCatchRequestExt(CatchRequest catchRequest);

    public boolean processCatchResponse(CatchResponse catchResponse);

    public void processShareResponse(ShareResponse shareResponse);

    public void processShareRequest(ShareRequest shareRequest);

}
