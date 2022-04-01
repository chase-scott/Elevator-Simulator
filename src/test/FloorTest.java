package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import floor.Floor;



class FloorTest {

	@Test
	void floorTest() {
		Floor f = new Floor(5,false,false);
		assertEquals(f.getFloorNumber(),5);

	}

}
