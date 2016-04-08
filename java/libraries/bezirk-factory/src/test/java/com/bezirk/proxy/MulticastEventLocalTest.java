package com.bezirk.proxy;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.IBezirk;
import com.bezirk.api.IBezirkListener;
import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.DiscoveredService;
import com.bezirk.api.addressing.Location;
import com.bezirk.api.addressing.Pipe;
import com.bezirk.api.addressing.PipePolicy;
import com.bezirk.api.addressing.ServiceEndPoint;
import com.bezirk.api.addressing.ServiceId;
import com.bezirk.api.messages.Event;
import com.bezirk.api.messages.ProtocolRole;
import com.bezirk.api.messages.Message.Stripe;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * @author vbd4kor
 * @Date 24-09-2014
 * This class tests the local MulticastEvent communication. Three MockServices register and subscribe with common ProtocolRole.
 * Sub-test- 1 :MockServiceA sends the multicastEvent on the wire. MockServiceB and MockServiceC should receive the Events.
 * Sub-test- 2 : MockServiceC changes to New Location. MockServiceA pings a multicast event by setting the address to new location
 * Only MockServiceC should receive the event.
 */
public class MulticastEventLocalTest {
	private final static Logger log = LoggerFactory.getLogger(MulticastEventLocalTest.class);
	private final Location loc = new Location("Liz Home", "floor-6", "Garage");  // change in the location
	private static boolean didMockBreceive = false, didMockCreceive = false, didMockCReceiveSpecifically = false;
	private MulticastMockServiceA mockA =new MulticastMockServiceA();
	private MulticastMockServiceB mockB = new MulticastMockServiceB();
	private MulticastMockServiceC mockC = new MulticastMockServiceC();
	
	@BeforeClass
	public static void setup(){
		log.info(" ************** Setting up MulticastEventLocalTest Testcase ****************************");
	}
	
	@Before
	public void setUpMockservices() {

		mockB.setupMockService();
		mockC.setupMockService();
		mockA.setupMockService();
		
	}

	
	// FIXME: This test sporadically fails, presumably after a timeout
	//@Test(timeout=60000)
	public void testForDiscovery(){
		mockA.pingServices();
	
		while((didMockBreceive != false) && (didMockCreceive != false)){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info(" **************  MulticastEventLocalTesting for NULL Location is Successful ****************************");
		// Change the location of Service C.
		mockC.changeLocation();
		mockA.pingServiceC();
		
		while(!didMockCReceiveSpecifically){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		log.info(" **************  MulticastEventLocalTesting Successful ****************************");
	}
	
	@After
	public void destroyMockservices() {

		IBezirk uhu= Factory.getInstance();
		uhu.unregisterService(mockA.myId);
		uhu.unregisterService(mockB.myId);
		uhu.unregisterService(mockC.myId);		
	}

	
	@AfterClass
	public static void tearDown(){
		log.info(" ************** Shutting down MulticastEventLocalTest Testcase ****************************");
	}
		
	/**
	 * MockServiceA that is simulating as Service that initiates the Multicast Communication
	 */
	private final class MulticastMockServiceA implements IBezirkListener {
		private final String serviceName = "MulticastMockServiceA";
		private IBezirk uhu = null;
		private ServiceId myId = null;
		private MulticastMockServiceProtocolRole pRole;
		
		/**
		 * Setup the Service
		 */
		private final void setupMockService(){
			uhu = Factory.getInstance();
			myId = uhu.registerService(serviceName);
			log.info("MulticastMockServiceA - regId : " + ((UhuServiceId)myId).getUhuServiceId());
			pRole = new MulticastMockServiceProtocolRole();
			uhu.subscribe(myId, pRole, this);
		}
		
		/**
		 * Send Multi cast request with null location on the wire
		 */
		private final void pingServiceC() {
			MulticastMockRequestEvent req = new MulticastMockRequestEvent(Stripe.REQUEST, "MockRequestEvent");
			Address address = new Address(loc);
			uhu.sendEvent(myId, address, req);
		}

		/**
		 * Send Multi cast request with specific location on the wire
		 */
		private final void pingServices(){
			MulticastMockRequestEvent req = new MulticastMockRequestEvent(Stripe.REQUEST, "MockRequestEvent");
			Address address = null;
			uhu.sendEvent(myId, address, req);
		}
		
		@Override
		public void receiveEvent(String topic, String event, ServiceEndPoint sender) {}

		@Override
		public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender) {	}

		@Override
		public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender) {}

		@Override
		public void streamStatus(short streamId, StreamConditions status) {	}


		@Override
		public void pipeStatus(Pipe p, PipeConditions status) {}

		@Override
		public void discovered(Set<DiscoveredService> serviceSet) {	}

		@Override
		public void pipeGranted(Pipe p, PipePolicy allowedIn,
				PipePolicy allowedOut) {
			
		}
	}
	
	/**
	 * ProtocolRole used by the mock Services
	 */
	private final class MulticastMockServiceProtocolRole extends ProtocolRole{

		private final String[] events = {"MockRequestEvent"};
		
		@Override
		public String getProtocolName() {
			return MulticastMockServiceProtocolRole.class.getSimpleName();
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String[] getEventTopics() {
			return events;
		}

		@Override
		public String[] getStreamTopics() {
			return null;
		}
		
	}
	/**
	 * Sample Event used by the services subscribing for a protocolRole
	 */
	private final class MulticastMockRequestEvent extends Event{
		
		private final String question = "Ping to Mock Services";
		private MulticastMockRequestEvent(Stripe stripe, String topic) {
			super(stripe, topic);
		}
	}
	/**
	 * MockServiceB the consumer of the event generated by MockServiecA
	 */
	private final class MulticastMockServiceB implements IBezirkListener {
		private final String serviceName = "MulticastMockServiceB";
		private IBezirk uhu = null;
		private ServiceId myId = null;
		
		/**
		 * Setup the service
		 */
		private final void setupMockService(){
			uhu = Factory.getInstance();
			myId = uhu.registerService(serviceName);
			log.info("MulticastMockServiceB - regId : " + ((UhuServiceId)myId).getUhuServiceId());
			uhu.subscribe(myId, new MulticastEventLocalTest.MulticastMockServiceProtocolRole(), this);
		}

		@Override
		public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
			log.info(" **** Received Event *****");
			
			assertEquals("MockRequestEvent", topic);
			MulticastMockRequestEvent receivedEvent = Event.deserialize(event, MulticastMockRequestEvent.class);
			assertEquals("Ping to Mock Services",receivedEvent.question);
			didMockBreceive = true;
			log.info("********* MOCK_SERVICE B received the Event successfully **************");
		}

		@Override
		public void receiveStream(String topic, String stream, short streamId,InputStream f, ServiceEndPoint sender) {}

		@Override
		public void receiveStream(String topic, String stream, short streamId,String filePath, ServiceEndPoint sender) {}

		@Override
		public void streamStatus(short streamId, StreamConditions status) {}

		@Override
		public void pipeStatus(Pipe p, PipeConditions status) {}

		@Override
		public void discovered(Set<DiscoveredService> serviceSet) {}

		@Override
		public void pipeGranted(Pipe p, PipePolicy allowedIn,
				PipePolicy allowedOut) {
			
		}
	}
	/**
	 * MockServiceC the consumer of the event generated by MockServiecA
	 */
	private final class MulticastMockServiceC implements IBezirkListener {
		private final String serviceName = "MulticastMockServiceC";
		private IBezirk uhu = null;
		private ServiceId myId = null;

		/**
		 * Setup the service
		 */
		private final void setupMockService(){
			uhu = Factory.getInstance();
			myId = uhu.registerService(serviceName);
			log.info("MulticastMockServiceC - regId : " + ((UhuServiceId)myId).getUhuServiceId());
			uhu.subscribe(myId, new MulticastMockServiceProtocolRole(), this);
		}
		
		/**
		 * Update the location of the service
		 */
		private final void changeLocation(){
			uhu.setLocation(myId, loc);
		}

		@Override
		public void receiveEvent(String topic, String event,
				ServiceEndPoint sender) {
			log.info(" **** Received Event *****");
			
			assertEquals("MockRequestEvent", topic);
			MulticastMockRequestEvent receivedEvent = Event.deserialize(event, MulticastMockRequestEvent.class);
			assertEquals("Ping to Mock Services",receivedEvent.question);
			if(!didMockCreceive){
				didMockCreceive = true;	
			}else{
				didMockCReceiveSpecifically = true;
			}
			
			log.info("********* MOCK_SERVICE C received the Event successfully **************");
			
		}

		@Override
		public void receiveStream(String topic, String stream, short streamId,InputStream f, ServiceEndPoint sender) {}

		@Override
		public void receiveStream(String topic, String stream, short streamId,String filePath, ServiceEndPoint sender) {}

		@Override
		public void streamStatus(short streamId, StreamConditions status) {}

		@Override
		public void pipeStatus(Pipe p, PipeConditions status) {}

		@Override
		public void discovered(Set<DiscoveredService> serviceSet) {}

		@Override
		public void pipeGranted(Pipe p, PipePolicy allowedIn,
				PipePolicy allowedOut) {
			
		}
	}
}
