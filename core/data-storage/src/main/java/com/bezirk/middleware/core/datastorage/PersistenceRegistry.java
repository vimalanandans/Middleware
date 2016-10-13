/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.datastorage;

import com.bezirk.middleware.core.pubsubbroker.PubSubBrokerRegistry;
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
