package com.bosch.upa.uhu.starter.helper;

/**
 * Created by AJC6KOR on 1/8/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.WindowManager;

import com.bosch.upa.uhu.R;
import com.bosch.upa.uhu.starter.IUhuStackHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An asysnc task which will restart the comms
 */
class RestartCommsAsyncTask extends AsyncTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestartCommsAsyncTask.class);
    private final Context context;
    private final String message;
    private final IUhuStackHandler stackHandler;
    private static AlertDialog dialog;

    RestartCommsAsyncTask(Context context, String alertMessage,IUhuStackHandler stackHandler){
        this.context = context;
        this.message = alertMessage;
        this.stackHandler = stackHandler;
    }
    private void buildAlertDialog(String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!!");
        builder.setMessage(message);
        builder.setIcon(R.drawable.upa_launcher);
        builder.setPositiveButton("DISMISS", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    } });
        dialog = builder.create();
        dialog.getWindow().setType( WindowManager.LayoutParams.TYPE_SYSTEM_ALERT );
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if(dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();
    }

    @Override
    protected void onPreExecute() {
        buildAlertDialog(message, context);
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... params) {
        LOGGER.debug("Comms has be re-started!!!!");
        stackHandler.restartComms();
        return null;
    }
}
