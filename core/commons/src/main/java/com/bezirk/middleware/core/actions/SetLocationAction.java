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
package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class SetLocationAction extends ZirkAction {

    private static final long serialVersionUID = -3253426082978826042L;
    private final Location location;

    public SetLocationAction(ZirkId zirkId, Location location) {
        super(zirkId);

        if (location == null) {
            throw new IllegalArgumentException("local must be non-null");
        }

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_BEZIRK_SET_LOCATION;
    }
}
