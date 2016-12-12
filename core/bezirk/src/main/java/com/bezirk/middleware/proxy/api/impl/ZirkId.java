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
package com.bezirk.middleware.proxy.api.impl;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class ZirkId implements Serializable {
    private static final long serialVersionUID = -6695395311323807495L;
    private final String zirkId;
    private String bezirkEventId;

    public ZirkId(@NotNull String zirkId) {
        this.zirkId = zirkId;
    }

    public ZirkId(@NotNull String zirkId, String bezirkEventId) {
        this.zirkId = zirkId;
        this.bezirkEventId = bezirkEventId;
    }

    @NotNull
    public String getZirkId() {
        return zirkId;
    }

    public String getBezirkEventId() {
        return bezirkEventId;
    }

    @Override
    public String toString() {
        return "ZirkId{" +
                "zirkId='" + zirkId + '\'' +
                "bezirkEventId='" + bezirkEventId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((bezirkEventId == null) ? 0 : bezirkEventId.hashCode());
        result = prime * result
                + ((zirkId == null) ? 0 : zirkId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZirkId other = (ZirkId) obj;
        if (bezirkEventId == null) {
            if (other.bezirkEventId != null)
                return false;
        } else if (!bezirkEventId.equals(other.bezirkEventId))
            return false;
        if (zirkId == null) {
            if (other.zirkId != null)
                return false;
        } else if (!zirkId.equals(other.zirkId))
            return false;
        return true;
    }
}