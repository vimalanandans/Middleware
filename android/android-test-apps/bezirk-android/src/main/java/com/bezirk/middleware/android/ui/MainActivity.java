/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bezirk.middleware.android.BezirkMiddleware;
import com.bezirk.middleware.core.proxy.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
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
}
