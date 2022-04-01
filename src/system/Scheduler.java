package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import event.ErrorEvent;
import event.Event;
import event.FloorEvent;
import state.Direction;
import state.SchedulerState;
import util.Constants;
import util.Timer;

public class Scheduler {

	private DatagramSocket floorSocket, elevatorSocket;
	private DatagramPacket floorPacket, ackPacket, elevatorPacket;

	private SchedulerState state;

	private ArrayList<byte[]> floorEventQueue;
	private byte[] elevatorStates = { Constants.ELEVATOR_INFO, 1, 1, 1, 0, 2, 1, 1, 0, 3, 1, 1, 0, 4, 1, 1, 0 }; // TODO find a way to not have this hard coded

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
					elevatorReply();

				}
			}
		}).start();

		// floorsystem --> scheduler --> floorsystem
		while (true) {

			floorReply();

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
		if(test.equals("FloorReplyTest")) {
			floorReply();			
		}else if(test.equals("ElevatorReplyTest")) {
			elevatorReply();
		}
		
	}

	/**
	 * elevatorsystem --> scheduler
	 */
	private void elevatorReply() {

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
		System.out.println(
				Timer.formatTime() + " [Scheduler] ElevatorPacket received" + this.printPacket(elevatorPacket));
		// END PACKET INFORMATION

		byte[] ackData;
		// decode packet
		if (data[0] == Constants.DATA_REQUEST) {
			// if elevator system send a request for move information
			// check if there is new move commands
			if (floorEventQueue.size() == 0) {
				// if no new move commands, send empty reply
				ackData = "no new move info".getBytes();
			} else {
				// if new move commands, remove them from queue and send them to elevator system
				// ackData = FloorEvent.marshal(floorEventQueue.remove(0));
				ackData = floorEventQueue.remove(0);
			}
		} else if (data[0] == Constants.ELEVATOR_INFO) {
			// if elevator system sent elevator state information, update elevator states
			// and send acknowledgment
			ackData = Constants.ACK_DATA;
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
		System.out.println(
				Timer.formatTime() + " [Scheduler] Sending packet to ElevatorSystem" + this.printPacket(ackPacket));
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
	private void floorReply() {

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
		System.out.println(Timer.formatTime() + " [Scheduler] FloorPacket received" + this.printPacket(floorPacket));
		// END PACKET INFORMATION

		// if DATA_REQUEST
		byte[] ackData;
		if (data[0] == Constants.DATA_REQUEST) {
			// if floor system is asking for elevator states to update lamps, send elevator
			// states
			ackData = elevatorStates;
		} else {
			// if floor system sent floor event information, reply with acknowledgment
			ackData = Constants.ACK_DATA;

			// TODO schedule the event and notify and add to event queue
			Event event = Event.unmarshal(data);
			if (!Constants.DEBUG) {
				System.out.println("\tReceived floor event with state: " + event + "\n");
			}
			// floorEventQueue.add(fe);

			floorEventQueue.add(chooseElevator(elevatorStates, event));
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(), floorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// PACKET INFORMATION
		System.out.println(
				Timer.formatTime() + " [Scheduler] Sending packet to FloorSystem" + this.printPacket(ackPacket));
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
	public byte[] chooseElevator(byte[] elevatorStateData, Event event) { 
			//elevatorStateData = [1, 0, 1, 0, 0, 1, 1, 0, 0, 2, 1, 0, 0, 3, 1, 0, 0]
				//first bit is 1 denoting that it is a send_request, then it repeats every 4 bits
				//0, 1, 0, 0 --> elevatorID = 0, curFloor = 1, motorState = IDLE, 0 to separate elevators
			

			// algorithm to find which elevator to send to user
				// find if same floor, send elevator that is on same floor and travel direction that has least current users
				// find closest same direction, send closest elevator that has least current users
				// find if idle, send closest idle elevator

		
			// event is error
			if (event instanceof ErrorEvent) {
				ErrorEvent errorEvent = (ErrorEvent) event;
				byte[] chosenElevator = new byte[3]; // [Constants.ERROR_DATA, elevator#, errorType]
				
				chosenElevator[0] = Constants.ERROR_DATA; 
				chosenElevator[1] = (byte) errorEvent.getElevatorID(); // elevator# chosen
				chosenElevator[2] = (byte) errorEvent.getErrorType(); // errorType
				return chosenElevator; // [Constants.ERROR_DATA, elevator#, errorType]
			}
			
			
			// event is floor
			if (event instanceof FloorEvent) {
				FloorEvent floorEvent = (FloorEvent) event;
				
				byte[] chosenElevator = new byte[4];
				boolean foundElevator = false;
				
				// TODO: balance # of users per elevator
				/* same floor, send first elevator that loop finds */
				if (foundElevator == false) {
					byte direction;
					
					// get direction of floorEvent
					if (Direction.parseDirection("up") == floorEvent.getDirection()) {
						direction = 2; // direction UP
					} else {
						if (Direction.parseDirection("down") == floorEvent.getDirection()) {
							direction = 3; // direction DOWN
						} else {
							direction = -1; // direction INVALID
						}
					}
					
					for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
						if (elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == direction || elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == 1) { // check if on same floor, motorState == direction, traveling in same direction OR motor idle
							if (elevatorStateData[i] != -1) { // active elevator
								// set chosenElevator data																
								chosenElevator[0] = Constants.MOVE_DATA; 
								chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
								chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
								chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
								foundElevator = true;
							} // else inactive elevator (inactive elevator -> elevatorStateData[i] = 1), do nothing with elevator	
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
					if (Direction.parseDirection("up") == floorEvent.getDirection()) {
						direction = 2; // direction UP
					} else {
						floorEvent.getDirection();
						if (Direction.parseDirection("down") == floorEvent.getDirection()) {
							direction = 3; // direction DOWN
						} else {
							direction = -1; // direction INVALID
						}
					}

					for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
						if (elevatorStateData[i] != -1) { // active elevator
							// find closest elevator in same direction 
							if (elevatorStateData[i+2] == direction) { // motorState == direction, traveling in same direction
								if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
									if (elevatorStateData[i+2] == 2) { // UP
										if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
											// don't send elevator
										} else { 
											// send elevator
											// set chosenElevator data
											chosenElevator[0] = Constants.MOVE_DATA; 
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
											chosenElevator[0] = Constants.MOVE_DATA; 
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
											chosenElevator[0] = Constants.MOVE_DATA; 
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
												chosenElevator[0] = Constants.MOVE_DATA; 
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
												chosenElevator[0] = Constants.MOVE_DATA; 
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
												chosenElevator[0] = Constants.MOVE_DATA; 
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
						} // else inactive elevator (inactive elevator -> elevatorStateData[i] = 1), do nothing with elevator						
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
						if (elevatorStateData[i] != -1) { // active elevator
							// find closest idle elevator
							if (elevatorStateData[i+2] == 1) { // MotorState IDLE(1)
								if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
									// set chosenElevator data
									chosenElevator[0] = Constants.MOVE_DATA; 
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									firstPass = false;
								} else { // compare and update numFloorsCloser
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = Constants.MOVE_DATA; 
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									}
								}
								foundElevator = true; // found idle elevator 
							}
						} // else inactive elevator (inactive elevator -> elevatorStateData[i] = 1), do nothing with elevator		
					}

					if (foundElevator == true) {
						return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
					}	
				}
			}
	
			/* big error (nothing worked), return 1 byte Constants.ERROR_DATA, no elevator can take user, scheduler big whoopsie  */
			byte[] chosenElevator = new byte[1];
			chosenElevator[0] = Constants.ERROR_DATA;
			return chosenElevator; 
	}

	/**
	 * Returns a string containing all of the packet information
	 * 
	 * @param packet DatagramPacket, the packet
	 * @return String, the info
	 */
	private String printPacket(DatagramPacket packet) {

		StringBuilder sb = new StringBuilder();

		byte[] data = Arrays.copyOf(packet.getData(), packet.getLength()); // truncate packet length

		if (Constants.DEBUG) {
			sb.append(":\n");
			// DEBUG INFORMATIONM
			sb.append("\tTo host: " + packet.getAddress() + "\n");
			sb.append("\tDestination host port: " + packet.getPort() + "\n");
			int len = packet.getLength();
			sb.append("\tLength: " + len + "\n");
			sb.append("\tContaining: " + new String(data, 0, len) + "\n");
			sb.append("\tContaining Bytes: " + Arrays.toString(data) + "\n");

		} else {
			sb.append(".\n");
		}

		return sb.toString();
	}

	public SchedulerState getState() {
		return state;
	}

	
	public ArrayList<byte[]> getFloorEventQueue(){ return floorEventQueue;}
	
	
	public void closeSockets() {
		floorSocket.close();
		elevatorSocket.close();
	}
	
	public DatagramPacket getElevatorPacket() {
		return elevatorPacket;
	}

	public DatagramPacket getFloorPacket() {
		return floorPacket;
	}
	
	public static void main(String[] args) {
		new Scheduler();
	}

}
