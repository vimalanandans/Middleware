/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.datastorage;

import com.bezirk.pubsubbroker.PubSubBrokerRegistry;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Database entity mapped to the RDBMS table. This class holds all the data structures that needs to be
 * persisted.
 */
@DatabaseTable(tableName = "registry")
public class PersistenceRegistry {
    @DatabaseField(id = true, columnName = PersistenceConstants.COLUMN_0)
    private int id;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = PersistenceConstants.COLUMN_1)
    private PubSubBrokerRegistry pubSubBrokerRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = PersistenceConstants.COLUMN_2)
    private SphereRegistry sphereRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = PersistenceConstants.COLUMN_3)
    private ProxyRegistry proxyRegistry;


    public PersistenceRegistry() {
        // TODO Auto-generated constructor stub
    }

    public PersistenceRegistry(int id, PubSubBrokerRegistry pubSubBrokerRegistry,
                               SphereRegistry sphereRegistry, ProxyRegistry proxyRegistry) {
        super();
        this.id = id;
        this.pubSubBrokerRegistry = pubSubBrokerRegistry;
        this.sphereRegistry = sphereRegistry;
        this.proxyRegistry = proxyRegistry;
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

    public ProxyRegistry getProxyRegistry() {
        return proxyRegistry;
    }

    public void setProxyRegistry(ProxyRegistry proxyRegistry) {
        this.proxyRegistry = proxyRegistry;
    }

}
