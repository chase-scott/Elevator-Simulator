package system;

import elevator.Elevator;
import floor.FloorEvent;

public class ElevatorSystem implements Runnable {
	
	private Elevator elevator;
	private Pipe pipe;
	
	public ElevatorSystem(Pipe pipe) {
		this.elevator = new Elevator(1, 11);
		this.pipe = pipe;
	}
	
	
	

	@Override
	public void run() {
		
		
		while(true) {
			
			//check if scheduler has job for elevator
			pipe.pollElevatorJob();
			
			handleElevatorJob(pipe.getNextEvent());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
		
		}
	}




	private void handleElevatorJob(FloorEvent e) {
		
		System.out.println(Thread.currentThread().getName() + " is handling elevator job: " + e.toString());
		
		try {
			Thread.sleep(2342);
		} catch (InterruptedException e1) {e1.printStackTrace();}
		
		System.out.println(Thread.currentThread().getName() + " is done handling elevator job: " + e.toString());
		
	}

}
