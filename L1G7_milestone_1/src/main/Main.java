package main;

import event.*;
import system.*;

/**
 * Main class for running the program.
 * 
 * @author Chase Scott - 101092194
 */
public class Main {
	
	public static void main(String[] args) {
		

		Pipe buffer = new Pipe();
		EventFile file = new EventFile();
		
		Thread floorSubsystem = new Thread(new FloorSystem(1, 11, buffer, file), "Floor subsystem");
		Thread elevatorSubsystem = new Thread(new ElevatorSystem(1, 11, buffer), "Elevator subsystem");
		Thread schedulingSubsystem = new Thread(new Scheduler(buffer), "Scheduler subsystem");
		
		floorSubsystem.start();
		elevatorSubsystem.start();
		schedulingSubsystem.start();
		
		
		new FloorEvent();
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//new FloorEvent();
		
		
	}
	
}
