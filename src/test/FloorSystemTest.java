/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import floor.Floor;
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
		Floor f1 = (Floor) fs.getFloors().get(0);
		assertEquals(f1.getFloorNumber(),1);
		Floor f2 = (Floor) fs.getFloors().get(1);
		assertEquals(f2.getFloorNumber(),2);
		Floor f3 = (Floor) fs.getFloors().get(2);
		assertEquals(f3.getFloorNumber(),3);
		Floor f4 = (Floor) fs.getFloors().get(3);
		assertEquals(f4.getFloorNumber(),4);
	}
	
	@Test
	void floorSystemSendTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("FloorReplyTest");
				s.closeSockets();
			}

		}).start();
		FloorSystem fs = new FloorSystem(1, 22, "SendTest");

		assertEquals(fs.getReceivePacket().getData()[0],97);
		assertEquals(fs.getReceivePacket().getData()[1],99);
		assertEquals(fs.getReceivePacket().getData()[2],107);
		assertEquals(fs.getReceivePacket().getData()[3],0);
		assertEquals(fs.getReceivePacket().getData()[4],0);

		
		

	}

}
