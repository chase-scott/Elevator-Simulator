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
import state.SchedulerState;
import system.FloorSystem;
import system.Observer;
import system.Scheduler;

class SchedulerTest {	
	
	@Test
	void schedulerElevatorReplyTest() {
		new Thread(new Runnable() {
			public void run() {

				HashMap<Integer, Observer> elevators = new HashMap<>();
				for (int i = 1; i < 3; i++) {
					Elevator e = new Elevator(1, 22);
					elevators.put(i, e);
					new Thread(e, "Elevator " + i).start();;
				}
				DatagramSocket sendReceiveSocket = null;
				try {
					sendReceiveSocket = new DatagramSocket();

				} catch (SocketException se) {
					se.printStackTrace();
				}
				
				ArrayList<Byte> data = new ArrayList<>();
				data.add((byte) 1); // denote it is a send state message

				// packet info (elevatorID, curFloor, desFloor, 0 , elevatorID, curFloor,
				// desFloor)

				for (int i : elevators.keySet()) {

					data.add((byte) i); // add elevatorID

					data.add((byte) ((Elevator) elevators.get(i)).getCurFloor()); // add current floor

					data.add((byte) ((Elevator) elevators.get(i)).getMotor().getState());

					data.add((byte) 0);

				}

				byte[] req = new byte[data.size()];
				
				for (int i = 0; i < data.size(); i++) {
					req[i] = data.get(i);
				}
				
				DatagramPacket sendPacket = null;
				try {
					sendPacket = new DatagramPacket(req, req.length, InetAddress.getLocalHost(), 69);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

				// MESSAGE INFORMATION		
				System.out.println("["+DateTimeFormatter.ofPattern("HH:mm:ss:A").format(LocalDateTime.now())+"] "
						+ "ElevatorSystem: Sending packet:");
				System.out.println("To host: " + sendPacket.getAddress());
				System.out.println("Destination host port: " + sendPacket.getPort());
				int len = sendPacket.getLength();
				System.out.println("Length: " + len);
				System.out.println("Containing: " + new String(sendPacket.getData(), 0, len));
				System.out.println("Containing Bytes: " + Arrays.toString(sendPacket.getData()));
				// MESSAGE INFORMATION

				// try to send message over the socket at port 69
				try {
					sendReceiveSocket.send(sendPacket);
					sendReceiveSocket.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		Scheduler s = new Scheduler("ElevatorReplyTest");
		assertEquals(SchedulerState.IDLE,s.getState());
		byte[] data = s.getElevatorPacket().getData();
		assertEquals(data[0],1);
		assertEquals(data[1],1);
		assertEquals(data[2],1);
		assertEquals(data[3],1);
		assertEquals(data[4],0);
		assertEquals(data[5],2);
		assertEquals(data[6],1);
		assertEquals(data[7],1);
		assertEquals(data[8],0);
		s.closeSockets();
		
	}
	
	@Test
	void schedulerFloorReplyTest() {

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				@SuppressWarnings("unused")
				FloorSystem fs = new FloorSystem(1,22,"SendTest");
			}
		}).start();
		Scheduler s = new Scheduler("FloorReplyTest");

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
	
}
