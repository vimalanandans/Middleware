
package com.bezirk.middleware.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.streaming.StreamPublishEvent;
import com.bezirk.middleware.core.streaming.StreamPublisherEventSet;
import com.bezirk.middleware.core.streaming.StreamReceiveEvent;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamEvent;
import com.bezirk.middleware.streaming.FileStream;
import com.bezirk.middleware.streaming.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StreamingActivity extends AppCompatActivity {

    private TextView mTextViewFilePath;
    private static final int RESULT_LOAD_VIDEO = 222;
    private String filePath;
    private final List<StreamDataModel> list = new ArrayList<>();
    private StreamAdapter arrayAdapter;
    private static ZirkEndPoint recipientEndpoint;
    //checkBox to see if the Encryption is enabled!!!

    //bezirk Instance
    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewFilePath = (TextView) findViewById(R.id.stream_receiver_activity);
        Button send = (Button) findViewById(R.id.sendButton);
        Button discover = (Button) findViewById(R.id.discoverRecipientButton);


        final ListView listView = (ListView) findViewById(R.id.discoveredlist);
        arrayAdapter = new StreamAdapter(getApplicationContext(), R.layout.simple_text_view, R.id.simple_textView, list);
        listView.setAdapter(arrayAdapter);

        //On item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StreamDataModel dataModel = list.get(position);
                recipientEndpoint = dataModel.getReceiverEndpoint();
            }
        });

        //register the bezirk middleware
        bezirk = BezirkMiddleware.registerZirk("Stream_Sending_Zirk");


        //on send click
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    File file = new File(filePath);
                    Stream fileStream = new FileStream(recipientEndpoint, file);
                    fileStream.setEventReceiver(new Stream.StreamEventReceiver() {
                        @Override
                        public void receiveStreamEvent(StreamEvent event, ZirkEndPoint sender) {
                            //to be tested
                        }
                    });
                    //StreamController controller = bezirk.sendStream(fileStream);
                    //use this line to stop streaming
                    //controller.stopStreaming();

                    //send streaming
                    bezirk.sendStream(fileStream);

                } else {
                    Toast.makeText(getApplicationContext(), "file path is null.. Select the File and send", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //on discover click
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Multicast the StreamReceiveEvent
                StreamReceiveEvent streamReceiveEvent = new StreamReceiveEvent();
                bezirk.sendEvent(streamReceiveEvent);


                //prep the StreamReceiverEventSet and multicast to identify the receivers.
                final StreamPublisherEventSet streamPublisherEventSet = new StreamPublisherEventSet();

                streamPublisherEventSet.setEventReceiver(new EventSet.EventReceiver() {
                    @Override
                    public void receiveEvent(Event event, ZirkEndPoint sender) {
                        //got a response from the receiver
                        if (event instanceof StreamPublishEvent) {
                            StreamPublishEvent streamPublishEvent = (StreamPublishEvent) event;

                            //show the receiverID to UI
                            StreamDataModel streamDataModel = new StreamDataModel(streamPublishEvent.getSubscriberId(), sender);
                            list.clear();
                            list.add(streamDataModel);
                            arrayAdapter.notifyDataSetChanged();
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
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                this.filePath = cursor.getString(columnIndex);

                mTextViewFilePath.setText(cursor.getString(columnIndex));
                cursor.close();
            }

        }

    }

    class StreamDataModel {
        private String subscriberId;
        private ZirkEndPoint receiverEndpoint;

        StreamDataModel(String subscriberId, ZirkEndPoint receiverEndpoint) {
            this.subscriberId = subscriberId;
            this.receiverEndpoint = receiverEndpoint;
        }

        ZirkEndPoint getReceiverEndpoint() {
            return receiverEndpoint;
        }

        @Override
        public String toString() {
            return subscriberId;
        }
    }


    private class StreamAdapter extends ArrayAdapter<StreamDataModel> {

        StreamAdapter(Context context, int resource, int textViewResourceId, List<StreamDataModel> objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }

}