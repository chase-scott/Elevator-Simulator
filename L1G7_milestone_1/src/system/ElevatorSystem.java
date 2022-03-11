package system;

import elevator.Elevator;
import elevator.Motor;
import event.FloorEvent;
import state.Direction;
import state.MotorState;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

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

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	private final int SEND_PORT = 69;


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
	
	public FloorEvent decodePacketData(byte[] data) {
		int zero_count = 0;
		byte[] time = new byte[20];
		byte[] floor_num = new byte[1];
		byte[] direction = new byte[2];
		byte[] floor_dest_num = new byte[1];
		int timecount = 0, floornum_count = 0, direction_count = 0, dest_count = 0;
		for(int i = 0 ; i < data.length;i++) {
			if(data[i] == 0) {
				zero_count++;
			}
			else if(zero_count==0) {
				time[timecount] = data[i];
				timecount++;
			}
			else if(zero_count==1) {
				floor_num[floornum_count] = data[i];
				floornum_count++;
			}
			else if(zero_count==2) {
				direction[direction_count] = data[i];
				direction_count++;
			}
			else if(zero_count==3) {
				floor_dest_num[dest_count] = data[i];
				dest_count++;
			}
		}
		String time_string = new String(time);
		System.out.println(time_string);
		int floor_int = floor_num[0];
		System.out.println(floor_int);
		String direction_string = new String(direction);
		Direction direc;

		if(direction_string.strip().equals("up")) {
			direc = Direction.UP;
		}else {
			direc = Direction.DOWN;
		}
		System.out.println(direction_string);
		int dest_floor_int = floor_dest_num[0];
		System.out.println(dest_floor_int);
		FloorEvent fe = new FloorEvent(time_string,floor_int,direc,dest_floor_int);
		return fe;
	}

}
