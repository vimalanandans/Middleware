package com.bezirk.controlui.commstest;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Util class that stores all the messages that will be sent and acknowledged
 *
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
public class UIStore {
    private final ConcurrentMap<String, Set<PongMessage>> waitingPongList = new ConcurrentHashMap<String, Set<PongMessage>>();

    public void clearStore() {
        waitingPongList.clear();
    }

    public void updatePongStatus(String key, PongMessage message) {
        if (waitingPongList.containsKey(key)) {
            waitingPongList.get(key).add(message);
        }
    }

    public void addToWaitingPongList(String key) {
        if (!waitingPongList.containsKey(key)) {
            Set<PongMessage> pongList = new HashSet<PongMessage>();
            waitingPongList.put(key, pongList);
        }
    }

    public Set<PongMessage> getPongMap(String pingId) {
        return waitingPongList.get(pingId);
    }

    public int getNoOfPongMessages(String pingId) {
        if (waitingPongList.containsKey(pingId)) {
            return waitingPongList.get(pingId).size();
        }
        return 0;
    }

}


