package floor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import state.DoorState;
import state.MotorState;

class FloorTest {

	@Test
	void floorTest() {
		Floor f = new Floor(5,false,false);
		assertEquals(f.getFloorNumber(),5);
	}

}
