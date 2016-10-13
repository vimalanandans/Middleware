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
package com.bezirk.middleware.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.proxy.Config;

public class HomeActivity extends AppCompatActivity {

    private static final String AUTO_TESTING_MESSAGE = "Test BezirkMiddleware & Bezirk API's for mvp using a single " +
            "bezirk service instance for this current test application along with a publisher and subscriber zirk";
    private static final String ADVANCED_TESTING_MESSAGE = "Test various combinations of using bezirk as a " +
            "standalone application or as an integration application with zirks";
    private static final String STOP_MIDDLEWARE_MESSAGE = "Stops the middleware instance. Once stopped, " +
            "middleware can start only by closing and opening the app again.";

    private TextView autoTextView;
    private TextView advancedTextView;
    private TextView stopTextView;

    private Button autoTestButton;
    private Button advancedTestButton;
    private Button stopTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeBezirk();

        //setup auto testing elements
        autoTextView = (TextView) findViewById(R.id.auto_test_tv);
        autoTextView.setText(AUTO_TESTING_MESSAGE);

        autoTestButton = (Button) findViewById(R.id.auto_test_button);
        autoTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AutoTestActivity.class);
                startActivity(intent);
            }
        });

        //setup advanced testing elements
        advancedTextView = (TextView) findViewById(R.id.advanced_test_tv);
        advancedTextView.setText(ADVANCED_TESTING_MESSAGE);

        advancedTestButton = (Button) findViewById(R.id.advanced_test_button);
        advancedTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AdvancedTestActivity.class);
                startActivity(intent);
            }
        });

        //setup stop bezirk middleware elements
        stopTextView = (TextView) findViewById(R.id.stop_bezirk_tv);
        stopTestButton = (Button) findViewById(R.id.stop_bezirk_button);

        stopTextView.setText(STOP_MIDDLEWARE_MESSAGE);
        stopTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezirkMiddleware.stop();
                autoTestButton.setEnabled(false);
                advancedTestButton.setEnabled(false);
                stopTestButton.setEnabled(false);
                Toast.makeText(HomeActivity.this, "Bezirk is turned off. Restart app to enable app functionality.",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initializeBezirk() {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();

        /*setting root log level*/
        configBuilder.setLogLevel(Config.Level.ERROR);

        /*setting package log level*/
        //configBuilder.setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.INFO);

        /*setting app name for notification*/
        configBuilder.setAppName("bezirk-android-testapp");

        /*disabling inter-device communication*/
        //configBuilder.setComms(false);

        /*using custom communication groups to prevent crosstalk*/
        //configBuilder.setGroupName("Test Group");

        /*keeping bezirk service alive even after the app is shutdown*/
        //configBuilder.setServiceAlive(true);

        /*initialize with default configurations*/
        //BezirkMiddleware.initialize(this);

        BezirkMiddleware.initialize(this, configBuilder.create());

    }

}
