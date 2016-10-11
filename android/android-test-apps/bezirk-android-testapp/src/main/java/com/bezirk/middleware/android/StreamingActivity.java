
package com.bezirk.middleware.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bezirk.middleware.android.testApp.R;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.streaming.StreamPublishEvent;
import com.bezirk.middleware.core.streaming.StreamPublisherEventSet;
import com.bezirk.middleware.core.streaming.StreamReceiveEvent;
import com.bezirk.middleware.core.streaming.StreamSend;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StreamingActivity extends AppCompatActivity {
    private TextView mTextViewFilePath;
    public static final int RESULT_LOAD_VIDEO = 222;
    private String filePath;
    private Button send, discover;
    private ListView listView;
    private final List<StreamDataModel> list = new ArrayList();
    private StreamAdapter arrayAdapter;
    private static ZirkEndPoint recipientEndpoint;
    //checkBox to see if the Encryption is enabled!!!

    //bezirk Instance
    private Bezirk bezirk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        mTextViewFilePath = (TextView) findViewById(R.id.choosenFile);
        send = (Button) findViewById(R.id.sendButton);
        discover = (Button) findViewById(R.id.discoverRecipientButton);


        listView = (ListView) findViewById(R.id.discoveredlist);
        arrayAdapter = new StreamAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, list);
        listView.setAdapter(arrayAdapter);

        //On item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StreamDataModel dataModel = list.get(position);
                recipientEndpoint = dataModel.getReceiverEndpoint();
            }
        });


        //on send click
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEncrypted = false;

                if (filePath != null) {
                    final File file = new File(filePath);
                    final StreamSend streamSend = new StreamSend(false, isEncrypted, file);
                    bezirk.sendStream(recipientEndpoint, streamSend);
                } else {
                    Toast.makeText(getApplicationContext(), "file path is null.. Select the File and send",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        //on discover click
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register the bezirk middleware
                bezirk = BezirkMiddleware.registerZirk(getApplicationContext(), "Stream_Sending_Zirk");

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
                            StreamDataModel streamDataModel =
                                    new StreamDataModel(streamPublishEvent.getSubscriberId(), sender);
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
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            this.filePath = cursor.getString(columnIndex);

            mTextViewFilePath.setText(cursor.getString(columnIndex));
            cursor.close();
        }

    }

    class StreamDataModel {
        private final String subscriberId;
        private final ZirkEndPoint receiverEndpoint;

        public StreamDataModel(String subscriberId, ZirkEndPoint receiverEndpoint) {
            this.subscriberId = subscriberId;
            this.receiverEndpoint = receiverEndpoint;
        }

        public String getSubscriberId() {
            return subscriberId;
        }

        public ZirkEndPoint getReceiverEndpoint() {
            return receiverEndpoint;
        }

        @Override
        public String toString() {
            return subscriberId;
        }
    }


    class StreamAdapter extends ArrayAdapter<StreamDataModel> {

        public StreamAdapter(Context context, int resource, int textViewResourceId, List<StreamDataModel> objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }


    /*class MyStreamDescriptor extends  StreamDescriptor{
        private File file;
        private boolean isIncremental;
        private boolean isEncrypted;

        public MyStreamDescriptor(boolean isIncremental, boolean isEncrypted, File file){
            super(isIncremental, isEncrypted, file);
        }

    }*/
}