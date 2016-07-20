/**
 *
 */
package com.bezirk.sphere.api;

/**
 * @author Rishabh Gulati, Vimal
 */
public interface SphereListener {

    void onLeaveResponseReceived(final String memberLeaveResponse);

    void onLeaveRequestReceived(final String leaveRequest);

    void onCatchStatus(final Status status, final String message);

    void onShareStatus(final Status status, final String message);

    void onSphereCreateStatus(String sphereId, SphereCreateStatus status);

    void onSphereDiscovered(final boolean status, String sphereId);

    enum SphereCreateStatus {
        INFO_SPHERE_ALREADY_EXISTS, INTERNAL_ERROR_SPHERE_NOT_CREATED, SPHERE_NAME_OR_CALLBACK_ERROR, SUCCESS
    }

    enum Status {
        SCAN_SUCCESS, REQUEST_CREATED, REQUEST_SENT, REQUEST_RECEIVED, RESPONSE_CREATED, RESPONSE_SENT, RESPONSE_RECEIVED, FAILURE, SUCCESS
    }
}
