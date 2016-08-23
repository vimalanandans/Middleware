package com.bezirk.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.android.subscriber.R;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.proxy.android.BezirkMiddleware;
import com.bezirk.proxy.Config;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.test.AirQualityUpdateEvent;
import com.bezirk.test.HouseInfoEventSet;
import com.bezirk.test.UpdateAcceptedEvent;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Button testStreamingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testStreamingButton = (Button) findViewById(R.id.testStreamingButton);

        testStreamingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StreameReceiverActivity.class);
                startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.tv);
        tv.setMovementMethod(new ScrollingMovementMethod());
        receiverZirk();
    }

    public void onClearClick(View view) {
        tv.setText("");
    }

    private void receiverZirk() {

        BezirkMiddleware.initialize(this);
        final Bezirk bezirk = BezirkMiddleware.registerZirk("Subscriber Zirk");

        HouseInfoEventSet houseEvents = new HouseInfoEventSet();

        houseEvents.setEventReceiver(new EventSet.EventReceiver() {
            @Override
            public void receiveEvent(Event event, ZirkEndPoint sender) {
                if (event instanceof AirQualityUpdateEvent) {
                    AirQualityUpdateEvent aqUpdate = (AirQualityUpdateEvent) event;
                    BezirkZirkEndPoint endPoint = (BezirkZirkEndPoint) sender;

                    updateDisplay("\n" + endPoint.device + " >> Received air quality update: " + aqUpdate.toString());
                    //do something in response to this event
                    if (aqUpdate.humidity > 0.7) {
                        updateDisplay("\nHumidity is high - recommend turning on the dehumidifier.");
                        bezirk.sendEvent(sender, new UpdateAcceptedEvent("","Got the value for humidity " + aqUpdate.humidity));
                    }
                    if (aqUpdate.dustLevel > 20) {
                        updateDisplay("\nDust level is high - recommend running the vacuum.");
                    }
                    if (aqUpdate.pollenLevel > 500) {
                        updateDisplay("\nPollen level is high - recommend closing the windows and running the air filter.");
                    }
                }
            }
        });

        bezirk.subscribe(houseEvents);
    }

    private void updateDisplay(String display) {
        display = display + tv.getText();
        tv.setText(display);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BezirkMiddleware.stop();
    }
}
