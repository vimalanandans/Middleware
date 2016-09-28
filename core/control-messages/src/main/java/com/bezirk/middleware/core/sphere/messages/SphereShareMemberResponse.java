//package com.bosch.upa.uhu.sphere.messages;
//
//import BezirkDeviceInfo;
//import ControlMessage;
//import MulticastControlMessage;
//import BezirkZirkEndPoint;
//
//
///**
// * Created by GUR1PI on 7/28/2014.
// */
//public class SphereShareMemberResponse extends MulticastControlMessage{   
//	/**
//	 * Important note and open point : If we have a owner sphere S
//	 * [owned by the current device] with local services S1 and S2
//	 * and external services S3 and S4[in other devices], when we
//	 * get S to this function, we currently ensure that only S1 and
//	 * S2 are added since this device only owns these 2 services
//	 * 
//	 * Due to this currently only one BezirkDeviceInfo is required. If we need to extend the concept, we could move towards a List of BezirkDeviceInfo's
//	 */
//	private final BezirkDeviceInfo uhuDeviceInfo;
//	private static final Discriminator discriminator = ControlMessage.Discriminator.SphereShareMemberResponse;
//
//    public SphereShareMemberResponse(String sphereID, BezirkDeviceInfo uhuDeviceInfo, BezirkZirkEndPoint sender) {
//        super(sender, sphereID, discriminator);        
//        this.uhuDeviceInfo = uhuDeviceInfo;
//    }
//
//	/**
//	 * @return the uhuDeviceInfo
//	 */
//	public final BezirkDeviceInfo getBezirkDeviceInfo() {
//		return uhuDeviceInfo;
//	}
//
//        
//    
//}
