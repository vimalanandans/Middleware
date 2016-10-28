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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.android.testApp.R;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.Reply;
import com.bezirk.middleware.java.Request;
import com.bezirk.middleware.java.RequestReplySet;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.jaredrummler.android.device.DeviceName;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String LAUNCH_MESSAGE = "For Testing inter-app communication/inter-platform communication.";
    private static final String deviceName = DeviceName.getDeviceName();
    private static final String SENDER_ID = deviceName + ":Android:Sender";
    private static final String RECEIVER_ID = deviceName + ":Android:Receiver";
    //Interval between each message being sent by the publisher
    private static final int INTERVAL = 1000;
    private TextView zirkLauncherTextView;
    private TextView messagesTextView;
    private EditText channelIdEditText;
    private Button channelEnterButton;
    private Button senderButton;
    private Button receiverButton;
    private Button resetButton;
    private Button clearButton;
    private Button shutdownButton;
    private Bezirk senderBezirk;
    private Bezirk receiverBezirk;
    private RequestReplySet reqRepSetForReceiver;
    private RequestReplySet reqRepSetForSender;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channelIdEditText = (EditText) findViewById(R.id.editText);
        channelEnterButton = (Button) findViewById(R.id.channel_button);
        zirkLauncherTextView = (TextView) findViewById(R.id.zirk_launcher_tv);
        //zirkLauncherTextView.setText(LAUNCH_MESSAGE);

        messagesTextView = (TextView) findViewById(R.id.messages_tv);
        messagesTextView.setMovementMethod(new ScrollingMovementMethod());

        senderButton = (Button) findViewById(R.id.publisher_launcher_button);
        receiverButton = (Button) findViewById(R.id.subscriber_launcher_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        clearButton = (Button) findViewById(R.id.clear_button);
        shutdownButton = (Button) findViewById(R.id.shutdown_bezirk_button);

        channelEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleOnChannelEntered();
            }
        });

        senderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonToggler(false);
                publisherZirk();
                setTitle(SENDER_ID);
            }
        });

        receiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonToggler(false);
                subscriberZirk();
                setTitle(RECEIVER_ID);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanUp();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesTextView.setText("");
            }
        });

        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Turn off Bezirk")
                        .setMessage("Are you sure you want to turn off Bezirk? All app functionalities would be disabled.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BezirkMiddleware.stop();
                                Toast.makeText(MainActivity.this, "Bezirk is turned off. Restart app to enable app functionality.",
                                        Toast.LENGTH_LONG).show();
                                //disable all buttons
                                senderButton.setEnabled(false);
                                receiverButton.setEnabled(false);
                                resetButton.setEnabled(false);
                                clearButton.setEnabled(false);
                                shutdownButton.setEnabled(false);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        senderButton.setEnabled(false);
        receiverButton.setEnabled(false);
        resetButton.setEnabled(false);
        clearButton.setEnabled(false);
        shutdownButton.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    private void toggleOnChannelEntered() {
        final String channelId = channelIdEditText.getText().toString();
        System.out.println("channelId entered: " + channelId);
        if (channelId.length() == 0) {
            Toast.makeText(MainActivity.this, "Enter valid Channel Id and press Enter",
                    Toast.LENGTH_LONG).show();
        } else {
            initializeBezirk(channelId);
            channelIdEditText.setEnabled(false);
            channelEnterButton.setEnabled(false);
            senderButton.setEnabled(true);
            receiverButton.setEnabled(true);
            shutdownButton.setEnabled(true);
            Toast.makeText(MainActivity.this, "Channel Id set as " + channelId + ". To change channel, restart application",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void initializeBezirk(final String channelId) {
        Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();

        /*setting root log level*/
        //configBuilder.setLogLevel(Config.Level.ERROR);

        /*setting package log level*/
        //configBuilder.setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.INFO);

        /*setting app name for notification*/
        configBuilder.setAppName("Test Sending/Receiving");

        /*disabling inter-device communication*/
        //configBuilder.setComms(false);

        /*using custom communication groups to prevent crosstalk*/
        configBuilder.setGroupName(channelId);

        /*keeping bezirk service alive even after the app is shutdown*/
        //configBuilder.setServiceAlive(true);

        /*initialize with default configurations*/
        //BezirkMiddleware.initialize(this);

        BezirkMiddleware.initialize(this, configBuilder.create());

    }

    private void cleanUp() {
        messagesTextView.setText("");
        buttonToggler(true);
        if (timer != null) {
            timer.cancel();
        }
        if (receiverBezirk != null && reqRepSetForReceiver != null) {
            receiverBezirk.unsubscribe(reqRepSetForReceiver);
        }
        if (senderBezirk != null && reqRepSetForSender != null) {
            senderBezirk.unsubscribe(reqRepSetForSender);
        }
        setTitle(R.string.title_activity_test);
    }

    private void subscriberZirk() {
        receiverBezirk = BezirkMiddleware.registerZirk(RECEIVER_ID);
        reqRepSetForReceiver = new RequestReplySet();
        reqRepSetForReceiver.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof Request) {
                    Request request = (Request) event;
                    messagesTextView.append(request.printMsgForReceiver() + "\n");
                    scroll();
                    receiverBezirk.sendEvent(sender, new Reply(RECEIVER_ID, request.getMessageNumber()));
                }
            }
        });
        receiverBezirk.subscribe(reqRepSetForReceiver);
    }

    private void publisherZirk() {
        senderBezirk = BezirkMiddleware.registerZirk(SENDER_ID);
        reqRepSetForSender = new RequestReplySet();
        reqRepSetForSender.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof Reply) {
                    Reply reply = (Reply) event;
                    messagesTextView.append(reply.printMsgForReceiver() + "\n");
                    scroll();
                }
            }
        });

        senderBezirk.subscribe(reqRepSetForSender);

        //publish messages periodically
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int messageNumber = 1;

            @Override
            public void run() {
                final Request request = new Request(SENDER_ID, messageNumber++);
                senderBezirk.sendEvent(request);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messagesTextView.append(request.printMsgForSender() + "\n");
                    }
                });

            }
        }, 2000, INTERVAL);
        Toast.makeText(MainActivity.this, "Sending messages every " + INTERVAL + "ms",
                Toast.LENGTH_SHORT).show();
    }

    private void buttonToggler(boolean toggle) {
        receiverButton.setEnabled(toggle);
        senderButton.setEnabled(toggle);
        resetButton.setEnabled(!toggle);
        clearButton.setEnabled(!toggle);
    }

    private void scroll() {
        final int scrollAmount = messagesTextView.getLayout().getLineTop(messagesTextView.getLineCount()) - messagesTextView.getHeight();
        if (scrollAmount > 0)
            messagesTextView.scrollTo(0, scrollAmount);
        else
            messagesTextView.scrollTo(0, 0);
    }
}
