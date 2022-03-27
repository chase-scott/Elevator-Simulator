package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import floor.Floor;
import floor.FloorButton;
import state.Direction;


class FloorTest {

	@Test
	void floorTest() {
		Floor f = new Floor(5,false,false);
		assertEquals(f.getFloorNumber(),5);

	}

}
