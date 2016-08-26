package com.bezirk.middleware.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bezirk.middleware.android.testApp.R;

public class HomeActivity extends AppCompatActivity {

    private static final String AUTO_TESTING_MESSAGE = "Test BezirkMiddleware & Bezirk API's for mvp using a single bezirk service instance for this current test application along with a publisher and subscriber zirk";
    private static final String ADVANCED_TESTING_MESSAGE = "Test various combinations of using bezirk as a standalone application or as an integration application with zirks";
    private static final String STREAM_TESTING_MESSAGE = "Test Bezirk Streaming api";

    private TextView autoTextView;
    private TextView advancedTextView;
    private TextView streamTextView;

    private Button autoTestButton;
    private Button advancedTestButton;
    private Button streamTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //setup stream testing elements
        streamTextView = (TextView) findViewById(R.id.stream_test_tv);
        streamTestButton = (Button) findViewById(R.id.stream_test_button);

        streamTextView.setText(STREAM_TESTING_MESSAGE);
        streamTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StreamingTestActivity.class);
                startActivity(intent);
            }
        });

    }

}
