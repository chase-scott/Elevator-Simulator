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
		Elevator e = new Elevator(1,11,1,1);
		assertEquals(e.getDoor().getState(), DoorState.CLOSED);
		assertEquals(e.getMotor().getState(), MotorState.IDLE);

	}
	
	@Test
	void pressButtonTest() {
		Elevator e = new Elevator(1,11,1,1);
		e.pressButton(5);
		assertEquals(e.getPressedButtons().get(0).getTargetFloor(),5);
		e.pressButton(6);
		assertEquals(e.getPressedButtons().get(1).getTargetFloor(),6);
		e.pressButton(7);
		e.pressButton(8);
		assertEquals(e.getPressedButtons().get(2).getTargetFloor(),7);
		assertEquals(e.getPressedButtons().get(3).getTargetFloor(),8);


	}

}
