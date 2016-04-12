/**
 * 
 */
package com.bezirk.api.objects;

/**
 * @author Rishabh Gulati
 *
 */
public class UhuPipeInfo {
	
	private final String pipeId;

	private final String pipeName;

	private final String pipeURL;
	
	/**
	 * @param pipeId
	 * @param pipeName
	 * @param pipeURL
	 */
	public UhuPipeInfo(final String pipeId, final String pipeName, final String pipeURL) {
		this.pipeId = pipeId;
		this.pipeName = pipeName;
		this.pipeURL = pipeURL;
	}

	/**
	 * @return the pipeId
	 */
	public final String getPipeId() {
		return pipeId;
	}

	/**
	 * @return the pipeName
	 */
	public final String getPipeName() {
		return pipeName;
	}

	/**
	 * @return the pipeURL
	 */
	public final String getPipeURL() {
		return pipeURL;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UhuPipeInfo [pipeId=" + pipeId + ",\npipeName=" + pipeName
				+ ",\npipeURL=" + pipeURL + "]";
	}

	
	
}
