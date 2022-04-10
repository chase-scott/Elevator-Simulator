/**
 * 
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.junit.jupiter.api.Test;

import event.EventFile;
import event.FloorEvent;
import floor.Floor;
import state.Direction;
import system.ElevatorSystem;
import system.FloorSystem;
import system.Scheduler;

/**
 * @author Colin
 *
 */
class FloorSystemTest {
	
	@Test
	void floorSystemSendTest() {
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemSendTest");
				es.closeSocket();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("PartialReplyTest");
				s.closeSockets();
			}

		}).start();
		FloorSystem fs = new FloorSystem(1, 22, "SendTest");

		assertEquals(fs.getReceivePacket().getData()[0],97);
		assertEquals(fs.getReceivePacket().getData()[1],99);
		assertEquals(fs.getReceivePacket().getData()[2],107);
		assertEquals(fs.getReceivePacket().getData()[3],0);
		assertEquals(fs.getReceivePacket().getData()[4],0);

	}
	
	@Test
	void floorSystemFullTest() {
		new Thread(new Runnable() {
			public void run() {
				Scheduler s = new Scheduler("FullTest");
				s.closeSockets();
			}

		}).start();
		new Thread(new Runnable() {
			public void run() {
				ElevatorSystem es = new ElevatorSystem("ElevatorSystemFullTest");
				es.closeSocket();
			}
		}).start();
		FloorSystem fs = new FloorSystem(1, 22, "FloorSystemFullTest");
		for(byte b :fs.getReceivePacket().getData() ) {
			System.out.print(b+ " ");
		}
		byte[] data = fs.getReceivePacket().getData();
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
	}

}
  