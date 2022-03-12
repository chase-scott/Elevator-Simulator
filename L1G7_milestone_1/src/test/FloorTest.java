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
	
	@Test
	void decoderTest() {
		Floor f = new Floor(5,false,false);
		byte[] data = new byte[]{1, 1, 1, 1, 0, 2, 1, 1, 0, 3, 1, 1, 0, 4, 1, 1, 0};
		byte[] floornums = f.decodeByteData(data);
		byte[] expected = new byte[]{1,1,1,1};
		assertEquals(floornums, expected);
	}

}
