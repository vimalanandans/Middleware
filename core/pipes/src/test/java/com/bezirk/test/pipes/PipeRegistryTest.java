package com.bezirk.test.pipes;

import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipeRecord;
import com.bezirk.pipe.core.PipeRegistry;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static org.junit.Assert.*;

public class PipeRegistryTest {

    private static final Logger log = LoggerFactory.getLogger(PipeRegistryTest.class);

    private PipeRegistry registry = new PipeRegistry();

    @Before
    public void beforeEachTest() throws Exception {
        log.info("before test...");
    }

	/*
	 * Test success cases
	 */

    /**
     * Test adding a new pipe to the registry
     *
     * @throws Exception
     */
    @Test
    public void testAddNewPipe() throws Exception {
        URI uri = new URI("http://bosch.com");
        CloudPipe pipe = new CloudPipe("bosch", uri);

        // Add the pipe
        PipeRecord record = registry.add(pipe, null, null);

        //Assert that the pipe exists and is the same one we tried to add
        URI newuri = new URI("http://bosch.com");
        CloudPipe newpipe = new CloudPipe("bosch", newuri);
        assertTrue(registry.isRegistered(newpipe));
        assertTrue(pipe.equals(record.getPipe()));
    }

    @Test
    public void testPipeEquals() throws Exception {
        URI uri = new URI("http://bosch.com");
        Pipe pipe = new CloudPipe("bosch", uri);

        //Assert that the pipe exists and is the same one we tried to add
        URI newuri = new URI("http://bosch.com");
        Pipe newPipe = new CloudPipe("bosch", newuri);

        assertTrue(pipe.equals(newPipe));
    }


    /**
     * Test updating an existing pipe
     *
     * @throws Exception
     */
    @Test
    public void testUpdatePipe() throws Exception {
        URI uri = new URI("http://bosch.com");
        Pipe pipe = new CloudPipe("bosch", uri);

        // Add the pipe
        PipeRecord origRecord = registry.add(pipe, null, null);

        // Demonstrate that allowedIn is null, as we specified in the add() above
        assertNull(origRecord.getAllowedIn());

        // Update the pipe's record with new policies
        PipePolicy in = new MockPipePolicy();
        registry.update(pipe, in, null);

        // Demonstrate that the allowedIn policies have changed
        assertNotNull(registry.lookup(pipe).getAllowedIn());
    }
	
	/*
	 * Test exception cases
	 */

    /**
     * Test adding the same pipe twice with PipeRegistry.add()
     * This should throw PipeApprovalException since it already
     * exists
     *
     * @throws Exception
     */
    @Test(expected = PipeApprovalException.class)
    public void testAddNewPipeTwice() throws Exception {
        URI uri = new URI("http://bosch.com");
        Pipe pipe = new CloudPipe("bosch", uri);

        // Add the pipe
        registry.add(pipe, null, null);

        // This should throw a PipeApprovalException, since it was added already
        registry.add(pipe, null, null);
    }


    /**
     * Test updating a pipe that has not been added.
     * This should throw pipe exception since PipeRegistry.add()
     * should be used in this case.
     *
     * @throws Exception
     */
    @Test(expected = PipeApprovalException.class)
    public void testUpdateWithoutAdd() throws Exception {
        URI uri = new URI("http://bosch.com");
        Pipe pipe = new CloudPipe("bosch", uri);

        // This should throw an exception since update() is being called before add()
        registry.update(pipe, null, null);
    }

    private class MockPipePolicy extends PipePolicy {
        public boolean isAuthorized(String protocolRoleName) {
            return false;
        }
    }
}
