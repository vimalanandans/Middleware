
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
import com.jaredrummler.android.device.DeviceName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StreamingActivity extends AppCompatActivity {

    private static final String DEVICE_NAME = DeviceName.getDeviceName();
    private static final String PUBLISHER_ID = DEVICE_NAME + ":STREAM_API:Publisher";
    private static final int RESULT_LOAD_VIDEO = 222;
    private static final Logger logger = LoggerFactory.getLogger(StreamingActivity.class);

    private final List<StreamDataModel> dataModels = new ArrayList<>();
    private TextView textViewFilePath;
    private String filePath;
    private StreamAdapter arrayAdapter;
    private static ZirkEndPoint recipientEndpoint;
    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bezirk = BezirkMiddleware.registerZirk(PUBLISHER_ID);

        final Button send = (Button) findViewById(R.id.sendButton);
        final Button discover = (Button) findViewById(R.id.discoverRecipientButton);
        final ListView listView = (ListView) findViewById(R.id.discoveredlist);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setContentView(R.layout.activity_streaming);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewFilePath = (TextView) findViewById(R.id.stream_receiver_activity);
        arrayAdapter = new StreamAdapter(getApplicationContext(), R.layout.simple_text_view, R.id.simple_textView, dataModels);
        listView.setAdapter(arrayAdapter);

        //On item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StreamDataModel dataModel = dataModels.get(position);
                recipientEndpoint = dataModel.getReceiverEndpoint();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    final File file = new File(filePath);
                    final Stream fileStream = new FileStream(recipientEndpoint, file);
                    fileStream.setEventReceiver(new Stream.StreamEventReceiver() {
                        @Override
                        public void receiveStreamEvent(StreamEvent event) {
                            Toast.makeText(getApplicationContext(), "Status :: " + event.getStreamRecordStatus(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    bezirk.sendStream(fileStream);

                } else {
                    Toast.makeText(getApplicationContext(), "file path is null.. Select the File and send", Toast.LENGTH_SHORT).show();
                }
            }
        });


        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the zirkendpoint dataModels view.
                dataModels.clear();

                //Multicast the StreamReceiveEvent
                final StreamReceiveEvent streamReceiveEvent = new StreamReceiveEvent();
                bezirk.sendEvent(streamReceiveEvent);


                //prep the StreamReceiverEventSet and multicast to identify the receivers.
                final StreamPublisherEventSet streamPublisherEventSet = new StreamPublisherEventSet();

                streamPublisherEventSet.setEventReceiver(new EventSet.EventReceiver() {
                    @Override
                    public void receiveEvent(Event event, ZirkEndPoint sender) {
                        logger.debug("received subscriber {}", sender.toString());

                        //got a response from the receiver
                        if (event instanceof StreamPublishEvent) {
                            final StreamPublishEvent streamPublishEvent = (StreamPublishEvent) event;

                            //show the receiverID to UI
                            final StreamDataModel streamDataModel = new StreamDataModel(streamPublishEvent.getSubscriberId(), sender);
                            dataModels.add(streamDataModel);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

                bezirk.subscribe(streamPublisherEventSet);

            }
        });

    }

    public void fileChooseButtonClick(View view) {

        final Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("video/*");
        startActivityForResult(intent, StreamingActivity.RESULT_LOAD_VIDEO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {
            final Uri selectedImage = data.getData();
            final String[] filePathColumn = {MediaStore.Video.Media.DATA};
            final Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                this.filePath = cursor.getString(columnIndex);

                textViewFilePath.setText(cursor.getString(columnIndex));
                cursor.close();
            }

        }

    }

    class StreamDataModel {
        final private String subscriberId;
        final private ZirkEndPoint receiverEndpoint;

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