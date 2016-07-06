/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.pubsubbroker.SadlRegistry;
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
    private SadlRegistry sadlRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = DBConstants.COLUMN_2)
    private SphereRegistry sphereRegistry;
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = DBConstants.COLUMN_3)
    private BezirkProxyRegistry bezirkProxyRegistry;


    public BezirkRegistry() {
        // TODO Auto-generated constructor stub
    }

    public BezirkRegistry(int id, SadlRegistry sadlRegistry,
                          SphereRegistry sphereRegistry, BezirkProxyRegistry bezirkProxyRegistry) {
        super();
        this.id = id;
        this.sadlRegistry = sadlRegistry;
        this.sphereRegistry = sphereRegistry;
        this.bezirkProxyRegistry = bezirkProxyRegistry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SadlRegistry getSadlRegistry() {
        return sadlRegistry;
    }

    public void setSadlRegistry(SadlRegistry sadlRegistry) {
        this.sadlRegistry = sadlRegistry;
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
