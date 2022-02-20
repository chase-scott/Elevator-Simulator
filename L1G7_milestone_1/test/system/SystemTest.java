package system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import event.EventFile;
import event.FloorEvent;
import state.SchedulerState;

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
		FloorEvent[] fe = EventFile.readTextFile(f);
				
		Thread floorSubsystem = new Thread(new FloorSystem(1, 11, buffer, file), "Floor subsystem");
		Thread elevatorSubsystem = new Thread(new ElevatorSystem(1, 11, buffer), "Elevator subsystem");
		Thread schedulingSubsystem = new Thread(new Scheduler(buffer), "Scheduler subsystem");
		buffer.floorToScheduler(fe[0]);
		assertEquals(buffer.isFloorToScheduler(),true);
		schedulingSubsystem.start(); //Scheduler receives FloorEvent -> Sends signal to ElevatorSystem
		assertEquals(buffer.isFloorToScheduler(),true);
		elevatorSubsystem.start(); //Elevator is moving -> ElevatorSystem receives FloorEvent -> Sends signal to Scheduler
		floorSubsystem.start();
		assertEquals(buffer.isFloorToScheduler(),true);
		buffer.sendToElevator(fe[0]);
		assertEquals(buffer.isSchedulerToElevator(),true);
		
		
		System.out.println(buffer.isElevatorToScheduler());
		System.out.println(buffer.isFloorToScheduler());
		System.out.println(buffer.isSchedulerToElevator());
		System.out.println(buffer.isSchedulerToFloor());
		 		
	}

}