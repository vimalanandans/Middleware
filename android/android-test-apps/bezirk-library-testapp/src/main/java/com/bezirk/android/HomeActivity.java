package com.bezirk.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.android.bezirkAsALibrary.R;

public class HomeActivity extends AppCompatActivity {

    private TextView autoTextView;
    private TextView advancedTextView;
    private final String AUTO_TESTING_MESSAGE = "Test BezirkMiddleware & Bezirk API's for mvp using a single bezirk service instance for this current test application along with a publisher and subscriber zirk";
    private final String ADVANCED_TESTING_MESSAGE = "Test various combinations of using bezirk as a standalone application or as an integration application with zirks";
    private Button autoTestButton;
    private Button advancedTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        autoTextView = (TextView) findViewById(R.id.auto_test_tv);
        advancedTextView = (TextView) findViewById(R.id.advanced_test_tv);

        autoTestButton = (Button) findViewById(R.id.auto_test_button);
        advancedTestButton = (Button) findViewById(R.id.advanced_test_button);

        autoTextView.setText(AUTO_TESTING_MESSAGE);
        advancedTextView.setText(ADVANCED_TESTING_MESSAGE);

        autoTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AutoTestActivity.class);
                startActivity(intent);
            }
        });

        advancedTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AdvancedTestActivity.class);
                startActivity(intent);
            }
        });

    }

}
