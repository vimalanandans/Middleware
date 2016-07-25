package com.bezirk.actions;

import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

public class SubscriptionAction extends ZirkAction {
    private BezirkAction action;
    private final String role;

    public SubscriptionAction(BezirkAction action, ZirkId zirkId, ProtocolRole role) {
        super(zirkId);

        // Role can be null for unsubscribing from all roles
        if (role != null &&
                role.getEventTopics() != null && role.getStreamTopics() != null &&
                role.getEventTopics().length == 0 && role.getStreamTopics().length == 0) {
            throw new IllegalArgumentException("Role must be non-null and must subscribe to at " +
                    "least one event or stream");
        }

        this.action = action;
        this.role = new SubscribedRole(role).toJson();
    }

    public ProtocolRole getRole() {
        return SubscribedRole.fromJson(role);
    }

    public BezirkAction getAction() {
        return action;
    }

    private static class SubscribedRole extends ProtocolRole {
        private static final Gson gson = new Gson();

        private String protocolName;
        private String protocolDesc;
        private String[] eventTopics;
        private String[] streamTopics;

        public SubscribedRole() {
            //Empty constructor required for gson.fromJson
        }

        public SubscribedRole(ProtocolRole pRole) {
            protocolName = pRole.getRoleName();
            protocolDesc = pRole.getDescription();
            eventTopics = pRole.getEventTopics();
            streamTopics = pRole.getStreamTopics();
        }

        public static ProtocolRole fromJson(String json) {
            return gson.fromJson(json, SubscribedRole.class);
        }

        public String toJson() {
            return gson.toJson(this);
        }

        @Override
        public String getRoleName() {
            return protocolName;
        }

        @Override
        public String getDescription() {
            return protocolDesc;
        }

        @Override
        public String[] getEventTopics() {
            return eventTopics == null ? null : eventTopics.clone();
        }

        @Override
        public String[] getStreamTopics() {
            return streamTopics == null ? null : streamTopics.clone();
        }
    }
}
