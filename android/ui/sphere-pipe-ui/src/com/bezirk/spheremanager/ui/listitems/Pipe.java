package com.bezirk.spheremanager.ui.listitems;


import java.net.URI;

/**
 * Represents a pipe with a name.
 */
public class Pipe {
    protected String type = getClass().getCanonicalName();
    private String name;
    private URI uri;

    public Pipe() {
        //Empty Constructor for gson.fromJson
    }

    /**
     * @param pName suggested name for the pipe - which may be changed by the user via Bezirk UIs
     */
    public Pipe(String pName, URI uri) {
        this.name = pName;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public URI getURI() {
        return uri;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public String toString() {
        return "|" + getClass().getSimpleName() + "," + getName() + "," + getURI() + "|";
    }
}
