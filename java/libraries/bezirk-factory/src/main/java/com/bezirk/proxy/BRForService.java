package com.bezirk.proxy;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.IBezirkListener;
import com.bezirk.api.IBezirkListener.StreamConditions;
import com.bezirk.api.addressing.DiscoveredService;
import com.bezirk.api.addressing.ServiceEndPoint;
import com.bosch.upa.uhu.callback.pc.IBoradcastReceiver;
import com.bosch.upa.uhu.messagehandler.DiscoveryIncomingMessage;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.ServiceIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BRForService implements IBoradcastReceiver{
    private static final Logger LOGGER = LoggerFactory.getLogger(BRForService.class);
    
    
    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;
    private static final LinkedHashMap<String, Long> duplicateMsgMap = new LinkedHashMap<String, Long>();
    private static final LinkedHashMap<String, Long> duplicateStreamMap = new LinkedHashMap<String, Long>();
    private final HashMap<String,String> activeStreams;
    private final HashMap<UhuServiceId, Proxy.DiscoveryBookKeeper> dListenerMap;
    private final HashMap<String, HashSet<IBezirkListener>> eventListenerMap;
    private final HashMap<UhuServiceId,HashSet<IBezirkListener>> sidMap;
    private final HashMap<String, HashSet<IBezirkListener>> streamListenerMap;
    
     /**
     * @param activeStreams
     * @param dListenerMap
     * @param eventListenerMap
     * @param sidMap
     * @param streamListenerMap
     */
    public BRForService(HashMap<String,String> activeStreams,
            HashMap<UhuServiceId, Proxy.DiscoveryBookKeeper> dListenerMap,
            HashMap<String, HashSet<IBezirkListener>> eventListenerMap,
            HashMap<UhuServiceId, HashSet<IBezirkListener>> sidMap,
            HashMap<String, HashSet<IBezirkListener>> streamListenerMap) {
        super();
        this.activeStreams = activeStreams;
        this.dListenerMap = dListenerMap;
        this.eventListenerMap = eventListenerMap;
        this.sidMap = sidMap;
        this.streamListenerMap = streamListenerMap;
    }

    @Override
	public void onReceive(ServiceIncomingMessage incomingMessage) {
            if(sidMap.containsKey(incomingMessage.getRecipient())){
                switch(incomingMessage.getCallbackType()){
                case "EVENT":
                	EventIncomingMessage eventCallbackMessage = (EventIncomingMessage) incomingMessage;
                    handleEventCallback(eventCallbackMessage);
                    break;
                case "STREAM_UNICAST":
                	StreamIncomingMessage strmMsg = (StreamIncomingMessage) incomingMessage;
                    handlerStreamUnicastCallback(strmMsg);
                    break;  
                case "STREAM_STATUS":
                	StreamStatusMessage streamStatusCallbackMessage = (StreamStatusMessage) incomingMessage;
                    handleStreamStatusCallback(streamStatusCallbackMessage);
                    break;
                case "DISCOVERY":
                	DiscoveryIncomingMessage discObj = (DiscoveryIncomingMessage) incomingMessage;
                    handleDiscoveryCallback(discObj);
                    break;
             /*	Not used   
              * case "MULTICAST_STREAM":
                    MulticastCallbackMessage multicastCallbackMessage = (MulticastCallbackMessage) incomingMessage;
                    handleMulticastCallback(multicastCallbackMessage);
                    break;*/
                default :
                     LOGGER.error("Unknown incoming message type : "+incomingMessage.getCallbackType());
                }
            }

        }

  /*      private void handleMulticastCallback(
                MulticastCallbackMessage multicastCallbackMessage) {
            LOGGER.debug("Not yet implemented");
        }*/

        /**
         * Handles the Event Callback Message and gives the callback to the services. It is being invoked from
         * Platform specific IUhuCallback implementation.
         * @param eCallbackMessage
         */
        private void handleEventCallback(EventIncomingMessage eCallbackMessage){
            LOGGER.debug("About to callback sid:"+eCallbackMessage.getRecipient().getUhuServiceId()+" for id:"+eCallbackMessage.msgId);
            //Make a combined sid for sender and recipient
            String combinedSid = eCallbackMessage.senderSEP.serviceId.getUhuServiceId() +":"+ eCallbackMessage.getRecipient().getUhuServiceId();
            if(checkDuplicateMsg(combinedSid, eCallbackMessage.msgId)){
                HashSet<IBezirkListener> tempListenersSidMap = sidMap.get(eCallbackMessage.getRecipient());
                HashSet<IBezirkListener> tempListenersTopicsMap = eventListenerMap.get(eCallbackMessage.eventTopic);
                if(null !=tempListenersSidMap  && null != tempListenersTopicsMap){
                    Iterator<IBezirkListener> listenerIterator = tempListenersSidMap.iterator();
                    while(listenerIterator.hasNext()){
                        IBezirkListener invokingListener = listenerIterator.next();
                        if(tempListenersTopicsMap.contains(invokingListener)){
                            invokingListener.receiveEvent(eCallbackMessage.eventTopic, eCallbackMessage.serialzedEvent, (ServiceEndPoint)eCallbackMessage.senderSEP);
                        }
                    }
                }
            }else{
                LOGGER.info("Duplicate Event Received");
            }
        }

        /**
         * Handle the stream Unicast callback.This is called from platform specific IUhuCallback Implementation.
         * @param strmMsg streamMessage that will be given back to the services.
         */
        private void handlerStreamUnicastCallback(StreamIncomingMessage strmMsg){
            if(checkDuplicateStream(strmMsg.senderSEP.serviceId.getUhuServiceId(),strmMsg.localStreamId)){
                if (streamListenerMap.containsKey(strmMsg.streamTopic)) {
                    for (IBezirkListener listener : streamListenerMap.get(strmMsg.streamTopic)) {
                        listener.receiveStream(strmMsg.streamTopic, strmMsg.serialzedStream,strmMsg.localStreamId, strmMsg.filePath, strmMsg.senderSEP);
                    }
                    return;
                }
                LOGGER.error(" StreamListnerMap doesnt have a mapped Stream");
                return;
            }
            LOGGER.error("Duplicate Stream Request Received");
        }

        /**
         * Handles the Stream Status callback and gives the callback to the service. This is called from
         * platform specific IUhuCallback Implementation.
         * @param streamStatusCallbackMessage StreamStatusCallback that will be invoked for the services.
         */
        private void handleStreamStatusCallback(StreamStatusMessage streamStatusCallbackMessage){
            String activeStreamkey = streamStatusCallbackMessage.getRecipient().getUhuServiceId() + streamStatusCallbackMessage.streamId;
            if(activeStreams.containsKey(activeStreamkey)){ 
                HashSet<IBezirkListener> tempHashSet = streamListenerMap.get(activeStreams.get(activeStreamkey));
                if (tempHashSet!=null && !tempHashSet.isEmpty()) { 
                    for (IBezirkListener listner : tempHashSet) {
                        listner.streamStatus(
                                streamStatusCallbackMessage.streamId,
                                ((1 == streamStatusCallbackMessage.streamStatus) ? StreamConditions.END_OF_DATA
                                        : StreamConditions.LOST_CONNECTION));
                    }
                }
                activeStreams.remove(activeStreamkey);
            }
        }

        /**
         * handles the DiscoveryCallback for the Services. This is called from Platform specific IUhuCallback Implementation.
         * @param discObj - callbackObject to the Service.
         */
        private void handleDiscoveryCallback(DiscoveryIncomingMessage discObj){
            if(dListenerMap.containsKey(discObj.getRecipient()) &&  dListenerMap.get(discObj.getRecipient()).getDiscoveryId()==discObj.discoveryId){
                final Gson gson = new Gson();
                final String discoveredListAsString = discObj.discoveredList;
                //Deserialiaze
                Type discoveredListType = new TypeToken<HashSet<UhuDiscoveredService>>() {
                }.getType();

                final HashSet<UhuDiscoveredService> discoveredList  = gson.fromJson(discoveredListAsString, discoveredListType);

                if(null == discoveredList || discoveredList.isEmpty()){
                    LOGGER.error("Empty discovered List");
                    return;
                }

                dListenerMap.get(discObj.getRecipient()).getListener().discovered(new HashSet<DiscoveredService>(discoveredList));
            }
        }

        /**
         * This method is used to check if the stream is a duplicate
         */
        private boolean checkDuplicateStream(final String sid, final int streamId) {
            final String key = sid + ":" + streamId;
            final Long currentTime = new Date().getTime();
            if (duplicateStreamMap.containsKey(key)) {
                
                if (currentTime - duplicateStreamMap.get(key) > TIME_DURATION) {
                    duplicateStreamMap.remove(key);
                    duplicateStreamMap.put(key, currentTime);
                    return true;
                } else{
                    
                    return false;
                }
            } else {
                
                if (duplicateStreamMap.size() < MAX_MAP_SIZE) {
                    duplicateStreamMap.put(key, currentTime);
                    return true;
                } else {
                    duplicateStreamMap.remove(duplicateStreamMap.keySet().iterator().next());
                    duplicateStreamMap.put(key, currentTime);
                    return true;
                }
            }
        }

        /**
         * This method is used to check if the Event is a duplicate
         */
        private boolean checkDuplicateMsg(final String sid, final String messageId) {
            final String key = sid + ":" + messageId;
            final Long currentTime = new Date().getTime();
            if (duplicateMsgMap.containsKey(key)) {
                
                if (currentTime - duplicateMsgMap.get(key) > TIME_DURATION) {
                    duplicateMsgMap.remove(key);
                    duplicateMsgMap.put(key, currentTime);
                    return true;
                } else{
                    
                    return false;
                }
                
                
            } else {

                if (duplicateMsgMap.size() < MAX_MAP_SIZE) {
                    duplicateMsgMap.put(key, currentTime);
                    return true;
                } else {
                    duplicateMsgMap.remove(duplicateMsgMap.keySet().iterator().next());
                    duplicateMsgMap.put(key, currentTime);
                    return true;
                }
            
            }
        }

		

}
