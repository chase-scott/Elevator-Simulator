package system;

import java.util.ArrayList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import elevator.ElevatorData;
import event.FloorEvent;
import state.DoorState;
import state.MotorState;
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
	
	public ElevatorData decodePacketData(byte[] data) {
		byte id = 0;
		byte[] motor = new byte[2];
		byte[] moving = new byte[1];
		byte floornum = 0;
		byte[] door = new byte[2];
		int zero_count =0, motor_count = 0, moving_count = 0, door_count = 0;
		for(int i = 0 ; i < data.length;i++) {
			if(data[i] == 0) {
				zero_count++;
			}
			else if(zero_count==0) {
				id = data[i];
			}
			else if(zero_count==1) {
				floornum = data[i];
			}
			else if(zero_count==2) {
				moving[0] = data[i];
			}
			else if(zero_count==3) {
				motor[motor_count] = data[i];
				motor_count++;
			}
			else{
				door[door_count] = data[i];
				door_count++;
			}
		}	
		
		int id_int = id;
		int floor_int = floornum;
		String motor_string = new String(motor);
		String moving_string = new String(moving);
		String door_string = new String(door);
		DoorState ds;
		MotorState ms;
		boolean is_moving;
		if(motor_string.equals("id")) {
			ms = MotorState.IDLE;
		}else if(motor_string.equals("up")) {
			ms = MotorState.UP;
		}else {
			ms = MotorState.DOWN;
		}
		if(door_string.equals("op")) {
			ds = DoorState.OPEN;
		}else {
			ds = DoorState.CLOSED;
		}
		if(moving_string.equals("T")) {
			is_moving=true;
		}else {
			is_moving=false;
		}
		ElevatorData ed = new ElevatorData(id_int,floor_int,ds,ms,is_moving);
		return ed;
		
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
