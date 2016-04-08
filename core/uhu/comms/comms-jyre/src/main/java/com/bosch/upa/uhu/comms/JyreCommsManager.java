package com.bosch.upa.uhu.comms;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.messagehandler.ServiceMessageHandler;
import com.bosch.upa.uhu.comms.thread.JyreReceiverThread;
import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.pipe.core.PipeManager;
import com.bosch.upa.uhu.sadl.UhuSadlManager;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;

public class JyreCommsManager implements IUhuComms{
	
	//private String group = null;

		MessageDispatcher msgDispatcher = null;

		//Thread for Event receiver Thread
		private Thread jyreEventReceiverThread = null;

		UhuSadlManager uhuSadlManager = null;

		private static final Logger log = LoggerFactory.getLogger(JyreCommsManager.class);

		public JyreCommsManager() {
			//this.group = group;
		}

		@Override
		public boolean initComms(CommsProperties commsProperties, InetAddress addr,
				UhuSadlManager uhuSadlManager, PipeManager pipe) {

			this.uhuSadlManager = uhuSadlManager;
			msgDispatcher = new MessageDispatcher(uhuSadlManager);

			//Start Receiver Threads
			jyreEventReceiverThread = new Thread( new JyreReceiverThread(null, null));

			return true;
		}

		@Override
		public boolean startComms() {
			if(jyreEventReceiverThread != null){
				jyreEventReceiverThread.start();	
			}

			return true;
		}


		@Override
		public boolean stopComms() {
			if(jyreEventReceiverThread != null)
				jyreEventReceiverThread.interrupt();	

			return true;
		}

		@Override
		public boolean closeComms() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean restartComms() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean sendMessage(Ledger message) {
			JyreCommsSend commsSend = new JyreCommsSend(null);
			commsSend.sendMessage(new byte['a'],false);
			return true;
		}

		@Override
		public boolean sendStream(String uniqueKey) {
			return false;
		}

		public boolean registerStreamBook(String key, StreamRecord sRecord) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean registerNotification(
				ICommsNotification errNotificationCallback) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean registerControlMessageReceiver(Discriminator id,
				ICtrlMsgReceiver receiver) {
			// TODO Auto-generated method stub
			return false;
		}

		public void setUhuCallback(ServiceMessageHandler uhuCallback) {
			// TODO Auto-generated method stub

		}

		public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
			// TODO Auto-generated method stub

		}
}
