/**
 * 
 */
package system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.EventFile;
import state.Direction;

/**
 * @author Colin
 *
 */
class ElevatorSystemTest {

	@Test
	void elevatorSystemTest() {
		Pipe buffer = new Pipe();
		EventFile file = new EventFile();
		ElevatorSystem es = new ElevatorSystem(1, 11, buffer);
		Thread elevatorSubsystem = new Thread(es, "Floor subsystem");
		assertEquals(buffer.isElevatorToScheduler(),false);
		assertEquals(buffer.isFloorToScheduler(),false);
		assertEquals(buffer.isSchedulerToElevator(),false);
		assertEquals(buffer.isSchedulerToFloor(),false);
		elevatorSubsystem.start();
		event.FloorEvent fe = new event.FloorEvent("15:20:43.997771100", 4, Direction.UP, 7);
		buffer.sendToElevator(fe);
		assertEquals(buffer.isElevatorToScheduler(),false);
		assertEquals(buffer.isFloorToScheduler(),false);
		assertEquals(buffer.isSchedulerToElevator(),true);
		assertEquals(buffer.isSchedulerToFloor(),false);

	}

}
