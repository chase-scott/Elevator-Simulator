package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import state.DoorState;
import state.MotorState;
import elevator.Door;


class ElevatorTest {

	@Test
	void elevatorTest() {
		Elevator e = new Elevator(1,11);
		assertEquals(e.getDoor().getState(), DoorState.CLOSED);
		e.getDoor().switchState();
		assertEquals(e.getDoor().getState(), DoorState.OPEN);
		assertEquals(e.getMotor().getState(), MotorState.IDLE);

	}

	@Test
	void doorTest() {
		Door d;
		d = new Door(DoorState.CLOSED);
		assertEquals(d.getState(), DoorState.CLOSED);
		d.switchState();
		assertEquals(d.getState(), DoorState.OPEN);
	}

}