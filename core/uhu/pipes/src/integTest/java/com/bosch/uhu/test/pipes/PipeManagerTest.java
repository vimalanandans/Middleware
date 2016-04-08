package com.bezirk.test.pipes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.samples.protocols.EchoReply;
import com.bezirk.samples.protocols.EchoRequest;
import com.bezirk.samples.protocols.FileReply;
import com.bezirk.samples.protocols.FileRequest;
import com.bezirk.test.util.TestUtils;
import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.CloudPipe;
import com.bezirk.api.addressing.Pipe;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.bosch.upa.uhu.pipe.mgr.LocalUhuSender;
import com.bosch.upa.uhu.pipe.mgr.PipeManager;
import com.bosch.upa.uhu.pipe.mgr.PipeManagerImpl;
import com.bosch.upa.uhu.pipe.mgr.PipeRegistry;

public class PipeManagerTest {
	
	private static Logger log = LoggerFactory.getLogger(PipeManagerTest.class);
	
	private PipeManager pipeManager = null;
	
	private Pipe boschPipe = null;

	private EchoRequest echoRequest = null;

	private MockLocalSender localSender = new MockLocalSender();

	private static final String TEMP_FILE_PREFIX = "testFile";
	private static final String TEMP_FILE_SUFFIX = ".txt";
	
	private String tempFileName = TEMP_FILE_PREFIX + TEMP_FILE_SUFFIX;
	
	@BeforeClass
    public static void setUpTestSuite() throws Exception {
		if (!PipesTestSuite.isWebServerRunning()) {
			PipesTestSuite.setUpTestSuite();
		}
	}
	
	@AfterClass
	public static void cleanUpAfterAllTests() throws Exception {
		log.info("cleaning up ...");
	}
	
	@Before
	public void beforeEachTest() throws Exception {
		// Set up data members needed by PipeManager
		PipeRegistry pipeRegistry = new PipeRegistry();
		URI uri = new URI(TestUtils.URL_UHUCLOUD_LOCALHOST);
		boschPipe = new CloudPipe("BoschPipe", uri);
		pipeRegistry.registerPipe(boschPipe, null, null);
		
		// Clear flags indicating if local sender methods called
		localSender.setInvokeIncomingCalled(false);
		localSender.setInvokeReceiveCalled(false);

		// Set up and initialize pipe manager
		PipeManagerImpl pipeManagerImpl = new PipeManagerImpl();
		pipeManagerImpl.setPipeRegistry(pipeRegistry);
		pipeManagerImpl.setLocalSender(localSender);
		File outputDir = Files.createTempDirectory("temp").toFile();
		pipeManagerImpl.setOutputDir(outputDir);
		pipeManagerImpl.init();
		pipeManager = pipeManagerImpl;

		// The request event to send
		echoRequest = new EchoRequest();
		echoRequest.setText("Hi mom");
	}
	
	@Test
	public void testSendEvent() throws Exception {
		MulticastHeader multicastHeader = new MulticastHeader();
		Address address = new Address(null, boschPipe, false);
		multicastHeader.setAddress(address);

		pipeManager.processRemoteSend(multicastHeader, echoRequest.serialize());
		Thread.sleep(1000);
		assertTrue(localSender.isInvokeReceiveCalled());
	}
	
	@Test
	public void testRequestFile() throws Exception {
		FileRequest fileRequest = new FileRequest();
		fileRequest.setFileName(tempFileName);
		String serializedRequest = fileRequest.serialize();
		log.info("Sending request: ");
		System.out.println(TestUtils.prettyPrintJson(serializedRequest));
		
		MulticastHeader multicastHeader = new MulticastHeader();
		Address address = new Address(null, boschPipe, false);
		multicastHeader.setAddress(address);

		pipeManager.processRemoteSend(multicastHeader, fileRequest.serialize());
		Thread.sleep(2000);
		assertTrue(localSender.isInvokeIncomingCalled());
	}
	
	//------------------------------------------------------
	
	public class MockLocalSender implements LocalUhuSender {
		
		private Logger log = LoggerFactory.getLogger(MockLocalSender.class);
		
		private boolean invokeReceiveCalled = false;
		private boolean invokeIncomingCalled = false;

		@Override
		public void invokeReceive(String serializedEvent) {
			log.info("invokeReceive: " + serializedEvent);
			invokeReceiveCalled = true;
			assertNotNull(serializedEvent);
			EchoReply reply = EchoReply.deserialize(serializedEvent);
			assertNotNull(reply);
			assertTrue( reply.getText().contains(echoRequest.getText()) );
		}

		@Override
		public void invokeIncoming(String serializedStream, String path) {
			log.info("invokeIncoming: " + serializedStream); 
			invokeIncomingCalled = true;
			assertNotNull(serializedStream);
			assertNotNull(path);
			
			FileReply reply = FileReply.deserialize(serializedStream, FileReply.class);
			assertNotNull(reply);
			
			File file = new File(path);
			assertTrue(file.exists());
			assertTrue(file.isFile());
		}

		public boolean isInvokeReceiveCalled() {
			return invokeReceiveCalled;
		}

		public void setInvokeReceiveCalled(boolean invokeReceiveCalled) {
			this.invokeReceiveCalled = invokeReceiveCalled;
		}

		public boolean isInvokeIncomingCalled() {
			return invokeIncomingCalled;
		}

		public void setInvokeIncomingCalled(boolean invokeIncomingCalled) {
			this.invokeIncomingCalled = invokeIncomingCalled;
		}

	}
}
