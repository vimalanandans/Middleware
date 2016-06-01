package com.bezirk.sphere.impl;

import com.bezirk.commons.BezirkId;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.SphereVitals;
import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.sphere.api.BezirkSphereListener;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.api.CryptoInternals;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.ISphereUtils;
import com.bezirk.sphere.api.BezirkDevMode.Mode;
import com.bezirk.sphere.api.BezirkSphereListener.SphereCreateStatus;
import com.bezirk.sphere.api.BezirkSphereListener.Status;
import com.bezirk.sphere.security.SphereKeys;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rishabh
 */
public class SphereRegistryWrapper {
    private static final Logger logger = LoggerFactory.getLogger(SphereRegistryWrapper.class);

    public static final String DEVELOPMENT_DEVICE_ID = "DevDeviceId";
    private static final String DEFAULT_SPHERE_NAME = "Default sphere";
    private static final String DEVELOPMENT_DEVICE_NAME = "DevDeviceName";
    private static final String DEVELOPMENT_DEVICE_TYPE = "DevDeviceType";

    private SphereRegistry registry = null;
    private SpherePersistence spherePersistence = null;
    private BezirkDeviceInterface upaDevice;
    private BezirkSphereListener sphereListener;
    private ISphereConfig sphereConfig;
    private CryptoInternals crypto;
    private DevelopmentSphere developmentSphere;

    /*
     * used for working around existing api for addLocalServicesToSphere. When
     * the addLocalServicesToSphere is used for copying services from default
     * sphere to dev sphere, this flag is set to true. After copying the flag is
     * reset to false. This is done so that the deviceId for dev sphere
     * configuration is used instead of the current deviceId
     */
    private boolean deviceIdControl = false;

    /**
     * Constructor to initialize SphereRegistry object and SpherePersistence
     * interface object
     *
     * @param registry          - SphereRegistry object. Should not be null.
     * @param spherePersistence - SpherePersistence interface object. Should not be null.
     */
    public SphereRegistryWrapper(SphereRegistry registry, SpherePersistence spherePersistence,
                                 BezirkDeviceInterface upaDevice, CryptoInternals crypto, BezirkSphereListener sphereListener,
                                 ISphereConfig sphereConfig) {
        if (null == registry || null == spherePersistence || null == upaDevice || null == crypto
                || null == sphereConfig) {
            throw new IllegalArgumentException("Parameters should be not null");
        }
        this.registry = registry;
        this.spherePersistence = spherePersistence;
        this.upaDevice = upaDevice;
        this.crypto = crypto;
        this.sphereConfig = sphereConfig;
        // null check not required, listener can be null, should be checked
        // before using in methods
        this.sphereListener = sphereListener;
        this.developmentSphere = new DevelopmentSphere();
    }

    /**********************************************
     * CONSTRUCTION & INITIALIZATION
     **************************************************/

    /**
     * Handle initialization of development sphere and default sphere
     */
    public void init() {
        initDefaultSphere();
        if (sphereConfig.getMode() == Mode.ON) {
            logger.info("Development Mode Configured as ON");
            developmentSphere.create();
            // add existing services to development sphere
            deviceIdControl = true;
            addLocalServicesToSphere(sphereConfig.getSphereId());
            deviceIdControl = false;
        }

        persist();
    }

    /**
     * Get a sphere from registry.
     *
     * @param sphereId whose sphere object is to be retrieved.
     * @return sphere associated with the sphereId if it is present in the
     * spheres map, null otherwise
     */
    public Sphere getSphere(String sphereId) {
        return registry.spheres.get(sphereId);
    }

    /********************************************** SPHERE **************************************************/

    /**
     * This method provides the sphereId which should be used for registration
     * of all services. In dev mode, it would be into the development sphere, in
     * Prod mode it will be into the default sphere. In dev mode, all services
     * across all devices[all devices in dev mode with same devSphere
     * properties] register to the same development sphere.
     *
     * @return
     */
    // private String getSphereIdForRegistration() {
    // if (sphereConfig.getMode() == Mode.DEV) {
    // return sphereConfig.getSphereId();
    // } else {
    // return getDefaultSphereId();
    // }
    // }

    // /**
    // * Creates the development sphere. Should be used only in {@link Mode#ON}
    // *
    // */
    // private void initDevelopmentSphere() {
    // addDevelopmentDevice();
    // if (!containsSphere(sphereConfig.getSphereId())) {
    // // add keys to registry
    // SphereKeys keys = new SphereKeys(sphereConfig.getSphereKey(), null,
    // null);
    // crypto.addMemberKeys(sphereConfig.getSphereId(), keys);
    //
    // // create the sphere
    // MemberSphere sphere = new MemberSphere(sphereConfig.getSphereName(),
    // "Development",
    // new HashSet<String>(Arrays.asList(DEVELOPMENT_DEVICE_ID)),
    // new LinkedHashMap<String, ArrayList<BezirkZirkId>>(), false);
    // addSphere(sphereConfig.getSphereId(), sphere);
    // // persist();
    // }
    // }
    //
    // /**
    // * Add the device to which all services get added across devices
    // */
    // private void addDevelopmentDevice() {
    // if (getDevice(DEVELOPMENT_DEVICE_ID) == null) {
    // addDevice(DEVELOPMENT_DEVICE_ID, new
    // DeviceInformation(DEVELOPMENT_DEVICE_NAME, DEVELOPMENT_DEVICE_TYPE));
    // }
    // }

    /**
     * Get the BezirkSphereInfo object for the sphere Id passed. If the sphere is a
     * temporary sphere, then it is skipped.
     *
     * @param sphereId whose BezirkSphereInfo object is to be retrieved.
     * @return : sphere Info if found else null.<br>
     * null if its a temporary sphere.
     */
    public BezirkSphereInfo getSphereInfo(String sphereId) {
        BezirkSphereInfo sphereInfo = null;

        if (containsSphere(sphereId)) {
            Sphere sphere = getSphere(sphereId);
            // check if the sphere is a temporary member sphere
            if (!(sphere instanceof MemberSphere && ((MemberSphere) sphere).isTemporarySphere())) {

                Iterable<BezirkDeviceInfo> devicesIterable = getBezirkDeviceInfo(sphere.getDeviceServices(),
                        (HashSet<String>) sphere.getOwnerDevices());

                ArrayList<BezirkDeviceInfo> devices = (devicesIterable != null)
                        ? (ArrayList<BezirkDeviceInfo>) devicesIterable : null;

                sphereInfo = new BezirkSphereInfo(sphereId, sphere.getSphereName(), sphere.getSphereType(), devices, null);

                if (sphere instanceof OwnerSphere) {
                    sphereInfo.setThisDeviceOwnsSphere(true);
                } else {
                    sphereInfo.setThisDeviceOwnsSphere(false);
                }
                logger.info("BezirkSphereInfo returned\n" + sphereInfo.toString());
            } else {
                logger.debug("Temporary sphere:" + sphereId + " skipped");
            }
        }
        return sphereInfo;
    }

    /**
     * Get BezirkSphereInfo objects for all the spheres stored in the registry.
     *
     * @return - List of BezirkSphereInfo objects for each sphere in the registry.
     * <br>
     * - null, if there are no spheres in the registry.
     */
    public Iterable<BezirkSphereInfo> getSpheres() {
        List<BezirkSphereInfo> spheres = null;
        Set<String> sphereIds = getSphereIds();

        if (sphereIds != null && !sphereIds.isEmpty()) {
            spheres = new ArrayList<BezirkSphereInfo>();
            for (String sphereId : sphereIds) {
                BezirkSphereInfo spInfo = getSphereInfo(sphereId);
                if (spInfo != null) {
                    spheres.add(spInfo);
                }
            }
        }
        return spheres;
    }

    /**
     * Check if a sphere with passed sphereId exists in registry.
     *
     * @param sphereId whose existence in the registry has to be checked.
     * @return true if sphereId exists in the spheres map, false otherwise
     */
    public boolean containsSphere(String sphereId) {
        return registry.spheres.containsKey(sphereId);
    }

    /**
     * Get list of all sphereIds from registry.
     *
     * @return The sphereIds stored in the spheres map. <br>
     * null if sphereIds don't exist in the registry.
     */
    public Set<String> getSphereIds() {
        return registry.spheres.keySet();
    }

    /**
     * Add a sphere to the registry.
     *
     * @param sphereId which has to be added to registry. It has to be non-null
     * @param sphere   - sphere object which has to be added to the registry. It has
     *                 to be non-null.
     * @return true if the sphere was added in spheres map, false otherwise
     */
    public boolean addSphere(String sphereId, Sphere sphere) {
        if (null == sphere || null == sphereId) {
            logger.error("sphere object or sphere Id is null. Adding sphere with sphere id " + sphereId + " failed.");
            return false;
        }
        registry.spheres.put(sphereId, sphere);
        logger.info("sphere " + sphere.getSphereName() + " with sphereId " + sphereId
                + " added successfully to spheres map");
        persist();
        return true;
    }

    /**
     * Delete a sphere from the registry. <br>
     * Includes cleaning sphere membership such that no zirk has this
     * sphereId as a reference. <br>
     * Also includes cleaning the sphereKeys associated with this sphere.
     *
     * @param sphereId
     * @return true if the sphere was deleted successfully
     */
    public boolean deleteSphere(String sphereId) {
        if (getDefaultSphereId().equalsIgnoreCase(sphereId)) {
            logger.info("Deleting default sphere is not allowed");
            return false;
        }
        // clean sphereMembership map
        cleanServiceMemberShip(sphereId);

        // clean sphereKey map
        registry.sphereKeyMap.remove(sphereId);

        // clean spheres map
        registry.spheres.remove(sphereId);

        return true;
    }

    /**
     * Removes reference of passed sphereId from all services
     *
     * @param sphereId
     */
    private void cleanServiceMemberShip(String sphereId) {
        for (Zirk zirk : registry.sphereMembership.values()) {
            Set<String> spheres = zirk.getSphereSet();
            spheres.remove(sphereId);
            logger.debug("Deleted membership of " + zirk.getZirkName() + " for sphere " + sphereId);
        }
    }

    /**
     * Check if sphereId is present in sphereKeyMap/sphereHashKeyMap. <br>
     * Registry should not be empty.
     *
     * @param sphereId whose existence in the sphere maps has to be checked.
     * @return true if sphereId exists in the sphereKeyMap or sphereHashKeyMap
     * false otherwise
     */
    public boolean existsSphereIdInKeyMaps(String sphereId) {
        return registry.isKeymapExist(sphereId);
    }

    /**
     * Get the 'default sphere id'
     * <p>
     * NOTE: Current implementation assumes use of LinkedHashMap for 'spheres'
     * map. The default sphere is the first entry in this map. It is required
     * that this entry is never deleted.
     * </p>
     *
     * @return sphereId of the default-sphere null otherwise
     */
    public String getDefaultSphereId() {
        String defaultSphereId = null;

        if (registry.spheres.keySet().iterator().hasNext()) {
            defaultSphereId = registry.spheres.keySet().iterator().next();
        }

        logger.debug("Default sphereId : " + defaultSphereId);
        return defaultSphereId;
        /*
         * Other ways :
         *
         * 1) use combination of DEFAULT_SPHERE_NAME and upadevice.getDeviceId
         * to directly get the default sphereId
         *
         * 2) iterate through the keyset of the spheres map to find the default
         * sphereId
         *
         * 3) if using the listener approach with BezirkSphere, we could store the
         * default sphereId on creation of the default sphere
         */
    }

    /**
     * Create the default sphere. Checks first if the default sphere Id exists.
     * If it does not, then it creates a default sphere.<br>
     * If the default sphere does exist and the name is different, then just the
     * name is updated.
     *
     * @param defaultSphereName - The name to be assigned to the default sphere. It has to be
     *                          non-null
     * @return - true, if sphere was created or if the name is updated. False,
     * otherwise.
     */
    public boolean createDefaultSphere(String defaultSphereName) {
        String defaultSphereId = getDefaultSphereId();
        if (null == defaultSphereId) {
            createSphere(defaultSphereName, BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT, null);
            return true;
        } else {
            // default sphere id exists. check the name of the sphere and update
            // the same if any change
            Sphere defaultSphere = getSphere(defaultSphereId);
            if (!defaultSphere.getSphereName().equals(defaultSphereName)) {
                logger.info("Change in default sphere name from > " + defaultSphere.getSphereName() + " to >"
                        + defaultSphereName);
                defaultSphere.setSphereName(defaultSphereName);
            }
        }
        return true;
    }

    /**
     * Creates a sphere with the name and type as passed in the parameters. If
     * there already exists the same sphereId, then new sphere is NOT created.
     *
     * @param sphereName        - Name to be assigned to the new sphere.
     * @param sphereType        - Type of sphere to be created
     * @param bezirkSphereListener
     * @return - SphereId if sphere was created successfully or if the sphereId
     * existed already, null otherwise.
     */
    public String createSphere(String sphereName, String sphereType, BezirkSphereListener bezirkSphereListener) {
        String name = (null == sphereName) ? DEFAULT_SPHERE_NAME : sphereName;
        String type = (null == sphereType) ? BezirkSphereType.BEZIRK_SPHERE_TYPE_OTHER : sphereType;
        SphereCreateStatus status;
        String sphereId = null;

        if (name.length() != 0) {
            // create a sphereId
            sphereId = name + upaDevice.getDeviceId();

            // check if sphereId already exists
            if (containsSphere(sphereId)) {
                status = SphereCreateStatus.INFO_SPHERE_ALREADY_EXISTS;
            } else {
                // generate keys for sphereId
                if (crypto.generateKeys(sphereId, true)) {
                    // create a owner sphere
                    OwnerSphere sphere = new OwnerSphere(name, upaDevice.getDeviceId(), type);
                    // add sphere to registry
                    addSphere(sphereId, sphere);
                    // persist
                    persist();
                    status = SphereCreateStatus.SUCCESS;
                } else {
                    status = SphereCreateStatus.INTERNAL_ERROR_SPHERE_NOT_CREATED;
                }
            }
        } else {
            status = SphereCreateStatus.SPHERE_NAME_OR_CALLBACK_ERROR;
        }

        logger.info("Create sphere, sphereId : {}, status : {}", sphereId, status);
        if (bezirkSphereListener != null) {
            bezirkSphereListener.onSphereCreateStatus(sphereId, status);
        } else {
            logger.debug("Create sphere, no listener registered");
        }
        return sphereId;

    }

    /**
     * Initialize the default sphere and persist the information
     */
    private void initDefaultSphere() {
        String defaultSphereName;
        // check if defaultSphereName is not defined
        if (sphereConfig.getDefaultSphereName().equalsIgnoreCase("")) {
            defaultSphereName = generateSphereName();
            // set the value in the properties file
            sphereConfig.setDefaultSphereName(defaultSphereName);
        } else {
            // in case it is defined in the properties file, use that
            defaultSphereName = sphereConfig.getDefaultSphereName();
        }

        // create the default sphere & persist
        createDefaultSphere(defaultSphereName);
        persist();
    }

    /**
     * Generate sphere name using information from {@link BezirkDeviceInterface}
     *
     * @return - generated sphere name.
     */
    private String generateSphereName() {
        String name = "";
        if (upaDevice != null) {
            String deviceName = upaDevice.getDeviceName();
            String deviceId = upaDevice.getDeviceId();
            String deviceIdSubString = deviceId.substring(deviceId.length() - 5, deviceId.length());
            if (deviceName != null) {
                if (deviceName.contains(deviceIdSubString)) {
                    name = "sphere-" + deviceName;
                } else {
                    name = "sphere-" + deviceName + "-" + deviceIdSubString;
                }
            }
        }
        logger.info("Default sphere name used: " + name);
        return name;
    }

    /**
     * Check if zirkId is present in the sphereMembership map
     *
     * @param serviceId whose existence in the registry has to be checked. It has to
     *                  be valid and non-null. No validation done in this method.
     * @return true if the zirkId is present in the sphereMembership map
     * false otherwise
     */
    public boolean containsService(String serviceId) {
        return registry.sphereMembership.containsKey(serviceId);
    }

    /********************************************** SERVICE **************************************************/

    /**
     * Get a zirk from registry.
     *
     * @param serviceId of the required Zirk object.
     * @return zirk associated with the zirkId if it is present in the
     * sphereMembership map null otherwise
     */
    public Zirk getService(String serviceId) {
        return registry.sphereMembership.get(serviceId);
    }

    /**
     * Add a zirk to the registry and persist the information.
     *
     * @param serviceId of the zirk which has to be added to the sphere membership
     *                  map.
     * @param zirk   - Zirk object which has to be added to the sphere
     *                  membership map.
     * @return true if the zirk was added to the sphereMembership map false
     * otherwise
     */
    public boolean addService(String serviceId, Zirk zirk) {
        if (zirk == null || serviceId == null) {
            logger.error(
                    "Zirk object or Zirk Id is null. Adding Zirk with Zirk id " + serviceId + " failed.");
            return false;
        }

        registry.sphereMembership.put(serviceId, zirk);
        logger.info("Zirk " + zirk.getZirkName() + " with zirkId " + serviceId
                + " added successfully to sphereMembership map");
        persist();
        return true;
    }

    /**
     * Checks if the zirk is a local zirk
     * <p>
     * NOTE: Another way of checking if the zirk is local is by checking what
     * instance of Zirk is stored in the sphereMembership i.e. OwnerZirk
     * or MemberZirk
     * </p>
     * TODO: Has to be moved to Zirk.java
     *
     * @param deviceId : zirk owner deviceId
     * @return <code>true</code> if the zirk is local for this device, <code>false</code> otherwise
     * or if deviceId is passed as <code>null</code>
     */
    public boolean isServiceLocal(String deviceId) {
        return deviceId != null && deviceId.equals(upaDevice.getDeviceId());
    }

    /**
     * Get BezirkZirkInfo objects for each zirk in the passed list of zirk
     * IDs.
     *
     * @param serviceIds - list of zirk IDs whose respective BezirkZirkInfo objects
     *                   are required.
     * @return - List of BezirkZirkInfo objects.<br>
     * - null, if no services passed.<br>
     */
    public Iterable<BezirkZirkInfo> getBezirkServiceInfo(Iterable<BezirkZirkId> serviceIds) {
        if (serviceIds == null) {
            logger.debug("No Services passed");
            return null;
        }

        List<BezirkZirkInfo> serviceInfoList = new ArrayList<BezirkZirkInfo>();
        for (BezirkZirkId servId : serviceIds) {

            if (containsService(servId.getBezirkZirkId())) {

                Zirk zirk = getService(servId.getBezirkZirkId());

                if (zirk != null) {
                    BezirkZirkInfo serviceInfo = new BezirkZirkInfo(servId.getBezirkZirkId(), zirk.getZirkName(),
                            isServiceLocal(zirk.getOwnerDeviceId()) ? "Owner" : "Member", true, true);
                    serviceInfoList.add(serviceInfo);
                } else {
                    logger.error("Zirk information for zirk : " + servId + " is null");
                }
            } else {
                logger.error("Zirk information for zirk : " + servId + " not found in registry");
            }
        }
        return serviceInfoList;
    }

    /**
     * Get set of sphere Ids for the passed zirkId. sphere set for the given
     * zirkId has to be non-null.
     *
     * @param serviceId whose sphere set is required.
     * @return - Set of sphereIds<br>
     * - Null if zirk does not exist<br>
     */
    public Iterable<String> getSphereMembership(BezirkZirkId serviceId) {

        Set<String> spheres = null;

        if (containsService(serviceId.getBezirkZirkId())) {
            spheres = new HashSet<String>(getService(serviceId.getBezirkZirkId()).getSphereSet());
        }

        return spheres;
    }

    /**
     * Registers the zirk with BezirkSphere's. In case the zirk is already
     * registered, call to this method updates the name of the zirk to
     * serviceName passed
     *
     * @param serviceId   BezirkZirkId to be registered - has to be non-null
     * @param serviceName Name to be associated with the zirk - has to be non-null
     * @return <code>true</code> if zirk was added successfully
     */
    public boolean registerService(BezirkZirkId serviceId, String serviceName) {
        List<String> spheresForRegistration = new ArrayList<>();
        // these deviceIds correspond to the respective spheresForRegistration
        // for dev sphere, deviceId is a globally used deviceId
        List<String> deviceIdsForRegistration = new ArrayList<>();
        spheresForRegistration.add(getDefaultSphereId());
        deviceIdsForRegistration.add(upaDevice.getDeviceId());

        // In dev mode, register services to dev sphere as well
        if (sphereConfig.getMode() == Mode.ON) {
            spheresForRegistration.add(sphereConfig.getSphereId());
            deviceIdsForRegistration.add(DEVELOPMENT_DEVICE_ID);
        }

        return registerService(serviceId, serviceName, spheresForRegistration, deviceIdsForRegistration);
    }

    private boolean registerService(BezirkZirkId serviceId, String serviceName, List<String> sphereIds,
                                    List<String> deviceIds) {
        int count = 0;
        for (String sphereId : sphereIds) {
            // add the zirk to the sphere with default device
            if (getSphere(sphereId).addService(deviceIds.get(sphereIds.indexOf(sphereId)),
                    serviceId.getBezirkZirkId())) {
                // if the zirk was added successfully to the sphere
                // add this information to sphereMembership map
                if (addMembership(serviceId, sphereId, upaDevice.getDeviceId(), serviceName)) {
                    logger.debug("Zirk " + serviceId + " registered to " + sphereId + "successfully");
                    persist();
                    count++;
                } else {
                    logger.debug("register zirk failed and adding membership " + serviceId);
                }
            } else {
                logger.debug("Registration failed, Zirk " + serviceId);
            }
        }

        return count == sphereIds.size();
    }

    /**
     * Adds the zirk membership. In case the zirk is already registered,
     * call to this method updates the name of the zirk to serviceName passed
     * and also adds the sphereId to the set of spheres, the zirk is a part
     * off
     *
     * @param serviceId     which has to be added to the sphere membership map. It has to
     *                      be non-null
     * @param sphereId      which has to be added to the sphere set of the zirk
     * @param ownerDeviceId - has to be non-null
     * @param serviceName   - Name of the zirk to be added to sphere membership map. It
     *                      has to be non-null
     * @return - True if zirk membership was added successfully, False
     * otherwise.
     */
    public boolean addMembership(BezirkZirkId serviceId, String sphereId, String ownerDeviceId, String serviceName) {
        if (containsSphere(sphereId)) {
            Zirk zirk;
            if (!containsService(serviceId.getBezirkZirkId())) {
                HashSet<String> sphereSet = new HashSet<String>();
                sphereSet.add(sphereId);
                zirk = new OwnerZirk(serviceName, ownerDeviceId, sphereSet);
                addService(serviceId.getBezirkZirkId(), zirk);
                logger.info("Zirk membership added for zirkId " + serviceId + " sphereId " + sphereId
                        + " zirk name " + serviceName + " owner deviceId " + ownerDeviceId);
            } else {
                zirk = getService(serviceId.getBezirkZirkId());
                // update the name
                zirk.setZirkName(serviceName);
                // add the sphereId passed to the set of spheres
                zirk.getSphereSet().add(sphereId);
                logger.info("Zirk already registered. Name changed to " + serviceName);
            }
            return true;
        } else {
            logger.error("Error in adding membership for zirkId because there is no sphere with sphereId " + sphereId
                    + " in the registry.");
            return false;
        }
    }

    /**
     * Checks if the list of passed zirk IDs exist in the sphere membership
     * map.
     *
     * @param serviceIds - List of zirk IDs which have to be validated. It has to be
     *                   non null
     * @return - True, if all the services are validated. False otherwise.
     */
    public boolean validateServices(Iterable<BezirkZirkId> serviceIds) {
        boolean valid = false;
        if (serviceIds != null) {
            for (BezirkZirkId service : serviceIds) {
                if (!containsService(service.getBezirkZirkId())) {
                    return valid;
                }
            }
            valid = true;
        }
        return valid;
    }

    /**
     * Check if the sphere is part of the zirk's sphere set.
     *
     * @param service  - BezirkZirkId object of the zirk. It has to be non-null
     * @param sphereId whose existence is the sphere set has to be checked.
     * @return - true if the zirk is part of the sphere. <br>
     * false otherwise.
     */
    public boolean isServiceInSphere(BezirkZirkId service, String sphereId) {

        boolean serviceInSphere = false;
        if (containsService(service.getBezirkZirkId()) && containsSphere(sphereId)) {

            HashSet<String> spheres = (HashSet<String>) getService(service.getBezirkZirkId()).getSphereSet();

            if (spheres != null && spheres.contains(sphereId)) {
                serviceInSphere = true;
            }
        }
        return serviceInSphere;
    }

    /**
     * Get the Zirk name mapped to the zirkId.
     *
     * @param serviceId of the Zirk whose name is required. It has to be non-null
     * @return - zirk name if the zirk exists. Else return null.
     */
    public String getServiceName(BezirkZirkId serviceId) {
        String serviceName = null;

        if (containsService(serviceId.getBezirkZirkId())) {
            serviceName = getService(serviceId.getBezirkZirkId()).getZirkName();
        }

        return serviceName;
    }

    /**
     * Get list of BezirkZirkInfo objects for all the services registered on the
     * default sphere.
     *
     * @return - list of BezirkZirkInfo objects.<br>
     * - null, if no devices in default sphere.
     */
    public List<BezirkZirkInfo> getServiceInfo() {
        List<BezirkZirkInfo> info = null;
        String defaultSphereId = getDefaultSphereId();

        // get the sphere info from default sphere id
        BezirkSphereInfo sphereInfo = getSphereInfo(defaultSphereId);
        List<BezirkDeviceInfo> deviceInfoList = sphereInfo.getDeviceList();

        if (deviceInfoList == null) {
            return info;
        }

        for (BezirkDeviceInfo bezirkInfo : deviceInfoList) {
            if (bezirkInfo.getDeviceId().equals(upaDevice.getDeviceId())) {
                // got the device info for
                info = bezirkInfo.getZirkList();
            }
        }
        return info;
    }

    /**
     * Create member zirk objects for all the services on the device and add
     * them to the registry and the sphere.
     *
     * @param bezirkDeviceInfo - BezirkDeviceInfo object from where the zirk list will be
     *                      retrieved. It has to be non-null
     * @param sphereId      - which has to be added to the sphere set of the services.
     * @param ownerDeviceId - has to be non-null
     * @return - True if the zirk was added, False otherwise.
     */
    public boolean addMemberServices(BezirkDeviceInfo bezirkDeviceInfo, String sphereId, String ownerDeviceId) {
        if (!containsSphere(sphereId)) {
            logger.error("sphere with sphere ID " + sphereId + " not in the registry.");
            return false;
        }

        List<BezirkZirkInfo> bezirkZirkInfo = bezirkDeviceInfo.getZirkList();

        if (bezirkZirkInfo == null || (bezirkZirkInfo.isEmpty())) {
            logger.error("No services available for this device.");
            return false;
        }

        for (BezirkZirkInfo serviceInfo : bezirkZirkInfo) {
            HashSet<String> spheres = new HashSet<String>();
            spheres.add(sphereId);
            MemberZirk memberService = new MemberZirk(serviceInfo.getZirkName(), ownerDeviceId, spheres);
            addService(serviceInfo.getZirkId(), memberService);
            logger.info("Zirk " + serviceInfo.getZirkName() + " added with membership " + spheres);
        }

        for (BezirkZirkInfo service : bezirkDeviceInfo.getZirkList()) {
            Sphere sphere = getSphere(sphereId);
            sphere.addService(bezirkDeviceInfo.getDeviceId(), service.getZirkId());
        }

        persist();
        return true;
    }

    /**
     * Add the local services to the given sphere. The zirk Ids are retrieved
     * from the list of BezirkZirkInfo objects.
     *
     * @param sphereId     of the sphere to be added in the sphere set of the services
     * @param zirkInfo - List of BezirkZirkInfo objects from which BezirkZirkId list
     *                     is retrieved. It has to be non-null
     * @return - True if the zirk was added. False otherwise.
     */
    public boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> zirkInfo) {
        if (!containsSphere(sphereId)) {
            logger.error("sphere with sphere ID " + sphereId + " not in the registry.");
            return false;
        }
        List<BezirkZirkId> serviceIds = new ArrayList<BezirkZirkId>();

        // Aggregate the list of zirk IDs.
        for (BezirkZirkInfo serviceInfo : zirkInfo) {
            serviceIds.add(serviceInfo.getBezirkZirkId());
        }
        return addLocalServicesToSphere(serviceIds, sphereId);
    }

    /**
     * Add the local services to the given sphere(sphere Id). Also, update the
     * sphere Id in the sphere set of the services.
     *
     * @param serviceIds - List of zirk IDs of the services whose sphere set have to
     *                   be updated.
     * @param sphereId   of the sphere to be added in the sphere set of the services
     * @return - True if all the services were added to the sphere and the
     * sphere Id was updated in sphere set of all services. - False
     * otherwise.
     */
    public boolean addLocalServicesToSphere(Iterable<BezirkZirkId> serviceIds, String sphereId) {
        boolean success = false;
        int services = 0;
        if (serviceIds instanceof Collection<?>) {
            services = ((Collection<?>) serviceIds).size();
        }

        // check if the sphereId and services passed are valid, if so add to
        // sphere
        if (containsSphere(sphereId) && validateServices(serviceIds) && services != 0 && getSphere(sphereId)
                .addServices((deviceIdControl) ? DEVELOPMENT_DEVICE_ID : upaDevice.getDeviceId(), serviceIds)) {
            int successfulUpdates = 0;

            // Add all the services to the registry. If they are already present
            for (BezirkZirkId serviceId : serviceIds) {
                if (updateMembership(serviceId, sphereId)) {
                    successfulUpdates++;
                }
            }
            success = successfulUpdates == services;
            persist();

        }
        return success;
    }

    /**
     * Add the services present in the default sphere to the sphere Id passed.
     *
     * @param sphereId of the sphere to be added in the sphere set of the services
     * @return
     */
    public boolean addLocalServicesToSphere(String sphereId) {

        boolean status = false;
        String defaultSphereId = getDefaultSphereId();

        // get the sphere info from default sphere id
        BezirkSphereInfo sphereInfo = getSphereInfo(defaultSphereId);

        List<BezirkDeviceInfo> deviceInfoList = sphereInfo.getDeviceList();

        if (deviceInfoList == null) {
            return status;
        }

        for (BezirkDeviceInfo bezirkDeviceInfo : deviceInfoList) {
            if (bezirkDeviceInfo.getDeviceId().equals(upaDevice.getDeviceId())) {
                // got the device info for
                List<BezirkZirkInfo> serviceInfoList = bezirkDeviceInfo.getZirkList();

                // add the list of services to the sphere.
                if (addLocalServicesToSphere(sphereId, serviceInfoList)) {
                    logger.info("services added default sphere > " + sphereId);
                    status = true;
                }
                break;
            }
        }

        if (!status) {
            logger.error("Unable to add to the sphere" + sphereId);
        }
        return status;
    }

    /**
     * Updates the sphere set of the zirk Id to include the passed sphere id.
     *
     * @param serviceId of the zirk whose sphere set has to be updated. It has to
     *                  be non-null
     * @param sphereId  of the sphere to be added in the sphere set of the services
     * @return - True if sphere Id was added to zirk or if it was already
     * present. <br>
     * - False otherwise.
     */
    private boolean updateMembership(BezirkZirkId serviceId, String sphereId) {

        // Check if the zirk id and sphere id are present in the registry.
        // If yes, then update the sphere set of the zirk id.
        if (containsService(serviceId.getBezirkZirkId()) && containsSphere(sphereId)) {
            Zirk zirk = getService(serviceId.getBezirkZirkId());
            if (zirk.getSphereSet().add(sphereId)) {
                logger.info("Zirk Membership updated, sphereId " + sphereId + " added to zirk " + serviceId);
            } else {
                logger.info("Zirk Membership not updated, sphereId " + sphereId + " already present for zirk "
                        + serviceId);
            }
            return true;
        } else {
            logger.error("Error in updating membership for zirkId " + serviceId + " sphereId " + sphereId);
            return false;
        }
    }

    /**
     * Change the active status to True for all the discovered services whose ID
     * matches the BezirkZirkInfo zirk ID.
     *
     * @param discoveredServices
     * @param serviceInfo
     */
    public void updateBezirkServiceInfo(Set<BezirkDiscoveredZirk> discoveredServices, BezirkZirkInfo serviceInfo) {
        for (BezirkDiscoveredZirk discoveredServ : discoveredServices) {
            if (discoveredServ.zirk.getBezirkZirkId().getBezirkZirkId().equals(serviceInfo.getZirkId())) {
                serviceInfo.setActive(true);
            }
        }
    }

    /**
     * Check if DeviceInformation with passed deviceId exists in registry
     *
     * @param deviceId whose existence in the registry has to be checked.
     * @return true if deviceId exists in the devices map, false otherwise
     */
    public boolean containsDevice(String deviceId) {
        return registry.devices.containsKey(deviceId);
    }

    /********************************************** DEVICE **************************************************/

    /**
     * Get a device from registry
     * <p>
     * NOTE: The current device information is not stored in the sphere
     * registry. For retrieving device information, use
     * {@link ISphereUtils#getDeviceInformation(String)}
     * which wraps around this method along with retrieving current device's
     * information from {@link BezirkDeviceInterface}
     * </p>
     *
     * @param deviceId whose DeviceInformation object has to be retrieved.
     * @return DeviceInformation if the deviceId is present in the devices map
     * null otherwise
     */
    public DeviceInformation getDevice(String deviceId) {
        return registry.devices.get(deviceId);
    }

    /**
     * Add a device to the registry and persist the information.
     *
     * @param deviceId          which has to be added to the registry.
     * @param deviceInformation - DeviceInformation which has to be added to the registry for
     *                          the corresponding device ID.
     * @return true if the device was added to the devices map false otherwise
     */
    public boolean addDevice(String deviceId, DeviceInformation deviceInformation) {

        if (null == deviceInformation || null == deviceId) {
            logger.error("DeviceInformation object or device Id is null. Adding device with device id " + deviceId
                    + " failed.");
            return false;
        }

        registry.devices.put(deviceId, deviceInformation);
        logger.info("Device " + deviceInformation.getDeviceName() + " with deviceId " + deviceId
                + " added successfully to devices map");
        persist();
        return true;

    }

    /**
     * Gets the deviceInformation for the passed deviceId. This method abstracts
     * out the current implementation of storing device information
     * <p>
     * Currently the information about the current device is maintained using
     * BezirkDeviceInterface implementation. Also the information which is received
     * from other devices in operation like invite/join/catch is stored in the
     * sphere registry.
     * </p><p>
     * These two information management storage types can be combined using just the
     * devices map. By extending the basic deviceInformation we can store both
     * current as well as external device information using just one map
     * </p>
     *
     * @param deviceId whose DeviceInformation object has to be retrieved.
     * @return
     */
    public DeviceInformation getDeviceInformation(String deviceId) {
        DeviceInformation deviceInfo = null;
        if (upaDevice.getDeviceId().equals(deviceId)) {
            deviceInfo = new DeviceInformation(upaDevice.getDeviceName(), upaDevice.getDeviceType());
        } else if (containsDevice(deviceId)) {
            deviceInfo = getDevice(deviceId);
        } else {
            logger.error("Unknown device id : " + deviceId);
        }
        return deviceInfo;
    }

    /**
     * Provides BezirkDeviceInfo iterable for the passed map of deviceId -&gt; device
     * services
     *
     * @param devices      - services mapped to the device Id. It has to be non-null
     * @param ownerDevices - HashSet of list of devices of the sphere. It has to be
     *                     non-null
     * @return iterable <code>BezirkDeviceInfo</code> if devices is not <code>null</code> and has size
     * greater than 0, <code>null</code> otherwise
     */
    public Iterable<BezirkDeviceInfo> getBezirkDeviceInfo(Map<String, ArrayList<BezirkZirkId>> devices,
                                                          HashSet<String> ownerDevices) {

        if (!devices.isEmpty()) {

            List<BezirkDeviceInfo> deviceInfoList = new ArrayList<>();

            for (Map.Entry<String, ArrayList<BezirkZirkId>> entry : devices.entrySet()) {
                String deviceId = entry.getKey();

                DeviceInformation deviceInformation = getDeviceInformation(deviceId);

                if (deviceInformation != null) {
                    BezirkDeviceInfo deviceInfo = new BezirkDeviceInfo(deviceId, deviceInformation.getDeviceName(),
                            deviceInformation.getDeviceType(),
                            ownerDevices.contains(deviceId) ? BezirkDeviceRole.BEZIRK_CONTROL : BezirkDeviceRole.BEZIRK_MEMBER,
                            true, (List<BezirkZirkInfo>) getBezirkServiceInfo(entry.getValue()));
                    deviceInfoList.add(deviceInfo);
                } else {
                    logger.error("Device information for device : " + deviceId + " is null");
                }
            }
            return deviceInfoList;
        } else {
            logger.debug("Devices map empty");
            return null;
        }

    }

    /**
     * Returns the Device Name associated with the Device ID
     *
     * @param deviceId Id of the device (IP)
     * @return Device Name if exists, null otherwise
     */
    public String getDeviceName(final String deviceId) {
        if (containsDevice(deviceId))
            return getDevice(deviceId).getDeviceName();
        return null;
    }

    /**
     * Checks if the sphere is owner sphere.
     *
     * @param sphereInfo - BezirkSphereInfo object whose from which the sphere ID is
     *                   retrieved.
     * @return - True, if the sphere is owner sphere. False otherwise.
     */
    public boolean isThisDeviceOwnsSphere(BezirkSphereInfo sphereInfo) {
        if (containsSphere(sphereInfo.getSphereID())) {

            Sphere sphere = getSphere(sphereInfo.getSphereID());
            if (sphere instanceof OwnerSphere) {
                return true;
            }
        }
        return false;
    }

    /**
     * Persist information in the registry
     */
    public void persist() {
        if (spherePersistence != null) {
            try {
                spherePersistence.persistSphereRegistry();
            } catch (Exception e) {
                System.out.println("\nexception\n");
                logger.error("Error in persisting sphere Data", e);
            }
        }
    }

    /********************************************** PERSISTENCE **************************************************/

    /**********************************************
     * MISC
     **************************************************/

    public String getSphereIdFromPasscode(String passCode) {
        return registry.getSphereIdFromPasscode(passCode);
    }

    /**
     * Get the short QR code
     *
     * @param sphereId
     * @return
     */

    public String getShareCode(String sphereId) {
        String qrString = null;
        if (containsSphere(sphereId)) {
            qrString = new BezirkId().getShortIdByHash(sphereId);
        } else {
            logger.error("Invalid sphereId for generation BitMatrix");
        }
        return qrString;
    }

    // TODO : The below methods are to be moved out of SphereRegistryWrapper.

    /**
     * Get QR code with complete information
     *
     * @param sphereId
     * @return
     */

    public String getShareCodeString(String sphereId) {
        String qrString = null;

        if (containsSphere(sphereId)) {

            Sphere sphere = getSphere(sphereId);
            if (sphere instanceof OwnerSphere) {

                SphereExchangeData shareData = new SphereExchangeData();

                // device info
                shareData.setDeviceID(upaDevice.getDeviceId());
                shareData.setDeviceName(upaDevice.getDeviceName());
                shareData.setDeviceType(upaDevice.getDeviceType());

                // sphere info
                shareData.setSphereID(sphereId);
                shareData.setSphereName(sphere.getSphereName());
                shareData.setSphereType(sphere.getSphereType());

                SphereVitals vitals = crypto.getSphereVitals(sphereId);

                if (vitals != null) { // store only if it is valid
                    // sphere keys. convert the byte array to base64 string
                    shareData.setSphereKey(vitals.getSphereKey());
                    shareData.setOwnerPublicKeyBytes(vitals.getPublicKey());
                }
                qrString = shareData.serialize();

            } else {
                logger.error("BitMatrix generation, sphereId " + sphereId + " is a not owned by this device");
            }

        } else {
            logger.error("Invalid sphereId for generation BitMatrix");
        }

        return qrString;
    }

    /**
     * Update listener if initialized, with status for corresponding operations
     *
     * @param operation
     * @param status
     * @param message
     */
    public void updateListener(Operation operation, Status status, String message) {
        if (sphereListener != null) {
            logger.debug("Updating listener, status: " + status.toString() + " message: " + message);
            switch (operation) {
                case CATCH:
                    sphereListener.onCatchStatus(status, message);
                    break;
                case SHARE:
                    sphereListener.onShareStatus(status, message);
                    break;
                default:
                    logger.error("Illegal operation for updating listener");
            }
        } else {
            logger.debug("listener not initialized");
        }

    }

    public BitMatrix getQRCodeMatrix(String sphereId) {
        BitMatrix matrix = null;
        final Writer writer = new QRCodeWriter();
        final String qrString = getShareCode(sphereId);

        if (qrString != null) {
            try {
                matrix = writer.encode(qrString, com.google.zxing.BarcodeFormat.QR_CODE, 600, 600);
                logger.debug("QRcode information written : " + qrString);
            } catch (WriterException e) {
                logger.error("Error encoding QR code", e);
            }
        }

        return matrix;
    }

    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height) {
        if (width < 0 || height < 0) {
            return getQRCodeMatrix(sphereId);
        }
        BitMatrix matrix = null;
        final Writer writer = new QRCodeWriter();

        final String qrString = getShareCode(sphereId);

        if (qrString != null) {
            try {
                matrix = writer.encode(qrString, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
                logger.debug("QRcode information written : " + qrString);
            } catch (WriterException e) {
                logger.error("Error encoding QR code", e);
            }
        }

        return matrix;
    }

    public boolean switchMode(Mode mode) {
        // if the current mode is not the same as requested, change mode
        if (!sphereConfig.getMode().equals(mode)) {
            logger.debug("Changing mode to: " + mode.name());
            switch (mode) {
                case ON:
                    if (developmentSphere.create()) {
                        sphereConfig.setMode(Mode.ON);
                        // add existing services to development sphere
                        deviceIdControl = true;
                        addLocalServicesToSphere(sphereConfig.getSphereId());
                        deviceIdControl = false;
                    }
                    return true;
                case OFF:
                    if (developmentSphere.destroy()) {
                        sphereConfig.setMode(Mode.OFF);
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    public enum Operation {
        SHARE, CATCH
    }

    private class DevelopmentSphere {
        public boolean create() {
            logger.debug("Creating development sphere");
            // add development device
            addDevice(DEVELOPMENT_DEVICE_ID, new DeviceInformation(DEVELOPMENT_DEVICE_NAME, DEVELOPMENT_DEVICE_TYPE));

            if (!containsSphere(sphereConfig.getSphereId())) {
                // add keys to registry
                SphereKeys keys = new SphereKeys(sphereConfig.getSphereKey(), null, null);
                crypto.addMemberKeys(sphereConfig.getSphereId(), keys);

                // create the sphere
                MemberSphere sphere = new MemberSphere(sphereConfig.getSphereName(), "Development",
                        Collections.singleton(DEVELOPMENT_DEVICE_ID),
                        new LinkedHashMap<String, ArrayList<BezirkZirkId>>(), false);
                addSphere(sphereConfig.getSphereId(), sphere);
                persist();
            }
            return true;
        }

        public boolean destroy() {
            logger.debug("Destroying development sphere");
            // TODO delete device
            registry.devices.remove(DEVELOPMENT_DEVICE_ID);
            // TODO delete sphere
            return deleteSphere(sphereConfig.getSphereId());
        }

        // TODO clean up
        // /**
        // * Creates the development sphere. Should be used only in {@link
        // Mode#ON}
        // *
        // */
        // private void initDevelopmentSphere() {
        // addDevelopmentDevice();
        // if (!containsSphere(sphereConfig.getSphereId())) {
        // // add keys to registry
        // SphereKeys keys = new SphereKeys(sphereConfig.getSphereKey(), null,
        // null);
        // crypto.addMemberKeys(sphereConfig.getSphereId(), keys);
        //
        // // create the sphere
        // MemberSphere sphere = new MemberSphere(sphereConfig.getSphereName(),
        // "Development",
        // new HashSet<String>(Arrays.asList(DEVELOPMENT_DEVICE_ID)),
        // new LinkedHashMap<String, ArrayList<BezirkZirkId>>(), false);
        // addSphere(sphereConfig.getSphereId(), sphere);
        // // persist();
        // }
        // }
        // /**
        // * Add the device to which all services get added across devices
        // */
        // private void addDevDevice() {
        // if (getDevice(DEVELOPMENT_DEVICE_ID) == null) {
        // addDevice(DEVELOPMENT_DEVICE_ID, new
        // DeviceInformation(DEVELOPMENT_DEVICE_NAME, DEVELOPMENT_DEVICE_TYPE));
        // }
        // }
    }

}
