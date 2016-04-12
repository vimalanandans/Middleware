package com.bezirk.controlui.commstest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.UhuComms;
import com.bezirk.controlui.R;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.starter.MainService;
import com.bezirk.util.UhuValidatorUtility;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Set;

/**
 * Activity that tests if the device is able to send and receive the multicast.
 * The configuration about the port that is used for sending and receiving can be configured from
 * the alert dialog.
 * @author Vijet Badigannavar
 *
 *
 *
 * Note this class needs to be refactored further. as of now it sends message to comms for zyre
 * 10-Dec-2015: Vimal
 */
public class CommsTestActivity extends ActionBarActivity {

    private static final String TAG = CommsTestActivity.class.getSimpleName();

    private final String myAddress = UhuNetworkUtilities.getDeviceIp(),
            name = UhuCompManager.getUpaDevice().getDeviceName(), ctrlMCastAddress = "224.5.5.5",
            BR_COMMS_DIAG_ACTION = "ACTION_DIAG_PING",BR_COMMS_DIAG_RESPONSE = "com.bezirk.comms.diag";

    // till the code is going to be compleltey refactored
    // use the below flag to switch manually
    private final boolean USE_UHU_UDP=false;

    private final UIStore uiStore = new UIStore();
    private int multicastSendingPort = 1234, multicastReceivingPort = 1234,
                unicastSendingPort = 2222, unicastReceivingPort = 2222,
                time_interval = 10000, pingCount;
    private TextView mTextViewDeviceText,mTextViewListItem,
                        mTextViewHelpLabel, mTextViewSent,
                        mTextViewReceived;
    private AlertDialog mAlertDilaog;
    private MulticastReceiver mReceiver;
    private UnicastReceiver uReceiver;
    private Button testButton,startStopButton;
    private LinearLayout mLinearLayoutList,mDeviceStatusLayout;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comms_test);
        init();
    }

    /**
     * init the UI components
     */
    private void init(){
        mHandler = new Handler(handlerCallback);
        testButton = (Button) findViewById(R.id.pingBtn);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        mLinearLayoutList = (LinearLayout)findViewById(R.id.deviceReplyLayout);
        mTextViewListItem = (TextView)findViewById(R.id.deviceReplyName);

        mDeviceStatusLayout = (LinearLayout) findViewById(R.id.deviceStatusLayout);
        mTextViewSent = (TextView) findViewById(R.id.deviceSendingText);
        mTextViewReceived = (TextView) findViewById(R.id.deviceReceivingText);

        mTextViewDeviceText = (TextView)findViewById(R.id.deviceName);
        mTextViewDeviceText.setText(name);

        mTextViewHelpLabel = (TextView) findViewById(R.id.infoLabel);


        updatePortsUI();

        startStopButton.performClick();

        // register a broadcast receiver
        registerReceiver(diagBroadcastReceiver, new IntentFilter(BR_COMMS_DIAG_RESPONSE));
    }

    /**
     * Broadcast receiver to receive the status from the stack if there is  a version mismatch
     */
    private final BroadcastReceiver diagBroadcastReceiver  =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String diagMessage = intent.getStringExtra("MSG");
            String address = intent.getStringExtra("ADDRESS");
            if(diagMessage != null)
            {
                processDiagMsg(address,diagMessage);
            }
        }
    };

    void processDiagMsg(String address, String diagMessage)
    {
        TestMessage msg = TestMessage.deserialize(diagMessage,TestMessage.class);
        if(msg == null) {
            Log.e(TAG, "unable to serialize the diag msg " + diagMessage);
            return;
        }
        if(PingMessage.isPing(msg)){
            PingMessage pingMsg = PingMessage.deserialize(diagMessage, PingMessage.class);
            pingMsg.deviceIp = address;
            updateListToAddPingReceived(pingMsg);
            //send Pong
            sendPong(pingMsg);
        }
        else{
            PongMessage pongMsg = PongMessage.deserialize(diagMessage, PongMessage.class);
            uiStore.updatePongStatus(pongMsg.pingRequestId,pongMsg);
            updateListToAddPongReceived(pongMsg);
        }
    }
    /**
     * Handler that highlights the infotext if we dont receive any responses.
     */
    private final Handler.Callback handlerCallback = new Handler.Callback(){

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == pingCount && uiStore.getNoOfPongMessages(name+":"+pingCount)==0){
                mTextViewHelpLabel.setTextColor(getResources().getColor(R.color.bg_info_label));
                mTextViewHelpLabel.setOnClickListener(helpListener);
            }
            return true;
        }
    };

    /**
     * Starts the Listeners (Unicast and Multicast)
     */
    private void startComms(){
        if(USE_UHU_UDP == false)
            return; // in case of zyre or any other comms don't init udps

        if(null == mReceiver){
            mReceiver = new MulticastReceiver(ctrlMCastAddress);
        }
        mReceiver.startComms();
        if(null == uReceiver){
            uReceiver = new UnicastReceiver();
        }
        uReceiver.startComms();
    }

    /**
     * Stop the listeners (Unicast and Multicast)
     */
    private void stopComms(){

        if(USE_UHU_UDP == false)
            return; // in case of zyre or any other comms don't init udps

        if(mReceiver != null) {
            mReceiver.stopComms();
            mReceiver = null;
        }

        if(uReceiver != null) {
            uReceiver.stopComms();
            uReceiver = null;
        }
    }


    /**
     * Start/ Stop button that starts stop the receiving thread.
     * @param view
     */
    public void startButtonClick(View view){
        Button receiverStartStopButton = (Button) view;
        if("START".equals(receiverStartStopButton.getText())){
            receiverStartStopButton.setText("STOP");
            startComms();
        }else{
            receiverStartStopButton.setText("START");
            stopComms();
        }
    }

    /**
     * Ping button that sends a multicast message on the configured port
     * @param view
     */
    public void pingButtonClick(View view){
        clearList();
        ++pingCount;
        updatePingButton();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PingMessage msg = new PingMessage();
                msg.pingId = pingCount;
                msg.deviceIp = myAddress;
                msg.deviceName = name;
                sendPing(msg);
            }
        }).start();
    }

    /**
     * Clear button that clears the List
     * @param view
     */
    public void clearButtonClick(View view){
        clearList();
    }

    /**
     * sends a ping
     * @param msg <DeviceName-PingId>
     */
    private void sendPing(final PingMessage msg){
        Log.e("TAG", "DataSent" + new Gson().toJson(msg));
        try {
            String data = new Gson().toJson(msg);

            if(!USE_UHU_UDP) {
                //Ping Uhu
                Intent serviceIntent = new Intent(this.getApplicationContext(), MainService.class);
                serviceIntent.setAction(BR_COMMS_DIAG_ACTION);
                serviceIntent.putExtra("MSG_TYPE", "ping");
                serviceIntent.putExtra("MSG", data);
                startService(serviceIntent);
            }
            updateListToAddPingSent(msg);
            uiStore.addToWaitingPongList(msg.deviceName +":" + msg.pingId);
            mHandler.sendEmptyMessageDelayed(pingCount, time_interval);

        } catch (Exception e){
            printToast("ERROR in SENDING PING");
            Log.e(TAG,"Error in sending ping.",e);
        }
    }

    private boolean sendPong(PingMessage msg) {
        //create a ongMessage
        PongMessage pongMessage = new PongMessage();
        pongMessage.senderIP = myAddress;
        pongMessage.deviceName = name;
        pongMessage.pingRequestId = msg.deviceName+":"+msg.pingId;
        pongMessage.pingId = msg.pingId;

        String sendDataString = new Gson().toJson(pongMessage);
        byte[] sendData = sendDataString.getBytes();
        InetAddress ipAddress;
        try {
            if(USE_UHU_UDP) {
                DatagramSocket clientSocket = new DatagramSocket();
                ipAddress = InetAddress.getByName(msg.deviceIp);
                DatagramPacket sendPacket;
                sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, unicastSendingPort);
                clientSocket.send(sendPacket);
                clientSocket.close();
            }else{
                //Ping Uhu
                Intent serviceIntent = new Intent(this.getApplicationContext(), MainService.class);
                serviceIntent.setAction(BR_COMMS_DIAG_ACTION);
                serviceIntent.putExtra("MSG_TYPE", "pong");
                serviceIntent.putExtra("MSG", sendDataString);
                serviceIntent.putExtra("ADDRESS",msg.deviceIp);
                startService(serviceIntent);
            }
            updateListToAddPongSent(msg);
            return true;
        } catch (Exception e) {
            printToast("ERROR in SENDING PONG");
            Log.e(TAG,"Error in sending pong.", e);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comms_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.port_config){
            showAlertDialogToConfig();
            return true;
        }
        else if(item.getItemId() == R.id.hints){
            //TO ADD THE COMMENTS TO THE COMMS HELP PLEASE INCLUDE IT IN array.xml file under diag_help_suggestions
           showAlertDialogHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns the TExtView of the Ping Message that is received bt the sender
     * @param msg Ping Message
     * @return
     */
    private TextView getListPingReceivedItem(final PingMessage msg){
        TextView listItem = new TextView(this);
        listItem.setLayoutParams(mTextViewListItem.getLayoutParams());
        listItem.setTextColor(mTextViewListItem.getCurrentTextColor());
        listItem.setPadding(mTextViewListItem.getPaddingLeft(), mTextViewListItem.getPaddingRight(), mTextViewListItem.getPaddingTop(), mTextViewListItem.getPaddingBottom());
        listItem.setBackgroundColor(getResources().getColor(R.color.bg_ping_received));
        listItem.setText("Message request from [" + msg.deviceName + "] Id : " + msg.pingId);
        return listItem;
    }


    /**
     * Prepares a List item to be shown on the device
     * @param msg
     * @return
     */
    private void updatePingSent(final PingMessage msg){
        mTextViewSent.setBackgroundColor(getResources().getColor(R.color.bg_ping_sent));
        mTextViewSent.setText("Message sent by this ["+ name+"]. Id : " + msg.pingId);
        mTextViewReceived.setTag(msg.deviceName+":"+msg.pingId);
        mTextViewReceived.setText("Message received by["+name+"] by 0 devices");
    }

    /**
     * Returns the TextView of the Pong Message that is sent.
     * @param msg
     * @return
     */
    private TextView getListPongSent(final PingMessage msg){
        TextView listItem = new TextView(this);
        listItem.setLayoutParams(mTextViewListItem.getLayoutParams());
        listItem.setTextColor(mTextViewListItem.getCurrentTextColor());
        listItem.setPadding(mTextViewListItem.getPaddingLeft(), mTextViewListItem.getPaddingRight(), mTextViewListItem.getPaddingTop(), mTextViewListItem.getPaddingBottom());
        listItem.setBackgroundColor(getResources().getColor(R.color.bg_pong_sent));
        listItem.setText("Message response to [" + msg.deviceName +"] Id : " + msg.pingId);
        return listItem;
    }

    /**
     * Updates the List once the ack is received in the form of pong message
     * @param msg
     */
    private void updateReceivedPongLabel(final PongMessage msg){
        Set<PongMessage> pongs = uiStore.getPongMap(msg.pingRequestId);
        if(pongs != null){
            mTextViewReceived.setOnClickListener(textViewListener);
            mTextViewReceived.setBackgroundColor(getResources().getColor(R.color.bg_pong_received));
            mTextViewReceived.setText("Message received by this ["+ name+"]. Id : " + msg.pingId+ " from " + pongs.size() + " devices" );
        }
    }

    private final View.OnClickListener textViewListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String key = (String)((TextView)view).getTag();
            Set<PongMessage> pongList = uiStore.getPongMap(key);
            showAlertDialog(pongList);
        }
    };

    private void showAlertDialog(Set<PongMessage> pongList){

        if(pongList == null || pongList.isEmpty()){
            printToast("No Devices responded to the request");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_pong_received_list, null);
        final LinearLayout linearLayout = (LinearLayout)alertView.findViewById(R.id.pongList);

        for(PongMessage msg : pongList){
            TextView textView = new TextView(this);
            textView.setLayoutParams(mTextViewListItem.getLayoutParams());
            textView.setText("Device-Name: "+msg.deviceName + " IP: " + msg.senderIP);
            textView.setPadding(mTextViewListItem.getPaddingLeft(), mTextViewListItem.getPaddingRight(), mTextViewListItem.getPaddingTop(), mTextViewListItem.getPaddingBottom());
            linearLayout.addView(textView);
        }
        builder.setView(alertView);
        builder.setTitle("DEVICES THAT RESPONDED TO PING MESSAGE");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mAlertDilaog!=null){
                    mAlertDilaog.cancel();
                    mAlertDilaog = null;
                }
            }
        });
        mAlertDilaog = builder.create();
        mAlertDilaog.show();
   }

    /**
     * Adds the ListItem to the list
     * @param msg
     */
    private void updateListToAddPingReceived(final PingMessage msg){
        mLinearLayoutList.post(new Runnable() {
            @Override
            public void run() {
                mLinearLayoutList.addView(getListPingReceivedItem(msg));
            }
        });
    }

    /**
     * Updates the list item for ping sent
     * @param msg
     */
    private void updateListToAddPingSent(final PingMessage msg){
        //changes
        mDeviceStatusLayout.post(new Runnable() {
            @Override
            public void run() {
                mDeviceStatusLayout.setVisibility(View.VISIBLE);
                updatePingSent(msg);
            }
        });
    }
    /**
     * Adds the ListItem for pong sent
     * @param pingMsg
     */
    private void updateListToAddPongSent(final PingMessage pingMsg){
        mLinearLayoutList.post(new Runnable() {
            @Override
            public void run() {
                mLinearLayoutList.addView(getListPongSent(pingMsg));
            }
        });
    }
    /**
     * Updates the Pong Message Received
     * @param msg
     */
    private void updateListToAddPongReceived(final PongMessage msg){
        mDeviceStatusLayout.post(new Runnable() {
            @Override
            public void run() {
              updateReceivedPongLabel(msg);
            }
        });
    }

    /**
     * Clears the List
     */
    private void clearList(){
        mLinearLayoutList.post(new Runnable() {
            @Override
            public void run() {
                mDeviceStatusLayout.setVisibility(View.GONE);
                mTextViewHelpLabel.setTextColor(getResources().getColor(R.color.black));
                mLinearLayoutList.removeAllViews();
            }
        });
    }

    private final View.OnClickListener helpListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            showAlertDialogHelp();
        }
    };

    /**
     * Alert Dialog that shows the suggestions for the failure of the comms.
     * To add the suggestions add an item into the array in res/values/array.xml file under diag_help_suggestions
     */
    private void showAlertDialogHelp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.fragment_diagnosis_hint,null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.diagoniseLayout);
        builder.setTitle("Diagnosis Hints");
        String[] helpInfoList = getResources().getStringArray(R.array.diag_help_suggestions);
        int count = 1;
        for(String info : helpInfoList){
            TextView textView = new TextView(this);
            textView.setLayoutParams(mTextViewListItem.getLayoutParams());
            textView.setTextColor(mTextViewListItem.getCurrentTextColor());
            textView.setPadding(mTextViewListItem.getPaddingLeft(), mTextViewListItem.getPaddingRight(), mTextViewListItem.getPaddingTop(), mTextViewListItem.getPaddingBottom());
            textView.setText(count + ". "+info);
            linearLayout.addView(textView);
            count++;
        }
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mAlertDilaog!= null){
                    mAlertDilaog.cancel();
                    mAlertDilaog = null;
                }
            }
        });
        mAlertDilaog = builder.create();
        mAlertDilaog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(diagBroadcastReceiver);
        stopComms();
    }

    /**
     * Pops a dialog to configure the parameters that are used for testing
     */
    private void showAlertDialogToConfig(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_comms_test,null);
        final EditText mSendingPort =(EditText) alertDialogView.findViewById(R.id.multicastSendingPort);
        final EditText mReceivingPort =(EditText) alertDialogView.findViewById(R.id.multicastReceivingPort);
        final EditText uSendingPort =(EditText) alertDialogView.findViewById(R.id.unicastSendingPort);
        final EditText uReceivingPort =(EditText) alertDialogView.findViewById(R.id.unicastReceivingPort);
        final EditText timeInterval = (EditText) alertDialogView.findViewById(R.id.waitingTime);

        mSendingPort.setText(String.valueOf(multicastSendingPort));
        mReceivingPort.setText(String.valueOf(multicastReceivingPort));
        uSendingPort.setText(String.valueOf(unicastSendingPort));
        uReceivingPort.setText(String.valueOf(unicastReceivingPort));
        timeInterval.setText(String.valueOf(time_interval));

        Button setButton = (Button) alertDialogView.findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UhuValidatorUtility.checkForString(mSendingPort.getText().toString(), mReceivingPort.getText().toString(),
                        uSendingPort.getText().toString(), uReceivingPort.getText().toString(),timeInterval.getText().toString())){
                    printToast("INVALID CONFIG");
                    return;
                }
                try{
                    configurePorts(mSendingPort, mReceivingPort, uSendingPort, uReceivingPort, timeInterval);
                }catch (Exception e){
                    printToast("CHECK PORT");
                    Log.e(TAG,"Check Port.",e);
                    return;
                }
                pingCount = 0;
                updatePingButton();
                clearList();
                if("STOP".equals(startStopButton.getText())){
                    startStopButton.performClick();
                }
                mAlertDilaog.cancel();
                mAlertDilaog = null;
            }
        });
        builder.setView(alertDialogView);

        mAlertDilaog = builder.create();
        mAlertDilaog.show();
    }

    private void configurePorts(EditText mSendingPort, EditText mReceivingPort, EditText uSendingPort, EditText uReceivingPort, EditText timeInterval) {
        multicastSendingPort = Integer.valueOf(mSendingPort.getText().toString().trim());
        multicastReceivingPort = Integer.valueOf(mReceivingPort.getText().toString().trim());
        unicastSendingPort = Integer.valueOf(uSendingPort.getText().toString().trim());
        unicastReceivingPort = Integer.valueOf(uReceivingPort.getText().toString().trim());
        time_interval =  Integer.valueOf(timeInterval.getText().toString().trim());
    }

    private void updatePortsUI(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Not used right now
            }
        });

    }

    /**
     * Update the ping button value on UI
     */
    private void updatePingButton(){
        testButton.post(new Runnable() {
            @Override
            public void run() {
                testButton.setText("TEST - " + (pingCount + 1));
            }
        });
    }

    /**
     * prints the toast msg on UI
     * @param text
     */
    private void printToast(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Thread class that listens to a multicast port and updates the UI when a ping is reeived.
     */
    private class MulticastReceiver extends Thread{

        boolean isRunning;
        private MulticastSocket multicastSocket;
        private InetAddress myAddress;

        public MulticastReceiver(String ctrlMCastAddr){
            try {
                multicastSocket = new MulticastSocket(multicastReceivingPort);
                multicastSocket.joinGroup(InetAddress.getByName(ctrlMCastAddr));
                isRunning=true;
                myAddress = UhuNetworkUtilities.getLocalInet();
                if(myAddress == null){
                    printToast("ERROR IN STARTING RECEIVER");
                }
            } catch (IOException e) {
                printToast("ERROR IN STARTING RECEIVER");
                Log.e(TAG,"Error in starting receiver.",e);
            }
        }

        @Override
        public void run() {
            super.run();
            byte[] buf = new byte[1024];
            DatagramPacket receivePacket;

            while(isRunning){
                if (Thread.interrupted()){
                    isRunning = false;
                    continue;
                }
                receivePacket = new DatagramPacket(buf, buf.length);
                try {
                    multicastSocket.receive(receivePacket);
                } catch (Exception e){
                    isRunning = false;
                    printToast("EXCEPTION IN RECEIVING");
                    Log.e(TAG,"Exception in receiving.",e);
                    continue;
                }
                if(myAddress.getHostAddress().toString().trim().equals(receivePacket.getAddress().getHostAddress().trim())){
                    Log.d("TAG", "local ping received");
                    continue;
                }else{
                    receivePing(receivePacket);
                }




            }
        }

        private void receivePing(DatagramPacket receivePacket) {
            byte[] recData = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(),0,recData,0,receivePacket.getLength());
            String yep = new String(recData);
            Log.e("TAG", "DataREceived" + yep);
            if(isRunning){
                try {
                    PingMessage msg = new Gson().fromJson(yep, PingMessage.class);
                    updateListToAddPingReceived(msg);
                    //send Pong
                    sendPong(msg);
                }catch (Exception e){
                    Log.e(TAG, "Error in parsing JSON",e);
                }
            }
        }


        /**
         * Stops the Thread
         */
        public void stopComms(){
            isRunning = false;
            if(multicastSocket != null) {
                this.interrupt();
            }
        }

        /**
         * Starts the tread
         */
        public void startComms(){
            isRunning = true;
            this.start();
        }
    }

    private class UnicastReceiver extends Thread{
        private DatagramSocket unicastListenerSocket;
        boolean isUnicastReceiverRunning;

        public UnicastReceiver(){
            NetworkInterface intf;
            InetAddress addr = null;
            try {
                intf = NetworkInterface.getByName(UhuComms.getINTERFACE_NAME());
                addr = UhuValidatorUtility.isObjectNotNull(intf)?UhuNetworkUtilities.getIpForInterface(intf):null;
                if(addr == null){
                    printToast("ERROR IN STARTING UNICAST LISTENER");
                }
            } catch (SocketException e){
                printToast("ERROR IN STARTING UNICAST LISTENER");
                Log.e(TAG,"Error in starting unicast listener.",e);
            }
            try {
                unicastListenerSocket = new DatagramSocket(null/*unicastReceivingPort, addr*/);
                unicastListenerSocket.setReuseAddress(true);
                unicastListenerSocket.bind(new InetSocketAddress(addr, unicastReceivingPort));
            } catch (SocketException e) {
                printToast("SOCKET ERROR");
                Log.e(TAG,"Socket error.",e);
            }
            isUnicastReceiverRunning = true;
        }

        @Override
        public void run() {
            super.run();
            byte[] buf = new byte[1024];
            DatagramPacket receivePacket;

            while (isUnicastReceiverRunning) {
                if (Thread.interrupted()) {
                    isUnicastReceiverRunning = false;
                    continue;
                }
                receivePacket = new DatagramPacket(buf, buf.length);
                try {
                    unicastListenerSocket.receive(receivePacket);
                } catch (Exception e) {
                    isUnicastReceiverRunning = false;
                    printToast("ERROR IN RECEIVING UNICAST");
                    Log.e(TAG,"Error in receiving unicast.",e);
                    continue;
                }
                byte[] recData = new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(),0,recData,0,receivePacket.getLength());
                String yep = new String(recData);
                Log.e("TAG", "PING REPLY RECEIVED" + yep);
                if(isUnicastReceiverRunning){
                    try {
                        PongMessage msg = new Gson().fromJson(yep, PongMessage.class);
                        uiStore.updatePongStatus(msg.pingRequestId,msg);
                        updateListToAddPongReceived(msg);
                    }catch (Exception e){
                        Log.e(TAG, "Error in parsing JSON",e);
                    }
                }
            }
        }

        /**
         * Stops the Thread
         */
        public void stopComms(){
            isUnicastReceiverRunning = false;
            if(unicastListenerSocket != null) {
                this.interrupt();
            }
        }

        /**
         * Starts the tread
         */
        public void startComms(){
            isUnicastReceiverRunning = true;
            this.start();
        }


    }

}