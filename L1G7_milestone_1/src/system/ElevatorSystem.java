package system;

import elevator.Elevator;
import elevator.Motor;
import event.FloorEvent;
import state.Direction;
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
	//current floor the elevator is on
	private int currentFloor;


	public ElevatorSystem(int MIN_FLOOR, int MAX_FLOOR, Pipe pipe) {
		this.elevator = new Elevator(MIN_FLOOR, MAX_FLOOR);
		this.pipe = pipe;
		this.isMoving = false;
		this.currentFloor = MIN_FLOOR;
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
				Thread.sleep(250);
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
		
		System.out.println("Elevator is handling event:\nTime = " + event.getTime() + "\nFloor# = " + event.getFloorNumber() + 
				"\nDestination Floor# = " + event.getDestinationFloor() + "\nDirection = " + event.getDirection().toString() + "\n");
		
		if(event.getFloorNumber() - currentFloor != 0) {
			if(event.getFloorNumber() > currentFloor) {
				elevatorMotor.setState(MotorState.UP);
				try {
					System.out.println("Elevator has started moving from " + currentFloor + " towards " + event.getFloorNumber());
					move(Direction.UP, event.getFloorNumber() - currentFloor);
				} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				elevatorMotor.setState(MotorState.DOWN);
				try {
					System.out.println("Elevator has started moving from " + currentFloor + " towards " + event.getFloorNumber());
					move(Direction.DOWN, currentFloor - event.getFloorNumber());
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
		if(event.getDestinationFloor() > currentFloor) {
			elevatorMotor.setState(MotorState.UP);
			try {
				System.out.println("Elevator has started moving from " + currentFloor + " towards " + event.getDestinationFloor());
				move(Direction.UP, event.getDestinationFloor() - currentFloor);
			} catch (InterruptedException e) {e.printStackTrace();}
		} else {
			elevatorMotor.setState(MotorState.DOWN);
			try {
				System.out.println("Elevator has started moving from " + currentFloor + " towards " + event.getDestinationFloor());
				move(Direction.DOWN, currentFloor - event.getDestinationFloor());
			} catch (InterruptedException e) {e.printStackTrace();}
		}


		elevatorMotor.setState(MotorState.IDLE);
		isMoving = false;
	}
	
	private void move(Direction direction, int floorsToMove) throws InterruptedException {
		
		
		for (int i = 0; i < floorsToMove; i++) {
			Thread.sleep(2000);

			if (direction.equals(Direction.UP)) {
				currentFloor++;
				System.out.println("The Elevator has moved up to floor " + currentFloor);
			} else {
				currentFloor--;
				System.out.println("The Elevator has moved down to floor " + currentFloor);
			}
			
			System.out.println(Thread.currentThread().getName() + " has signaled the lamps to the Scheduler.");
			pipe.elevatorToScheduler();
		}
		
		
		System.out.println("The elevator has reached its destination. Opening doors...");
		Thread.sleep(1000);

		elevator.getDoor().switchState();
		System.out.println("Doors are " + elevator.getDoor().getState() + "\n");
		
		System.out.println("Loading/unloading passengers...\n");
		Thread.sleep(5000);
		System.out.println("Done. Closing doors...");
		Thread.sleep(1000);
		
		elevator.getDoor().switchState();
		System.out.println("Door are " + elevator.getDoor().getState()+ "\n");
		
	}
	

}
