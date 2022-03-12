package system;

import elevator.Elevator;
import elevator.Motor;
import event.FloorEvent;
import state.Direction;
import state.MotorState;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ElevatorSystem class
 * 
 * @author Chase Scott - 101092194
 */
public class ElevatorSystem implements Runnable {
	
	//the pipe through with this system communicates
	private Pipe pipe;
	//if the elevator is moving
	//the elevator associated with this system
	private ArrayList<Elevator> elevators;
	//current floor the elevator is on

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	private final int SEND_PORT = 69;


	public ElevatorSystem() {
		
		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}
		
		elevators = new ArrayList<Elevator>();
	}

	public void receiveAndReply() {
		//todo
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
	
	public Elevator getElevatorData() {
		return ;
	}
	
	public Scheduler getSchedulerData() {
		return ;
	}
	
	public void addElevator(int elevatorNum) {
		elevatorList.add(new Elevator(elevatorNum, this));
	}

	public Elevator getElevator(int elevatorNum) {
		return elevatorList.get(elevatorNum - 1);
	}
	
	
	public static void main(String[] args) {
		ElevatorSystem c = new ElevatorSystem();
		c.receiveAndReply();
	}

}
