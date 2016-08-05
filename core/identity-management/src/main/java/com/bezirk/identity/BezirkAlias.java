package com.bezirk.identity;

import com.bezirk.middleware.identity.Alias;

import java.util.Arrays;

public class BezirkAlias {
    private final String name;
    private final byte[] hash;

    public BezirkAlias(String name, byte[] hash) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot create an alias with a null name");
        }

        if (hash == null) {
            throw new IllegalArgumentException("Cannot create an alias with a null hash");
        }

        this.name = name;
        this.hash = new byte[hash.length];
        System.arraycopy(hash, 0, this.hash, 0, hash.length );
    }

    public String getName() { return name; }

    public byte[] getHash() { return hash; }

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
