
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
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

public class StreamingActivity extends AppCompatActivity {

    private TextView mTextViewFilePath;
    public static final int RESULT_LOAD_VIDEO = 222;
    private String filePath;
    private Button send,discover;
    private BezirkZirkEndPoint recipientEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        mTextViewFilePath = (TextView)findViewById(R.id.choosenFile);
        send = (Button)findViewById(R.id.sendButton);
        discover = (Button)findViewById(R.id.discoverRecipientButton);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a bezirk event and send the file.
                Toast.makeText(getApplicationContext(), "file path is "+filePath, Toast.LENGTH_SHORT).show();
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a bezirk event and send the file.
                Toast.makeText(getApplicationContext(), "selected recipient is  "+recipientEndpoint, Toast.LENGTH_SHORT).show();
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
