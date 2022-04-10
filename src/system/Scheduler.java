package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import event.FloorEvent;
import state.MotorState;
import state.SchedulerState;
import util.Constants;
import util.Timer;

/**
 * Scheduler class
 * 
 * @author Chase Scott - 101092194
 */
public class Scheduler implements Runnable {

	private DatagramSocket floorSocket, elevatorSocket;
	private DatagramPacket floorPacket, ackPacket, elevatorPacket;

	private SchedulerState state;

	private ArrayList<byte[]> floorEventQueue;

	private byte[] elevatorStates;

	private final int FLOOR_PORT = 23;
	private final int ELEVATOR_PORT = 69;

	public Scheduler() {

		initElevatorStates();

		floorEventQueue = new ArrayList<>();

		try {
			floorSocket = new DatagramSocket(FLOOR_PORT);
			elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		this.state = SchedulerState.IDLE;

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
			floorReply();			

		}else if(test.equals("ElevatorReplyTest")) {
			elevatorReply();
			elevatorReply();

		}else if(test.equals("FullTest")) {
			elevatorReply();
			floorReply();
			elevatorReply();
			floorReply();
		}
		else if(test.equals("PickUpTest")) {
			elevatorReply();
			floorReply();
			elevatorReply();
			floorReply();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			elevatorReply();
			elevatorReply();

		}
		else if(test.equals("DropOffTest")) {
			elevatorReply();
			floorReply();
			elevatorReply();
			floorReply();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			elevatorReply();
			elevatorReply();

		}
		else if(test.equals("DropOffErrorTest")) {
			elevatorReply();
			floorReply();
			elevatorReply();
			floorReply();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			elevatorReply();
			elevatorReply();

		}
		else if(test.equals("ElevatorPartialReplyTest")) {
			elevatorReply();
		}else if (test.equals("PartialReplyTest")) {
			elevatorReply();
			floorReply();
		}
		
	}

	/**
	 * Runs the basic loop for the scheduler. Monitors packet information from both
	 * the elevator and floor subsystems.
	 */
	@Override
	public void run() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					// elevator system --> scheduler
					elevatorReply();
				}
			}
		}).start();

		while (true) {
			// floor system --> scheduler
			floorReply();
		}

	}

	/**
	 * elevatorsystem --> scheduler
	 */
	private void elevatorReply() {

		this.state = SchedulerState.RECEIVING;

		byte[] data = new byte[256];
		elevatorPacket = new DatagramPacket(data, data.length);

		try {
			elevatorSocket.receive(elevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

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
			this.elevatorStates = Arrays.copyOf(data, elevatorPacket.getLength());
			;
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
		// System.out.println(
		// Timer.formatTime() + " [Scheduler] Sending packet to ElevatorSystem" +
		// this.printPacket(ackPacket));
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

		// if DATA_REQUEST
		byte[] ackData;
		if (data[0] == Constants.DATA_REQUEST) {
			// if floor system is asking for elevator states to update lamps, send elevator
			// states
			ackData = elevatorStates;
		} else {
			// if floor system sent floor event information, reply with acknowledgment
			ackData = Constants.ACK_DATA;

			FloorEvent event = FloorEvent.unmarshal(data);

			// print information
			if (Constants.DEBUG) {
				System.out.println(
						Timer.formatTime() + " [Scheduler] Scheduling floor event with state: " + event + "\n");
			}

			byte[] chosenElevator = scheduleEvent(elevatorStates, event);

			floorEventQueue.add(chosenElevator);
		}

		try {
			ackPacket = new DatagramPacket(ackData, ackData.length, InetAddress.getLocalHost(), floorPacket.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.state = SchedulerState.SENDING;

		try {
			floorSocket.send(ackPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.state = SchedulerState.IDLE;

	}

	/**
	 * Schedules a floor event for the given elevator states
	 * 
	 * @param elevatorStateData	byte[], the elevator states
	 * @param event				FloorEvent, the event to schedule
	 * @return					byte[], the move data
	 */
	private byte[] scheduleEvent(byte[] elevatorStateData, FloorEvent event) {

		int fs = 1; // current highest floor suitability value
		int selectedCar = 1; // assume first car was best

		for (int i = 1; i < (Constants.NUM_ELEVATORS * 5 + 1); i += 5) {

			int newfs = 0; // new fs checking

			// if this elevator has been disabled, skip
			if (elevatorStateData[i] == -1) {
				// set fs factor to -1 so it'll never be chosen
				newfs = -1;
				continue;
			}

			// distance between current floor and floor the call is from
			int d = Math.abs(event.getFloorNumber() - elevatorStateData[i + 1]);

			// if elevator is idle
			if (elevatorStateData[i + 2] == MotorState.IDLE.getState()) {

				newfs = Constants.MAX_FLOOR + 1 - d;

			}

			// if elevator is going down
			else if (elevatorStateData[i + 2] == MotorState.DOWN.getState()) {

				// if call is above or on same floor and not idle
				if (event.getFloorNumber() > elevatorStateData[i + 1]
						|| elevatorStateData[i + 1] == event.getFloorNumber()) {
					newfs = 1;
				}
				// if call is below and call direction is same as elevator direction
				else if (event.getFloorNumber() < elevatorStateData[i + 1]
						&& event.getDirection().getState() == elevatorStateData[i + 2]) {
					newfs = Constants.MAX_FLOOR + 2 - d;
				}
				// if call is below and call direction is not the same as elevator direction
				else if (event.getFloorNumber() < elevatorStateData[i + 1]
						&& event.getDirection().getState() != elevatorStateData[i + 2]) {
					newfs = Constants.MAX_FLOOR + 1 - d;
				}

			}
			// if elevator is going up
			else if (elevatorStateData[i + 2] == MotorState.UP.getState()) {
				// if call is below or on same floor and not idle
				if (event.getFloorNumber() < elevatorStateData[i + 1]
						|| elevatorStateData[i + 1] == event.getFloorNumber()) {
					newfs = 1;
				}
				// if call is above and call direction is same as elevator direction
				else if (event.getFloorNumber() > elevatorStateData[i + 1]
						&& event.getDirection().getState() == elevatorStateData[i + 2]) {
					newfs = Constants.MAX_FLOOR + 2 - d;
				}
				// if call is above and call direction is not the same as elevator direction
				else if (event.getFloorNumber() > elevatorStateData[i + 1]
						&& event.getDirection().getState() != elevatorStateData[i + 2]) {
					newfs = Constants.MAX_FLOOR + 1 - d;
				}

			}
			// if we found a new best fs
			if (newfs > fs) {
				selectedCar = elevatorStateData[i];
				fs = newfs;
			}

		}

		// create move data byte data
		byte[] moveData = new byte[5];
		moveData[0] = Constants.MOVE_DATA;
		moveData[1] = (byte) selectedCar; // elevator# chosen
		moveData[2] = (byte) event.getFloorNumber(); // pickupFloor#
		moveData[3] = (byte) event.getDestinationFloor(); // destinationFloor
		moveData[4] = (byte) event.getErrorType();

		return moveData;
	}

	/**
	 * Initializes the elevator states to their default values
	 */
	private void initElevatorStates() {
		// generate initial elevator states
		ArrayList<Byte> temp = new ArrayList<>();
		temp.add((byte) Constants.ELEVATOR_INFO);
		for (int i = 1; i <= Constants.NUM_ELEVATORS; i++) {
			temp.add((byte) i);
			temp.add((byte) 1);
			temp.add((byte) 1);
			temp.add((byte) -1);
			temp.add((byte) 0);
		}
		byte[] req = new byte[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			req[i] = temp.get(i);
		}
		this.elevatorStates = req;
	}

	public SchedulerState getState() {
		return state;
	}
	
	public byte[] getElevatorStates() {
		return elevatorStates;
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
		new Thread(new Scheduler(), "Scheduler").start();
	}

}
