package com.bosch.upa.uhu.sphere.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.api.objects.SphereVitals;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo.UhuDeviceRole;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.commons.UhuId;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.ICryptoInternals;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuDevMode.Mode;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener.SphereCreateStatus;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener.Status;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.security.SphereKeys;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @author rishabh
 *
 */
public class SphereRegistryWrapper {

    private SphereRegistry registry = null;
    private ISpherePersistence spherePersistence = null;
    private UPADeviceInterface upaDevice;
    private IUhuSphereListener sphereListener;
    private ISphereConfig sphereConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(SphereRegistryWrapper.class);
    private static final String DEFAULT_SPHERE_NAME = "Default Sphere";
    public static final String DEVELOPMENT_DEVICE_ID = "DevDeviceId";
    private static final String DEVELOPMENT_DEVICE_NAME = "DevDeviceName";
    private static final String DEVELOPMENT_DEVICE_TYPE = "DevDeviceType";
    private ICryptoInternals crypto;
    private DevelopmentSphere developmentSphere;

    /*
     * used for working around existing api for addLocalServicesToSphere. When
     * the addLocalServicesToSphere is used for copying services from default
     * sphere to dev sphere, this flag is set to true. After copying the flag is
     * reset to false. This is done so that the deviceId for dev sphere
     * configuration is used instead of the current deviceId
     */
    private boolean deviceIdControl = false;

    public enum Operation {
        SHARE, CATCH
    }

    /**********************************************
     * CONSTRUCTION & INITIALIZATION
     **************************************************/
    /**
     * Constructor to initialize SphereRegistry object and ISpherePersistence
     * interface object
     * 
     * @param registry
     *            - SphereRegistry object. Should not be null.
     * @param spherePersistence
     *            - ISpherePersistence interface object. Should not be null.
     */
    public SphereRegistryWrapper(SphereRegistry registry, ISpherePersistence spherePersistence,
            UPADeviceInterface upaDevice, ICryptoInternals crypto, IUhuSphereListener sphereListener,
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

    /**
     * Handle initialization of development sphere and default sphere
     */
    public void init() {
        initDefaultSphere();
        if (sphereConfig.getMode() == Mode.ON) {
            LOGGER.info("Development Mode Configured as ON");
            developmentSphere.create();
            // add existing services to development sphere
            deviceIdControl = true;
            addLocalServicesToSphere(sphereConfig.getSphereId());
            deviceIdControl = false;
        }

        persist();
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
    // new LinkedHashMap<String, ArrayList<UhuServiceId>>(), false);
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
     * Get a sphere from registry.
     * 
     * @param sphereId
     *            whose Sphere object is to be retrieved.
     * @return sphere associated with the sphereId if it is present in the
     *         spheres map, null otherwise
     */
    public Sphere getSphere(String sphereId) {
        return registry.spheres.get(sphereId);
    }

    /**
     * Get the UhuSphereInfo object for the sphere Id passed. If the sphere is a
     * temporary sphere, then it is skipped.
     * 
     * @param sphereId
     *            whose UhuSphereInfo object is to be retrieved.
     * 
     * @return : Sphere Info if found else null.<br>
     *         null if its a temporary sphere.
     */
    public UhuSphereInfo getSphereInfo(String sphereId) {
        UhuSphereInfo sphereInfo = null;

        if (containsSphere(sphereId)) {
            Sphere sphere = getSphere(sphereId);
            // check if the sphere is a temporary member sphere
            if (!(sphere instanceof MemberSphere && ((MemberSphere) sphere).isTemporarySphere())) {

                Iterable<UhuDeviceInfo> devicesIterable = getUhuDeviceInfo(sphere.getDeviceServices(),
                        (HashSet<String>) sphere.getOwnerDevices());

                ArrayList<UhuDeviceInfo> devices = (devicesIterable != null)
                        ? (ArrayList<UhuDeviceInfo>) devicesIterable : null;

                sphereInfo = new UhuSphereInfo(sphereId, sphere.getSphereName(), sphere.getSphereType(), devices, null);

                if (sphere instanceof OwnerSphere) {
                    sphereInfo.setThisDeviceOwnsSphere(true);
                } else {
                    sphereInfo.setThisDeviceOwnsSphere(false);
                }
                LOGGER.info("UhuSphereInfo returned\n" + sphereInfo.toString());
            } else {
                LOGGER.debug("Temporary sphere:" + sphereId + " skipped");
            }
        }
        return sphereInfo;
    }

    /**
     * Get UhuSphereInfo objects for all the spheres stored in the registry.
     * 
     * @return - List of UhuSphereInfo objects for each sphere in the registry.
     *         <br>
     *         - null, if there are no spheres in the registry.
     */
    public Iterable<UhuSphereInfo> getSpheres() {
        List<UhuSphereInfo> spheres = null;
        Set<String> sphereIds = getSphereIds();

        if (sphereIds != null && !sphereIds.isEmpty()) {
            spheres = new ArrayList<UhuSphereInfo>();
            for (String sphereId : sphereIds) {
                UhuSphereInfo spInfo = getSphereInfo(sphereId);
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
     * @param sphereId
     *            whose existence in the registry has to be checked.
     * @return true if sphereId exists in the spheres map, false otherwise
     */
    public boolean containsSphere(String sphereId) {
        return registry.spheres.containsKey(sphereId);
    }

    /**
     * Get list of all sphereIds from registry.
     * 
     * @return The sphereIds stored in the spheres map. <br>
     *         null if sphereIds don't exist in the registry.
     */
    public Set<String> getSphereIds() {
        return registry.spheres.keySet();
    }

    /**
     * Add a sphere to the registry.
     * 
     * @param sphereId
     *            which has to be added to registry. It has to be non-null
     * @param sphere
     *            - Sphere object which has to be added to the registry. It has
     *            to be non-null.
     * 
     * @return true if the sphere was added in spheres map, false otherwise
     */
    public boolean addSphere(String sphereId, Sphere sphere) {
        if (null == sphere || null == sphereId) {
            LOGGER.error("Sphere object or sphere Id is null. Adding sphere with sphere id " + sphereId + " failed.");
            return false;
        }
        registry.spheres.put(sphereId, sphere);
        LOGGER.info("Sphere " + sphere.getSphereName() + " with sphereId " + sphereId
                + " added successfully to spheres map");
        persist();
        return true;
    }

    /**
     * Delete a sphere from the registry. <br>
     * Includes cleaning sphere membership such that no service has this
     * sphereId as a reference. <br>
     * Also includes cleaning the sphereKeys associated with this sphere.
     * 
     * @param sphereId
     * 
     * @return true if the sphere was deleted successfully
     */
    public boolean deleteSphere(String sphereId) {
        if (getDefaultSphereId().equalsIgnoreCase(sphereId)) {
            LOGGER.info("Deleting default sphere is not allowed");
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
        for (Service service : registry.sphereMembership.values()) {
            Set<String> spheres = service.getSphereSet();
            spheres.remove(sphereId);
            LOGGER.debug("Deleted membership of " + service.getServiceName() + " for sphere " + sphereId);
        }
    }

    /**
     * Check if sphereId is present in sphereKeyMap/sphereHashKeyMap. <br>
     * Registry should not be empty.
     * 
     * @param sphereId
     *            whose existence in the sphere maps has to be checked.
     * @return true if sphereId exists in the sphereKeyMap or sphereHashKeyMap
     *         false otherwise
     */
    public boolean existsSphereIdInKeyMaps(String sphereId) {
        return registry.isKeymapExist(sphereId);
    }

    /**
     * Get the 'default sphere id'
     * 
     * <br>
     * NOTE: Current implementation assumes use of LinkedHashMap for 'spheres'
     * map. The default sphere is the first entry in this map. It is required
     * that this entry is never deleted.
     * 
     * @return sphereId of the default-sphere null otherwise
     */
    public String getDefaultSphereId() {

        String defaultSphereId = null;

        if (registry.spheres.keySet().iterator().hasNext()) {
            defaultSphereId = new String(registry.spheres.keySet().iterator().next());
        }
        LOGGER.debug("Default sphereId : " + defaultSphereId);
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
         * 3) if using the listener approach with UhuSphere, we could store the
         * default sphereId on creation of the default sphere
         */
    }

    /**
     * Create the default sphere. Checks first if the default sphere Id exists.
     * If it does not, then it creates a default sphere.<br>
     * If the default sphere does exist and the name is different, then just the
     * name is updated.
     * 
     * @param defaultSphereName
     *            - The name to be assigned to the default sphere. It has to be
     *            non-null
     * @return - true, if sphere was created or if the name is updated. False,
     *         otherwise.
     */
    public boolean createDefaultSphere(String defaultSphereName) {
        String defaultSphereId = getDefaultSphereId();
        if (null == defaultSphereId) {
            createSphere(defaultSphereName, UhuSphereType.UHU_SPHERE_TYPE_DEFAULT, null);
            return true;
        } else {
            // default sphere id exists. check the name of the sphere and update
            // the same if any change
            Sphere defaultSphere = getSphere(defaultSphereId);
            if (!defaultSphere.getSphereName().equals(defaultSphereName)) {
                LOGGER.info("Change in default sphere name from > " + defaultSphere.getSphereName() + " to >"
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
     * @param sphereName
     *            - Name to be assigned to the new sphere.
     * @param sphereType
     *            - Type of sphere to be created
     * @param uhuSphereListener
     * @return - SphereId if sphere was created successfully or if the sphereId
     *         existed already, null otherwise.
     */
    public String createSphere(String sphereName, String sphereType, IUhuSphereListener uhuSphereListener) {
        String name = (null == sphereName) ? DEFAULT_SPHERE_NAME : sphereName;
        String type = (null == sphereType) ? UhuSphereType.UHU_SPHERE_TYPE_OTHER : sphereType;
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

        LOGGER.info("Create Sphere, sphereId : " + sphereId + ", status : " + status);
        if (uhuSphereListener != null) {
            uhuSphereListener.onSphereCreateStatus(sphereId, status);
        } else {
            LOGGER.debug("Create Sphere, no listener registered");
        }
        return sphereId;

    }

    /**
     * Initialize the default Sphere and persist the information
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
     * Generate sphere name using information from {@link UPADeviceInterface}
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
                    name = "Sphere-" + deviceName;
                } else {
                    name = "Sphere-" + deviceName + "-" + deviceIdSubString;
                }
            }
        }
        LOGGER.info("Default Sphere name used: " + name);
        return name;
    }

    /********************************************** SERVICE **************************************************/

    /**
     * Check if serviceId is present in the sphereMembership map
     * 
     * @param serviceId
     *            whose existence in the registry has to be checked. It has to
     *            be valid and non-null. No validation done in this method.
     * @return true if the serviceId is present in the sphereMembership map
     *         false otherwise
     */
    public boolean containsService(String serviceId) {
        return registry.sphereMembership.containsKey(serviceId);
    }

    /**
     * Get a service from registry.
     * 
     * @param serviceId
     *            of the required Service object.
     * @return service associated with the serviceId if it is present in the
     *         sphereMembership map null otherwise
     */
    public Service getService(String serviceId) {
        return registry.sphereMembership.get(serviceId);
    }

    /**
     * Add a service to the registry and persist the information.
     * 
     * @param serviceId
     *            of the service which has to be added to the sphere membership
     *            map.
     * @param service
     *            - Service object which has to be added to the sphere
     *            membership map.
     * @return true if the service was added to the sphereMembership map false
     *         otherwise
     */
    public boolean addService(String serviceId, Service service) {
        if (service == null || serviceId == null) {
            LOGGER.error(
                    "Service object or Service Id is null. Adding Service with Service id " + serviceId + " failed.");
            return false;
        }

        registry.sphereMembership.put(serviceId, service);
        LOGGER.info("Service " + service.getServiceName() + " with serviceId " + serviceId
                + " added successfully to sphereMembership map");
        persist();
        return true;
    }

    /**
     * Checks if the service is a local service
     * 
     * NOTE: Another way of checking if the service is local is by checking what
     * instance of Service is stored in the sphereMembership i.e. OwnerService
     * or MemberService
     * 
     * @param deviceId
     *            : service owner deviceId
     * 
     * @return: True if the service is local for this device. <br>
     *          False otherwise or if deviceId is passed as null.
     * 
     *          <br>
     *          <br>
     *          TODO: Has to be moved to Service.java
     */
    public boolean isServiceLocal(String deviceId) {
        if (deviceId != null && deviceId.equals(upaDevice.getDeviceId())) {
            return true;
        }
        return false;
    }

    /**
     * Get UhuServiceInfo objects for each service in the passed list of service
     * IDs.
     * 
     * @param serviceIds
     *            - list of service IDs whose respective UhuServiceInfo objects
     *            are required.
     * @return - List of UhuServiceInfo objects.<br>
     *         - null, if no services passed.<br>
     */
    public Iterable<UhuServiceInfo> getUhuServiceInfo(Iterable<UhuServiceId> serviceIds) {
        if (serviceIds == null) {
            LOGGER.debug("No Services passed");
            return null;
        }

        List<UhuServiceInfo> serviceInfoList = new ArrayList<UhuServiceInfo>();
        for (UhuServiceId servId : serviceIds) {

            if (containsService(servId.getUhuServiceId())) {

                Service service = getService(servId.getUhuServiceId());

                if (service != null) {
                    UhuServiceInfo serviceInfo = new UhuServiceInfo(servId.getUhuServiceId(), service.getServiceName(),
                            isServiceLocal(service.getOwnerDeviceId()) ? "Owner" : "Member", true, true);
                    serviceInfoList.add(serviceInfo);
                } else {
                    LOGGER.error("Service information for service : " + servId + " is null");
                }
            } else {
                LOGGER.error("Service information for service : " + servId + " not found in registry");
            }
        }
        return serviceInfoList;
    }

    /**
     * Get set of sphere Ids for the passed serviceId. Sphere set for the given
     * serviceId has to be non-null.
     * 
     * @param serviceId
     *            whose sphere set is required.
     * @return - Set of sphereIds<br>
     *         - Null if service does not exist<br>
     */
    public Iterable<String> getSphereMembership(UhuServiceId serviceId) {

        Set<String> spheres = null;

        if (containsService(serviceId.getUhuServiceId())) {
            spheres = new HashSet<String>(getService(serviceId.getUhuServiceId()).getSphereSet());
        }

        return spheres;
    }

    /**
     * Registers the service with UhuSphere's. In case the service is already
     * registered, call to this method updates the name of the service to
     * serviceName passed
     * 
     * @param serviceId
     *            UhuServiceId to be registered - has to be non-null
     * @param serviceName
     *            Name to be associated with the service - has to be non-null
     * @return true if service was added successfully
     * 
     *         false otherwise
     */
    public boolean registerService(UhuServiceId serviceId, String serviceName) {
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

    private boolean registerService(UhuServiceId serviceId, String serviceName, List<String> sphereIds,
            List<String> deviceIds) {
        int count = 0;
        for (String sphereId : sphereIds) {
            // add the service to the sphere with default device
            if (getSphere(sphereId).addService(deviceIds.get(sphereIds.indexOf(sphereId)),
                    serviceId.getUhuServiceId())) {
                // if the service was added successfully to the sphere
                // add this information to sphereMembership map
                if (addMembership(serviceId, sphereId, upaDevice.getDeviceId(), serviceName)) {
                    LOGGER.debug("Service " + serviceId + " registered to " + sphereId + "successfully");
                    persist();
                    count++;
                } else {
                    LOGGER.debug("register service failed and adding membership " + serviceId);
                }
            } else {
                LOGGER.debug("Registration failed, Service " + serviceId);
            }
        }
        return (count == sphereIds.size()) ? true : false;
    }

    /**
     * Adds the service membership. In case the service is already registered,
     * call to this method updates the name of the service to serviceName passed
     * and also adds the sphereId to the set of spheres, the service is a part
     * off
     * 
     * @param serviceId
     *            which has to be added to the sphere membership map. It has to
     *            be non-null
     * @param sphereId
     *            which has to be added to the sphere set of the service
     * @param ownerDeviceId
     *            - has to be non-null
     * @param serviceName
     *            - Name of the service to be added to sphere membership map. It
     *            has to be non-null
     * 
     * @return - True if service membership was added successfully, False
     *         otherwise.
     */
    public boolean addMembership(UhuServiceId serviceId, String sphereId, String ownerDeviceId, String serviceName) {
        if (containsSphere(sphereId)) {
            Service service;
            if (!containsService(serviceId.getUhuServiceId())) {
                HashSet<String> sphereSet = new HashSet<String>();
                sphereSet.add(sphereId);
                service = new OwnerService(serviceName, ownerDeviceId, sphereSet);
                addService(serviceId.getUhuServiceId(), service);
                LOGGER.info("Service membership added for serviceId " + serviceId + " sphereId " + sphereId
                        + " service name " + serviceName + " owner deviceId " + ownerDeviceId);
            } else {
                service = getService(serviceId.getUhuServiceId());
                // update the name
                service.setServiceName(serviceName);
                // add the sphereId passed to the set of spheres
                service.getSphereSet().add(sphereId);
                LOGGER.info("Service already registered. Name changed to " + serviceName);
            }
            return true;
        } else {
            LOGGER.error("Error in adding membership for serviceId because there is no sphere with sphereId " + sphereId
                    + " in the registry.");
            return false;
        }
    }

    /**
     * Checks if the list of passed service IDs exist in the sphere membership
     * map.
     * 
     * @param serviceIds
     *            - List of service IDs which have to be validated. It has to be
     *            non null
     * 
     * @return - True, if all the services are validated. False otherwise.
     */
    public boolean validateServices(Iterable<UhuServiceId> serviceIds) {
        boolean valid = false;
        if (serviceIds != null) {
            for (UhuServiceId service : serviceIds) {
                if (!containsService(service.getUhuServiceId())) {
                    return valid;
                }
            }
            valid = true;
        }
        return valid;
    }

    /**
     * Check if the sphere is part of the service's sphere set.
     * 
     * @param service
     *            - UhuServiceId object of the service. It has to be non-null
     * @param sphereId
     *            whose existence is the sphere set has to be checked.
     * @return - true if the service is part of the sphere. <br>
     *         false otherwise.
     */
    public boolean isServiceInSphere(UhuServiceId service, String sphereId) {

        boolean serviceInSphere = false;
        if (containsService(service.getUhuServiceId()) && containsSphere(sphereId)) {

            HashSet<String> spheres = (HashSet<String>) getService(service.getUhuServiceId()).getSphereSet();

            if (spheres != null && spheres.contains(sphereId)) {
                serviceInSphere = true;
            }
        }
        return serviceInSphere;
    }

    /**
     * Get the Service name mapped to the serviceId.
     * 
     * @param serviceId
     *            of the Service whose name is required. It has to be non-null
     * @return - service name if the service exists. Else return null.
     */
    public String getServiceName(UhuServiceId serviceId) {

        String serviceName = null;

        if (containsService(serviceId.getUhuServiceId())) {
            serviceName = new String(getService(serviceId.getUhuServiceId()).getServiceName());
        }
        return serviceName;
    }

    /**
     * Get list of UhuServiceInfo objects for all the services registered on the
     * default sphere.
     * 
     * @return - list of UhuServiceInfo objects.<br>
     *         - null, if no devices in default sphere.
     */
    public List<UhuServiceInfo> getServiceInfo() {
        List<UhuServiceInfo> info = null;
        String defaultSphereId = getDefaultSphereId();

        // get the sphere info from default sphere id
        UhuSphereInfo sphereInfo = getSphereInfo(defaultSphereId);
        List<UhuDeviceInfo> deviceInfoList = sphereInfo.getDeviceList();

        if (deviceInfoList == null) {
            LOGGER.error("Device name not exist in the default sphere");
            return info;
        }

        Iterator<UhuDeviceInfo> deviceIterator = deviceInfoList.iterator();

        while (deviceIterator.hasNext()) {
            UhuDeviceInfo uhuInfo = deviceIterator.next();

            if (uhuInfo.getDeviceId().equals(upaDevice.getDeviceId())) {
                // got the device info for
                info = uhuInfo.getServiceList();
            }
        }
        return info;
    }

    /**
     * Create member service objects for all the services on the device and add
     * them to the registry and the sphere.
     * 
     * @param uhuDeviceInfo
     *            - UhuDeviceInfo object from where the service list will be
     *            retrieved. It has to be non-null
     * @param sphereId
     *            - which has to be added to the sphere set of the services.
     * @param ownerDeviceId
     *            - has to be non-null
     * 
     * @return - True if the service was added, False otherwise.
     */
    public boolean addMemberServices(UhuDeviceInfo uhuDeviceInfo, String sphereId, String ownerDeviceId) {
        if (!containsSphere(sphereId)) {
            LOGGER.error("Sphere with sphere ID " + sphereId + " not in the registry.");
            return false;
        }

        List<UhuServiceInfo> uhuServiceInfos = uhuDeviceInfo.getServiceList();

        if (uhuServiceInfos == null || (uhuServiceInfos.isEmpty())) {
            LOGGER.error("No services available for this device.");
            return false;
        }

        for (UhuServiceInfo serviceInfo : uhuServiceInfos) {
            HashSet<String> spheres = new HashSet<String>();
            spheres.add(sphereId);
            MemberService memberService = new MemberService(serviceInfo.getServiceName(), ownerDeviceId, spheres);
            addService(serviceInfo.getServiceId(), memberService);
            LOGGER.info("Service " + serviceInfo.getServiceName() + " added with membership " + spheres);
        }

        for (UhuServiceInfo service : uhuDeviceInfo.getServiceList()) {
            Sphere sphere = getSphere(sphereId);
            sphere.addService(uhuDeviceInfo.getDeviceId(), service.getServiceId());
        }

        persist();
        return true;
    }

    /**
     * Add the local services to the given sphere. The service Ids are retrieved
     * from the list of UhuServiceInfo objects.
     * 
     * @param sphereId
     *            of the sphere to be added in the sphere set of the services
     * @param serviceInfos
     *            - List of UhuServiceInfo objects from which UhuServiceId list
     *            is retrieved. It has to be non-null
     * 
     * @return - True if the service was added. False otherwise.
     */
    public boolean addLocalServicesToSphere(String sphereId, Iterable<UhuServiceInfo> serviceInfos) {
        if (!containsSphere(sphereId)) {
            LOGGER.error("Sphere with sphere ID " + sphereId + " not in the registry.");
            return false;
        }
        List<UhuServiceId> serviceIds = new ArrayList<UhuServiceId>();

        // Aggregate the list of service IDs.
        for (UhuServiceInfo serviceInfo : serviceInfos) {
            serviceIds.add(serviceInfo.getUhuServiceId());
        }
        return addLocalServicesToSphere(serviceIds, sphereId);
    }

    /**
     * Add the local services to the given sphere(sphere Id). Also, update the
     * sphere Id in the sphere set of the services.
     * 
     * @param serviceIds
     *            - List of service IDs of the services whose sphere set have to
     *            be updated.
     * @param sphereId
     *            of the sphere to be added in the sphere set of the services
     * 
     * @return - True if all the services were added to the sphere and the
     *         sphere Id was updated in sphere set of all services. - False
     *         otherwise.
     */
    public boolean addLocalServicesToSphere(Iterable<UhuServiceId> serviceIds, String sphereId) {
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
            for (UhuServiceId serviceId : serviceIds) {
                if (updateMembership(serviceId, sphereId)) {
                    successfulUpdates++;
                }
            }
            success = (successfulUpdates == services) ? true : false;
            persist();

        }
        return success;
    }

    /**
     * Add the services present in the default sphere to the sphere Id passed.
     * 
     * @param sphereId
     *            of the sphere to be added in the sphere set of the services
     * @return
     */
    public boolean addLocalServicesToSphere(String sphereId) {

        boolean status = false;
        String defaultSphereId = getDefaultSphereId();

        // get the sphere info from default sphere id
        UhuSphereInfo sphereInfo = getSphereInfo(defaultSphereId);

        List<UhuDeviceInfo> deviceInfoList = sphereInfo.getDeviceList();

        if (deviceInfoList == null) {
            LOGGER.error("Device name not exist in the default sphere");
            return status;
        }

        Iterator<UhuDeviceInfo> deviceIterator = deviceInfoList.iterator();

        while (deviceIterator.hasNext()) {
            UhuDeviceInfo uhuDeviceInfo = deviceIterator.next();

            if (uhuDeviceInfo.getDeviceId().equals(upaDevice.getDeviceId())) {
                // got the device info for
                List<UhuServiceInfo> serviceInfoList = uhuDeviceInfo.getServiceList();

                // add the list of services to the sphere.
                if (addLocalServicesToSphere(sphereId, serviceInfoList)) {
                    LOGGER.info("services added defalt sphere > " + sphereId);
                    status = true;
                }
                break;
            }
        }

        if (!status) {
            LOGGER.error("Unable to add to the sphere" + sphereId);
        }
        return status;
    }

    /**
     * Updates the sphere set of the service Id to include the passed sphere id.
     * 
     * @param serviceId
     *            of the service whose sphere set has to be updated. It has to
     *            be non-null
     * @param sphereId
     *            of the sphere to be added in the sphere set of the services
     * 
     * @return - True if sphere Id was added to service or if it was already
     *         present. <br>
     *         - False otherwise.
     */
    private boolean updateMembership(UhuServiceId serviceId, String sphereId) {

        // Check if the service id and sphere id are present in the registry.
        // If yes, then update the sphere set of the service id.
        if (containsService(serviceId.getUhuServiceId()) && containsSphere(sphereId)) {
            Service service = getService(serviceId.getUhuServiceId());
            if (service.getSphereSet().add(sphereId)) {
                LOGGER.info("Service Membership updated, sphereId " + sphereId + " added to service " + serviceId);
            } else {
                LOGGER.info("Service Membership not updated, sphereId " + sphereId + " already present for service "
                        + serviceId);
            }
            return true;
        } else {
            LOGGER.error("Error in updating membership for serviceId " + serviceId + " sphereId " + sphereId);
            return false;
        }
    }

    /**
     * Change the active status to True for all the discovered services whose ID
     * matches the UhuServiceInfo service ID.
     * 
     * @param discoveredServices
     * @param serviceInfo
     */
    public void updateUhuServiceInfo(Set<UhuDiscoveredService> discoveredServices, UhuServiceInfo serviceInfo) {
        for (UhuDiscoveredService discoveredServ : discoveredServices) {
            if (discoveredServ.service.getUhuServiceId().getUhuServiceId().equals(serviceInfo.getServiceId())) {
                serviceInfo.setActive(true);
            }
        }
    }

    /********************************************** DEVICE **************************************************/

    /**
     * Check if DeviceInformation with passed deviceId exists in registry
     * 
     * @param deviceId
     *            whose existence in the registry has to be checked.
     * @return true if deviceId exists in the devices map, false otherwise
     */
    public boolean containsDevice(String deviceId) {
        return registry.devices.containsKey(deviceId);
    }

    /**
     * Get a device from registry
     * 
     * NOTE: The current device information is not stored in the sphere
     * registry. For retrieving device information, use
     * {@link com.bosch.upa.uhu.sphere.api.ISphereUtils#getDeviceInformation(String)}
     * which wraps around this method along with retrieving current device's
     * information from {@link com.bosch.upa.devices.UPADeviceInterface}
     * 
     * @param deviceId
     *            whose DeviceInformation object has to be retrieved.
     * 
     * @return DeviceInformation if the deviceId is present in the devices map
     *         null otherwise
     */
    public DeviceInformation getDevice(String deviceId) {
        return registry.devices.get(deviceId);
    }

    /**
     * Add a device to the registry and persist the information.
     * 
     * @param deviceId
     *            which has to be added to the registry.
     * @param deviceInformation
     *            - DeviceInformation which has to be added to the registry for
     *            the corresponding device ID.
     * 
     * @return true if the device was added to the devices map false otherwise
     */
    public boolean addDevice(String deviceId, DeviceInformation deviceInformation) {

        if (null == deviceInformation || null == deviceId) {
            LOGGER.error("DeviceInformation object or device Id is null. Adding device with device id " + deviceId
                    + " failed.");
            return false;
        }

        registry.devices.put(deviceId, deviceInformation);
        LOGGER.info("Device " + deviceInformation.getDeviceName() + " with deviceId " + deviceId
                + " added successfully to devices map");
        persist();
        return true;

    }

    /**
     * Gets the deviceInformation for the passed deviceId. This method abstracts
     * out the current implementation of storing device information
     * 
     * Currently the information about the current device is maintained using
     * UPADeviceInterface implementation. Also the information which is received
     * from other devices in operation like invite/join/catch is stored in the
     * sphere registry.
     * 
     * These two information management storages can be combined using just the
     * devices map. By extending the basic deviceInformation we can store both
     * current as well as external device information using just one map
     * 
     * @param deviceId
     *            whose DeviceInformation object has to be retrieved.
     * @return
     */
    public DeviceInformation getDeviceInformation(String deviceId) {
        DeviceInformation deviceInfo = null;
        if (upaDevice.getDeviceId().equals(deviceId)) {
            deviceInfo = new DeviceInformation(upaDevice.getDeviceName(), upaDevice.getDeviceType());
        } else if (containsDevice(deviceId)) {
            deviceInfo = getDevice(deviceId);
        } else {
            LOGGER.error("Unkown device id : " + deviceId);
        }
        return deviceInfo;
    }

    /**
     * Provides UhuDeviceInfo iterable for the passed map of deviceId -> device
     * services
     * 
     * @param devices
     *            - services mapped to the device Id. It has to be non-null
     * @param ownerDevices
     *            - HashSet of list of devices of the sphere. It has to be
     *            non-null
     * 
     * @return Iterable UhuDeviceInfo if devices is not null and has size
     *         greater than 0.
     * 
     *         null otherwise
     */
    public Iterable<UhuDeviceInfo> getUhuDeviceInfo(Map<String, ArrayList<UhuServiceId>> devices,
            HashSet<String> ownerDevices) {

        if (!devices.isEmpty()) {

            List<UhuDeviceInfo> deviceInfoList = new ArrayList<>();

            for (String deviceId : devices.keySet()) {
                DeviceInformation deviceInformation = getDeviceInformation(deviceId);

                if (deviceInformation != null) {
                    UhuDeviceInfo deviceInfo = new UhuDeviceInfo(deviceId, deviceInformation.getDeviceName(),
                            deviceInformation.getDeviceType(),
                            ownerDevices.contains(deviceId) ? UhuDeviceRole.UHU_CONTROL : UhuDeviceRole.UHU_MEMBER,
                            true, (List<UhuServiceInfo>) getUhuServiceInfo(devices.get(deviceId)));
                    deviceInfoList.add(deviceInfo);
                } else {
                    LOGGER.error("Device information for device : " + deviceId + " is null");
                }
            }
            return deviceInfoList;
        } else {
            LOGGER.debug("Devices map empty");
            return null;
        }

    }

    /**
     * Returns the Device Name associated with the Device ID
     * 
     * @author Vijet
     * @param deviceId
     *            Id of the device (IP)
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
     * @param sphereInfo
     *            - UhuSphereInfo object whose from which the sphere ID is
     *            retrieved.
     * 
     * @return - True, if the sphere is owner sphere. False otherwise.
     */
    public boolean isThisDeviceOwnsSphere(UhuSphereInfo sphereInfo) {
        if (containsSphere(sphereInfo.getSphereID())) {

            Sphere sphere = getSphere(sphereInfo.getSphereID());
            if (sphere instanceof OwnerSphere) {
                return true;
            }
        }
        return false;
    }

    /********************************************** PERSISTENCE **************************************************/

    /**
     * Persist information in the registry
     */
    public void persist() {
        if (spherePersistence != null) {
            try {
                spherePersistence.persistSphereRegistry();
            } catch (Exception e) {
                System.out.println("\nexception\n");
                LOGGER.error("Error in persisting Sphere Data", e);
            }
        }
    }

    /********************************************** MISC **************************************************/

    public String getSphereIdFromPasscode(String passCode) {
        return registry.getSphereIdFromPasscode(passCode);
    }

    // TODO : The below methods are to be moved out of SphereRegistryWrapper.

    /**
     * Get the short QR code
     * 
     * @param sphereId
     * @return
     */

    public String getShareCode(String sphereId) {
        String qrString = null;
        if (containsSphere(sphereId)) {
            qrString = new UhuId().getShortIdByHash(sphereId);
        } else {
            LOGGER.error("Invalid sphereId for generation BitMatrix");
        }
        return qrString;
    }

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
                LOGGER.error("BitMatrix generation, sphereId " + sphereId + " is a not owned by this device");
            }

        } else {
            LOGGER.error("Invalid sphereId for generation BitMatrix");
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
            LOGGER.debug("Updating listener, status: " + status.toString() + " message: " + message);
            switch (operation) {
            case CATCH:
                sphereListener.onCatchStatus(status, message);
                break;
            case SHARE:
                sphereListener.onShareStatus(status, message);
                break;
            default:
                LOGGER.error("Illegal operation for updating listener");
            }
        } else {
            LOGGER.debug("listener not initialized");
        }

    }

    public BitMatrix getQRCodeMatrix(String sphereId) {
        BitMatrix matrix = null;
        Writer writer = new QRCodeWriter();

        String qrString = null;
        qrString = getShareCode(sphereId);

        if (qrString != null) {
            try {
                matrix = writer.encode(qrString, com.google.zxing.BarcodeFormat.QR_CODE, 600, 600);
                LOGGER.debug("QRcode information written : " + qrString);
            } catch (WriterException e) {
                LOGGER.error("Error encoding QR code", e);
            }
        }

        return matrix;
    }

    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height) {
        if (width < 0 || height < 0) {
            return getQRCodeMatrix(sphereId);
        }
        BitMatrix matrix = null;
        Writer writer = new QRCodeWriter();

        String qrString = null;
        qrString = getShareCode(sphereId);

        if (qrString != null) {
            try {
                matrix = writer.encode(qrString, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
                LOGGER.debug("QRcode information written : " + qrString);
            } catch (WriterException e) {
                LOGGER.error("Error encoding QR code", e);
            }
        }

        return matrix;
    }

    public boolean switchMode(Mode mode) {
        // if the current mode is not the same as requested, change mode
        if (!sphereConfig.getMode().equals(mode)) {
            LOGGER.debug("Changing mode to: " + mode.name());
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

    private class DevelopmentSphere {
        public boolean create() {
            LOGGER.debug("Creating development sphere");
            // add development device
            addDevice(DEVELOPMENT_DEVICE_ID, new DeviceInformation(DEVELOPMENT_DEVICE_NAME, DEVELOPMENT_DEVICE_TYPE));

            if (!containsSphere(sphereConfig.getSphereId())) {
                // add keys to registry
                SphereKeys keys = new SphereKeys(sphereConfig.getSphereKey(), null, null);
                crypto.addMemberKeys(sphereConfig.getSphereId(), keys);

                // create the sphere
                MemberSphere sphere = new MemberSphere(sphereConfig.getSphereName(), "Development",
                        new HashSet<String>(Arrays.asList(DEVELOPMENT_DEVICE_ID)),
                        new LinkedHashMap<String, ArrayList<UhuServiceId>>(), false);
                addSphere(sphereConfig.getSphereId(), sphere);
                persist();
            }
            return true;
        }

        public boolean destroy() {
            LOGGER.debug("Destroying development sphere");
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
        // new LinkedHashMap<String, ArrayList<UhuServiceId>>(), false);
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
