/**
 * 
 */
package system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import event.EventFile;
import event.FloorEvent;
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
	@Test
	void decodePacketDataTest() {
		EventFile file = new EventFile();
		FloorEvent[] events = EventFile.readTextFile(file.getFile());
		Pipe buffer = new Pipe();
		ElevatorSystem es = new ElevatorSystem(1, 11, buffer);
		FloorSystem fs = new FloorSystem(1, 11, buffer, file);
		byte[] data = fs.buildPacketData(events[0]);
		for(int i = 0;i<data.length;i++) {
			System.out.print(data[i] +" ");
		}
		System.out.print("\n");
		FloorEvent fe = es.decodePacketData(data);
		System.out.println(fe.getTime());
		System.out.println(fe.getFloorNumber());
		System.out.println(fe.getDirection());
		System.out.println(fe.getDestinationFloor());

	}
	
	@Test
	void buildPacketDataTest() {
		Pipe buffer = new Pipe();
		ElevatorSystem es = new ElevatorSystem(1, 11, buffer);
		Elevator e = new Elevator(1,11,1);

		byte[] data = es.buildPacketData(e);
		for(int i = 0;i<data.length;i++) {
			System.out.print(data[i] + " ");
		}

		

		
	}

}
