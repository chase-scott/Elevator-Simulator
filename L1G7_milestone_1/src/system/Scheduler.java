package system;

import java.util.ArrayList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import event.FloorEvent;
import state.SchedulerState;

public class Scheduler implements Runnable {
	
	private Pipe pipe;
	private SchedulerState state; 
	private ArrayList<FloorEvent> queue;
	
	private DatagramSocket floorReceiveSocket, elevatorReceiveSocket, floorSendSocket, elevatorSendSocket;
	private DatagramPacket floorSystemPacket, elevatorSystemPacket, floorSendPacket, elevatorSendPacket;
	private final int FLOOR_PORT = 23;
	private final int ELEVATOR_PORT = 69;

	public Scheduler(Pipe pipe) {
		this.pipe = pipe;
		this.state = SchedulerState.IDLE;
		queue = new ArrayList<FloorEvent>();

	}


	public void handleFloorEvent(FloorEvent e) {
		
		queue.add(e);
	
		System.out.println(Thread.currentThread().getName() + " has received FloorEvent:\nTime = " + e.getTime() + "\nFloor# = " + e.getFloorNumber() + 
				"\nDestination Floor# = " + e.getDestinationFloor() + "\nDirection = " + e.getDirection().toString() + "\n");
		
		//scheduler state sending
		state = SchedulerState.SENDING;
		pipe.sendToElevator(queue.remove(0));
		
	}

	public void handleElevatorEvent() {
		System.out.println(Thread.currentThread().getName() + " has received elevator signal. Sending signal to FloorSystem");
		
		
		pipe.setElevatorToScheduler(false);
		pipe.schedulerToFloor();
		//scheduler state sending
		state = SchedulerState.SENDING;
		
	}
	
	public SchedulerState getSchedulerState() {
		return this.state;
	}

	@Override
	public void run() {
		while (true) {
			
			//put scheduler in an idle state
			state = SchedulerState.IDLE;
			
			if (pipe.isElevatorToScheduler()) {
				//scheduler state receiving 
				state = SchedulerState.RECEIVING;
				handleElevatorEvent();
			}
			if (pipe.isFloorToScheduler()) {
				//scheduler state receiving 
				state = SchedulerState.RECEIVING;
				handleFloorEvent(pipe.getGeneratedEvent());
			}

			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
	}

}
