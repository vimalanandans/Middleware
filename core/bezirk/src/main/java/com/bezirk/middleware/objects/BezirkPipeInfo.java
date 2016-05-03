/**
 *
 */
package com.bezirk.middleware.objects;

/**
 * @author Rishabh Gulati
 */
public class BezirkPipeInfo {

    private final String pipeId;

    private final String pipeName;

    private final String pipeURL;

    public BezirkPipeInfo(final String pipeId, final String pipeName, final String pipeURL) {
        this.pipeId = pipeId;
        this.pipeName = pipeName;
        this.pipeURL = pipeURL;
    }

    public final String getPipeId() {
        return pipeId;
    }

    public final String getPipeName() {
        return pipeName;
    }

    public final String getPipeURL() {
        return pipeURL;
    }

    @Override
    public String toString() {
        return "BezirkPipeInfo [pipeId=" + pipeId + ",\npipeName=" + pipeName
                + ",\npipeURL=" + pipeURL + "]";
    }


}
