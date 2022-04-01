package system;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import elevator.Elevator;
import util.Constants;
import util.Timer;

public class ElevatorSystem extends JFrame{
	
	private JPanel pane;
    private JPanel pane1;
    private JPanel pane2;
	
    private JTextArea elevator1;
    private JTextArea elevator2;
    private JTextArea elevator3;
    private JTextArea elevator4;
    private JTextArea status;
	
	private HashMap<Integer, Observer> elevators; // elevatorID, elevator

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;

	private static byte[] DATA_REQUEST = { Constants.DATA_REQUEST };

	private final int SEND_PORT = 69;

	public ElevatorSystem() {
		
		// JFrame and contentPane
	    JFrame frame = new JFrame(" L1G7 Visual Interface");
	    Container contentPane = frame.getContentPane();
	    contentPane.setLayout(new BorderLayout()); // set the layout of the contentPane
	    //Defining the differents JPanels and JTextArea
	    pane =new JPanel();
	    pane1=new  JPanel();
	    pane2=new JPanel();
	    elevator1 = new JTextArea(5,5);
        elevator1.setEditable(false);
        JScrollPane scroll1 =
            new JScrollPane(elevator1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll1.setBorder(BorderFactory.createTitledBorder("Elevator 1"));
        elevator2 = new JTextArea(5,5);
        elevator2.setEditable(false);
        JScrollPane scroll2 =
            new JScrollPane(elevator2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll2.setBorder(BorderFactory.createTitledBorder("Elevator 2"));
        elevator3 = new JTextArea(5,5);
        elevator3.setEditable(false);
        JScrollPane scroll3 =
            new JScrollPane(elevator3, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll3.setBorder(BorderFactory.createTitledBorder("Elevator 3"));
        elevator4 = new JTextArea(5,5);
        elevator4.setEditable(false);
        JScrollPane scroll4 =
            new JScrollPane(elevator4, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll4.setBorder(BorderFactory.createTitledBorder("Elevator 4"));
        status = new JTextArea(12,12);
        status.setEditable(false);
        JScrollPane scroll5 =
            new JScrollPane(status, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll5.setBorder(BorderFactory.createTitledBorder("Faults"));
	    
	    //Setting the different Borders for each JPanel.
	    pane.setBorder(BorderFactory.createTitledBorder("Elevators"));
	    pane1.setBorder(BorderFactory.createLineBorder(Color.black,1));
	    pane2.setBorder(BorderFactory.createTitledBorder("Status"));
	    
	    //Setting the Layout for the JPanels
	    pane.setLayout(new GridLayout(2,2));
	    pane1.setLayout(new GridLayout(1,0));
	    
	    //Adding the convenient panel to the contentPane and a making the Buttons panels up North.
	    contentPane.add(pane,BorderLayout.NORTH);
	    contentPane.add(pane2,BorderLayout.CENTER);
	    contentPane.add(pane1,BorderLayout.SOUTH);
	    pane.add(scroll1);
	    pane.add(scroll2);
	    pane.add(scroll3);
	    pane.add(scroll4);
	    pane1.add(scroll5);

		// create 4 elevators and add them to the system
		this.elevators = new HashMap<>();
		for (int i = 1; i < Constants.NUM_ELEVATORS + 1; i++) {
			Elevator e = new Elevator(Constants.MIN_FLOOR, Constants.MAX_FLOOR);
			elevators.put(i, e);
			new Thread(e, "Elevator " + i).start();
			;
		}
		// finish setting up the frame
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setResizable(true);
     	frame.setVisible(true);

		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}

		// request new data

		// continuously ask for move info
		while (true) {

			send(this.buildStateData());

			try {
				Thread.sleep(Timer.DEFAULT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			send(DATA_REQUEST);

		}
		
		// close socket
		// sendReceiveSocket.close();

	}

	public ElevatorSystem(String test) {
		System.out.println(test);
		// create 4 elevators and add them to the system
		this.elevators = new HashMap<>();
		for (int i = 1; i < 5; i++) {
			Elevator e = new Elevator(1, 22);
			elevators.put(i, e);
			new Thread(e, "Elevator " + i).start();
		}

		try {
			sendReceiveSocket = new DatagramSocket();

		} catch (SocketException se) {
			se.printStackTrace();
		}

		if (test.equals("ElevatorSystemSendTest")) {
			send(this.buildStateData());
		}

	}

	/**
	 * Make a request for data from the scheduler
	 */
	private byte[] send(byte[] data) {

		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// MESSAGE INFORMATION
		System.out.println(Timer.formatTime() + " [ElevatorSystem] Sending packet" + printPacket(sendPacket));
		// MESSAGE INFORMATION

		// try to send message over the socket at port 69
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// receiving data
		byte[] ack = new byte[32];
		receivePacket = new DatagramPacket(ack, ack.length);

		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();

		}

		System.out.println(Timer.formatTime() + " [ElevatorSystem] Packet received" + printPacket(receivePacket));

		// if move data was received, update observers
		if (ack[0] == Constants.MOVE_DATA || ack[0] == Constants.ERROR_DATA) {
			this.updateObservers(ack);
		}

		return ack;

	}

	/**
	 * Update all elevator observers
	 * 
	 * @param data byte[], the data to update
	 */
	private void updateObservers(byte[] data) {
		for (int i : elevators.keySet()) {
			// if this packet is meant for this elevatorID
			if (i == data[1]) {
				elevators.get(i).update(data);
			}
		}
	}

	/**
	 * Builds the packet that contains the state information of each elevator
	 * 
	 * @return byte[], the state information in the form {1, elevatorID, curFloor,
	 *         motorState, 0, ...}
	 */
	private byte[] buildStateData() {

		ArrayList<Byte> data = new ArrayList<>();
		data.add((byte) 2); // denote it is a send state message

		// packet info (elevatorID, curFloor, desFloor, 0 , elevatorID, curFloor,
		// desFloor)

		for (int i : elevators.keySet()) {

			data.add(((Elevator) elevators.get(i)).getActive() ? (byte) i : (byte) -1);

			data.add((byte) ((Elevator) elevators.get(i)).getCurFloor()); // add current floor

			data.add((byte) ((Elevator) elevators.get(i)).getMotor().getState());

			data.add((byte) 0);
			
			if(i==1) {
				Elevator ev1= (Elevator) elevators.get(i);
				this.elevator1.append(String.valueOf(ev1.getCurFloor())+'\n');
			}else if(i==2) {
				Elevator ev2= (Elevator) elevators.get(i);
				this.elevator2.append(String.valueOf(ev2.getCurFloor())+'\n');
			}else if(i==3) {
				Elevator ev3= (Elevator) elevators.get(i);
				this.elevator3.append(String.valueOf(ev3.getCurFloor())+'\n');
			}else if(i==4) {
				Elevator ev4= (Elevator) elevators.get(i);
				this.elevator4.append(String.valueOf(ev4.getCurFloor())+'\n');
			}

		}

		byte[] req = new byte[data.size()];

		for (int i = 0; i < data.size(); i++) {
			req[i] = data.get(i);
		}

		return req;

	}

	/**
	 * Returns a string containing all of the packet information
	 * 
	 * @param packet DatagramPacket, the packet
	 * @return String, the info
	 */
	private String printPacket(DatagramPacket packet) {

		StringBuilder sb = new StringBuilder();

		byte[] data = Arrays.copyOf(packet.getData(), packet.getLength()); // truncate packet length

		if (Constants.DEBUG) {
			sb.append(":\n");
			// DEBUG INFORMATION
			sb.append("\tTo host: " + packet.getAddress() + "\n");
			sb.append("\tDestination host port: " + packet.getPort() + "\n");
			int len = packet.getLength();
			sb.append("\tLength: " + len + "\n");
			sb.append("\tContaining: " + new String(data, 0, len) + "\n");
			sb.append("\tContaining Bytes: " + Arrays.toString(data) + "\n");

		} else {
			sb.append(".\n");
			// USER FRIENDLY INFORMATION

			// IF ack data is received, let the user know
			if (Arrays.equals(data, Constants.ACK_DATA)) {
				sb.append("\tAcknowledgedment.\n");
			}

			// IF move data is received, let the user know
			if (data[0] == Constants.MOVE_DATA) {
				sb.append("\tElevator " + data[1] + " has received move information.\n");
				sb.append("\tPickup at floor: " + data[2] + "\n");
				sb.append("\tWants to go to floor: " + data[3] + "\n");
			}
			if (data[0] == Constants.ERROR_DATA) {
				sb.append("\tElevator " + data[1] + " has received error information.\n");
				sb.append("\tError type: " + data[2] + "\n");

			}

		}

		return sb.toString();
	}

	public DatagramPacket getReceivePacket() {
		return receivePacket;
	}

	public void closeSocket() {
		sendReceiveSocket.close();
	}

	public HashMap<Integer, Observer> getElevators() {
		return elevators;
	}

	public static void main(String[] args) {

		new ElevatorSystem();

	}

}
