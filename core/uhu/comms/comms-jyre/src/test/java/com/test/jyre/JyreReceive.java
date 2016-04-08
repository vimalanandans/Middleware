package com.test.jyre;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zyre.ZreInterface;
import org.zyre.ZreMsg;

public class JyreReceive {



	public static void main(String[] args) throws InterruptedException {
		ZreInterface inf = new ZreInterface ();
		inf.join ("Test");
		long start = System.currentTimeMillis();
		long end = start + 5*1000;

		//only keeps listening for 5 seconds.
		while(true){
			//listining to the event!!!!
			ZMsg incoming = inf.recv ();

			if(incoming!= null){
				String event = incoming.popString ();
				if (event.equals ("SHOUT")) {
					String identity = incoming.popString ();
					incoming.popString ();
					String msg = incoming.popString ();
					System.out.println("Recieved Messge "+msg);
					/*if (msg.equals ("HELLO")) {
						System.out.println("Recieved Heloooooo msg from "+identity);
						break;
					}

					if (msg.equals ("QUIT")) {
						break;
					}*/
				}else{
					System.out.println("Out!!!");
				}
			}
		}

		/*new Thread(new InputThread()).start();
		try{
			ZContext ctx = new ZContext ();

			Socket output = ctx.createSocket (ZMQ.PUB);
			output.bind ("inproc://selftest");
			ZreMsg self  = null;
			while(true){
				self = new ZreMsg (ZreMsg.SHOUT);
				self.setGroup ("TestGrp");
				self.setContent (new ZFrame ("Captcha Diem"));
				self.send (output);
				System.out.println("Shouting !!!");
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//ctx.destroy ();
*/	}

}

class InputThread implements Runnable {
	@Override
	public void run() {
		System.out.println("Running!!!!");
		ZContext ctx = new ZContext ();
		Socket input = ctx.createSocket (ZMQ.SUB);
		input.connect ("inproc://selftest");
		ZreMsg self;
		while(true){
			self = ZreMsg.recv (input);
			System.out.println("msg :: " +self.content ());

		}
		//self.destroy ();

	}
}
