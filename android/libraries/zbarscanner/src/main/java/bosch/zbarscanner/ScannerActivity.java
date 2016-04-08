/**
 * Created by Rishabh Gulati on 12/9/15.
 * Robert Bosch LLC
 * rishabh.gulati@us.bosch.com
 * <p/>
 * Reference:
 * https://github.com/ZBar/ZBar/tree/master/android/examples/CameraTest/src/net/sourceforge/zbar/android/CameraTest
 */
package bosch.zbarscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/* Import ZBar Class files */

public class ScannerActivity extends Activity {

    public static final int REQUEST_CODE = 0;
    public static final String DATA = "data";
    private Camera camera;
    private CameraPreview cameraPreview;
    private Handler autoFocusHandler;
    private ImageScanner imageScanner;

    private boolean previewing = true;
    private Bundle receivedBundle;
    private final String TAG = ScannerActivity.class.getName();

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.d(TAG, "Inside bundle received value of ScanType: " + getIntent().getExtras().getString("ScanType"));
        receivedBundle = getIntent().getExtras();

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoFocusHandler = new Handler();
        camera = getCameraInstance();

        /* instantiate barcode imageScanner */
        imageScanner = new ImageScanner();
        imageScanner.setConfig(0, Config.X_DENSITY, 3);
        imageScanner.setConfig(0, Config.Y_DENSITY, 3);

        cameraPreview = new CameraPreview(this, camera, previewCallback, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(cameraPreview);
    }

    @Override
    public void onPause() {
        releaseCamera();
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.removeView(cameraPreview);
        onDestroy();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Get camera object
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera cameraInstance = null;
        try {
            cameraInstance = Camera.open();
        } catch (Exception e) {
            Log.e(TAG,"Exception in opening camera instance.",e);
        }
        return cameraInstance;
    }

    /**
     * Release the camera
     */
    private void releaseCamera() {
        if (camera != null) {
            previewing = false;
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private final Runnable doAutoFocus = new Runnable() {
        @Override
        public void run() {
            if (previewing)
                camera.autoFocus(autoFocusCB);
        }
    };

    /**
     * Camera preview callback called for each preview frame
     */
    private final PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = imageScanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                ScannerActivity.this.camera.setPreviewCallback(null);
                ScannerActivity.this.camera.stopPreview();
                SymbolSet syms = imageScanner.getResults();
                for (Symbol sym : syms) {
                    Log.d(TAG, "data: " + sym.getData());
                    Intent barcodeData = new Intent();
                    barcodeData.putExtra(DATA, sym.getData());
                    if(receivedBundle != null){
                        barcodeData.putExtras(receivedBundle);
                    }
                    setResult(RESULT_OK, barcodeData);
                    finish();
                }
            }
        }
    };

    /**
     * Auto-focus callback
     */
    private final AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
