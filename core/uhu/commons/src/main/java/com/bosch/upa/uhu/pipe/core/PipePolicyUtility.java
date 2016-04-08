package com.bosch.upa.uhu.pipe.core;

import java.util.HashMap;

import com.bosch.upa.uhu.pipe.policy.ext.UhuPipePolicy;

/**
 * Created by anm1pi on 12/25/2014.
 */
public final class PipePolicyUtility {
	public static final HashMap<String, UhuPipePolicy> policyInMap = new HashMap<String, UhuPipePolicy>();
	public static final HashMap<String, UhuPipePolicy> policyOutMap = new HashMap<String, UhuPipePolicy>();
	public static final HashMap<String, PipeRequester> pipeRequesterMap = new HashMap<String, PipeRequester>();

	private PipePolicyUtility(){
		//this is a utility class which stores selected policies
	}
	
	public static void removeId(String id){
		policyInMap.remove(id);
		policyOutMap.remove(id);
		pipeRequesterMap.remove(id);
	}

}

