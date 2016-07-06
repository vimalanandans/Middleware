package com.bezirk.protocols.policy;

import com.bezirk.middleware.messages.Event;

import java.util.Set;


public class PrivacyPolicy extends Event {

    /**
     * topic
     */
    public static final String topic = PrivacyPolicy.class.getSimpleName();

    public PrivacyPolicy() {
        super(Flag.NOTICE, topic);
    }

    private String user;
    private Set<BackupPolicy> backupPolicies;
    private Set<CollectionPolicy> collectionPolicies;
    private Set<DistributionPolicy> distributionPolicies;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Set<BackupPolicy> getBackupPolicies() {
        return backupPolicies;
    }

    public void setBackupPolicies(Set<BackupPolicy> backupPolicies) {
        this.backupPolicies = backupPolicies;
    }

    public Set<CollectionPolicy> getCollectionPolicies() {
        return collectionPolicies;
    }

    public void setCollectionPolicies(Set<CollectionPolicy> collectionPolicies) {
        this.collectionPolicies = collectionPolicies;
    }

    public Set<DistributionPolicy> getDistributionPolicies() {
        return distributionPolicies;
    }

    public void setDistributionPolicies(Set<DistributionPolicy> distributionPolicies) {
        this.distributionPolicies = distributionPolicies;
    }

    public static PrivacyPolicy deserialize(String json) {
        return Event.fromJson(json, PrivacyPolicy.class);
    }

}
