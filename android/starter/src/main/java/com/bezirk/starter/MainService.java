package com.bezirk.starter;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.bezirk.pipe.android.PipeRegistryFactory;
import com.bezirk.proxy.android.PipeActionParser;
import com.bezirk.proxy.android.ProxyforServices;
import com.bezirk.starter.helper.NetworkBroadCastReceiver;
import com.bezirk.starter.helper.UhuActionProcessor;
import com.bezirk.starter.helper.UhuServiceHelper;
import com.bezirk.starter.helper.UhuStackHandler;
import com.bezirk.R;
import com.bezirk.application.IUhuApp;
import com.bezirk.comms.ICommsNotification;
import com.bezirk.logging.LogServiceActivatorDeactivator;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipePolicyUtility;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.pipe.core.PipeRequest;
import com.bezirk.pipe.core.PipeRequester;
import com.bezirk.sphere.api.IUhuSphereAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MainService extends Service implements INotificationCallback{
    private static final Logger LOGGER = LoggerFactory.getLogger(MainService.class);

    private final UhuActionProcessor uhuActionProcessor = new UhuActionProcessor();

    private UhuServiceHelper uhuServiceHelper;

    private UhuStackHandler uhuStackHandler;

    private final PipeActionParser pipeActionParser = new PipeActionParser();

    private ICommsNotification commsNotification;

    private NetworkBroadCastReceiver broadcastReceiver;

    public static Boolean getStartedStack() {
        return UhuStackHandler.isStackStarted();
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Acquire the Wifi Lock for Multicast
        LOGGER.info("Uhu Services is Created");
        final ProxyforServices proxy = new ProxyforServices(this);
        uhuServiceHelper = new UhuServiceHelper(proxy);
        //Gain permissions for multicast

        //initialize the commsNotification object
        commsNotification = new CommsNotification(this);

        uhuStackHandler = new UhuStackHandler(proxy,commsNotification);

        //register to the broadcast receiver to receive changes in network state.
        broadcastReceiver = new NetworkBroadCastReceiver(this,uhuStackHandler);
        registerToWifiBroadcastReceivers(broadcastReceiver);

        // this is needed when the service starts first before uhu stack.
        // (service sends registration intent before start stack intent)
        if (!UhuStackHandler.isStackStarted()) {
            uhuStackHandler.startStack(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            uhuActionProcessor.processUhuAction(intent, this,uhuServiceHelper,uhuStackHandler);
        }

        return START_STICKY;


    }

    @Override
    public void onDestroy(){
        if(!uhuStackHandler.isStackStopped()){

            uhuStackHandler.stopStack(this);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Process pipe request
     * @param intent Intent Received
     */
    public void processPipeRequest(Intent intent) {
        LOGGER.info("Received pipe request intent");

        PipeRequest pipeRequest = pipeActionParser.parsePipeRequest(intent);
        PipeRequester myPipeRequester = new PipeRequester();
        IUhuApp uhuApp = new AndroidApp(this, myPipeRequester);
        myPipeRequester.setApp(uhuApp);
        myPipeRequester.setRegistry(PipeRegistryFactory.getPipeRegistry());

        if (pipeRequest == null) {
            LOGGER.error("Could not register pipe");
        } else {
            //Update PipeRequester Reference
            PipePolicyUtility.pipeRequesterMap.put(pipeRequest.getId(), myPipeRequester);
            try {
                myPipeRequester.requestPipe(pipeRequest);
            }
            catch (PipeApprovalException e) {
                LOGGER.error("Pipe request failed", e);
            }
        }
    }

    /***
     * Set the service as foreground android service
     * */
    public Notification buildForegroundNotification(String filename) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        Intent notificationIntent;
        PackageManager manager = getPackageManager();

        notificationIntent = manager.getLaunchIntentForPackage(getApplicationContext().getPackageName());

        if(notificationIntent == null)
        {
            notificationIntent = new Intent(Intent.ACTION_MAIN);
        }

        notificationIntent.setAction(Intent.ACTION_MAIN);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,0);

        notification.setOngoing(true);

        notification.setContentIntent(pendingIntent);
        BitmapFactory.decodeResource(getResources(), R.drawable.upa_notification_s);
        notification.setContentTitle(getString(R.string.app_name))
                .setContentText(filename)
                        /** Changed notification icon to white color. */

                        //.setLargeIcon(bm)
                .setSmallIcon(R.drawable.upa_notification_s)
                .setTicker(getString(R.string.app_name)
                );

        return notification.build();
    }

    // TODO :use iBinder interface to send the handle reference
    /** get the sphere object handle.  */
    public static boolean sendLoggingServiceMsgToClients(final String[] selSpheres,
                                                         final String[] tempLoggingSphereList, boolean isActivate)
    {
        LogServiceActivatorDeactivator.sendLoggingServiceMsgToClients(UhuStackHandler.getUhuComms(), selSpheres, tempLoggingSphereList, isActivate);
        return true;
    }

    // TODO :use iBinder interface to send the handle reference
    public static IUhuSphereAPI getSphereHandle()
    {
        return UhuStackHandler.getSphereForAndroid();
    }
    // TODO :use iBinder interface to send the handle reference
    /** get the pipe registry handle */

    public static PipeRegistry getPipeRegistryHandle()
    {
        return PipeRegistryFactory.getPipeRegistry();
    }

    /**
     * sends the broadcast with the passed intent
     * @param intent
     */
    @Override
    public void sendBroadCast(Intent intent) {
        sendBroadcast(intent);
    }

    private void registerToWifiBroadcastReceivers(BroadcastReceiver broadcastReceiver) {
        //register to the broadcast receiver even if wifi sate is off, so that after wifi on, it will be detected.
        IntentFilter connectedFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectedFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        connectedFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        connectedFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        connectedFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // register the service only once.
        registerReceiver(broadcastReceiver, connectedFilter);
    }

    public NetworkBroadCastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }
}