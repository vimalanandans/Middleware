package com.bezirk.spheremanager.ui.listitems;


import java.net.URI;

/**
 * Represents a pipe with a name.
 *
 * @see CloudPipe
 */
public class Pipe {
    protected String type = getClass().getCanonicalName();
    private String name;
    private URI uri;

    public Pipe() {
        //Empty Constructor for gson.fromJson
    }

    /**
     * @param pName suggested name for the pipe - which may be changed by the user via UhU UIs
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

    /**
     * Comparison realized by each subclass of Pipe.
     *
     * @param thatPipe Another pipe to compare to this one
     * @return whether thatPipe refers to the same end point as this, regardless of what the user-approved policy for the pipe currently is.
     */
//	public boolean equals(Pipe thatPipe) {
//		if (thatPipe instanceof CloudPipe) {
//			URI thatUri = ((CloudPipe) thatPipe).getURI();
//			return this.uri.equals(thatUri);
//		} 
//		else  {
//			return false;
//		}
//	}
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
