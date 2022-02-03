package system;

import java.util.LinkedList;
import java.util.List;

import event.FloorEvent;

public class Pipe {
	
	//if the elevator has a job to service
	private boolean elevatorJob;
	
	//if the floor has produced a floor event
	private boolean floorEvent;
	
	//queue of floor events sent to the pipe
	private List<FloorEvent> events;
	
	public Pipe() {
		this.elevatorJob = false;
		this.floorEvent = false;
		this.events = new LinkedList<>();
	}
	
	
	/**
	 * Check if there is an elevator job
	 */
	public synchronized void pollElevatorJob() {
		
		while(!elevatorJob) {
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		System.out.println(Thread.currentThread().getName() + " is servicing a new elevator job");
		this.elevatorJob = false;
		
		notifyAll();
		
	}


	/**
	 * Inform scheduler that there is a new elevator job
	 */
	public synchronized void scheduleElevatorJob() {
		
		while(!floorEvent) {
			try {
				wait();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		//if removing last FloorEvent in queue
		if(events.size() == 1) {
			this.floorEvent = false;
		}
		System.out.println(Thread.currentThread().getName() + " is scheduling a new floor event");
		notifyAll();
		
	}
	
	/**
	 * FloorSystem calls this inform the scheduler that there is a new floor event
	 */
	public synchronized void sendFloorEvent(FloorEvent e) throws NullPointerException {
		
		System.out.println(Thread.currentThread().getName() + " is sending a new floor event to the scheduler: " + e.toString());
		this.events.add(e);
		this.floorEvent = true;
		
		notifyAll();
		
	}
	
	/**
	 * Called by the scheduler to inform the elevator that the current floor event is ready to be processed
	 */
	public synchronized void isElevatorJob(FloorEvent e) {
		this.events.add(e);
		this.elevatorJob = true;
		notifyAll();
	}
	
	
	public synchronized FloorEvent getNextEvent() {
		
		if(events.size() == 0) return null;
		
		return this.events.remove(0);

	}


}
