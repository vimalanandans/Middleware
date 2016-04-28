package com.bezirk.pipe.core;

import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to register and retrieve information on all bezirk pipes in a bezirk instance
 */
public class PipeRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PipeRegistry.class);
    private final Map<Pipe, PipeRecord> pipeMap = Collections.synchronizedMap(new HashMap<Pipe, PipeRecord>());

	/*
	 * Retrieval functionality
	 */

    /**
     * Check if the specified pipe is registered with bezirk
     *
     * @param pipe
     * @return True if the pipe is registered
     */
    public boolean isRegistered(Pipe pipe) {
        return pipe != null && pipeMap.containsKey(pipe);
    }

    /**
     * Return the PipeRecord for the specified pipe.
     *
     * @param pipe
     * @returns Null if the pipe is not registered
     */
    public PipeRecord lookup(Pipe pipe) {
        return pipeMap.get(pipe);
    }

    /**
     * Return all registered PipeRecords
     *
     * @return
     */
    public Collection<PipeRecord> allPipes() {
        return pipeMap.values();
    }

    /**
     * Remove the specified pipe
     *
     * @param pipe
     * @return false if the pipe doesn't exist
     */
    public boolean remove(Pipe pipe) {

        PipeRecord removed = pipeMap.remove(pipe);

        // Pipe does not exist if map.remove() returns null
        if (removed == null) {
            return false;
        }

        return true;
    }
	
	/*
	 * Creation and modification functionality
	 */

    /**
     * Add a new pipe
     *
     * @param pipe
     * @param allowedIn
     * @param allowedOut
     * @param sphereId
     * @param password
     * @return
     * @throws PipeApprovalException
     */
    public PipeRecord add(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut, String sphereId, String password) throws PipeApprovalException {
        validatePipe(pipe);

        if (isRegistered(pipe)) {
            throw new PipeApprovalException("Could not add new pipe because it is already registered: " + pipe);
        }

        PipeRecord record = new PipeRecord(pipe);
        record.setAllowedIn(allowedIn);
        record.setAllowedOut(allowedOut);
        record.setSphereId(sphereId);
        record.setPassword(password);

        pipeMap.put(pipe, record);

        return record;
    }

    public PipeRecord add(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut) throws PipeApprovalException {
        return add(pipe, allowedIn, allowedOut, "", "");
    }

    /**
     * @param pipe
     * @param allowedIn
     * @param allowedOut
     * @return
     * @throws PipeApprovalException
     */
    public PipeRecord update(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut) throws PipeApprovalException {
        return update(pipe, allowedIn, allowedOut, "", "");
    }

    /**
     * @param pipe
     * @param allowedIn
     * @param allowedOut
     * @throws PipeApprovalException If pipe is invalid or pipe not registered
     */
    public PipeRecord update(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut, String sphereId, String password) throws PipeApprovalException {
        validatePipe(pipe);

        if (!isRegistered(pipe)) {
            throw new PipeApprovalException("Can't update a pipe that has not been added yet: " + pipe);
        }

        logger.info("Pipe already registered, updating");
        PipeRecord record = pipeMap.get(pipe);

        // TODO: validate these fields instead of just setting

        record.setAllowedIn(allowedIn);
        record.setAllowedOut(allowedOut);
        record.setSphereId(sphereId);
        record.setPassword(password);

        pipeMap.put(pipe, record);

        return record;
    }

	/*
	 * Helper methods
	 */
    private void validatePipe(Pipe pipe) throws PipeApprovalException {
        String prefix = "Could not register pipe. ";

        if (pipe == null) {
            throw new PipeApprovalException(prefix + "Pipe is null");
        }

        if (pipe.getName() == null || pipe.getName().isEmpty()) {
            throw new PipeApprovalException(prefix + "Pipe name not defined");
        }
    }
}
