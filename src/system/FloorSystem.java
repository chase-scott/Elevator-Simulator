package system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import event.EventFile;
import event.FloorEvent;
import floor.Floor;
import util.Constants;
import util.Timer;

public class FloorSystem implements Runnable {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;

	private ArrayList<Observer> floorObservers;
	private FloorEvent[] events; // array of events from the event file

	private static byte[] DATA_REQUEST = { Constants.DATA_REQUEST };
	private final int SEND_PORT = 23; // the port to send the packets

	public FloorSystem() {

		// add floorObservers for each floor
		floorObservers = new ArrayList<>();
		for (int i = Constants.MIN_FLOOR; i <= Constants.MAX_FLOOR; i++) {
			floorObservers.add(new Floor(i, i == Constants.MIN_FLOOR, i == Constants.MAX_FLOOR));
		}

		// read events from event file
		events = EventFile.readTextFile();

		// open socket for sending and receiving data
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
		}

		// close socket
		// sendReceiveSocket.close();

	}
	
	/**
	 * Runs the communication loop for the floor subsystem.
	 */
	@Override
	public void run() {
		// continuously ask for elevator states
		new Thread(new Runnable() {
			public void run() {
				while (true) {

					send(DATA_REQUEST);

					try {
						Thread.sleep(Timer.DEFAULT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();

		// This loop sends new floor events after the specified delay
		int i = 0;
		while (i < events.length) {

			try {				
				Thread.sleep(events[i].getTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (Constants.DEBUG) {
				System.out.println(
						Timer.formatTime() + " [FloorSystem] Sending floor event with state: " + events[i] + "\n");
			}
			
			// send new floor event after delay
			send(FloorEvent.marshal(events[i]));
			
			//turn on the light when there is a new floor event
			for(Observer f : floorObservers) {
				if(((Floor)f).getFloorNumber() == events[i].getFloorNumber()) {
					((Floor)f).toggleLamps(events[i].getDirection());
				}
			}



			i++;

		}

	}


	/**
	 * Send data to the scheduler
	 * 
	 * @param data byte[], the data to send
	 */
	private synchronized byte[] send(byte[] data) {

		// create a new Datagram packet to send the message
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// try to send message over the socket at port 23
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Wait on acknowledgment
		byte[] ack = new byte[256];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}

		if (ack[0] == Constants.ELEVATOR_INFO) {
			
			this.updateObservers(Arrays.copyOf(ack, receivePacket.getLength()));

		}

		return ack;

	}

	/**
	 * Updates each floor's lamps for the given elevator state information.
	 * 
	 * @param data byte[], the data
	 */
	private void updateObservers(byte[] data) {
		floorObservers.forEach(e -> e.notify(data));
	}

	public ArrayList<Observer> getFloors() {
		return floorObservers;
	}

	public static void main(String[] args) {

		new Thread(new FloorSystem(), "FloorSystem").start();

	}

}
