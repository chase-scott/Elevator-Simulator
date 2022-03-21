package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import elevator.Elevator;

public class ElevatorSystem {

	private HashMap<Integer, Observer> elevators; // elevatorID, elevator

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;

	private static byte[] DATA_REQUEST = { 2 };

	private final int SEND_PORT = 69;

	public ElevatorSystem() {

		// create 4 elevators and add them to the system
		this.elevators = new HashMap<>();
		for (int i = 1; i < 3; i++) {
			Elevator e = new Elevator(1, 22);
			elevators.put(i, e);
			new Thread(e, "Elevator " + i).start();;
		}
		
		
		
		

		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}

		byte[] return_data = {};

		// request new data

		// continuously ask for move info
		while (true) {

			rpc_send(this.buildStateData(), return_data);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			rpc_send(DATA_REQUEST, return_data);

		}

		// close socket
		// sendReceiveSocket.close();

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

	}

	private void rpc_send(byte[] out, byte[] in) {

		System.out.println(new String(in, 0, in.length));

		in = send(out);

		System.out.println(new String(in, 0, in.length));

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

		// MESSAGE INFORMATION
		System.out.println("ElevatorSystem: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(sendPacket.getData(), 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(sendPacket.getData()));
		// MESSAGE INFORMATION

		// try to send message over the socket at port 69
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("ElevatorSystem: Packet sent.\n");

		// receiving data
		byte[] ack = new byte[256];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		//if move data was received, update observers
		if(ack[0] == 1) this.updateObservers(ack);

		System.out.println("ElevatorSystem: returnPacket received: " + new String(Arrays.toString(ack)) + "\n");

		return ack;

	}

	/**
	 * Update all elevator observers
	 * 
	 * @param data	byte[], the data to update
	 */
	private void updateObservers(byte[] data) {
		for(int i : elevators.keySet()) {
			//if this packet is meant for this elevatorID
			if(i == data[1]) {
				elevators.get(i).update(data);
			}
		}
	}

	/**
	 * Builds the packet that contains the state information of each elevator
	 * 
	 * @return	byte[], the state information in the form {1, elevatorID, curFloor, motorState, 0, ...}
	 */
	private byte[] buildStateData() {

		ArrayList<Byte> data = new ArrayList<>();
		data.add((byte) 1); // denote it is a send state message

		// packet info (elevatorID, curFloor, desFloor, 0 , elevatorID, curFloor,
		// desFloor)

		for (int i : elevators.keySet()) {

			data.add((byte) i); // add elevatorID

			data.add((byte) ((Elevator) elevators.get(i)).getCurFloor()); // add current floor

			data.add((byte) ((Elevator) elevators.get(i)).getMotor().getState());

			data.add((byte) 0);

		}

		byte[] req = new byte[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			req[i] = data.get(i);
		}

		return req;

	}

	public HashMap<Integer, Observer> getElevators(){
		return elevators;
	}
	
	public void stop() {
		for(int i: elevators.keySet()) {
			elevators.get(i).getElevator().exit();
		}
	}

	public static void main(String[] args) {
		
		new ElevatorSystem();
		
	}

}
