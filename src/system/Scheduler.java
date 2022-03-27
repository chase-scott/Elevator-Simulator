<<<<<<< HEAD
package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import event.FloorEvent;
import state.Direction;
import state.SchedulerState;

public class Scheduler {

	private DatagramSocket floorSocket, elevatorSocket;
	private DatagramPacket floorPacket, ackPacket, elevatorPacket;
	
	private SchedulerState state;

	private ArrayList<byte[]> floorEventQueue;
	private byte[] elevatorStates = {1, 1, 1, 1, 0, 2, 1, 1, 0};

	private final int FLOOR_PORT = 23;
	private final int ELEVATOR_PORT = 69;

	public Scheduler() {

		floorEventQueue = new ArrayList<>();

		try {
			floorSocket = new DatagramSocket(FLOOR_PORT);
			elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

		new Thread(new Runnable() {
			public void run() {
				while (true) {

					// elevatorsystem --> scheduler --> elevatorsystem
					reply2();

				}
			}
		}).start();

		// floorsystem --> scheduler --> floorsystem
		while (true) {

			reply();

		}

	}
	
	public Scheduler(String test) {
		System.out.println(test);
		floorEventQueue = new ArrayList<>();

		try {
			floorSocket = new DatagramSocket(FLOOR_PORT);
			elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;
		
		reply();
		
	}

	/**
	 * elevatorsystem --> scheduler
	 */
	private void reply2() {

		System.out.println("reply2()");
		
		this.state = SchedulerState.RECEIVING;

		byte[] data = new byte[25];
		elevatorPacket = new DatagramPacket(data, data.length);

		try {
			elevatorSocket.receive(elevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"]"
				+ " ElevatorSystem Packet received:");
		System.out.println("From: " + elevatorPacket.getAddress());
		System.out.println("Port #: " + elevatorPacket.getPort());
		int len = elevatorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(data, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(data) + "\n");
		// END PACKET INFORMATION

		byte[] ackData;
		// decode packet
		if (data[0] == 2) {
			// if elevator system send a request for move information
			// check if there is new move commands
			if (floorEventQueue.size() == 0) {
				// if no new move commands, send empty reply
				ackData = "no change".getBytes();
			} else {
				// if new move commands, remove them from queue and send them to elevator system
				// ackData = FloorEvent.marshal(floorEventQueue.remove(0));
				ackData = floorEventQueue.remove(0);
			}
		} else if (data[0] == 1) {
			// if elevator system sent elevator state information, update elevator states
			// and send acknowledgment
			ackData = "ack".getBytes();
			this.elevatorStates = data;
		} else {
			// if command is unknown, return unknown
			ackData = "unknown".getBytes();
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(),
					elevatorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Scheduler Sending ack packet:");
		System.out.println("To ElevatorSystem: " + ackPacket.getAddress());
		System.out.println("Destination port: " + ackPacket.getPort());
		len = ackPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(ackData, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(ackData) + "\n");
		// END PACKET INFORMATION

		
		this.state = SchedulerState.SENDING;
		
		try {
			elevatorSocket.send(ackPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

	}

	/**
	 * floorsystem --> scheduler --> floorsystem
	 */
	private void reply() {

		System.out.println("reply()");

		this.state = SchedulerState.RECEIVING;
		
		byte data[] = new byte[256];
		floorPacket = new DatagramPacket(data, data.length);

		try {
			floorSocket.receive(floorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] FloorSystem Packet received:");
		System.out.println("From: " + floorPacket.getAddress());
		System.out.println("Port #: " + floorPacket.getPort());
		int len = floorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(data, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(data) + "\n");
		// END PACKET INFORMATION

		// if DATA_REQUEST
		byte[] ackData;
		if (data[0] == 2) {
			// if floor system is asking for elevator states to update lamps, send elevator
			// states
			ackData = elevatorStates;
		} else {
			// if floor system sent floor event information, reply with acknowledgment
			ackData = "ack".getBytes();

			// TODO schedule the event and notify and add to event queue
			FloorEvent fe = FloorEvent.unmarshal(data);
			System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Received floor event with state: " + fe);
			// floorEventQueue.add(fe);

			floorEventQueue.add(chooseElevator(elevatorStates, fe));
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(), floorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Scheduler Sending ack packet:");
		System.out.println("To FloorSystem: " + ackPacket.getAddress());
		System.out.println("Destination port: " + ackPacket.getPort());
		len = ackPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(ackData, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(ackData) + "\n");
		// END PACKET INFORMATION

		this.state = SchedulerState.SENDING;
		
		try {
			floorSocket.send(ackPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

	}

	/*
	* parameters: byte array of elevator state data, floor data of user waiting on elevator
	* return: byte array [1, elevator#, pickupFloor, destinationFloorNum]
	*			1: send_request
	*/
	public byte[] chooseElevator(byte[] elevatorStateData, FloorEvent floorEvent) { 
			//elevatorStateData = [1, 0, 1, 0, 0, 1, 1, 0, 0, 2, 1, 0, 0, 3, 1, 0, 0]
				//first bit is 1 denoting that it is a send_request, then it repeats every 4 bits
				//0, 1, 0, 0 --> elevatorID = 0, curFloor = 1, motorState = IDLE, 0 to separate elevators
			

			// algorithm to find which elevator to send to user
				// find if same floor, send elevator that is on same floor and travel direction that has least current users
				// find closest same direction, send closest elevator that has least current users
				// find if idle, send closest idle elevator

			byte[] chosenElevator = new byte[4];
			boolean foundElevator = false;				
			
			
			// TODO: balance # of users per elevator
			/* same floor, send first elevator that loop finds */
			if (foundElevator == false) {
				byte direction;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					if (elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == direction || elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == 1) { // check if on same floor, motorState == direction, traveling in same direction OR motor idle
						// set chosenElevator data																
						chosenElevator[0] = 1; // send_request
						chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
						chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
						chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
						foundElevator = true;
					}
	
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find an elevator in the same direction, elevator must be heading to pickupFloor */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				byte direction;
				boolean firstPass = true;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}

				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest elevator in same direction 
					if (elevatorStateData[i+2] == direction) { // motorState == direction, traveling in same direction
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							*/
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							*/	
						}
					}
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find closest idle elevator */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				boolean firstPass = true;
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest idle elevator
					if (elevatorStateData[i+2] == 1) { // MotorState IDLE(1)
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							// set chosenElevator data
							chosenElevator[0] = 1; // send_request
							chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
							chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
							chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
								numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
								// set chosenElevator data
								chosenElevator[0] = 1; // send_request
								chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
								chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
								chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							}
						}
						foundElevator = true; // found idle elevator 
					}	
				}

				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}	
			}	
			
			return chosenElevator; // error, return empty, no elevator can take user
					
	}	
	
	public ArrayList<byte[]> getFloorEventQueue(){ return floorEventQueue;}
	
	public SchedulerState getState() {return state;}

	public static void main(String[] args) {
		new Scheduler();
	}



}
=======
package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import event.FloorEvent;
import state.Direction;
import state.SchedulerState;

public class Scheduler {

	private DatagramSocket floorSocket, elevatorSocket;
	private DatagramPacket floorPacket, ackPacket, elevatorPacket;
	
	private SchedulerState state;

	private ArrayList<byte[]> floorEventQueue;
	private byte[] elevatorStates = {1, 1, 1, 1, 0, 2, 1, 1, 0};

	private final int FLOOR_PORT = 23;
	private final int ELEVATOR_PORT = 69;

	public Scheduler() {

		floorEventQueue = new ArrayList<>();

		try {
			floorSocket = new DatagramSocket(FLOOR_PORT);
			elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

		new Thread(new Runnable() {
			public void run() {
				while (true) {

					// elevatorsystem --> scheduler --> elevatorsystem
					reply2();

				}
			}
		}).start();

		// floorsystem --> scheduler --> floorsystem
		while (true) {

			reply();

		}

	}
	
	public Scheduler(String test) {
		
		System.out.println(test);
		floorEventQueue = new ArrayList<>();

		try {
			floorSocket = new DatagramSocket(FLOOR_PORT);
			elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;
		if(test.equals("ReplyTest")) {
			reply();			
		}else if(test.equals("Reply2Test")) {
			reply2();
		}
		
	}

	/**
	 * elevatorsystem --> scheduler
	 */
	private void reply2() {

		System.out.println("reply2()");
		
		this.state = SchedulerState.RECEIVING;

		byte[] data = new byte[25];
		elevatorPacket = new DatagramPacket(data, data.length);

		try {
			elevatorSocket.receive(elevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"]"
				+ " ElevatorSystem Packet received:");
		System.out.println("From: " + elevatorPacket.getAddress());
		System.out.println("Port #: " + elevatorPacket.getPort());
		int len = elevatorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(data, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(data) + "\n");
		// END PACKET INFORMATION

		byte[] ackData;
		// decode packet
		if (data[0] == 2) {
			// if elevator system send a request for move information
			// check if there is new move commands
			if (floorEventQueue.size() == 0) {
				// if no new move commands, send empty reply
				ackData = "no change".getBytes();
			} else {
				// if new move commands, remove them from queue and send them to elevator system
				// ackData = FloorEvent.marshal(floorEventQueue.remove(0));
				ackData = floorEventQueue.remove(0);
			}
		} else if (data[0] == 1) {
			// if elevator system sent elevator state information, update elevator states
			// and send acknowledgment
			ackData = "ack".getBytes();
			this.elevatorStates = data;
		} else {
			// if command is unknown, return unknown
			ackData = "unknown".getBytes();
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(),
					elevatorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Scheduler Sending ack packet:");
		System.out.println("To ElevatorSystem: " + ackPacket.getAddress());
		System.out.println("Destination port: " + ackPacket.getPort());
		len = ackPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(ackData, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(ackData) + "\n");
		// END PACKET INFORMATION

		
		this.state = SchedulerState.SENDING;
		
		try {
			elevatorSocket.send(ackPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

	}

	/**
	 * floorsystem --> scheduler --> floorsystem
	 */
	private void reply() {

		System.out.println("reply()");

		this.state = SchedulerState.RECEIVING;
		
		byte data[] = new byte[256];
		floorPacket = new DatagramPacket(data, data.length);

		try {
			floorSocket.receive(floorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] FloorSystem Packet received:");
		System.out.println("From: " + floorPacket.getAddress());
		System.out.println("Port #: " + floorPacket.getPort());
		int len = floorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(data, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(data) + "\n");
		// END PACKET INFORMATION

		// if DATA_REQUEST
		byte[] ackData;
		if (data[0] == 2) {
			// if floor system is asking for elevator states to update lamps, send elevator
			// states
			ackData = elevatorStates;
		} else {
			// if floor system sent floor event information, reply with acknowledgment
			ackData = "ack".getBytes();

			// TODO schedule the event and notify and add to event queue
			FloorEvent fe = FloorEvent.unmarshal(data);
			System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Received floor event with state: " + fe);
			// floorEventQueue.add(fe);

			floorEventQueue.add(chooseElevator(elevatorStates, fe));
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(), floorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] Scheduler Sending ack packet:");
		System.out.println("To FloorSystem: " + ackPacket.getAddress());
		System.out.println("Destination port: " + ackPacket.getPort());
		len = ackPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(ackData, 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(ackData) + "\n");
		// END PACKET INFORMATION

		this.state = SchedulerState.SENDING;
		
		try {
			floorSocket.send(ackPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.state = SchedulerState.IDLE;

	}

	/*
	* parameters: byte array of elevator state data, floor data of user waiting on elevator
	* return: byte array [1, elevator#, pickupFloor, destinationFloorNum]
	*			1: send_request
	*/
	public byte[] chooseElevator(byte[] elevatorStateData, FloorEvent floorEvent) { 
			//elevatorStateData = [1, 0, 1, 0, 0, 1, 1, 0, 0, 2, 1, 0, 0, 3, 1, 0, 0]
				//first bit is 1 denoting that it is a send_request, then it repeats every 4 bits
				//0, 1, 0, 0 --> elevatorID = 0, curFloor = 1, motorState = IDLE, 0 to separate elevators
			

			// algorithm to find which elevator to send to user
				// find if same floor, send elevator that is on same floor and travel direction that has least current users
				// find closest same direction, send closest elevator that has least current users
				// find if idle, send closest idle elevator

			byte[] chosenElevator = new byte[4];
			boolean foundElevator = false;				
			
			
			// TODO: balance # of users per elevator
			/* same floor, send first elevator that loop finds */
			if (foundElevator == false) {
				byte direction;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					if (elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == direction || elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == 1) { // check if on same floor, motorState == direction, traveling in same direction OR motor idle
						// set chosenElevator data																
						chosenElevator[0] = 1; // send_request
						chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
						chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
						chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
						foundElevator = true;
					}
	
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find an elevator in the same direction, elevator must be heading to pickupFloor */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				byte direction;
				boolean firstPass = true;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}

				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest elevator in same direction 
					if (elevatorStateData[i+2] == direction) { // motorState == direction, traveling in same direction
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							*/
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							*/	
						}
					}
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find closest idle elevator */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				boolean firstPass = true;
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest idle elevator
					if (elevatorStateData[i+2] == 1) { // MotorState IDLE(1)
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							// set chosenElevator data
							chosenElevator[0] = 1; // send_request
							chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
							chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
							chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
								numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
								// set chosenElevator data
								chosenElevator[0] = 1; // send_request
								chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
								chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
								chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							}
						}
						foundElevator = true; // found idle elevator 
					}	
				}

				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}	
			}	
			
			return chosenElevator; // error, return empty, no elevator can take user
					
	}
	
	public ArrayList<byte[]> getFloorEventQueue(){ return floorEventQueue;}
	
	public SchedulerState getState() {return state;}
	
	public void closeSockets() {
		floorSocket.close();
		elevatorSocket.close();
	}
	
	public DatagramPacket getElevatorPacket() {
		return elevatorPacket;
	}

	public static void main(String[] args) {
		new Scheduler();
	}



}
>>>>>>> a2f7d9b56ab929beb79d172b9e9f6b2f1a06f9d8
