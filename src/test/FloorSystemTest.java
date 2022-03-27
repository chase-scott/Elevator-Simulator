/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.EventFile;
import floor.Floor;
import state.Direction;
import system.FloorSystem;

/**
 * @author Colin
 *
 */
class FloorSystemTest {

	@Test
	void floorSystemTest() {
		FloorSystem fs = new FloorSystem(1, 22,"THIS IS A FLOORSYSTEM TEST");
		
		Floor f1 = fs.getFloors().get(0).getFloor();
		assertEquals(f1.getFloorNumber(),1);
		Floor f2 = fs.getFloors().get(1).getFloor();
		assertEquals(f2.getFloorNumber(),2);
		Floor f3 = fs.getFloors().get(2).getFloor();
		assertEquals(f3.getFloorNumber(),3);
		Floor f4 = fs.getFloors().get(3).getFloor();
		assertEquals(f4.getFloorNumber(),4);
	}

}
