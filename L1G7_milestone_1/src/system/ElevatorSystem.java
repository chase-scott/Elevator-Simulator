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
import java.util.ArrayList;
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
	//the elevator associated with this system
	private ArrayList<Elevator> elevators;
	//current floor the elevator is on

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	private final int SEND_PORT = 69;


	public ElevatorSystem(int MIN_FLOOR, int MAX_FLOOR, Pipe pipe) {
		elevators = new ArrayList<>();
		this.elevators.add(new Elevator(MIN_FLOOR, MAX_FLOOR,MIN_FLOOR,1));
		this.pipe = pipe;
	}

	@Override
	public void run() {
		Elevator elevator = elevators.get(0);
		while (true) {
			//if there a floor event to handle
			if (pipe.isSchedulerToElevator()) {
				handleEvent();
			}
			//if the elevator is moving
			if (elevator.getIsMoving()) {
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
		Elevator e = elevators.get(0);
		MotorState state = e.getMotor().getState();
		//System.out.println("Current Elevator state >>> {" + currentFloor + ", " + state + "}\n");

		//if the elevator isn't currently moving
		if (state.equals(MotorState.IDLE)) {
			pipe.setSchedulerToElevator(false);
			e.setMoving(true);
		}
	}

	/**
	 * Handles the elevator moving to the destination specified by the FloorEvent
	 */
	private void handleMove() {
		Elevator elevator = elevators.get(0);
		Motor elevatorMotor = elevator.getMotor();
		FloorEvent event = pipe.getNextEvent();
		
		System.out.println("Elevator is handling event:\nTime = " + event.getTime() + "\nFloor# = " + event.getFloorNumber() + 
				"\nDestination Floor# = " + event.getDestinationFloor() + "\nDirection = " + event.getDirection().toString() + "\n");
		
		if(event.getFloorNumber() - elevator.getFloorNum() != 0) {
			if(event.getFloorNumber() > elevator.getFloorNum()) {
				elevatorMotor.setState(MotorState.UP);
				try {
					System.out.println("Elevator has started moving from " + elevator.getFloorNum() + " towards " + event.getFloorNumber());
					move(Direction.UP, event.getFloorNumber() - elevator.getFloorNum());
				} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				elevatorMotor.setState(MotorState.DOWN);
				try {
					System.out.println("Elevator has started moving from " + elevator.getFloorNum() + " towards " + event.getFloorNumber());
					move(Direction.DOWN, elevator.getFloorNum() - event.getFloorNumber());
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
		if(event.getDestinationFloor() > elevator.getFloorNum()) {
			elevatorMotor.setState(MotorState.UP);
			try {
				System.out.println("Elevator has started moving from " + elevator.getFloorNum() + " towards " + event.getDestinationFloor());
				move(Direction.UP, event.getDestinationFloor() - elevator.getFloorNum());
			} catch (InterruptedException e) {e.printStackTrace();}
		} else {
			elevatorMotor.setState(MotorState.DOWN);
			try {
				System.out.println("Elevator has started moving from " + elevator.getFloorNum() + " towards " + event.getDestinationFloor());
				move(Direction.DOWN, elevator.getFloorNum() - event.getDestinationFloor());
			} catch (InterruptedException e) {e.printStackTrace();}
		}


		elevatorMotor.setState(MotorState.IDLE);
		elevator.setMoving(false);
	}
	
	private void move(Direction direction, int floorsToMove) throws InterruptedException {
		Elevator elevator = elevators.get(0);
		
		for (int i = 0; i < floorsToMove; i++) {
			Thread.sleep(2000);

			if (direction.equals(Direction.UP)) {
				elevator.moveUpFloor();
				System.out.println("The Elevator has moved up to floor " + elevator.getFloorNum());
			} else {
				elevator.moveDownFloor();
				System.out.println("The Elevator has moved down to floor " + elevator.getFloorNum());
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
	
	public byte[] buildPacketData(Elevator e) {
		System.out.println("ElevatorSystem: Building data packet");
		byte elevator_id = (byte)e.getId();
		byte floor_num = (byte)e.getFloorNum();
		byte[] moving;
		if(e.getIsMoving()==true) {
			moving="T".getBytes();
		}else {
			moving="F".getBytes();
		}
		byte[] motor = e.getMotor().getState().getState().getBytes();
		byte[] door = e.getDoor().getState().getState().getBytes();
		int data_size = 3 + moving.length + motor.length + door.length + 5;		
		byte[] data = new byte[data_size];
		
		data[0] = elevator_id;
		data[1] = 0;
		//Add floor num byte to data byte array	
		data[2]=floor_num;
		data[3] = 0;
		int j =4;
		//Add moving byte array to data byte array
		for(int i = 0; i < moving.length ; i++) {
			data[j]=moving[i];
			j++;
		}
		data[j] = 0;
		//Add destination floor num byte array to data byte array
		j++;
		for(int i = 0; i < motor.length ; i++) {
			data[j]=motor[i];
			j++;
		}
		data[j] = 0;
		j++;
		for(int i = 0; i < door.length ; i++) {
			data[j]=door[i];
			j++;
		}
		data[j] = 0;
		j++;
		data[j] = 0;		
		return data;
		//
		
	}
	
	public Elevator getElevator(int index) {
		return elevators.get(index);
	}

}
