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
package com.bezirk.middleware.objects;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class BezirkZirkInfo {
    private final String zirkId;
    private final String zirkName;
    private final String zirkType;
    private final boolean visible; // true if zirk is visible
    private boolean active;

    @Deprecated
    public BezirkZirkInfo(ZirkId zirkId, String zirkName, String zirkType, boolean active,
                          boolean visible) {
        this.zirkId = zirkId.getZirkId();
        this.zirkType = zirkType;
        this.active = active;
        this.visible = visible;
        this.zirkName = zirkName;
    }

    public BezirkZirkInfo(String zirkId, String zirkName, String zirkType, boolean active,
                          boolean visible) {
        this.zirkId = zirkId;
        this.zirkType = zirkType;
        this.active = active;
        this.visible = visible;
        this.zirkName = zirkName;
    }

    /**
     * @return the zirkId
     */
    @Deprecated
    public final ZirkId getBezirkZirkId() {
        return new ZirkId(zirkId);
    }

    public String getZirkId() {
        return zirkId;
    }

    public final String getZirkName() {
        return zirkName;
    }

    /**
     * @return the zirkType
     */
    public final String getZirkType() {
        return zirkType;
    }

    /**
     * @return the active
     */
    public final boolean isActive() {
        return active;
    }

    public void setActive(boolean status) {
        active = status;
    }

    /**
     * @return the visible
     */
    public final boolean isVisible() {
        return visible;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BezirkZirkInfo [zirkId=" + zirkId + ",\nzirkName="
                + zirkName + ",\nzirkType=" + zirkType + ",\nactive="
                + active + ",\nvisible=" + visible + "]";
    }


}
