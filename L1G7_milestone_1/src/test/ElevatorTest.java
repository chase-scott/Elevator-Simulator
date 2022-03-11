package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import state.DoorState;
import state.MotorState;

class ElevatorTest {

	@Test
	void elevatorTest() {
		Elevator e = new Elevator(1,11,1);
		assertEquals(e.getDoor().getState(), DoorState.CLOSED);
		assertEquals(e.getMotor().getState(), MotorState.IDLE);

	}

}