package system;

import elevator.Elevator;
import elevator.Motor;
import event.FloorEvent;
import state.MotorState;

/**
 * ElevatorSystem class
 * 
 * @author Chase Scott - 101092194
 */
public class ElevatorSystem implements Runnable {
	
	//the pipe through with this system communicates
	private Pipe pipe;
	//if the elevator is moving
	private boolean isMoving;
	//the elevator associated with this system
	private Elevator elevator;


	public ElevatorSystem(int MIN_FLOOR, int MAX_FLOOR, Pipe pipe) {
		this.elevator = new Elevator(MIN_FLOOR, MAX_FLOOR);
		this.pipe = pipe;
		this.isMoving = false;
	}

	@Override
	public void run() {
		while (true) {
			//if there a floor event to handle
			if (pipe.isSchedulerToElevator()) {
				handleEvent();
			}
			//if the elevator is moving
			if (isMoving) {
				handleMove();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	/**
	 * Handles the event sent from the scheduler
	 */
	private void handleEvent() {

		MotorState state = elevator.getMotor().getState();
		//System.out.println("Current Elevator state >>> {" + currentFloor + ", " + state + "}\n");

		//if the elevator isn't currently moving
		if (state.equals(MotorState.IDLE)) {
			pipe.setSchedulerToElevator(false);
			isMoving = true;
		}
	}

	/**
	 * Handles the elevator moving to the destination specified by the FloorEvent
	 */
	private void handleMove() {
		
		Motor elevatorMotor = elevator.getMotor();
		FloorEvent event = pipe.getNextEvent();
		
		
		if(event.getDestinationFloor() > event.getFloorNumber()) {
			elevatorMotor.setState(MotorState.UP);
		} else {
			elevatorMotor.setState(MotorState.DOWN);
		}
		
		//print that a signal has been received from the floor system
		System.out.println(Thread.currentThread().getName() + " has recieved FloorEvent >>> {" + event.toString() + "}");
		//send return signal back to floor system
		System.out.println(Thread.currentThread().getName() + " is sending return signal to the Scheduler.\n");
		pipe.elevatorToScheduler();
		

		elevatorMotor.setState(MotorState.IDLE);
		isMoving = false;
	}

}
