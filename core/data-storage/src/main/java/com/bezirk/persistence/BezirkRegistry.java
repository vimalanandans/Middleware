/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.pubsubbroker.PubSubBrokerRegistry;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Database entity mapped to the RDBMS table. This class holds all the data structures that needs to be
 * persisted.
 */
@DatabaseTable(tableName = "uhuregistry")
public class BezirkRegistry {
    @DatabaseField(id = true, columnName = DBConstants.COLUMN_0)
    private int id;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = DBConstants.COLUMN_1)
    private PubSubBrokerRegistry pubSubBrokerRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = DBConstants.COLUMN_2)
    private SphereRegistry sphereRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = DBConstants.COLUMN_3)
    private BezirkProxyRegistry bezirkProxyRegistry;


    public BezirkRegistry() {
        // TODO Auto-generated constructor stub
    }

    public BezirkRegistry(int id, PubSubBrokerRegistry pubSubBrokerRegistry,
                          SphereRegistry sphereRegistry, BezirkProxyRegistry bezirkProxyRegistry) {
        super();
        this.id = id;
        this.pubSubBrokerRegistry = pubSubBrokerRegistry;
        this.sphereRegistry = sphereRegistry;
        this.bezirkProxyRegistry = bezirkProxyRegistry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PubSubBrokerRegistry getPubSubBrokerRegistry() {
        return pubSubBrokerRegistry;
    }

    public void setPubSubBrokerRegistry(PubSubBrokerRegistry pubSubBrokerRegistry) {
        this.pubSubBrokerRegistry = pubSubBrokerRegistry;
    }

    public SphereRegistry getSphereRegistry() {
        return sphereRegistry;
    }

    public void setSphereRegistry(SphereRegistry sphereRegistry) {
        this.sphereRegistry = sphereRegistry;
    }

    public BezirkProxyRegistry getBezirkProxyRegistry() {
        return bezirkProxyRegistry;
    }

    public void setBezirkProxyRegistry(BezirkProxyRegistry bezirkProxyRegistry) {
        this.bezirkProxyRegistry = bezirkProxyRegistry;
    }

}
