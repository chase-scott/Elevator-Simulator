package system;

import floor.FloorEvent;

public class Scheduler implements Runnable {
	
	private Pipe pipe;
	
	public Scheduler(Pipe pipe) {
		this.pipe = pipe;
	}
	
	@Override
	public void run() {
		
		
		while(true) {
			
			pipe.scheduleElevatorJob();
			
			scheduleEvent(pipe.getNextEvent());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
			
		}
	}






	private void scheduleEvent(FloorEvent e) {
		System.out.println(Thread.currentThread().getName() + " is scheduling a new floor event");
		
		//when scheduling is done, notify via pipe that event is ready
		pipe.isElevatorJob(e);
		
		
	}

}
