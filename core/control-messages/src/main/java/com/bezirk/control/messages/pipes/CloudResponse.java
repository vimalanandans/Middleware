package com.bezirk.control.messages.pipes;

import java.util.List;
import java.util.Map;

/**
 * Requirements:
 * <ul>
 * <li>Represent multi and uni-cast messages</li>
 * <li>Represent stream and regular response messages</li>
 * </ul>
 */
public class CloudResponse {

    /**
     * The full HTTP header received from the web server
     */
    protected Map<String, List<String>> httpHeader;

    /**
     * Bezirk header information needed for routing to local services
     */
    protected PipeHeader pipeHeader = null;

    /**
     * Can be a "regular" event or a stream descriptor for child classes
     */
    protected String serializedEvent;

    /**
     */
    public CloudResponse() {
        // Empty Constructor required for gson.fromJson
    }

	/*
	 * Getters / setters
	 */

    public Map<String, List<String>> getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(Map<String, List<String>> header) {
        this.httpHeader = header;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public void setSerializedEvent(String serializedEvent) {
        this.serializedEvent = serializedEvent;
    }

    public PipeHeader getPipeHeader() {
        return pipeHeader;
    }

    public void setPipeHeader(PipeHeader pipeHeader) {
        this.pipeHeader = pipeHeader;
    }
}
