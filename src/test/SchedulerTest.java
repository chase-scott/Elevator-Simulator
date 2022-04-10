package test;

import static org.junit.jupiter.api.Assertions.*;

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

import org.junit.jupiter.api.Test;

import elevator.Elevator;
import event.FloorEvent;
import event.EventFile;
import state.Direction;
import state.SchedulerState;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Observer;
import system.Scheduler;

class SchedulerTest {	
	
	@Test
	void schedulerElevatorReplyTest() {
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
				es.closeSocket();
			}
		}).start();
		Scheduler s = new Scheduler("ElevatorPartialReplyTest");
		assertEquals(SchedulerState.IDLE,s.getState());
		byte[] data = s.getElevatorPacket().getData();

		assertEquals(data[0],2);
		assertEquals(data[1],1);
		assertEquals(data[2],1);
		assertEquals(data[3],1);
		assertEquals(data[4],-1);
		assertEquals(data[5],0);
		assertEquals(data[6],2);
		assertEquals(data[7],1);
		assertEquals(data[8],1);
		assertEquals(data[9],-1);
		assertEquals(data[10],0);
		assertEquals(data[11],3);
		assertEquals(data[12],1);
		assertEquals(data[13],1);
		assertEquals(data[14],-1);
		assertEquals(data[15],0);
		assertEquals(data[16],4);
		assertEquals(data[17],1);
		assertEquals(data[18],1);
		assertEquals(data[19],-1);
		assertEquals(data[20],0);
		s.closeSockets();
		
	}
	
	@Test
	void schedulerFloorReplyTest() {
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
				es.closeSocket();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"SendTest");
			}
		}).start();
		Scheduler s = new Scheduler("PartialReplyTest");

		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(s.getFloorPacket().getData()[0],-84);
		assertEquals(s.getFloorPacket().getData()[1],-19);
		assertEquals(s.getFloorPacket().getData()[2],0);
		assertEquals(s.getFloorPacket().getData()[3],5);
		assertEquals(s.getFloorPacket().getData()[4],115);
		assertEquals(s.getFloorPacket().getData()[5],114);
   	    assertEquals(s.getFloorPacket().getData()[6],0);
		assertEquals(s.getFloorPacket().getData()[7],16);
		assertEquals(s.getFloorPacket().getData()[8],101);
		assertEquals(s.getFloorPacket().getData()[9],118);
		assertEquals(s.getFloorPacket().getData()[10],101);
		assertEquals(s.getFloorPacket().getData()[11],110);
		s.closeSockets();
	}
	
	@Test
	void floorErrorTest() {
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
				es.closeSocket();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"FloorErrorTest");
			}
		}).start();
		
		Scheduler s = new Scheduler("PartialReplyTest");
		ArrayList<byte[]> queue = s.getFloorEventQueue();
		byte[] event = queue.get(0);
		for (byte b : event) {
			System.out.println(b + " ");
		}
		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(event[0],15);
		assertEquals(event[1],1);
		assertEquals(event[2],17);
		assertEquals(event[3],3);
		assertEquals(event[4],-1);
		s.closeSockets();

	}
	
	@Test
	void doorErrorTest() {
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
				es.closeSocket();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"DoorErrorTest");
			}
		}).start();
		
		Scheduler s = new Scheduler("PartialReplyTest");
		ArrayList<byte[]> queue = s.getFloorEventQueue();
		byte[] event = queue.get(0);
		for (byte b : event) {
			System.out.println(b + " ");
		}
		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(event[0],15);
		assertEquals(event[1],1); 
		assertEquals(event[2],2);
		assertEquals(event[3],5);
		assertEquals(event[4],-69);
		byte[] states = s.getElevatorStates();

		s.closeSockets();
	}
	
	@Test
	void schedulerPickupTestTest() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"FloorSystemFullTest");
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemPickupTest");
				es.closeSocket();
			}
		}).start();
		
		Scheduler s = new Scheduler("PickUpTest");
		DatagramPacket fp = s.getFloorPacket();
		DatagramPacket ep = s.getElevatorPacket();
		for(byte b:fp.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();
		for(byte b:ep.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();

		ArrayList<byte[]> queue = s.getFloorEventQueue();		
		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(fp.getData()[0],1);
		assertEquals(fp.getData()[1],0);
		
		assertEquals(ep.getData()[0],1);
		assertEquals(ep.getData()[1],0);
		assertEquals(queue.isEmpty(),true);
		byte[] states = s.getElevatorStates();
		//Elevator States
		assertEquals(states[0],2);
		assertEquals(states[1],1);
		assertEquals(states[2],5);
		assertEquals(states[3],1);
		assertEquals(states[4],3);
		assertEquals(states[5],0);
		assertEquals(states[6],2);
		assertEquals(states[7],1);
		assertEquals(states[8],1);
		assertEquals(states[9],-1);
		assertEquals(states[10],0);
		assertEquals(states[11],3);
		assertEquals(states[12],1);
		assertEquals(states[13],1);
		assertEquals(states[14],-1);
		assertEquals(states[15],0);		
		assertEquals(states[16],4);
		assertEquals(states[17],1);
		assertEquals(states[18],1);
		assertEquals(states[19],-1);
		assertEquals(states[20],0);
		s.closeSockets();
			
	}
	
	@Test
	void schedulerDropoffTest() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"FloorSystemFullTest");
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemDropOffTest");
				es.closeSocket();
			}
		}).start();
		
		Scheduler s = new Scheduler("DropOffTest");
		DatagramPacket fp = s.getFloorPacket();
		DatagramPacket ep = s.getElevatorPacket();
		for(byte b:fp.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();
		for(byte b:ep.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();

		ArrayList<byte[]> queue = s.getFloorEventQueue();

//		for (byte b : event) {
//			System.out.println(b + " ");
//		}
		
		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(fp.getData()[0],1);
		assertEquals(fp.getData()[1],0);
		
		assertEquals(ep.getData()[0],1);
		assertEquals(ep.getData()[1],0);
		assertEquals(queue.isEmpty(),true);
		byte[] states = s.getElevatorStates();
		System.out.println("Elevator states");
		for (byte b : states) {
			System.out.print(b + " ");
		}
		//Elevator States
		assertEquals(states[0],2);
		assertEquals(states[1],1);
		assertEquals(states[2],1);
		assertEquals(states[3],1);
		assertEquals(states[4],3);
		assertEquals(states[5],0);
		assertEquals(states[6],2);
		assertEquals(states[7],1);
		assertEquals(states[8],1);
		assertEquals(states[9],-1);
		assertEquals(states[10],0);
		assertEquals(states[11],3);
		assertEquals(states[12],1);
		assertEquals(states[13],1);
		assertEquals(states[14],-1);
		assertEquals(states[15],0);		
		assertEquals(states[16],4);
		assertEquals(states[17],1);
		assertEquals(states[18],1);
		assertEquals(states[19],-1);
		assertEquals(states[20],0);
		s.closeSockets();
			
	}
	
	@Test
	void schedulerCheckElevatorActiveTest() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FloorSystem fs = new FloorSystem(1,22,"FloorErrorFullTest");
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemDropOffTest");
				es.closeSocket();
			}
		}).start();
		
		Scheduler s = new Scheduler("DropOffErrorTest");
		DatagramPacket fp = s.getFloorPacket();
		DatagramPacket ep = s.getElevatorPacket();
		for(byte b:fp.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();
		for(byte b:ep.getData()) {
			System.out.print(b + " ");
		}
		System.out.println();

		ArrayList<byte[]> queue = s.getFloorEventQueue();

//		for (byte b : event) {
//			System.out.println(b + " ");
//		}
		
		assertEquals(SchedulerState.IDLE,s.getState());
		assertEquals(fp.getData()[0],1);
		assertEquals(fp.getData()[1],0);
		
		assertEquals(ep.getData()[0],1);
		assertEquals(ep.getData()[1],0);
		assertEquals(queue.isEmpty(),true);
		byte[] states = s.getElevatorStates();

		//Elevator States
		assertEquals(states[0],2);
		assertEquals(states[1],-1);
		assertEquals(states[2],1);
		assertEquals(states[3],1);
		assertEquals(states[4],-1);
		assertEquals(states[5],0);
		assertEquals(states[6],2);
		assertEquals(states[7],1);
		assertEquals(states[8],1);
		assertEquals(states[9],-1);
		assertEquals(states[10],0);
		assertEquals(states[11],3);
		assertEquals(states[12],1);
		assertEquals(states[13],1);
		assertEquals(states[14],-1);
		assertEquals(states[15],0);		
		assertEquals(states[16],4);
		assertEquals(states[17],1);
		assertEquals(states[18],1);
		assertEquals(states[19],-1);
		assertEquals(states[20],0);	
		s.closeSockets();
			
	}
	
	
	
}
