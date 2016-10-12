package com.bezirk.middleware.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.streaming.StreamSend;

import java.io.File;

public class StreamingTestActivity extends AppCompatActivity {
    private Button sender, receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sender = (Button) findViewById(R.id.streamSender);
        receiver = (Button) findViewById(R.id.streamReceiver);


        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open the sender activity
                Intent intent = new Intent(StreamingTestActivity.this, StreamingActivity.class);
                startActivity(intent);
            }
        });

        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open the receiver activity
                Intent intent = new Intent(StreamingTestActivity.this, StreameReceiverActivity.class);
                startActivity(intent);
            }
        });

    }

}
