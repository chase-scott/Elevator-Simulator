package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import event.Event;
import event.EventFile;
import event.FloorEvent;
import floor.Floor;
import util.Constants;
import util.Timer;

public class FloorSystem {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	private ArrayList<Observer> floorObservers;
	private Event[] events; //array of events from the event file


	private static byte[] DATA_REQUEST = { Constants.DATA_REQUEST };
	private final int SEND_PORT = 23; // the port to send the packets

	public FloorSystem() {
		
		//add floorObservers for each floor
		floorObservers = new ArrayList<>();
		for (int i = Constants.MIN_FLOOR; i <= Constants.MAX_FLOOR; i++) {
			floorObservers.add(new Floor(i, i == Constants.MIN_FLOOR, i == Constants.MAX_FLOOR));
		}
		
		//read events from event file
		events = EventFile.readTextFile();
		

		// open socket for sending and receiving data
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
		}


		int i = 0;
		// continuously ask for elevator states
		while (true) {

			//send new floor event after delay
			if(i < events.length)
				send(FloorEvent.marshal(events[i]));
			i++;
			
			try {
				Thread.sleep(Timer.DEFAULT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			send(DATA_REQUEST);
			
		}

		// close socket
		// sendReceiveSocket.close();

	}
	
	public FloorSystem(int MIN_FLOOR, int MAX_FLOOR, String test) {
		System.out.println(test);
		//add floorObservers for each floor
		floorObservers = new ArrayList<>();
		for (int i = Constants.MIN_FLOOR; i <= Constants.MAX_FLOOR; i++) {
			floorObservers.add(new Floor(i, i == Constants.MIN_FLOOR, i == Constants.MAX_FLOOR));
		}
		
		//read events from event file
		events = EventFile.readTextFile();
		

		// open socket for sending and receiving data
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
		}

		if(test.equals("SendTest")) {
			send(FloorEvent.marshal(events[0]));

		}

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
		System.out.println(Timer.formatTime() + " [FloorSystem] Sending packet" + printPacket(sendPacket));
		// MESSAGE INFORMATION

		// try to send message over the socket at port 23
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Wait on acknowledgment
		byte[] ack = new byte[25];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		if(ack[0] == Constants.ELEVATOR_INFO) this.updateObservers(ack);

		System.out.println(Timer.formatTime() + " [FloorSystem] Packet received" + printPacket(receivePacket));

		return ack;

	}
	
	/**
	 * Updates each floor's lamps for the given elevator state information.
	 * 
	 * @param data	byte[], the data
	 */
	private void updateObservers(byte[] data) {
		//System.out.println("Updating lamps...");
		floorObservers.forEach(e -> e.update(data));
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
			
			// IF ack data is received, let the user know
			if (Arrays.equals(data, Constants.ACK_DATA)) {
				sb.append("\tAcknowledgedment.\n");
			}
			
			if(data[0] == Constants.ELEVATOR_INFO) {
				sb.append("\tReceived elevator states.\n");
			}
			
			
		}

		return sb.toString();
	}
	
	public ArrayList<Observer> getFloors(){
		return floorObservers;
	}	
	
	public DatagramPacket getReceivePacket() {
		return receivePacket;
	}
	
	public static void main(String[] args) {
		
		new FloorSystem();
	
	}
}
