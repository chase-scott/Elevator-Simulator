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
import util.Constants;

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
	private int errorType; // the type of error

	/**
	 * Creates a random FloorEvent
	 */
	public FloorEvent() {

		this.time = "00:00:000";

		int MAXFLOOR = Constants.MAX_FLOOR;
		int MINFLOOR = Constants.MIN_FLOOR;

		this.errorType = Constants.NO_ERROR;

		this.floorNum = ((int) (Math.random() * (MAXFLOOR - MINFLOOR))) + MINFLOOR;

		do {
			this.destinationFloorNum = ((int) (Math.random() * (MAXFLOOR - MINFLOOR))) + MINFLOOR;
		} while (this.destinationFloorNum == this.floorNum);

		if (this.floorNum > this.destinationFloorNum) {
			this.direction = Direction.DOWN;
		} else {
			this.direction = Direction.UP;
		}

	}

	/**
	 * Constructor for a specific FloorEvent
	 * 
	 * @param time             String, time
	 * @param floorNumber      int, floor number
	 * @param direction        Direction, direction of button press
	 * @param destinationFloor int, destination floor
	 */
	public FloorEvent(String time, int floorNumber, Direction direction, int destinationFloor, int errorType) {
		this.time = time;
		this.floorNum = floorNumber;
		this.direction = direction;
		this.destinationFloorNum = destinationFloor;
		this.errorType = errorType;
	}

	/**
	 * Reads a string for EventFile and creates a FloorEvent object
	 * 
	 * @param eventString String, the event string
	 * @throws ParseException if parsing of string fails
	 */
	public FloorEvent(String eventString) throws ParseException {

		String[] splitString = eventString.split(" ");
		if (splitString.length == 4 || splitString.length == 5) {
			
			this.time = splitString[0];

			direction = Direction.parseDirection(splitString[2]);

			try {
				floorNum = Integer.parseInt(splitString[1]);
				destinationFloorNum = Integer.parseInt(splitString[3]);

				try {
					if (splitString[4].equals("FLOOR_ERROR")) {
						errorType = Constants.FLOOR_ERROR;
					} else if (splitString[4].equals("DOOR_ERROR")) {
						errorType = Constants.DOOR_ERROR;
					}
				} catch (IndexOutOfBoundsException e1) {
					this.errorType = Constants.NO_ERROR;
				}

			} catch (NumberFormatException e) {
				throw new ParseException("EventFile parsing failed", 0);
			}

		} else {
			throw new ParseException("EventFile parsing failed", 0);
		}

	}

	/**
	 * Marshal this event to a byte array.
	 * 
	 * @param e
	 * @return
	 */
	public static byte[] marshal(FloorEvent e) {
		// Getting byte arrays of FloorEvent attributes
		try {
			ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(new BufferedOutputStream(baoStream));
			ooStream.flush();
			ooStream.writeObject(e);
			ooStream.flush();

			return baoStream.toByteArray();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;

	}

	/**
	 * Unmarshals the data if it is a event, otherwise throws exception
	 * 
	 * @param data byte[], the data to unmarshal
	 * @return FloorEvent, the unmarshalled floor event
	 */
	public static FloorEvent unmarshal(byte[] data) {

		// decode floor event
		FloorEvent event = null;
		try {
			// Retrieve the ElevatorData object from the receive packet
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is;
			is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();

			event = (FloorEvent) o;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Invalid packet recieved");
			e.printStackTrace();
		}

		return event;
	}

	/**
	 * Return the time delay for this event in Ms
	 * 
	 * @return
	 */
	public long getTime() {
		String[] timeSplit = time.split(":");
		
		return Integer.valueOf(timeSplit[0])*60000 + Integer.valueOf(timeSplit[1])*1000 + Integer.valueOf(timeSplit[2]);
	}
	
	public int getFloorNumber() {
		return this.floorNum;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public int getDestinationFloor() {
		return this.destinationFloorNum;
	}

	public int getErrorType() {
		return errorType;
	}

	@Override
	public String toString() {
		return floorNum + " " + direction + " " + destinationFloorNum + " " + errorType;
	}

}
