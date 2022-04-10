/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import event.EventFile;
import event.FloorEvent;
import state.Direction;
import state.DoorState;
import state.MotorState;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Observer;
import system.Scheduler;

/**
 * @author Colin
 *
 */
class ElevatorSystemTest {
	
	@Test
	void elevatorSystemSendTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("ElevatorPartialReplyTest");
				s.closeSockets();
			}

		}).start();

		ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
		assertEquals(es.getReceivePacket().getData()[0],97);
		assertEquals(es.getReceivePacket().getData()[1],99);
		assertEquals(es.getReceivePacket().getData()[2],107);
		assertEquals(es.getReceivePacket().getData()[3],0);
		es.closeSocket();
	
	}
	
	@Test
	void elevatorSystemFullTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("FullTest");
				s.closeSockets();
			}

		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"FloorSystemFullTest");
			}
		}).start();

		ElevatorSystem es = new ElevatorSystem("ElevatorSystemFullTest");
		
		assertEquals(es.getReceivePacket().getData()[0],15);
		assertEquals(es.getReceivePacket().getData()[1],1);
		assertEquals(es.getReceivePacket().getData()[2],5);
		assertEquals(es.getReceivePacket().getData()[3],1);
		assertEquals(es.getReceivePacket().getData()[4],-1);

		es.closeSocket();
	
	}
	

}
