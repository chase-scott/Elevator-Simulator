package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import floor.Floor;
import floor.FloorButton;
import state.Direction;


class FloorTest {
	// TODO: add tests to see if Floor button(direction) is correct

	@Test
	void floorTest() {
		// test values
		// bottom floor, middle floor, top floor
		Floor f;
		// int Floor(floorNumber, boolean topFloor, boolean bottomFloor)
		f = new Floor(1,false,false);
		assertEquals(f.getFloorNumber(),1);
		f = new Floor(5,false,false);
		assertEquals(f.getFloorNumber(),5);
		f = new Floor(11,false,false);
		assertEquals(f.getFloorNumber(),11);	
		//assertEquals(f.getFloorButtons(), Direction.DOWN);	// not working rn, showing address	
	}

	@Test
	void floorButtonTest() {
		// FloorButton(Direction direction)
		FloorButton d;
		d = new FloorButton(Direction.UP);
		assertEquals(d.getDirection(), Direction.UP);
		d = new FloorButton(Direction.DOWN);
		assertEquals(d.getDirection(), Direction.DOWN);
	}

}
