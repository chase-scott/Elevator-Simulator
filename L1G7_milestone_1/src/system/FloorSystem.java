package system;

import java.util.ArrayList;
import java.util.List;

import event.*;
import floor.Floor;

/**
 * FloorSystem class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorSystem implements Runnable {

	//The floors of the building
	private List<Floor> floors;
	//The pipe through which this system communicates
	private Pipe pipe;
	//The EventFile that is read for FloorEvents
	private EventFile eventFile;

	public FloorSystem(int MIN_FLOOR, int MAX_FLOOR, Pipe pipe, EventFile eventFile) {
		
		floors = new ArrayList<Floor>();
		for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
			floors.add(new Floor(i, i == MIN_FLOOR, i == MAX_FLOOR));
		}
		
		this.pipe = pipe;
		this.eventFile = eventFile;
	}

	/**
	 * Handles the event sent from the scheduler
	 */
	private void handleEvent() {

		System.out.println(Thread.currentThread().getName() + " has received the signal from the elevator.\n");
		pipe.setSchedulerToFloor(false);
	}

	/**
	 * Monitors the EventFile for any updates.
	 * If an updated is detected, send it to the scheduler.
	 */
	private void monitor() {

		//if file has been updated, retrieve floor event and send to scheduler
		if (eventFile.isFileUpdated()) {
			
			FloorEvent e = EventFile.readTextFile(eventFile.getFile());
			System.out.println(Thread.currentThread().getName() + " has received a new FloorEvent >>> {" + e.toString() + "}\nSending signal to Scheduler...\n");

			pipe.floorToScheduler(e);
		}
	}

	@Override
	public void run() {
		while (true) {
			
			//if scheduler is sending a signal to this system, handle it.
			//else, monitor EventFile
			if (pipe.isSchedulerToFloor()) {
				handleEvent();
			} else {
				monitor();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
			
		}
	}
}