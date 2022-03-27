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
import java.util.HashMap;

import event.FloorEvent;
import floor.Floor;

public class FloorSystem {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	private ArrayList<Observer> floorObservers;


	private static byte[] DATA_REQUEST = { 2 };
	private final int SEND_PORT = 23; // the port to send the packets

	public FloorSystem(int MIN_FLOOR, int MAX_FLOOR) {
		
		//add floorObservers for each floor
		floorObservers = new ArrayList<>();
		for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
			floorObservers.add(new Floor(i, i == MIN_FLOOR, i == MAX_FLOOR));
		}
		

		// open socket for sending and receiving data
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
		}

		byte[] return_data = {};

		// continuously ask for elevator states
		while (true) {

			
			rpc_send(FloorEvent.marshal(new FloorEvent()), return_data);
			

			/*
			 * if(new floor event) { rpc_send(FLOOR_EVENT, return_data); }
			 */

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
	
	public FloorSystem(int MIN_FLOOR, int MAX_FLOOR, String test) {
		System.out.println(test);
		//add floorObservers for each floor
		floorObservers = new ArrayList<>();
		for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
			floorObservers.add(new Floor(i, i == MIN_FLOOR, i == MAX_FLOOR));
		}
		

		// open socket for sending and receiving data
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
	 * Send data to the scheduler
	 * 
	 * @param data byte[], the data to send
	 */
	private byte[] send(byte[] data) {

		// create a new Datagram packet to send the message
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// MESSAGE INFORMATION
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] "
				+ "FloorSystem: Sending packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(sendPacket.getData(), 0, len));
		System.out.println("Containing Bytes: " + Arrays.toString(sendPacket.getData()));
		// MESSAGE INFORMATION

		// try to send message over the socket at port 23
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] "
				+ "FloorSystem: Packet sent.\n");

		// Wait on acknowledgment
		byte[] ack = new byte[25];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		if(ack[0] == 1) this.updateObservers(ack);

		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] "
				+ "FloorSystem: returnPacket received: " + new String(Arrays.toString(ack)) + "\n");

		return ack;

	}
	
	private void updateObservers(byte[] data) {
		System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] "
				+ "Updating lamps...");
		floorObservers.forEach(e -> e.update(data));
	}
	public ArrayList<Observer> getFloors(){
		return floorObservers;
	}
	
	
	
	public static void main(String[] args) {
		
		new FloorSystem(1, 22);
	}

}
