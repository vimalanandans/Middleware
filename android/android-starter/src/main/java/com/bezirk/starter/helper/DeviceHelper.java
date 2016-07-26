//package com.bezirk.starter.helper;
//
//import android.content.Context;
//import android.telephony.TelephonyManager;
//
//import com.bezirk.device.Device;
//import com.bezirk.device.DeviceType;
//import com.bezirk.middleware.addressing.Location;
//import com.bezirk.starter.MainService;
//import com.bezirk.starter.MainStackPreferences;
//import com.bezirk.util.ValidatorUtility;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.UUID;
//
///**
// * Configures the bezirk device by setting the location and preferences.
// */
//public final class DeviceHelper {
//    private static final Logger logger = LoggerFactory.getLogger(DeviceHelper.class);
//
//    Device setBezirkDevice(final MainStackPreferences preferences, final MainService service) {
//        Device bezirkDevice = initDevice(preferences, service);
//
//        if (ValidatorUtility.isObjectNotNull(bezirkDevice)) {
//
//
//            //Load Location
//            Location deviceLocation = loadLocation(preferences);
//            bezirkDevice.setDeviceLocation(deviceLocation);
//
//        } else {
//            logger.error(" ### unable to init device");
//        }
//
//        return bezirkDevice;
//    }
//
//    private Location loadLocation(final MainStackPreferences preferences) {
//        String location = preferences.getString("indoorlocation", null);
//        return new Location(location);
//    }
//
//    /**
//     * create and initialise the sphere
//     * TODO: Move this code to modular place
//     */
//    private Device initDevice(final MainStackPreferences preferences, final MainService service) {
//
//        String deviceId = preferences.getString(MainStackPreferences.DEVICE_ID_TAG_PREFERENCE, null);
//
//        if (null == deviceId || deviceId.isEmpty()) {
//
//            String tmDevice = null;
//            String tmSerial = null;
//            String androidId = android.provider.Settings.Secure.getString(service.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//            try {
//                // http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
//                final TelephonyManager telephonyManager = (TelephonyManager) service.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//
//                tmDevice = telephonyManager.getDeviceId();
//                tmSerial = telephonyManager.getSimSerialNumber();
//            }
//            catch (Exception e)
//            {
//                logger.debug("permission error to get the device id "+e);
//            }
//
//            UUID deviceUuid;
//            if (tmDevice == null || tmSerial == null) // any of these are not valid
//            {
//                deviceUuid = new UUID(androidId.hashCode(), androidId.hashCode());
//            } else {
//                deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//            }
//            deviceId = deviceUuid.toString();
//            logger.info("DEVICE ID not persisted, generating from the android id > " + androidId + " device uuid id > " + deviceId);
//
//            // save to persistence
//            preferences.putString(MainStackPreferences.DEVICE_ID_TAG_PREFERENCE, deviceId);
//
//        } else {
//            logger.info(" DEVICE ID from storage > " + deviceId);
//        }
//
//        String deviceType = preferences.getString(MainStackPreferences.DEVICE_TYPE_TAG_PREFERENCE, null);
//
//        if (deviceType == null) {
//            logger.info("device type is not initialized. setting to default");
//
//            deviceType = DeviceType.BEZIRK_DEVICE_TYPE_SMARTPHONE;
//        }
//
//
//        Device bezirkDevice = new Device();
//
//        bezirkDevice.initDevice(deviceId, deviceType);
//
//        String deviceName = preferences.getString(MainStackPreferences.DEVICE_NAME_TAG_PREFERENCE, null);
//
//        if (ValidatorUtility.checkForString(deviceName)) {
//            logger.info("device type is already initialized to " + deviceName);
//            bezirkDevice.setDeviceName(deviceName);
//        } else {
//            logger.info("device type is not initialized. setting to default, type based name ");
//        }
//
//        return bezirkDevice;
//    }
//}