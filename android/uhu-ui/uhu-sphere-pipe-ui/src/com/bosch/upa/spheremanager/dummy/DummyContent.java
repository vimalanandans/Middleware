package com.bosch.upa.spheremanager.dummy;

import com.bosch.upa.spheremanager.ui.listitems.AbstractInformationListItem;
import com.bosch.upa.spheremanager.ui.listitems.AbstractPolicyListItem;
import com.bosch.upa.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bosch.upa.spheremanager.ui.listitems.DeviceListItem;
import com.bosch.upa.spheremanager.ui.listitems.DeviceServiceItem;
import com.bosch.upa.spheremanager.ui.listitems.InformationListItem;
import com.bosch.upa.spheremanager.ui.listitems.Pipe;
import com.bosch.upa.spheremanager.ui.listitems.PipePolicy;
import com.bosch.upa.spheremanager.ui.listitems.PipeRecord;
import com.bosch.upa.spheremanager.ui.listitems.ProtocolItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
     * An array of sample (dummy) pipes.
     */
	public static final ArrayList<PipeRecord> pipeList = new ArrayList<PipeRecord>();
	
    /**
     * An array of sample (dummy) items.
     */
    public static final List<AbstractSphereListItem> ITEMS = new ArrayList<AbstractSphereListItem>();
    /**
     * An array of sample (dummy) devices.
     */
	public static final ArrayList<DeviceListItem> deviceList = new ArrayList<DeviceListItem>();
    /**
     * An array of sample (dummy) services.
     */
	public static final List<DeviceServiceItem> serviceList = new ArrayList<DeviceServiceItem>();
	
	/**
     * An array of sample (dummy) policies.
     */
	public static final List<AbstractPolicyListItem> policyListInbound = new ArrayList<AbstractPolicyListItem>();
	public static final List<AbstractPolicyListItem> policyListOutbound = new ArrayList<AbstractPolicyListItem>();
	
	/**
     * An array of sample (dummy) informations.
     */
	public static final List<AbstractInformationListItem> informationListInbound = new ArrayList<AbstractInformationListItem>();
	public static final List<AbstractInformationListItem> informationListOutbound = new ArrayList<AbstractInformationListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, AbstractSphereListItem> ITEM_MAP = new LinkedHashMap<String, AbstractSphereListItem>();

    static {
    	//generate UUIDs to have association from Pipe to Sphere
    	UUID dummySphereUUID = UUID.randomUUID();
    	UUID carSphereUUID = UUID.randomUUID();
    	UUID smartHomeSphereUUID = UUID.randomUUID();
    	//add dummy pipes:
    	Pipe testPipe = new Pipe();
    	testPipe.setName("Test Pipe");
    	PipePolicy polIn = new PipePolicy();
    	//ProtocolRole was modified because "adding Pipe Screen" is not yet adapted to new data structure
    	polIn.addProtocol(new ProtocolItem("Test Policy In", "Reason Role Ebene", true, true),"Reason PipePolicy Ebene");
    	polIn.addProtocol(new ProtocolItem("Test 2 Policy In", "Reason 2 Role Ebene", true, true),"Reason PipePolicy Ebene");
    	polIn.addProtocol(new ProtocolItem("Test 3 Policy In", "Reason 3 Role Ebene", true, true),"Reason PipePolicy Ebene");
    	PipePolicy polOut = new PipePolicy();
    	polOut.addProtocol(new ProtocolItem("Test Policy Out", "Reason Role Ebene", true, true),"Reason PipePolicy Ebene");
    	polOut.addProtocol(new ProtocolItem("Test 2 Policy Out", "Reason 2 Role Ebene", true, true),"Reason PipePolicy Ebene");
    	PipeRecord record = new PipeRecord(testPipe);
    	record.setAllowedIn(polIn);
    	record.setAllowedOut(polOut);
    	record.setSphereId(dummySphereUUID.toString());
    	pipeList.add(record);
    	Pipe testPipe2 = new Pipe();
    	testPipe2.setName("Bacon ipsum dolor amet");
    	PipeRecord record2 = new PipeRecord(testPipe2);
    	record2.setPassword("BaconIsKing");
    	record2.setUsername("Username1");
    	PipePolicy polIn2 = polIn;
    	PipePolicy polOut2 = polOut;
    	record2.setAllowedIn(polIn2);
    	record2.setAllowedOut(polOut2);
    	record2.setSphereId(carSphereUUID.toString());
    	pipeList.add(record2);
    	Pipe testPipe3 = new Pipe();
    	testPipe3.setName("Strip steak corned beef");
    	PipeRecord record3 = new PipeRecord(testPipe3);
    	record3.setPassword("SteakIsKing");
       	record3.setUsername("Username2");
    	PipePolicy polIn3 = polIn;
    	PipePolicy polOut3 = polOut;
    	record3.setAllowedIn(polIn3);
    	record3.setAllowedOut(polOut3);
    	record3.setSphereId(smartHomeSphereUUID.toString());
    	pipeList.add(record3);
    	
    	
    	
    	
    	
    	List<AbstractInformationListItem> dummyInformationList = new ArrayList<AbstractInformationListItem>();
        dummyInformationList.add(new InformationListItem("Location", "Dummy Data", false));
    	/*
     	//Add sample devices.
    	//because DeviceListItem has changed for prototype we need dummy List<AbstractInformationListItem>
    	
    	deviceList.add(new DeviceListItem("Bob's Nexus 5", "Smartphone", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Gina's Nexus 7", "Tablet", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Bob's UHD-TV", "TV", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Gina's Playstation 4", "PS4", true, dummyInformationList, dummyInformationList));;
    	deviceList.add(new DeviceListItem("Family Car", "Car", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Family Chainsaw", "Chainsaw", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Family Microwave", "Microwave", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Bosch Tassimo", "Coffee", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Heating", "Heating", true, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Anja's iPhone 6", "Smartphone", false, dummyInformationList, dummyInformationList));
    	deviceList.add(new DeviceListItem("Metz TV", "TV", false, dummyInformationList, dummyInformationList));
        // Add sample items.
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Smart Home", true, true, deviceList)));
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Jan's Jamboree", true, true, deviceList)));
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Car - Multimedia", true, false, deviceList)));
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Car - Driving Assistance", true, false, deviceList)));
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Paul's Party", false, false, deviceList)));
        addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Frank's Festivity", false, false, deviceList)));
        // Add samlpe services.
        serviceList.add(new DeviceServiceItem("Dummy Data Video",  true));
        serviceList.add(new DeviceServiceItem("Stream Music",  true));
        serviceList.add(new DeviceServiceItem("Data Mining",  true));
        serviceList.add(new DeviceServiceItem("Lighting control",  true));
        serviceList.add(new DeviceServiceItem("Location",  true));
        serviceList.add(new DeviceServiceItem("Payment",  true));
        serviceList.add(new DeviceServiceItem("Banking",  false));
        serviceList.add(new DeviceServiceItem("Briefing",  false));
        serviceList.add(new DeviceServiceItem("Record Video",  false));
        serviceList.add(new DeviceServiceItem("Adverstisement",  false));
    	 */
        // Add samlpe policies.
        policyListOutbound.add(new ProtocolItem("Location Outbound", "Finding heating near you", true, false));
        policyListOutbound.add(new ProtocolItem("Temperature Outbound", "Room temperature", true, false));
        policyListOutbound.add(new ProtocolItem("Heart Rate Outbound", "Your heartrate while watching horror movies", true, true));
        policyListOutbound.add(new ProtocolItem("Picture Streaming Outbound", "Your pictures to facebook", true, true));
        policyListOutbound.add(new ProtocolItem("Video Streaming Outbound", "Your security camera to security server", true, true));
        policyListOutbound.add(new ProtocolItem("Audio Streaming Outbound", "Your security camera audio to security server", true, true));

        policyListInbound.add(new ProtocolItem("Location Inbound", "Finding heating near you", true, false));
        policyListInbound.add(new ProtocolItem("Temperature Inbound", "Room temperature", true, false));
        policyListInbound.add(new ProtocolItem("Heart Rate Inbound", "Your heartrate while watching horror movies", true, true));
        
//Set up devices
        
        //random
        List<AbstractInformationListItem> informationListOutboundRandom = new ArrayList<AbstractInformationListItem>();	
        informationListOutboundRandom.add(new InformationListItem("Random Outbound Info", "Provided by Service(s): Random", false));
        List<AbstractInformationListItem> informationListInboundRandom = new ArrayList<AbstractInformationListItem>();
        informationListInboundRandom.add(new InformationListItem("Random Inbound Info","Used by Service(s): Random", true));
        List<DeviceServiceItem> serviceListRandom = new ArrayList<DeviceServiceItem>();
        serviceListRandom.add(new DeviceServiceItem("Random Service 1",  true));
        serviceListRandom.add(new DeviceServiceItem("Random Service 2",  false));
        serviceListRandom.add(new DeviceServiceItem("Random Service 3",  false));
        
        //end random
        

        /*
         * Because linking of Services and Information makes no differences in Dummy Data - Variations are as follows:
         * 0 == Prototype Version 1.a and 2.a Information (in-&outbound combined) with Use-Case 1 (Local)
         * 1 == Prototype Version 1.b and 2.b Information (in-&outbound separated) with Use-Case 1 (Local)
         * 2 == Prototype Version 1.a and 2.a Information (in-&outbound combined) with Use-Case 2 (Cloud-centric)
         * 3 == Prototype Version 1.b and 2.b Information (in-&outbound separated) with Use-Case 2 (Cloud-centric)
         */
        List<AbstractInformationListItem> informationListOutboundBobsPhone = new ArrayList<AbstractInformationListItem>();	
        List<AbstractInformationListItem> informationListInboundBobsPhone = new ArrayList<AbstractInformationListItem>();
        List<AbstractInformationListItem> informationListOutboundBobsCar = new ArrayList<AbstractInformationListItem>();
        List<AbstractInformationListItem> informationListInboundBobsCar = new ArrayList<AbstractInformationListItem>();
        List<AbstractInformationListItem> informationListOutboundNavigation = new ArrayList<AbstractInformationListItem>();
        List<AbstractInformationListItem> informationListInboundNavigation = new ArrayList<AbstractInformationListItem>();
        List<DeviceServiceItem> serviceListBob = new ArrayList<DeviceServiceItem>();
        List<DeviceServiceItem> serviceListBobsCar = new ArrayList<DeviceServiceItem>();
        List<DeviceServiceItem> serviceListNavigation = new ArrayList<DeviceServiceItem>();
        // FOR USABILITY STUDY: select dummy variation
        int dummyDataVariation  = 1;
        
        if(dummyDataVariation==0){
        	//outbound == combined
	        //Bob's Phone        
	        informationListOutboundBobsPhone.add(new InformationListItem("Contact(s)", "Used/Provided by Service(s): Contacts", true));
	        informationListOutboundBobsPhone.add(new InformationListItem("Appointment(s)", "Used/Provided by Service(s): Calendar", true));
	        serviceListBob.add(new DeviceServiceItem("Contacts",  true));
	        serviceListBob.add(new DeviceServiceItem("Calendar",  true));
	        serviceListBob.add(new DeviceServiceItem("Phone",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Video",  false));
	        serviceListBob.add(new DeviceServiceItem("Social Media Sharing",  false));
	        
	        //Bob's Car  
	        informationListOutboundBobsCar.add(new InformationListItem("Position", "Used/Provided by Service(s): Navigation", true)); 
	        informationListOutboundBobsCar.add(new InformationListItem("Destination(s)", "Used/Provided by Service(s): Navigation, Navigation Assistance", true)); 
	        informationListOutboundBobsCar.add(new InformationListItem("Contact(s)", "Used/Provided  by Service(s): Navigation Assistance", true));
	        informationListOutboundBobsCar.add(new InformationListItem("Appointment(s)", "Used/Provided  by Service(s): Navigation Assistance", true));
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation Assistance",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Travel Guide",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("News",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Maintenance Assistance",  false));
        }
	        else if(dummyDataVariation==1){
	        //Bob's Phone        
	        informationListOutboundBobsPhone.add(new InformationListItem("Contact(s)", "Provided by Service(s): Contacts", true));
	        informationListOutboundBobsPhone.add(new InformationListItem("Appointment(s)", "Provided by Service(s): Calendar", true));
	        informationListInboundBobsPhone.add(new InformationListItem("Contact(s)","Used by Service(s): Contacts", true));
	        informationListInboundBobsPhone.add(new InformationListItem("Appointment(s)","Used by Service(s): Calendar", true));
	        serviceListBob.add(new DeviceServiceItem("Contacts",  true));
	        serviceListBob.add(new DeviceServiceItem("Calendar",  true));
	        serviceListBob.add(new DeviceServiceItem("Phone",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Video",  false));
	        serviceListBob.add(new DeviceServiceItem("Social Media Sharing",  false));
	        
	        //Bob's Car  
	        informationListOutboundBobsCar.add(new InformationListItem("Position", "Provided by Service(s): Navigation", true)); 
	        informationListOutboundBobsCar.add(new InformationListItem("Destination(s)", "Provided by Service(s): Navigation, Navigation Assistance", true)); 
	        informationListInboundBobsCar.add(new InformationListItem("Destination(s)", "Used by Service(s): Navigation, Navigation Assistance", true));
	        informationListInboundBobsCar.add(new InformationListItem("Contact(s)", "Used by Service(s): Navigation Assistance", true));
	        informationListInboundBobsCar.add(new InformationListItem("Appointment(s)", "Used by Service(s): Navigation Assistance", true));
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation Assistance",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Travel Guide",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("News",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Maintenance Assistance",  false));
	        
	        }
	        else if(dummyDataVariation==2){
	        //Bob's Phone        
	        informationListOutboundBobsPhone.add(new InformationListItem("Contact(s)", "Used/Provided by Service(s): Contacts", true));
	        informationListOutboundBobsPhone.add(new InformationListItem("Appointment(s)", "Used/Provided by Service(s): Calendar", true));    
	        serviceListBob.add(new DeviceServiceItem("Contacts",  true));
	        serviceListBob.add(new DeviceServiceItem("Calendar",  true));
	        serviceListBob.add(new DeviceServiceItem("Phone",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Video",  false));
	        serviceListBob.add(new DeviceServiceItem("Social Media Sharing",  false));
	        
	        //Bob's Car  
	        informationListOutboundBobsCar.add(new InformationListItem("Position", "Used/Provided by Service(s): Navigation", true));
	        informationListOutboundBobsCar.add(new InformationListItem("Destination(s)", "Used/Provided by Service(s): Navigation", true)); 
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Travel Guide",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("News",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Maintenance Assistance",  false));
	        
	        //Intelligent Navigation
	        informationListOutboundNavigation.add(new InformationListItem("Position", "Used/Provided by Service(s): Navigation Assistance", true));
	        informationListOutboundNavigation.add(new InformationListItem("Destination(s)", "Used/Provided by Service(s): Navigation Assistance", true)); 
	        informationListOutboundNavigation.add(new InformationListItem("Contact(s)", "Used/Provided by Service(s): Navigation Assistance", true));
	        informationListOutboundNavigation.add(new InformationListItem("Appointment(s)", "Used/Provided by Service(s): Navigation Assistance", true));
	        serviceListNavigation.add(new DeviceServiceItem("Navigation Assistance",  true));
	        //serviceListNavigation.add(new DeviceServiceItem("",  false));
	        
        }else if(dummyDataVariation==3){
	        //Bob's Phone        
	        informationListOutboundBobsPhone.add(new InformationListItem("Contact(s)", "Provided by Service(s): Contacts", true));
	        informationListOutboundBobsPhone.add(new InformationListItem("Appointment(s)", "Provided by Service(s): Calendar", true));
	        informationListInboundBobsPhone.add(new InformationListItem("Contact(s)","Used by Service(s): Contacts", true));
	        informationListInboundBobsPhone.add(new InformationListItem("Appointment(s)","Used by Service(s): Calendar", true));
	        serviceListBob.add(new DeviceServiceItem("Contacts",  true));
	        serviceListBob.add(new DeviceServiceItem("Calendar",  true));
	        serviceListBob.add(new DeviceServiceItem("Phone",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBob.add(new DeviceServiceItem("Stream Video",  false));
	        serviceListBob.add(new DeviceServiceItem("Social Media Sharing",  false));
	        
	        //Bob's Car  
	        informationListOutboundBobsCar.add(new InformationListItem("Position", "Provided by Service(s): Navigation", true));
	        informationListOutboundBobsCar.add(new InformationListItem("Destination(s)", "Provided by Service(s): Navigation", true)); 
	        informationListInboundBobsCar.add(new InformationListItem("Destination(s)", "Used by Service(s): Navigation", true));
	        serviceListBobsCar.add(new DeviceServiceItem("Navigation",  true));
	        serviceListBobsCar.add(new DeviceServiceItem("Stream Music",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Travel Guide",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("News",  false));
	        serviceListBobsCar.add(new DeviceServiceItem("Maintenance Assistance",  false));
	        
	        //Intelligent Navigation
	        informationListOutboundNavigation.add(new InformationListItem("Destination(s)", "Provided by Service(s): Navigation Assistance", true)); 
	        informationListInboundNavigation.add(new InformationListItem("Location", "Used by Service(s): Navigation Assistance", true));
	        informationListInboundNavigation.add(new InformationListItem("Destination(s)", "Used by Service(s): Navigation Assistance", true));
	        informationListInboundNavigation.add(new InformationListItem("Contact(s)", "Used by Service(s): Navigation Assistance", true));
	        informationListInboundNavigation.add(new InformationListItem("Appointment(s)", "Used by Service(s): Navigation Assistance", true));
	        serviceListNavigation.add(new DeviceServiceItem("Navigation Assistance",  true));
	        //serviceListNavigation.add(new DeviceServiceItem("",  false));
        }
        
      
   
       
        
        //Heating-System 
        List<AbstractInformationListItem> informationListOutboundHeating = new ArrayList<AbstractInformationListItem>();	
        informationListOutboundHeating.add(new InformationListItem("Temperature", "Provided by Temperature Service", true));
        informationListOutboundHeating.add(new InformationListItem("Energy consumption", "Provided by Track Energy consumption", true));
        List<AbstractInformationListItem> informationListInboundHeating = new ArrayList<AbstractInformationListItem>();
        informationListInboundHeating.add(new InformationListItem("Temperature preferences","Used by Intelligent Heating, Party Heating", true));
        informationListInboundHeating.add(new InformationListItem("Location","Used by Intelligent Heating", true));
        List<DeviceServiceItem> serviceListHeating = new ArrayList<DeviceServiceItem>();
        serviceListHeating.add(new DeviceServiceItem("Intelligent Heating",  true));
        serviceListHeating.add(new DeviceServiceItem("Party Heating",  true));
        serviceListHeating.add(new DeviceServiceItem("Track Energy consumption",  true));
        
        //Party attendee's Phone A
        List<AbstractInformationListItem> informationListOutboundPhoneA = new ArrayList<AbstractInformationListItem>();	
        informationListOutboundPhoneA.add(new InformationListItem("Temperature", "Provided by Temperature Service", false));
        List<AbstractInformationListItem> informationListInboundPhoneA = new ArrayList<AbstractInformationListItem>();
        informationListInboundPhoneA.add(new InformationListItem("Temperature","Provided by Temperature Service", true));
        List<DeviceServiceItem> serviceListPhoneA = new ArrayList<DeviceServiceItem>();
        serviceListPhoneA.add(new DeviceServiceItem("Stream Music",  true));
        serviceListPhoneA.add(new DeviceServiceItem("Intelligent Navigation",  false));
        serviceListPhoneA.add(new DeviceServiceItem("Intelligent Heating",  false));
        serviceListPhoneA.add(new DeviceServiceItem("Party Heating",  false));
        


        
 //Initialize Spheres         

    //Smart-Heating
    	ArrayList<DeviceListItem> thingsInSmartHomeSphere = new ArrayList<DeviceListItem>();
    	//add things with info filters
    	//add Bob's Phone
       	thingsInSmartHomeSphere.add(new DeviceListItem("Bob's Phone", "Smartphone", true, serviceListBob, informationListOutboundBobsPhone, informationListInboundBobsPhone));
    	thingsInSmartHomeSphere.add(new DeviceListItem("Heating System", "Heating", true,serviceListHeating, informationListOutboundHeating, informationListInboundHeating));    	
    	//filled with random data:
    	thingsInSmartHomeSphere.add(new DeviceListItem("TV", "TV", true,serviceListRandom, informationListOutboundRandom, informationListInboundRandom));   
    	thingsInSmartHomeSphere.add(new DeviceListItem("Gaming Console", "PS4", true,serviceListRandom, informationListOutboundRandom, informationListInboundRandom));   
        //VIMAL : addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Smart Home", true, true, thingsInSmartHomeSphere)));
 
        
   //Car
        ArrayList<DeviceListItem> thingsInCarSphere = new ArrayList<DeviceListItem>();
    	//add things with info filters
		thingsInCarSphere.add(new DeviceListItem("Bob's Phone", "Smartphone", true, serviceListBob, informationListOutboundBobsPhone, informationListInboundBobsPhone));
		thingsInCarSphere.add(new DeviceListItem("Bob's Car", "Car", true, serviceListBobsCar, informationListOutboundBobsCar, informationListInboundBobsCar));
        if(dummyDataVariation==3||dummyDataVariation==2){
        	thingsInCarSphere.add(new DeviceListItem("iNavigation.com", "Cloud", true, serviceListNavigation, informationListOutboundNavigation, informationListInboundNavigation));
        }
		thingsInCarSphere.add(new DeviceListItem("Alice's Phone", "Smartphone", true, serviceListBob, informationListOutboundRandom, informationListInboundRandom));
        //VIMAL :addItem(new SphereListItem(new DummySphere(carSphereUUID, "Car", true, true, thingsInCarSphere)));
            
            
     //Another Random Sphere
    	ArrayList<DeviceListItem> thingsInRandomSphere = new ArrayList<DeviceListItem>();
    	thingsInRandomSphere.add(new DeviceListItem("TV", "TV", true,serviceListRandom, informationListOutboundRandom, informationListInboundRandom));   
    	thingsInRandomSphere.add(new DeviceListItem("Gaming Console", "PS4", true,serviceListRandom, informationListOutboundRandom, informationListInboundRandom));
        //VIMAL :addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Work", true, true, thingsInRandomSphere)));

        
    //Party
        //data for party use-case
    	ArrayList<DeviceListItem> thingsInPartySphere = new ArrayList<DeviceListItem>();
    	//add things with info filters
    	//add Bob's Phone
   
		thingsInPartySphere.add(new DeviceListItem("Bob's Phone", "Smartphone", true, serviceListBob, informationListOutboundBobsPhone, informationListInboundBobsPhone));
		thingsInPartySphere.add(new DeviceListItem("Party attendee's Phone A", "Smartphone", true,serviceListPhoneA, informationListOutboundPhoneA, informationListInboundPhoneA));
        //VIMAL :addItem(new SphereListItem(new DummySphere(UUID.randomUUID(), "Bob's Party", true, true, thingsInPartySphere)));
        
           
    
     
       	
    }

    public static void addItem(AbstractSphereListItem item) {
        ITEMS.add(item);
        if (item.getId()!=null) ITEM_MAP.put(item.getId(), item);
    }

}
