package main;

import system.*;

public class Main {
	
	public static void main(String[] args) {
		
		Pipe communicationPipe = new Pipe();
	
		Thread floorSystem = new Thread(new FloorSystem(communicationPipe), "floor subsystem");
		Thread schedulerSystem = new Thread(new Scheduler(communicationPipe), "scheduler subsystem");
		Thread elevatorSystem = new Thread(new ElevatorSystem(communicationPipe), "elevator subsystem");

	
		floorSystem.start();
		schedulerSystem.start();
		elevatorSystem.start();

		
		
	}
	
	
	
	

}
