package com.bezirk.middleware.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.middleware.android.testApp.R;

public class StreamingTestActivity extends AppCompatActivity {

    private static final String STREAM_SENDING_MESSAGE = "STREAM SENDING";
    private static final String STREAM_RECEIVING_MESSAGE = "STREAM RECEIVING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup auto testing elements
        TextView senderTextView = (TextView) findViewById(R.id.stream_sender_tv);
        senderTextView.setText(STREAM_SENDING_MESSAGE);

        //setup auto testing elements
        TextView receiverTextView = (TextView) findViewById(R.id.stream_receiver_tv);
        receiverTextView.setText(STREAM_RECEIVING_MESSAGE);


        Button streamSendButton = (Button) findViewById(R.id.stream_sender);
        streamSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StreamingTestActivity.this, StreamingActivity.class);
                startActivity(intent);
            }
        });

        Button streamReceiveButton = (Button) findViewById(R.id.stream_receiver);
        streamReceiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StreamingTestActivity.this, StreamReceiverActivity.class);
                startActivity(intent);
            }
        });


    }

}
