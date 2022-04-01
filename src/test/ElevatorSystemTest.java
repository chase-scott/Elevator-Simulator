/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import elevator.Elevator;
import state.DoorState;
import state.MotorState;
import system.ElevatorSystem;
import system.Observer;
import system.Scheduler;

/**
 * @author Colin
 *
 */
class ElevatorSystemTest {

	@Test
	void elevatorSystemTest() {

		ElevatorSystem es = new ElevatorSystem("THIS IS A TEST ELEVATOR SYSTEM");
		HashMap<Integer, Observer> elevators = es.getElevators();
		Elevator e1;
		Elevator e2;
		Elevator e3;
		Elevator e4;
		e1 = (Elevator) elevators.get(1);
		e2 = (Elevator) elevators.get(2);
		e3 = (Elevator) elevators.get(3);
		e4 = (Elevator) elevators.get(4);
		assertEquals(e1.getCurFloor(),1);
		assertEquals(e2.getCurFloor(),1);
		assertEquals(e3.getCurFloor(),1);
		assertEquals(e4.getCurFloor(),1);
		
		assertEquals(e1.getMotor(),MotorState.IDLE);
		assertEquals(e2.getMotor(),MotorState.IDLE);
		assertEquals(e3.getMotor(),MotorState.IDLE);
		assertEquals(e4.getMotor(),MotorState.IDLE);
		
		assertEquals(e1.getDoor(),DoorState.CLOSED);
		assertEquals(e2.getDoor(),DoorState.CLOSED);
		assertEquals(e3.getDoor(),DoorState.CLOSED);
		assertEquals(e4.getDoor(),DoorState.CLOSED);
		es.closeSocket();
		
	}
	
	@Test
	void elevatorSystemSendTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("ElevatorReplyTest");
				s.closeSockets();
			}

		}).start();

		ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
		System.out.println(es.getReceivePacket().getData());	
		assertEquals(es.getReceivePacket().getData()[0],97);
		assertEquals(es.getReceivePacket().getData()[1],99);
		assertEquals(es.getReceivePacket().getData()[2],107);
		assertEquals(es.getReceivePacket().getData()[3],0);
		es.closeSocket();
	
	}
	

}
