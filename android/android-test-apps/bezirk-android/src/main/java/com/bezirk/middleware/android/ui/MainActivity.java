package com.bezirk.middleware.android.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bezirk.middleware.android.BezirkMiddleware;
import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;
import com.bezirk.middleware.core.componentManager.LifeCycleCallbacks;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.proxy.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LifeCycleCallbacks{
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private Switch bezirkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bezirkSwitch = (Switch) findViewById(R.id.switch1);

        bezirkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startBezirk();
                } else {
                    stopBezirk();
                }
            }
        });
        startBezirk();
    }

    private void startBezirk() {
        logger.info("Starting Bezirk");
        Config config = new Config.ConfigBuilder().setPackageLogLevel("com.bezirk.middleware.android.ui", Config.Level.DEBUG).create();
        BezirkMiddleware.initialize(this, config);

        bezirkSwitch.setText("Bezirk is on");
        toggle();
    }

    private void stopBezirk() {
        logger.info("Stopping Bezirk");
        BezirkMiddleware.stop();
        bezirkSwitch.setText("Bezirk is off");
        toggle();
    }

    private void toggle() {
        bezirkSwitch.setEnabled(false);
        logger.debug("switch disabled");
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bezirkSwitch.setEnabled(true);
                        logger.debug("switch enabled");
                    }
                });
            }
        }, 2000);
    }


    @Override
    public void start(StartServiceAction startServiceAction) {

    }

    @Override
    public void stop(StopServiceAction stopServiceAction) {

    }
}
