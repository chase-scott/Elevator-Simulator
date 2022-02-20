package system;

import java.util.ArrayList;

import event.FloorEvent;
import state.SchedulerState;

public class Scheduler implements Runnable {
	
	private Pipe pipe;
	private SchedulerState state; 
	private ArrayList<FloorEvent> queue;
	
	

	public Scheduler(Pipe pipe) {
		this.pipe = pipe;
		this.state = SchedulerState.IDLE;
		queue = new ArrayList<FloorEvent>();

	}


	public void handleFloorEvent(FloorEvent e) {
		
		queue.add(e);
	
		System.out.println(Thread.currentThread().getName() + " has received FloorEvent:\nTime = " + e.getTime() + "\nFloor# = " + e.getFloorNumber() + 
				"\nDestination Floor# = " + e.getDestinationFloor() + "\nDirection = " + e.getDirection().toString() + "\n");
		
		//scheduler state sending
		state = SchedulerState.SENDING;
		pipe.sendToElevator(queue.remove(0));
		
	}

	public void handleElevatorEvent() {
		System.out.println(Thread.currentThread().getName() + " has received elevator signal. Sending signal to FloorSystem");
		
		
		pipe.setElevatorToScheduler(false);
		pipe.schedulerToFloor();
		//scheduler state sending
		state = SchedulerState.SENDING;
		
	}
	
	public SchedulerState getSchedulerState() {
		return this.state;
	}

	@Override
	public void run() {
		while (true) {
			
			//put scheduler in an idle state
			state = SchedulerState.IDLE;
			
			if (pipe.isElevatorToScheduler()) {
				//scheduler state receiving 
				state = SchedulerState.RECEIVING;
				handleElevatorEvent();
			}
			if (pipe.isFloorToScheduler()) {
				//scheduler state receiving 
				state = SchedulerState.RECEIVING;
				handleFloorEvent(pipe.getGeneratedEvent());
			}

			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
	}

}
