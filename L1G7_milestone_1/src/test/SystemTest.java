package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import event.EventFile;
//import event.FloorEvent;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Pipe;
import system.Scheduler;

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
		//FloorEvent fe = EventFile.readTextFile(f);
				
		Thread floorSubsystem = new Thread(new FloorSystem(1, 11, buffer, file), "Floor subsystem");
		Thread elevatorSubsystem = new Thread(new ElevatorSystem(1, 11, buffer), "Elevator subsystem");
		Thread schedulingSubsystem = new Thread(new Scheduler(buffer), "Scheduler subsystem");
				
		//buffer.floorToScheduler(fe);
		
		assertEquals(buffer.isFloorToScheduler(),true);
		schedulingSubsystem.start(); //Scheduler receives FloorEvent -> Sends signal to ElevatorSystem
		assertEquals(buffer.getNextEvent(),null);
		elevatorSubsystem.start(); //Elevator is moving -> ElevatorSystem receives FloorEvent -> Sends signal to Scheduler
		assertEquals(buffer.isSchedulerToElevator(),false);		
		floorSubsystem.start();
		assertEquals(buffer.isElevatorToScheduler(),false);		
		buffer.schedulerToFloor();
		assertEquals(buffer.isSchedulerToFloor(),true);

		
		
	}

}
