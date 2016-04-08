package com.bosch.upa.uhu.sphere.api;

public class UhuSphereType {

    // ------------------------
    // Sphere type constants
    // ------------------------
    // default sphere is only at the local device
    public static final String UHU_SPHERE_TYPE_DEFAULT = "UHU_SPHERE_DEFAULT";

    public static final String UHU_SPHERE_TYPE_HOME = "UHU_SPHERE_HOME";

    public static final String UHU_SPHERE_TYPE_CAR = "UHU_SPHERE_CAR";

    public static final String UHU_SPHERE_TYPE_OFFICE = "UHU_SPHERE_OFFICE";

    public static final String UHU_SPHERE_TYPE_HOME_ENTERTAINMENT = "UHU_SPHERE_HOME_ENTERTAINMENT";

    public static final String UHU_SPHERE_TYPE_HOME_CONTROL = "UHU_SPHERE_HOME_CONTROL";

    public static final String UHU_SPHERE_TYPE_HOME_SECURITY = "UHU_SPHERE_HOME_SECURITY";

    public static final String UHU_SPHERE_TYPE_OTHER = "UHU_SPHERE_OTHER";

    /**
     * Utility classes, which are a collection of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors.
     * 
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private UhuSphereType() {

    }
}
