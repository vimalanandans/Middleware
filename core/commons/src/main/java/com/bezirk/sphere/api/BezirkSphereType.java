package com.bezirk.sphere.api;

public class BezirkSphereType {

    // ------------------------
    // sphere type constants
    // ------------------------
    // default sphere is only at the local device
    public static final String BEZIRK_SPHERE_TYPE_DEFAULT = "BEZIRK_SPHERE_DEFAULT";

    public static final String BEZIRK_SPHERE_TYPE_HOME = "BEZIRK_SPHERE_HOME";

    public static final String BEZIRK_SPHERE_TYPE_CAR = "BEZIRK_SPHERE_CAR";

    public static final String BEZIRK_SPHERE_TYPE_OFFICE = "BEZIRK_SPHERE_OFFICE";

    public static final String BEZIRK_SPHERE_TYPE_HOME_ENTERTAINMENT = "BEZIRK_SPHERE_HOME_ENTERTAINMENT";

    public static final String BEZIRK_SPHERE_TYPE_HOME_CONTROL = "BEZIRK_SPHERE_HOME_CONTROL";

    public static final String BEZIRK_SPHERE_TYPE_HOME_SECURITY = "BEZIRK_SPHERE_HOME_SECURITY";

    public static final String BEZIRK_SPHERE_TYPE_OTHER = "BEZIRK_SPHERE_OTHER";

    /**
     * Utility classes, which are a collection of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors.
     * <p/>
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private BezirkSphereType() {

    }
}
