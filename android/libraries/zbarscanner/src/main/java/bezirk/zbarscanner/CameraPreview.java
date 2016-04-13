/**
 * Created by Rishabh Gulati on 12/9/15.
 * Robert Bosch LLC
 * rishabh.gulati@us.bosch.com
 *
 * Reference:
 * https://github.com/ZBar/ZBar/tree/master/android/examples/CameraTest/src/net/sourceforge/zbar/android/CameraTest
 *
 */

package bezirk.zbarscanner;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder surfaceHolder;
    private final Camera camera;
    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;
    private final String TAG = CameraPreview.class.getName();
    public CameraPreview(Context context, Camera camera,
                         PreviewCallback previewCb,
                         AutoFocusCallback autoFocusCb) {
        super(context);
        this.camera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;
        surfaceHolder = getHolder();

        /*Install a SurfaceHolder.Callback so we get notified when the
        underlying surface is created and destroyed.*/
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " ,e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG,"Tried to stop non-existent preview. ",e);
        }

        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            camera.setDisplayOrientation(90);

            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(previewCallback);
            camera.startPreview();
            camera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " ,e);
        }
    }
}
