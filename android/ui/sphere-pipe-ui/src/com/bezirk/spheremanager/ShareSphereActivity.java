package com.bezirk.spheremanager;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.commons.UhuVersion;
import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.sphere.impl.IUhuQRCode;
import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.starter.MainService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class dislay the QR Code of the sphere.
 *
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
public class ShareSphereActivity extends ActionBarActivity {
    static final String TAG = ShareSphereActivity.class.getSimpleName();
    static final int BACK_INTENT = 8;

    private String sphereID = null;
    private UhuSphereInfo sphereInfo = null;
    private ImageView mImageViewQRCode = null;
    private Bitmap qrCodeBitmap = null;
    private File qrCodeFile = null;
    private AlertDialog mAlertDialog = null;

    private String BR_SYSTEM_STATUS_ACTION = "com.bezirk.systemstatus";
    private boolean showWarning = false;
    private String receivedUhuVersion = UhuVersion.UHU_VERSION;
    private final BroadcastReceiver systemStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Version Mismatch");
            showWarning = true;
            receivedUhuVersion = intent.getExtras().getString("misMatchVersion");
            invalidateOptionsMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sphere_smartphone);

        // register a broadcast receiver
        registerReceiver(systemStatusBroadcastReceiver, new IntentFilter(BR_SYSTEM_STATUS_ACTION));

        final ActionBar actionBar = getActionBar();
        sphereID = getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        IUhuSphereAPI api = MainService.getSphereHandle();

        if (api != null) {
            sphereInfo = api.getSphere(sphereID);
        } else {
            printToast("Main Service Not Available");
            NavUtils.navigateUpTo(this, createBackIntent());
        }

        mImageViewQRCode = (ImageView) findViewById(R.id.QR_Code);

        //read the screen co-ordinates
        DisplayMetrics displayScreenMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayScreenMetrics);
        int screenHeight = displayScreenMetrics.heightPixels;
        int screenWidth = displayScreenMetrics.widthPixels;

        int imageWidth = 0;
        int imageHeight = 0;

        // get to know the config
        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageHeight = screenHeight;
            imageWidth = screenHeight;
        } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            imageWidth = screenWidth;
            imageHeight = screenWidth;
        }

        qrCodeBitmap = ((IUhuQRCode) api).getQRCode(sphereID, imageWidth, imageHeight);

        mImageViewQRCode.post(new Runnable() {
            @Override
            public void run() {
                mImageViewQRCode.setImageBitmap(qrCodeBitmap);
                actionBar.setTitle("QR-Code for Sphere: " + sphereInfo.getSphereName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "invalidated...");
        getMenuInflater().inflate(R.menu.share_sphere, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "invalidated.in Prepare");
        MenuItem warningMenuItem = menu.findItem(R.id.warning);
        if (showWarning) {
            warningMenuItem.setVisible(true);
        } else {
            warningMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private Intent createBackIntent() {
        Intent backIntent = new Intent(this, DeviceListActivity.class);
        if (getCallingActivity().getClassName().equals("DeviceListActivity")) {
            // go back without remembering history
            backIntent.addFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            backIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                    .getStringExtra(DeviceListFragment.ARG_ITEM_ID));
            startActivityForResult(backIntent, BACK_INTENT);
        }
        return backIntent;
    }

    // change action of back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            NavUtils.navigateUpTo(this, createBackIntent());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            NavUtils.navigateUpTo(this, createBackIntent());
            return true;
        } else if (itemId == R.id.print_qrcode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                PrintHelper photoPrinter = new PrintHelper(this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap("SPHERE-QR-CODE", qrCodeBitmap);
                return true;
            } else {
                printToast("PRINT OPTION IS NOT AVAILABLE");
            }
            return true;
        } else if (itemId == R.id.email_qrcode) {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.setType("application/image");
                emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR-Code of " + sphereInfo.getSphereName() + " Sphere");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find attached the QR code of the " + sphereInfo.getSphereName());
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + getQRcodeImage().getAbsolutePath()));
                startActivity(emailIntent);
            } catch (Exception e) {
                printToast("ERROR IN SENDING EMAIL");
                Log.e(TAG, "Error in sending email.", e);
            }
            return true;
        } else if (itemId == R.id.qr_code_id) {
            //show Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sphere-ID");
            builder.setMessage("To join to the spere Manually, use the following code:\n " + sphereInfo.getSphereID());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mAlertDialog != null) {
                        mAlertDialog.cancel();
                        mAlertDialog = null;
                    }
                }
            });
            mAlertDialog = builder.create();
            mAlertDialog.show();
        } else if (itemId == R.id.warning) {
            showAlertDialogToShowSystemStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }

        unregisterReceiver(systemStatusBroadcastReceiver);
    }

    private void showAlertDialogToShowSystemStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("STACK STATUS");
        View alertView = LayoutInflater.from(this).inflate(R.layout.layout_menu_dialog_status, null);
        final TextView uhuVersion = (TextView) alertView.findViewById(R.id.versionUhu);
        final TextView uhuStatus = (TextView) alertView.findViewById(R.id.versionStatus);
        final TextView uhuExpectedVersionStatus = (TextView) alertView.findViewById(R.id.receivedVersionUhu);

        class CustomClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                    mAlertDialog = null;
                }
            }
        }

        builder.setPositiveButton("OK", new CustomClickListener());

        uhuVersion.setText("Expected Uhu-Version: " + UhuVersion.UHU_VERSION);
        if (receivedUhuVersion != null) {
            uhuExpectedVersionStatus.setText("Received Uhu-Version: " + receivedUhuVersion);
        } else {
            uhuExpectedVersionStatus.setText("Received Uhu-Version: " + UhuVersion.UHU_VERSION);
        }

        if (showWarning) {
            uhuStatus.setText("Different versions of Uhu exist in the network, there might be failure in the communication");
        }

        builder.setView(alertView);

        mAlertDialog = builder.create();
        mAlertDialog.show();

    }

    private File getQRcodeImage() throws IOException {
        qrCodeFile = new File(getExternalCacheDir(), "QR_Code_" + sphereInfo.getSphereName() + ".png");
        qrCodeFile.createNewFile();
        qrCodeFile.deleteOnExit();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(qrCodeFile);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        return qrCodeFile;
    }

    private void printToast(final String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }
}
