package system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import event.EventFile;
import event.FloorEvent;

/**
 * Pipe class
 * 
 * @author Colin Marsh - 101112765
 */
class SystemTest {

	@Test
	void systemTest() {
		Pipe buffer = new Pipe();
		
		EventFile file = new EventFile();
		File f = file.getFile();
		FloorEvent fe = file.readTextFile(f);
				
		Thread floorSubsystem = new Thread(new FloorSystem(1, 11, buffer, file), "Floor subsystem");
		Thread elevatorSubsystem = new Thread(new ElevatorSystem(1, 11, buffer), "Elevator subsystem");
		Thread schedulingSubsystem = new Thread(new Scheduler(buffer), "Scheduler subsystem");
				
		buffer.floorToScheduler(fe);
		
		assertEquals(buffer.isFloorToScheduler(),true);
		schedulingSubsystem.start(); //Scheduler receives FloorEvent -> Sends signal to ElevatorSystem
		assertEquals(buffer.getNextEvent(),null);
		elevatorSubsystem.start(); //Elevator is moving -> ElevatorSystem receives FloorEvent -> Sends signal to Scheduler
		assertEquals(buffer.isSchedulerToElevator(),true);		
		floorSubsystem.start();
		assertEquals(buffer.isElevatorToScheduler(),true);		
		buffer.schedulerToFloor();
		assertEquals(buffer.isSchedulerToFloor(),true);

		
		
	}

}