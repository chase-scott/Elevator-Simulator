package main;

import java.io.File;

import event.EventFile;
import event.FloorEvent;
import system.*;

public class Main {
	
	public static void main(String[] args) {
		
		Pipe communicationPipe = new Pipe();
		EventFile file = new EventFile(new File(EventFile.EVENT_FILEPATH));
	
		Thread floorSystem = new Thread(new FloorSystem(communicationPipe, file), "floor subsystem");
		Thread schedulerSystem = new Thread(new Scheduler(communicationPipe), "scheduler subsystem");
		Thread elevatorSystem = new Thread(new ElevatorSystem(communicationPipe), "elevator subsystem");

	
		floorSystem.start();
		schedulerSystem.start();
		elevatorSystem.start();
		
		for(int i = 0; i < 3; i++) {
			new FloorEvent();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
	
		}
		
	
	}
	
	
	
	

}
