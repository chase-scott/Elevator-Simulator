package system;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import event.*;
import floor.Floor;
import state.Direction;

/**
 * FloorSystem class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorSystem implements Runnable {

	//The floors of the building
	private List<Floor> floors;
	//The pipe through which this system communicates
	private Pipe pipe;
	//The EventFile that is read for FloorEvents
	private EventFile eventFile;
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	private final int SEND_PORT = 23; // the port to send the packets

	public FloorSystem(int MIN_FLOOR, int MAX_FLOOR, Pipe pipe, EventFile eventFile) {
		
		floors = new ArrayList<Floor>();
		for (int i = MIN_FLOOR; i <= MAX_FLOOR; i++) {
			floors.add(new Floor(i, i == MIN_FLOOR, i == MAX_FLOOR));
		}
		
		this.pipe = pipe;
		this.eventFile = eventFile;
	}

	/**
	 * Handles the event sent from the scheduler
	 */
	private void handleEvent() {

		System.out.println(Thread.currentThread().getName() + " has received the signal from the elevator.\n");
		pipe.setSchedulerToFloor(false);
	}

	/**
	 * Monitors the EventFile for any updates.
	 * If an updated is detected, send it to the scheduler.
	 */
	private void readEventFile() {

		//read all events in file and send them to scheduler
		FloorEvent[] events = EventFile.readTextFile(eventFile.getFile());
		for(FloorEvent e : events) {
			
			System.out.println(Thread.currentThread().getName() + " has received a new FloorEvent:\nTime = " + e.getTime() + "\nFloor# = " + e.getFloorNumber() + 
				"\nDestination Floor# = " + e.getDestinationFloor() + "\nDirection = " + e.getDirection().toString() + "\n");
			pipe.floorToScheduler(e);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	
		}		
	}
	
	public byte[] buildPacketData(FloorEvent fe) {
		System.out.println("FloorSystem: Building data packet");
		//Getting byte arrays of FloorEvent attributes
		byte[] time = fe.getTime().getBytes();
		byte floor_num = (byte) fe.getFloorNumber();
		byte[] direction = fe.getDirection().getState().getBytes();
		byte floor_dest_num = (byte)fe.getDestinationFloor();
		//Data size is size of FloorEvent attribute byte arrays, plus 5 zero bytes
		int data_size = time.length+1+direction.length+1+5;
		byte[] data = new byte[data_size];
		//Add time byte array to data byte array
		for(int i = 0; i < time.length ; i++) {
			data[i]=time[i];
		}
		int j = time.length;
		data[j] = 0;
		//Add floor num byte array to data byte array
		
		j++;
		data[j]=floor_num;
		
		j++;
		data[j] = 0;
		//Add direction byte array to data byte array
		for(int i = 0; i < direction.length ; i++) {
			j++;
			data[j]=direction[i];
		}
		j++;
		data[j] = 0;
		//Add destination floor num byte array to data byte array
		j++;
		data[j]=floor_dest_num;
		
		j++;
		data[j] = 0;
		j++;
		data[j] = 0;
		return data;
	}
	

	@Override
	public void run() {
		
		//read events from file and send them to scheduler
		readEventFile();
		
		while (true) {
			
			//if scheduler is sending a signal to this system, handle it.
			if (pipe.isSchedulerToFloor()) {
				handleEvent();
			} 
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {e.printStackTrace();}
			
		}
	}
}