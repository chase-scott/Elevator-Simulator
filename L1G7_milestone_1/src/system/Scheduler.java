package system;

import java.util.ArrayList;

import event.FloorEvent;

public class Scheduler implements Runnable {
	
	private Pipe pipe;
	private ArrayList<FloorEvent> queue;

	public Scheduler(Pipe pipe) {
		this.pipe = pipe;
		queue = new ArrayList<FloorEvent>();
	}


	public void handleFloorEvent(FloorEvent e) {

		queue.add(e);
		System.out.println(Thread.currentThread().getName() + " has recieved FloorEvent >>> {" + e.toString() + "}\nSending signal to ElevatorSystem...\n");
		pipe.sendToElevator(e);
		
	}

	public void handleElevatorEvent() {
		System.out.println(Thread.currentThread().getName() + " has received elevator signal. Sending signal to FloorSystem\n");

		pipe.setElevatorToScheduler(false);
		pipe.schedulerToFloor();
	}

	@Override
	public void run() {
		while (true) {
			if (pipe.isFloorToScheduler()) {
				handleFloorEvent(pipe.getGeneratedEvent());
			}
			if (pipe.isElevatorToScheduler()) {
				handleElevatorEvent();
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

}
