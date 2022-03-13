/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.EventFile;
import state.Direction;
import system.FloorSystem;
import system.Pipe;

/**
 * @author Colin
 *
 */
class FloorSystemTest {

	@Test
	void floorSystemTest() {
		Pipe buffer = new Pipe();
		EventFile file = new EventFile();
		FloorSystem fs = new FloorSystem(1, 11, buffer, file);
		Thread floorSubsystem = new Thread(fs, "Floor subsystem");
		assertEquals(buffer.isElevatorToScheduler(),false);
		assertEquals(buffer.isFloorToScheduler(),false);
		assertEquals(buffer.isSchedulerToElevator(),false);
		assertEquals(buffer.isSchedulerToFloor(),false);
		floorSubsystem.start();
		event.FloorEvent fe = new event.FloorEvent("15:20:43.997771100", 4, Direction.UP, 7);
		buffer.floorToScheduler(fe);
		assertEquals(buffer.isElevatorToScheduler(),false);
		assertEquals(buffer.isFloorToScheduler(),true);
		assertEquals(buffer.isSchedulerToElevator(),false);
		assertEquals(buffer.isSchedulerToFloor(),false);


	}

}
