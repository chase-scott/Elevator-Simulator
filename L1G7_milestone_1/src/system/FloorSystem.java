package system;

import java.util.ArrayList;
import java.util.List;

import elevator.Elevator;

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


	public void send() {
		//todo
	}
	
	public void receive() {
		// todo
	}
	
	public void outputSendMessageInformation() {
		// todo
	}
	
	public void outputReceiveMessageInformation() {
		// todo
	}
	
	public Floor getFloorData() {
		return ;
	}
	
	public Scheduler getSchedulerData() {
		return ;
	}
	
	
	public static void main(String args[]) {

		FloorSystem c = new FloorSystem();

		c.send();
		c.receive();

	}
}