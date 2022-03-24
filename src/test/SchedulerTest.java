package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.junit.jupiter.api.Test;

import event.FloorEvent;
import event.EventFile;
import state.Direction;
import state.SchedulerState;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Scheduler;

class SchedulerTest {	
	@Test
	void schedulerReplyTest() {
		new Thread(new Runnable() {
			public void run() {
				DatagramSocket sendReceiveSocket;
				try {
					sendReceiveSocket = new DatagramSocket();
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					DatagramPacket sendPacket = new DatagramPacket(FloorEvent.marshal(new FloorEvent("15:20:43.997771100", 4, Direction.UP, 7)), FloorEvent.marshal(new FloorEvent()).length, InetAddress.getLocalHost(), 23);
					sendReceiveSocket.send(sendPacket);

					
				} catch (SocketException se) {
					se.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		Scheduler s = new Scheduler("THIS IS A SCHEDULER TEST");
		assertEquals(SchedulerState.IDLE,s.getState());
		byte[] b = {1,1,4,7};
		System.out.println(s.getFloorEventQueue().size());
		assertEquals(s.getFloorEventQueue().get(0)[0],1);
		assertEquals(s.getFloorEventQueue().get(0)[1],1);
		assertEquals(s.getFloorEventQueue().get(0)[2],4);
		assertEquals(s.getFloorEventQueue().get(0)[3],7);


		
	}
}
