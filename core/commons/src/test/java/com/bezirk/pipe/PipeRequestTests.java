package com.bezirk.pipe;

import com.bezirk.application.IUhuApp;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipePolicyUtility;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.pipe.core.PipeRequest;
import com.bezirk.pipe.core.PipeRequester;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test and validate the pipeRequest call sequence for different cases.
 * This class should be considered the specification for making and
 * responding to pipe requests.
 */
public class PipeRequestTests {

    public final static Logger log = LoggerFactory.getLogger(PipeRequestTests.class);
    private static final String pipeName = "bosch.com";
    private static final ProtocolRole role = new MockProtocolRole();
    private static final PipePolicy allowedIn = new MockPipePolicy();
    private static final PipePolicy allowedOut = new MockPipePolicy();
    private static final PipePolicy nullAllowedInPolicy = null;
    private static final PipePolicy nullAllowedOutPolicy = null;
    private static URI uri;
    private static UhuPipePolicy allowedInPolicy = null;
    private static UhuPipePolicy allowedOutPolicy = null;
    // Used to make requests on behalf a uhu zirk
    private PipeRequester pipeRequester = null;

	
	/* Null means that the policies are open. In other words, 
     * there are no restrictions on the in/out events */
    // Holds the uhu state
    private PipeRegistry registry = null;
    // Interface to the uhu application (e.g., android UI)
    private IUhuApp uhuApp = null;
    // Zirk used for testing purposes only
    private MockUhuZirk mockService = null;
    private CloudPipe pipe;

    /*
     * Methods that set up / tear down test environment
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up PipeRequestTests TestCase *****");

        uri = new URI("http://foo-bar/uhu");
        allowedIn.addAllowedProtocol(role, "Sharing email address with bosch");
        allowedInPolicy = new UhuPipePolicy(allowedIn);
        allowedOut.addAllowedProtocol(role, "Receiving weather updates");
        allowedOutPolicy = new UhuPipePolicy(allowedOut);

    }

    /**
     * Set up environment for each test
     *
     * @throws Exception
     */
    @Before
    public void beforeEachTest() throws Exception {
        log.debug("@Before each test ... ");


        // Piperequester mediates requests from services to the management UI
        pipeRequester = new PipeRequester();

        // Registry stores information on pipes in the system
        registry = new PipeRegistry();

		/* MockUhuApp is a test class that implements IUhuApp and 
		 * acts as the platform specific application for this test */
        MockUhuApp mockApp = new MockUhuApp();
        uhuApp = mockApp;

        mockApp.setPipeAPI(pipeRequester);

		/* A fake uhu "zirk" used for this test that implements UhuAPIListener.
		 * This is used to make sure the pipeGranted() method is called in 
		 * the zirk */
        mockService = new MockUhuZirk();

		/* Pipe requester needs the registry so that it can update pipe
		 * information once it has been approved by the user*/
        pipeRequester.setRegistry(registry);
		
		/* Pipe requester needs a reference to the platform-specific
		 * application so that it can forward requests for pipes */
        pipeRequester.setApp(uhuApp);

        ZirkMessageHandler mockZirkMessageHandler = new MockCallBackZirk(mockService);
        UhuCompManager.setplatformSpecificCallback(mockZirkMessageHandler);
    }

    /**
     * Tear down method executed after each test
     *
     * @throws Exception
     */
    @After
    public void afterEachTest() throws Exception {
        log.debug("@After each test ...");
        // this isn't needed yet, but may be later...

    }
	
	/*
	 * Test cases
	 */

    /**
     * Test requesting a new pipe using PipeRequester and open policies
     *
     * @throws Exception
     */
    @Test
    public void testRequestNewPipe() throws Exception {
        String requestId = getPipeWithNullPolicies(pipeName, uri);

        // Assert that the request was received by the uhu app
        assertTrue("ApprovePipeRequest was not called.", ((MockUhuApp) uhuApp).wasApprovePipeRequestCalled());
        // Assert that a pipeGranted response was sent back to the requesting zirk
        assertTrue("PipeGrant is not called.", mockService.isPipeGrantedCalled());
        // Assert that the pipe was correctly granted
        assertTrue("Pipe is not granted.", mockService.isPipeGranted());
        // Assert that the pipe is now registered
        assertTrue("Pipe is not registered.", registry.isRegistered(pipe));
        // Assert that we can lookup the registered pipe
        assertNotNull("Pipe is not found in registry.", registry.lookup(pipe).getPipe());
        removePipeAndPolicies(requestId);
    }


    /**
     * Test requesting an existing pipe using PipeRequester and open policies
     *
     * @throws Exception
     */
    @Test
    public void testRequestExistingPipe() throws Exception {
        String requestId = requestPipeWithPolicy();

        PipeRequest request = new PipeRequest(UUID.randomUUID().toString());
        request.setPipe(pipe);
        request.setRequestingService(mockService.getServiceId());
        request.setAllowedIn(nullAllowedInPolicy);
        request.setAllowedOut(allowedOut);
        request.setListener(mockService);

        pipeRequester.requestPipe(request);

        assertTrue(((MockUhuApp) uhuApp).wasApprovePipeRequestCalled());
        assertTrue(mockService.isPipeGrantedCalled());
        assertTrue(mockService.isPipeGranted());
        assertTrue(registry.isRegistered(pipe));
        assertNotNull(registry.lookup(pipe));

        removePipeAndPolicies(requestId);

    }


    /**
     * Test requesting a new pipe using PipeRequester and specifying inbound and outbound policies
     *
     * @throws Exception
     */
    @Test
    public void testRequestNewPipeWithPolicies() throws Exception {
        String requestId = requestPipeWithPolicy();

        assertTrue("ApprovePipeRequest was not called.", ((MockUhuApp) uhuApp).wasApprovePipeRequestCalled());
        assertTrue("PipeGrant is not called.", mockService.isPipeGrantedCalled());
        assertTrue("Pipe is not granted.", mockService.isPipeGranted());
        assertTrue("Pipe is not registered.", registry.isRegistered(pipe));
        assertNotNull("AllowedInPolicy is null.", registry.lookup(pipe).getAllowedIn());
        assertNotNull("AllowedOutPolicy is null.", registry.lookup(pipe).getAllowedOut());
        assertNotNull("Password is null in registry.", registry.lookup(pipe).getPassword());
        assertNotNull("SphereId is null in registry.", registry.lookup(pipe).getSphereId());
        assertNotNull("Pipe is not found in registry.", registry.lookup(pipe).getPipe());

        removePipeAndPolicies(requestId);

    }

    private void removePipeAndPolicies(String requestId) {
        registry.remove(pipe);
        PipePolicyUtility.policyInMap.remove(requestId);
        PipePolicyUtility.policyOutMap.remove(requestId);

    }

    /**
     * Test requesting an existing pipe using PipeRequester and specifying inbound and outbound policies
     *
     * @throws Exception
     */
    @Test
    public void testRequestExistingPipeWithPolicies() throws Exception {
        String requestId = requestPipeWithPolicy();


        //Request for same pipe
        PipeRequest request = new PipeRequest(UUID.randomUUID().toString());
        request.setPipe(pipe);
        request.setRequestingService(mockService.getServiceId());
        request.setAllowedIn(allowedIn);
        request.setAllowedOut(allowedOut);
        request.setListener(mockService);

        PipePolicyUtility.policyInMap.put(request.getId(), allowedInPolicy);
        PipePolicyUtility.policyOutMap.put(request.getId(), allowedOutPolicy);

        pipeRequester.requestPipe(request);

        assertTrue("ApprovePipeRequest was not called.", ((MockUhuApp) uhuApp).wasApprovePipeRequestCalled());
        assertTrue("PipeGrant is not called.", mockService.isPipeGrantedCalled());
        assertTrue("Pipe is not granted.", mockService.isPipeGranted());
        assertTrue("Pipe is not registered.", registry.isRegistered(pipe));
        assertNotNull("AllowedInPolicy is null.", registry.lookup(pipe).getAllowedIn());
        assertNotNull("AllowedOutPolicy is null.", registry.lookup(pipe).getAllowedOut());
        assertNotNull("Password is null in registry.", registry.lookup(pipe).getPassword());
        assertNotNull("SphereId is null in registry.", registry.lookup(pipe).getSphereId());
        assertNotNull("Pipe is not found in registry.", registry.lookup(pipe).getPipe());


        //Update Policies for same pipe
        PipePolicy updatedAllowedInPolicy = new MockPipePolicy();
        updatedAllowedInPolicy.addAllowedProtocol(role, "Policy to share numbers");
        PipePolicy updatedAllowedOutPolicy = new MockPipePolicy();
        updatedAllowedOutPolicy.addAllowedProtocol(role, "Policy to verify number");
        request = new PipeRequest(UUID.randomUUID().toString());
        request.setPipe(pipe);
        request.setRequestingService(mockService.getServiceId());
        request.setAllowedIn(updatedAllowedInPolicy);
        request.setAllowedOut(updatedAllowedOutPolicy);
        request.setListener(mockService);

        PipePolicyUtility.policyInMap.put(request.getId(), new UhuPipePolicy(updatedAllowedInPolicy));
        PipePolicyUtility.policyOutMap.put(request.getId(), new UhuPipePolicy(updatedAllowedOutPolicy));
        pipeRequester.requestPipe(request);

        assertTrue("ApprovePipeRequest was not called.", ((MockUhuApp) uhuApp).wasApprovePipeRequestCalled());
        assertTrue("PipeGrant is not called.", mockService.isPipeGrantedCalled());
        assertTrue("Pipe is not granted.", mockService.isPipeGranted());
        assertTrue("Pipe is not registered.", registry.isRegistered(pipe));
        assertEquals("AllowedInPolicy not matching with the set value.", updatedAllowedInPolicy, registry.lookup(pipe).getAllowedIn());
        assertEquals("AllowedOutPolicy not matching with the set value.", updatedAllowedOutPolicy, registry.lookup(pipe).getAllowedOut());
        assertNotNull("Password is null in registry.", registry.lookup(pipe).getPassword());
        assertNotNull("SphereId is null in registry.", registry.lookup(pipe).getSphereId());
        assertNotNull("Pipe is not found in registry.", registry.lookup(pipe).getPipe());

        removePipeAndPolicies(requestId);

    }
	
/*	*//**
     * Test requesting an invalid pipe using PipeRequester. This is expected to
     * return a PipeApprovalException
     * @throws PipeApprovalException
     *//*
	@Test(expected = PipeApprovalException.class )
	public void testRequestInvalidPipe() throws PipeApprovalException {
		String requestId  = getPipeWithNullPolicies(null, null);
		
		removePipeAndPolicies(requestId);

	}
	
	*/

    /**
     * Test requesting a null pipe using PipeRequester. This is expected to
     * return a PipeApprovalException
     *
     * @throws PipeApprovalException
     *//*
	@Test(expected = PipeApprovalException.class )
	public void testRequestNullPipe() throws PipeApprovalException {
		String requestId = getPipeWithNullPolicies(null, null);
		
		removePipeAndPolicies(requestId);

	}*/
    private String getPipeWithNullPolicies(String pipeName, URI uri) throws PipeApprovalException {
        pipe = new CloudPipe(pipeName, uri);
		
		/* Build the pipeRequest object. This is typically done in 
		 * a given platform's "main" method. */
        String requestId = UUID.randomUUID().toString();
        PipeRequest request = new PipeRequest(requestId);
        request.setPipe(pipe);
        request.setRequestingService(mockService.getServiceId());
        request.setAllowedIn(nullAllowedInPolicy);
        request.setAllowedOut(nullAllowedOutPolicy);
        request.setListener(mockService);

        // Make the pipe request
        pipeRequester.requestPipe(request);
        return requestId;
    }

    private String requestPipeWithPolicy() throws PipeApprovalException {
        pipe = new CloudPipe(pipeName, uri);

        String pipeRequestId = UUID.randomUUID().toString();
        PipeRequest request = new PipeRequest(pipeRequestId);
        request.setPipe(pipe);
        request.setRequestingService(mockService.getServiceId());
        request.setAllowedIn(allowedIn);
        request.setAllowedOut(allowedOut);
        request.setListener(mockService);

        PipePolicyUtility.policyInMap.put(request.getId(), allowedInPolicy);
        PipePolicyUtility.policyOutMap.put(request.getId(), allowedOutPolicy);

        pipeRequester.requestPipe(request);
        return pipeRequestId;
    }

    private static class MockPipePolicy extends PipePolicy {
        public boolean isAuthorized(String protocolRoleName) {
            return false;
        }
    }
}

