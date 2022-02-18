package system;

import java.util.LinkedList;
import java.util.List;

import event.FloorEvent;

/**
 * Pipe class
 * 
 * @author Chase Scott - 101092194
 */
public class Pipe {
	
	//State variables for checking if a message is being sent through the systems
	private boolean elevatorToScheduler, schedulerToElevator, floorToScheduler, schedulerToFloor;
	
	//List of queued floor events
	private List<FloorEvent> events;

	private FloorEvent generatedEvent;

	public Pipe() {
		floorToScheduler = false;
		schedulerToFloor = false;
		elevatorToScheduler = false;
		schedulerToElevator = false;
		events = new LinkedList<>();
	}

	/**
	 * Send a FloorEvent to the scheduler.
	 * Called by FloorSystem.
	 * 
	 * @param e	FloorEvent, the event
	 */
	public synchronized void floorToScheduler(FloorEvent e) {
		generatedEvent = e;
		floorToScheduler = true;
		notifyAll();
	}

	/**
	 * Notify floor of signal from elevator.
	 * Called by Scheduler.
	 */
	public synchronized void schedulerToFloor() {
		schedulerToFloor = true;
		notifyAll();
	}

	/**
	 * Send FloorEvent to an elevator.
	 * Called by Scheduler.
	 * 
	 * @param e
	 */
	public synchronized void sendToElevator(FloorEvent e) {
		
		events.add(e);
	
		floorToScheduler = false;
	
		
		while (schedulerToElevator) {
			try {
				wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		schedulerToElevator = true;
		notifyAll();
	}

	/**
	 * Send a signal from the elevator to the Scheduler.
	 * Called by ElevatorSystem.
	 */
	public synchronized void elevatorToScheduler() {
		elevatorToScheduler = true;
		notifyAll();
	}
	
	/**
	 * Removes a floor event from the queue
	 * 
	 * @return	FloorEvent, the event
	 */
	public FloorEvent getNextEvent() {	
		
		return events.remove(0);
	}

	public boolean isFloorToScheduler() {
		return floorToScheduler;
	}

	public boolean isSchedulerToFloor() {
		return schedulerToFloor;
	}

	public boolean isElevatorToScheduler() {
		return elevatorToScheduler;
	}

	public boolean isSchedulerToElevator() {
		return schedulerToElevator;
	}

	public void setSchedulerToElevator(boolean isEvent) {
		schedulerToElevator = isEvent;
	}

	public void setElevatorToScheduler(boolean isEvent) {
		elevatorToScheduler = isEvent;
	}

	public void setSchedulerToFloor(boolean isEvent) {
		schedulerToFloor = isEvent;
	}

	public void setFloorToScheduler(boolean isEvent) {
		floorToScheduler = isEvent;
	}

	public FloorEvent getGeneratedEvent() {
		return generatedEvent;
	}

}
