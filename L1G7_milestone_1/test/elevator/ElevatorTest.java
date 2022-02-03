package elevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import state.DoorState;
import state.MotorState;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Pipe;
import system.Scheduler;

class ElevatorTest {

	@Test
	void elevatorTest() {
		Elevator e = new Elevator(1,11);
		assertEquals(e.getDoor().getState(), DoorState.CLOSED);
		assertEquals(e.getMotor().getState(), MotorState.IDLE);

	}

}
