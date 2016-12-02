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
package com.bezirk.middleware.identity;


import java.io.Serializable;
import java.util.Arrays;

/**
 * <h1 style="color: red">Experimental</h1>
 *
 * An alias is an identity assigned to a running instance of the middleware. This identity is used
 * to make authorization decisions for the user of a middleware instance, to segment preferences
 * for shared middleware instances, and to uniquely identify a middleware user that a message is
 * about (i.e. the person or Thing whose alias is assigned to the middleware instance). Aliases
 * are created and managed by the middleware.
 */
public class Alias implements Serializable {

    private static final long serialVersionUID = -7970996737976317494L;
    private final String name;
    private final byte[] hash;

    public Alias(String name, byte[] hash) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot create an alias with a null name");
        }

        if (hash == null) {
            throw new IllegalArgumentException("Cannot create an alias with a null hash");
        }

        this.name = name;
        this.hash = new byte[hash.length];
        System.arraycopy(hash, 0, this.hash, 0, hash.length);
    }

    /**
     * Returns the human-readable identifier for this alias. This identifier may be a person's name,
     * a Thing's assigned name (e.g. "Antibiotic Box 3456"), etc.
     *
     * @return the human-readable identifier for this alias
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the identifier assigned this alias to ensure the alias is unique.
     *
     * @return the unique identifier for this alias
     */
    public byte[] getHash() {
        byte[] hash = new byte[this.hash.length];
        System.arraycopy(this.hash, 0, hash, 0, this.hash.length);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alias alias = (Alias) o;

        return name.equals(alias.getName()) && Arrays.equals(hash, alias.getHash());

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(hash);
        return result;
    }
}
