package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import state.DoorState;
import state.MotorState;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Scheduler;

class ElevatorTest {

	@Test
	void elevatorTest() {
		Elevator e = new Elevator(1,11);
		assertEquals(e.getDoor(), DoorState.CLOSED);
		assertEquals(e.getMotor(), MotorState.IDLE);
		assertEquals(e.getCurFloor(), 1);


	}

}
