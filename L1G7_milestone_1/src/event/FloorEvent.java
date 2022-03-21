package event;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;

import state.Direction;

/**
 * FloorEvent class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String time;
	private int floorNum;
	private Direction direction;
	private int destinationFloorNum;
	
	/**
	 * Creates a random FloorEvent and writes it to the EventFile
	 */
	public FloorEvent() {
		int MAXFLOOR = 11;
		int MINFLOOR = 1;
		
		this.time = formatTime();
		this.floorNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		
		do {
			this.destinationFloorNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		} while(this.destinationFloorNum == this.floorNum);
		
		
		if(this.floorNum > this.destinationFloorNum) {
			this.direction = Direction.DOWN;
		} else {
			this.direction = Direction.UP;
		}
		
	}
	
	/**
	 * Constructor for a specific FloorEvent
	 * 
	 * @param time				String, time
	 * @param floorNumber		int, floor number
	 * @param direction			Direction, direction of button press
	 * @param destinationFloor	int, destination floor
	 */
	public FloorEvent(String time, int floorNumber, Direction direction, int destinationFloor) {
		this.time = time;
		this.floorNum = floorNumber;
		this.direction = direction;
		this.destinationFloorNum = destinationFloor;
	}
	
	/**
	 * Reads a string for EventFile and creates a FloorEvent object
	 * 
	 * @param eventString	String, the event string
	 * @throws ParseException if parsing of string fails
	 */
	public FloorEvent(String eventString) throws ParseException {
		String[] splitString = eventString.split(" ");
		if (splitString.length == 4) {
			time = splitString[0];
			direction = Direction.parseDirection(splitString[2]);

			try {
				floorNum = Integer.parseInt(splitString[1]);
				destinationFloorNum = Integer.parseInt(splitString[3]);
			} catch (NumberFormatException e) {
				throw new ParseException("EventFile parsing failed", 0);
			}

		} else {
			throw new ParseException("EventFile parsing failed", 0);
		}
		
	}

	public String getTime() { return this.time; }
	
	public int getFloorNumber() { return this.floorNum; }
	
	public Direction getDirection() { return this.direction; }
	
	public int getDestinationFloor() {return this.destinationFloorNum;}
	
	/**
	 * Builds the floor event packet
	 * 
	 * @param fe FloorEvent, the event to be marshaled
	 * @return byte[], the packet data
	 */
	public static byte[] marshal(FloorEvent fe) {
		
		// Getting byte arrays of FloorEvent attributes
		try {
			ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(new BufferedOutputStream(baoStream));
			ooStream.flush();
			ooStream.writeObject(fe);
			ooStream.flush();
			
			System.out.println(fe.toString());
			
			return baoStream.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * Unmarshals the data if it is a floor event, otherwise throws exception
	 * 
	 * @param data	byte[], the data to unmarshal 
	 * @return		FloorEvent, the unmarshalled floor event
	 */
	public static FloorEvent unmarshal(byte[] data) {
		
		// decode floor event
		FloorEvent floorEvent = null;
		try {
			// Retrieve the ElevatorData object from the receive packet
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is;
			is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();

			floorEvent = (FloorEvent) o;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Invalid packet recieved");
			e.printStackTrace();
		}
		
		return floorEvent;
		
	}
	
	/**
	 * Format the time
	 * 
	 * @return	String, the time
	 */
	private String formatTime() {
        String[] s = java.time.LocalTime.now().toString().split(":");
        String t = Integer.parseInt(s[0]) >= 12 ? "PM":"AM";
        return (Integer.parseInt(s[0]) >= 12 ? Integer.parseInt(s[0]) - 12 : s[0]) + ":" + s[1] + ":" + Math.round(Float.parseFloat(s[2])) + " " + t;
    }
	
	
	@Override
	public String toString() {
		String str = time + " " + floorNum + " " + direction + " " + destinationFloorNum;
		return str;
	}
	
	
	

}
