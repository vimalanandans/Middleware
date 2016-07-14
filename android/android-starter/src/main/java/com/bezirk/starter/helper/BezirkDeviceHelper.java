package com.bezirk.starter.helper;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.bezirk.BezirkCompManager;
import com.bezirk.device.Device;
import com.bezirk.device.DeviceType;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.starter.MainService;
import com.bezirk.starter.BezirkPreferences;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Configures the bezirk device by setting the location and preferences.
 */
public final class BezirkDeviceHelper {
    private static final Logger logger = LoggerFactory.getLogger(BezirkDeviceHelper.class);

    Device setBezirkDevice(final BezirkPreferences preferences, final MainService service) {
        Device bezirkDevice = initDevice(preferences, service);

        if (ValidatorUtility.isObjectNotNull(bezirkDevice)) {

            BezirkCompManager.setUpaDevice(bezirkDevice);

            //Load Location
            Location deviceLocation = loadLocation(preferences);
            bezirkDevice.setDeviceLocation(deviceLocation);

        } else {
            logger.error(" ### unable to init device");
        }

        return bezirkDevice;
    }

    private Location loadLocation(final BezirkPreferences preferences) {
        String location = preferences.getString("indoorlocation", null);
        return new Location(location);
    }

    /**
     * create and initialise the sphere
     * TODO: Move this code to modular place
     */
    private Device initDevice(final BezirkPreferences preferences, final MainService service) {

        String deviceId = preferences.getString(BezirkPreferences.DEVICE_ID_TAG_PREFERENCE, null);

        if (null == deviceId || deviceId.isEmpty()) {
            // http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
            final TelephonyManager telephonyManager = (TelephonyManager) service.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice = telephonyManager.getDeviceId();
            String tmSerial = telephonyManager.getSimSerialNumber();
            String androidId = android.provider.Settings.Secure.getString(service.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid;
            if (telephonyManager.getDeviceId() == null || telephonyManager.getSimSerialNumber() == null) // any of these are not valid
            {
                deviceUuid = new UUID(androidId.hashCode(), androidId.hashCode());
            } else {
                deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            }
            deviceId = deviceUuid.toString();
            logger.info("DEVICE ID not persisted, generating from the android id > " + androidId + " device uuid id > " + deviceId);

            // save to persistence
            preferences.putString(BezirkPreferences.DEVICE_ID_TAG_PREFERENCE, deviceId);

        } else {
            logger.info(" DEVICE ID from storage > " + deviceId);
        }

        String deviceType = preferences.getString(BezirkPreferences.DEVICE_TYPE_TAG_PREFERENCE, null);

        if (deviceType == null) {
            logger.info("device type is not initialized. setting to default");

            deviceType = DeviceType.BEZIRK_DEVICE_TYPE_SMARTPHONE;
        }


        Device bezirkDevice = new Device();

        bezirkDevice.initDevice(deviceId, deviceType);

        String deviceName = preferences.getString(BezirkPreferences.DEVICE_NAME_TAG_PREFERENCE, null);

        if (ValidatorUtility.checkForString(deviceName)) {
            logger.info("device type is already initialized to " + deviceName);
            bezirkDevice.setDeviceName(deviceName);
        } else {
            logger.info("device type is not initialized. setting to default, type based name ");
        }

        return bezirkDevice;
    }
}