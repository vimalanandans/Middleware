package com.bezirk.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bezirk.android.bezirkAsALibrary.R;
import com.bezirk.remotelogging.RemoteLoggingManager;

import static com.bezirk.android.bezirkAsALibrary.R.id.mainactivity_button;

public class RemoteLoggingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private String REMOTE_LOGIN = "remote_login";
    private RemoteLoggingManager remoteLog = new RemoteLoggingManager();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_logging);

        sharedPreferences = getSharedPreferences(REMOTE_LOGIN, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("isRemoteLoginEnabled", false);
        editor.commit();

        button = (Button) findViewById(R.id.mainactivity_button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        editor = sharedPreferences.edit();
        switch (v.getId()) {
            case R.id.mainactivity_button:

                if (sharedPreferences.getBoolean("isRemoteLoginEnabled", false)) {
                    //off -to - on

                    editor.putBoolean("isRemoteLoginEnabled", false);
                    button.setText("ON");
                } else {
                    //on -to-off

                    editor.putBoolean("isRemoteLoginEnabled", true);
                    button.setText("OFF");
                }
                editor.commit();

        }
        boolean remoteLogValue = sharedPreferences.getBoolean("isRemoteLoginEnabled",true);

        remoteLog = new RemoteLoggingManager();

        remoteLog.setRemoteLoggingForAllSpheres(remoteLogValue);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}

