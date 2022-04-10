package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import elevator.Elevator;
import state.Direction;
import util.Constants;
import util.Timer;

public class ElevatorSystem implements Runnable {

	private HashMap<Integer, Observer> elevators; // elevatorID, elevator

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;

	private static byte[] DATA_REQUEST = { Constants.DATA_REQUEST };

	private final int SEND_PORT = 69;

	public ElevatorSystem() {

		// create 4 elevators and add them to the system
		this.elevators = new HashMap<>();
		for (int i = 1; i <= Constants.NUM_ELEVATORS; i++) {
			Elevator e = new Elevator(Constants.MIN_FLOOR, Constants.MAX_FLOOR);
			elevators.put(i, e);
			new Thread(e, "Elevator " + i).start();
		}

		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}

	}
	
	
	public ElevatorSystem(String test) {
		System.out.println(test);
		// create 4 elevators and add them to the system
		this.elevators = new HashMap<>();
		for (int i = 1; i < 5; i++) {
			Elevator e = new Elevator(1, 22);
			elevators.put(i, e);
			new Thread(e, "Elevator " + i).start();
		}

		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}
		
		if(test.equals("ElevatorSystemSendTest")) {
			send(this.buildStateData());	
		}else if(test.equals("ElevatorSystemFullTest")) {
			send(this.buildStateData());	
			send(DATA_REQUEST);

		}
		else if(test.equals("ElevatorSystemPickupTest")) {
			send(this.buildStateData());	
			send(DATA_REQUEST);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			send(this.buildStateData());	
			send(DATA_REQUEST);
		}else if(test.equals("ElevatorSystemDropOffTest")) {
			send(this.buildStateData());	
			send(DATA_REQUEST);
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			send(this.buildStateData());	
			send(DATA_REQUEST);
		}else if(test.equals("ElevatorSystemDropOffErrorTest")) {
			send(this.buildStateData());	
			send(DATA_REQUEST);
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			send(this.buildStateData());	
			send(DATA_REQUEST);
		}
		

	}

	/**
	 * Runs the communication loop for the elevator subsystem.
	 */
	@Override
	public void run() {
		// request new data
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		long startTime = Instant.now().getEpochSecond();

		// continuously ask for move info
		while (true) {

			send(this.buildStateData());

			send(DATA_REQUEST);

			try {
				Thread.sleep(Timer.DEFAULT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// check if everything is done
			boolean done = true;
			for (Integer i : elevators.keySet()) {
				if (((Elevator) elevators.get(i)).isHandlingUp() || ((Elevator) elevators.get(i)).isHandlingDown()) {
					done = false;
				}
			}
			//if every event is done being handled, kill program and print execution time
			if (done) {
				long elapsedTime = Instant.now().getEpochSecond();
				System.err.println("Total execution time: " + (elapsedTime - startTime) + "s.");
				System.exit(0);
			}

		}
		
	}
	
	/**
	 * Make a request for data from the scheduler
	 */
	private byte[] send(byte[] data) {

		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// try to send message over the socket at port 69
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// receiving data
		byte[] ack = new byte[32];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}

		// print MESSAGE INFORMATION
		if (Constants.DEBUG)
			printPacket(receivePacket);

		// if move data was received, update observers
		if (ack[0] == Constants.MOVE_DATA) {
			this.updateObservers(ack);
		}

		return ack;

	}

	/**
	 * Update all elevator observers
	 * 
	 * @param data byte[], the data to update
	 */
	private void updateObservers(byte[] data) {
		for (int i : elevators.keySet()) {
			// if this packet is meant for this elevatorID
			if (i == data[1]) {
				elevators.get(i).notify(data);
			}
		}
	}

	/**
	 * Builds the packet that contains the state information of each elevator
	 * 
	 * @return byte[], the state information in the form {1, elevatorID, curFloor,
	 *         motorState, 0, ...}
	 */
	private byte[] buildStateData() {

		ArrayList<Byte> data = new ArrayList<>();
		ArrayList<Byte> errorLamps = new ArrayList<>();
		
		data.add((byte) Constants.ELEVATOR_INFO); // denote it is a elevator state info message

		// packet info (ELEVATOR_INFO, elevatorID, curFloor, motorState, directionHandling, 0 , repeat...)


		for (int i : elevators.keySet()) {

			data.add(((Elevator) elevators.get(i)).getActive() ? (byte) i : (byte) -1);
			
			//if this elevator is not active, add all of it elevators to end of packet
			if (!((Elevator) elevators.get(i)).getActive()) {
				
				((Elevator) elevators.get(i)).getUpfloorQueue().forEach(e -> errorLamps.add(e));
				((Elevator) elevators.get(i)).getDownfloorQueue().forEach(e -> errorLamps.add(e));
				
				((Elevator) elevators.get(i)).getUpfloorQueue().clear();
				((Elevator) elevators.get(i)).getDownfloorQueue().clear();
		
			}

			data.add((byte) ((Elevator) elevators.get(i)).getCurFloor()); // add current floor

			data.add((byte) ((Elevator) elevators.get(i)).getMotor().getState()); //add the motor state
			
			//add the direction the elevator is handling
			if(((Elevator) elevators.get(i)).isHandlingUp()) {
				data.add((byte) Direction.UP.getState());
			} else if (((Elevator) elevators.get(i)).isHandlingDown()) {
				data.add((byte) Direction.DOWN.getState());
			} else {
				data.add((byte)Direction.INVALID.getState());
			}

			data.add((byte) 0);

		}
		
		data.addAll(errorLamps);
 
		byte[] req = new byte[data.size()];

		for (int i = 0; i < data.size(); i++) {
			req[i] = data.get(i);
		}

		return req;

	}

	/**
	 * Returns a string containing all of the packet information
	 * 
	 * @param packet DatagramPacket, the packet
	 * @return String, the info
	 */
	private void printPacket(DatagramPacket packet) {

		StringBuilder sb = new StringBuilder();

		byte[] data = Arrays.copyOf(packet.getData(), packet.getLength()); // truncate packet length

		// IF move data is received, print packet contents
		if (data[0] == Constants.MOVE_DATA) {
			sb.append(Timer.formatTime() + " [ElevatorSystem] Packet received:\n");
			sb.append("\tElevator " + data[1] + " has received move information.\n");
			sb.append("\tPickup at floor: " + data[2] + "\n");
			sb.append("\tWants to go to floor: " + data[3] + "\n");
			sb.append("\tError type: " + data[4] + "\n");

		}
		//if wasn't move data, not worth printing...
		if (!sb.toString().isEmpty()) {
			System.out.println(sb.toString());
		}
	}
	
	public DatagramPacket getReceivePacket() {
		return receivePacket;
	}
	
	public void closeSocket() {
		sendReceiveSocket.close();
	}

	public HashMap<Integer, Observer> getElevators(){
		return elevators;
	}

	public static void main(String[] args) {

		new Thread(new ElevatorSystem(), "ElevatorSystem").start();

	}



}
