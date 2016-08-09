
package com.bezirk.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.android.publisher.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.test.streaming.StreamPublishEvent;
import com.bezirk.test.streaming.StreamPublisherEventSet;
import com.bezirk.test.streaming.StreamReceiveEvent;
import com.bezirk.test.streaming.StreamReceiverEventSet;
import com.bezirk.test.streaming.StreamSend;

import java.io.File;

public class StreamingActivity extends AppCompatActivity {

    private TextView mTextViewFilePath;
    public static final int RESULT_LOAD_VIDEO = 222;
    private String filePath;
    private Button send,discover;
    private ZirkEndPoint recipientEndpoint;
    private TextView discoverdTextView;

    //checkBox to see if the Encryption is enabled!!!

    //bezirk Instance
    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        mTextViewFilePath = (TextView)findViewById(R.id.choosenFile);
        send = (Button)findViewById(R.id.sendButton);
        discover = (Button)findViewById(R.id.discoverRecipientButton);
        discoverdTextView = (TextView) findViewById(R.id.discoveredRecipient);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a bezirk event and send the file.
                //Toast.makeText(getApplicationContext(), "file path is "+filePath, Toast.LENGTH_SHORT).show();

                //pick this value from the checkbox
                boolean isEncrypted  = false;

                //if file prepsent
                if(filePath != null){
                    File file = new File(filePath);
                    StreamSend streamSend = new StreamSend(false,isEncrypted,file);
                    bezirk.sendStream(recipientEndpoint, streamSend);
                }else{
                    Toast.makeText(getApplicationContext(), "file path is null.. Select the File and send", Toast.LENGTH_SHORT).show();
                }
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register the bezirk middleware
                bezirk  = BezirkMiddleware.registerZirk(getApplicationContext(), "Stream_Sending_Zirk");

                //Multicast the StreamReceiveEvent
                StreamReceiveEvent streamReceiveEvent = new StreamReceiveEvent();
                bezirk.sendEvent(streamReceiveEvent);


                //prep the StreamReceiverEventSet and multicast to identify the receivers.
                StreamPublisherEventSet streamPublisherEventSet = new StreamPublisherEventSet();

                streamPublisherEventSet.setEventReceiver(new EventSet.EventReceiver() {
                    @Override
                    public void receiveEvent(Event event, ZirkEndPoint sender) {
                       //got a response from the receiver
                        if(event instanceof StreamReceiveEvent) {
                            StreamPublishEvent streamPublishEvent = (StreamPublishEvent)event;

                            //set the receiver endpoint
                            recipientEndpoint = sender;

                            //show the receiverID to UI
                            discoverdTextView.setText(streamPublishEvent.getSubscriberId());

                       }
                    }
                });

                bezirk.subscribe(streamPublisherEventSet);

            }
        });

    }

    public void fileChooseButtonClick(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("video/*");
        startActivityForResult(intent, StreamingActivity.RESULT_LOAD_VIDEO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Video.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            this.filePath = cursor.getString(columnIndex);

            mTextViewFilePath.setText(cursor.getString(columnIndex));
            cursor.close();
        }

    }
}





//TO be deleted


/*//prep the StreamReceiverEventSet and multicast to identify the receivers.
                StreamReceiverEventSet receiverEventSet = new StreamReceiverEventSet();

                receiverEventSet.setEventReceiver(new EventSet.EventReceiver() {
                    @Override
                    public void receiveEvent(Event event, ZirkEndPoint sender) {
                       //got a response from the receiver
                        if(event instanceof StreamReceiveEvent) {
                            StreamReceiveEvent streamReceiveEvent = (StreamReceiveEvent)event;

                            //set the receiver endpoint
                            recipientEndpoint = sender;

                            //show the receiverID to UI
                            discoverdTextView.setText(streamReceiveEvent.getMessageId());

                       }
                    }
                });*/
