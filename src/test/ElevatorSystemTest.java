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

/**
 * @author Colin
 *
 */
class ElevatorSystemTest {

	@Test
	void elevatorSystemTest() {
		Thread t = new Thread();
		ElevatorSystem es = new ElevatorSystem("THIS IS A TEST ELEVATOR SYSTEM");
		HashMap<Integer, Observer> elevators = es.getElevators();
		
		Elevator e1;
		Elevator e2;
		Elevator e3;
		Elevator e4;
		e1 = elevators.get(1).getElevator();
		e2 = elevators.get(2).getElevator();
		e3 = elevators.get(3).getElevator();
		e4 = elevators.get(4).getElevator();
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
		
	}
	

}
