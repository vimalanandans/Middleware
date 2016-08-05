package com.bezirk.middleware.identity;


/**
 * An alias is an identity assigned to a running instance of the middleware. This identity is used
 * to make authorization decisions for the user of a middleware instance, to segment preferences
 * for shared middleware instances, and to uniquely identify a middleware user that a message is
 * about (i.e. the person or Thing whose alias is assigned to the middleware instance). Aliases
 * are created and managed by the middleware.
 */
public interface Alias {
    /**
     * Returns the human-readable identifier for this alias. This identifier may be a person's name,
     * a Thing's assigned name (e.g. "Antibiotic Box 3456"), etc.
     *
     * @return the human-readable identifier for this alias
     */
    String getName();

    /**
     * Returns the identifier assigned this alias to ensure the alias is unique.
     *
     * @return the unique identifier for this alias
     */
    byte[] getHash();
}
