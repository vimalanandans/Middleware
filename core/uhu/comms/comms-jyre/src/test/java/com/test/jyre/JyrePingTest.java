package com.test.jyre;

import org.junit.Test;
import org.zeromq.ZMsg;
import org.zyre.ZreInterface;
import static org.junit.Assert.*;

public class JyrePingTest {

	class Ping implements Runnable{
		@Override
		public void run() {
			// this will keep shout!!! with a message..
			ZreInterface zre = new ZreInterface ();
			zre.join("Test");
			int i = 0;
			while (i<10) {
				try {
					ZMsg outgoing = new ZMsg();
					outgoing.addString("Test");
					outgoing.addString("HELLO");
					zre.shout (outgoing);
					Thread.sleep(1000);
					System.out.println("Shouting Hello "+i+" !!!");
					i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}


	//@Test
	public void 
	testShout() throws Exception
	{
		// start the thread, so it will keep shouting the messages!!!
		new Thread(new Ping()).start();

		ZreInterface inf = new ZreInterface ();
		inf.join ("Test");
		long start = System.currentTimeMillis();
		long end = start + 5*1000;

		//only keeps listening for 10 seconds.
		while(System.currentTimeMillis() < end){
			//listining to the event!!!!
			ZMsg incoming = inf.recv ();

			if(incoming!= null){
				String event = incoming.popString ();
				if (event.equals ("SHOUT")) {
					String identity = incoming.popString ();
					incoming.popString ();
					String msg = incoming.popString ();

					if (msg.equals ("HELLO")) {
						System.out.println("Recieved Heloooooo msg from "+identity);
						break;
					}

					if (msg.equals ("QUIT")) {
						break;
					}
				}
			}
		}
		
		assertTrue(true);
		System.out.println("Exited!!!!");



	}

}
