/**
 * 
 */
package com.bezirk.sphere.messages;

import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

/**
 * @author rishabh
 *
 */
public class ShareRequest extends MulticastControlMessage {

	/**
	 * Important note and open point : If we have a owner sphere S [owned by the
	 * current device] with local services S1 and S2 and external services S3
	 * and S4[in other devices], when we get S to this function, we currently
	 * ensure that only S1 and S2 are added since this device only owns these 2
	 * services
	 * 
	 * Due to this currently only one UhuDeviceInfo is required. If we need to
	 * extend the concept, we could move towards a List of UhuDeviceInfo's
	 */
	private final UhuDeviceInfo uhuDeviceInfo;
	private final String sharerSphereId;
	private final static Discriminator discriminator = ControlMessage.Discriminator.ShareRequest;

	/**
	 * 
	 * @param shortCode
	 *            short code of the device sharing its sphere - Has to be non-null.
	 * @param uhuDeviceInfo - Has to be non-null.
	 * @param sender - Has to be non-null.
	 * @param sharerSphereId:
	 *            sphereId of the sphere which is sharing its services, required
	 *            in order to complete the process when the ShareResponse is
	 *            received. Would not be needed if all the devices with their
	 *            services are sent back to the device requesting to the share
	 *            the services. In order to add the services from the sphereId
	 *            which is sharing the services into the new sphere. Has to be non-null.
	 */
	public ShareRequest(String shortCode, UhuDeviceInfo uhuDeviceInfo, UhuServiceEndPoint sender,
			String sharerSphereId) {
		super(sender, shortCode, discriminator);
		// null checks for sender and shortCode added here because call to the
		// super method has to be the first line in a constructor.
		if(shortCode == null || uhuDeviceInfo == null|| sender == null || sharerSphereId == null) {
    		throw new IllegalArgumentException("Paramters of the constructor have to be non-null");
    	}
		this.uhuDeviceInfo = uhuDeviceInfo;
		this.sharerSphereId = sharerSphereId;
	}

	/**
	 * @return the uhuDeviceInfo
	 */
	public final UhuDeviceInfo getUhuDeviceInfo() {
		return uhuDeviceInfo;
	}

	public String getSharerSphereId() {
		return sharerSphereId;
	}

}
