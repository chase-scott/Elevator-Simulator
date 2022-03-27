<<<<<<< HEAD
/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.EventFile;
import floor.Floor;
import state.Direction;
import system.FloorSystem;

/**
 * @author Colin
 *
 */
class FloorSystemTest {

	@Test
	void floorSystemTest() {
		FloorSystem fs = new FloorSystem(1, 22,"THIS IS A FLOORSYSTEM TEST");
		
		Floor f1 = fs.getFloors().get(0).getFloor();
		assertEquals(f1.getFloorNumber(),1);
		Floor f2 = fs.getFloors().get(1).getFloor();
		assertEquals(f2.getFloorNumber(),2);
		Floor f3 = fs.getFloors().get(2).getFloor();
		assertEquals(f3.getFloorNumber(),3);
		Floor f4 = fs.getFloors().get(3).getFloor();
		assertEquals(f4.getFloorNumber(),4);
	}

}
=======
/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.junit.jupiter.api.Test;

import event.EventFile;
import event.FloorEvent;
import floor.Floor;
import state.Direction;
import system.FloorSystem;
import system.Scheduler;

/**
 * @author Colin
 *
 */
class FloorSystemTest {

	@Test
	void floorSystemTest() {
		FloorSystem fs = new FloorSystem(1, 22,"THIS IS A FLOORSYSTEM TEST");
		Floor f1 = fs.getFloors().get(0).getFloor();
		assertEquals(f1.getFloorNumber(),1);
		Floor f2 = fs.getFloors().get(1).getFloor();
		assertEquals(f2.getFloorNumber(),2);
		Floor f3 = fs.getFloors().get(2).getFloor();
		assertEquals(f3.getFloorNumber(),3);
		Floor f4 = fs.getFloors().get(3).getFloor();
		assertEquals(f4.getFloorNumber(),4);
	}
	
	@Test
	void floorSystemSendTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("ReplyTest");
			}

		}).start();
		FloorSystem fs = new FloorSystem(1, 22, "SendTest");
		assertEquals(fs.getReceivePacket().getData()[0],1);
		assertEquals(fs.getReceivePacket().getData()[1],1);
		assertEquals(fs.getReceivePacket().getData()[2],1);
		assertEquals(fs.getReceivePacket().getData()[3],1);
		assertEquals(fs.getReceivePacket().getData()[4],0);
		assertEquals(fs.getReceivePacket().getData()[5],2);
		assertEquals(fs.getReceivePacket().getData()[6],1);
		assertEquals(fs.getReceivePacket().getData()[7],1);
		assertEquals(fs.getReceivePacket().getData()[8],0);
		

	}

}
>>>>>>> a2f7d9b56ab929beb79d172b9e9f6b2f1a06f9d8
