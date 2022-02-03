package main;

import event.EventFile;
import event.FloorEvent;
import system.*;

public class Main {
	
	public static void main(String[] args) {
		
		Pipe communicationPipe = new Pipe();
		EventFile eventFile = new EventFile();
	
		Thread floorSystem = new Thread(new FloorSystem(communicationPipe, eventFile), "floor subsystem");
		Thread schedulerSystem = new Thread(new Scheduler(communicationPipe), "scheduler subsystem");
		Thread elevatorSystem = new Thread(new ElevatorSystem(communicationPipe), "elevator subsystem");

	
		floorSystem.start();
		schedulerSystem.start();
		elevatorSystem.start();
	
		int NUM_EVENTS = 3; //How many events to send
		for(int i = 0; i < NUM_EVENTS; i++) {
			new FloorEvent();
			try {
				Thread.sleep((long) (Math.random()*1000));
			} catch (InterruptedException e) {}
		}
		
		
	}
	
	
	
	

}
