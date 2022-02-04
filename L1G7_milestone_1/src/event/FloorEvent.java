package event;

import java.text.ParseException;

import state.Direction;

/**
 * FloorEvent class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorEvent {
	
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
		
		this.time = java.time.LocalTime.now().toString();
		this.floorNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		
		do {
			this.destinationFloorNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		} while(this.destinationFloorNum == this.floorNum);
		
		
		if(this.floorNum > this.destinationFloorNum) {
			this.direction = Direction.DOWN;
		} else {
			this.direction = Direction.UP;
		}
		
		EventFile.writeEvent(this);
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
	
	@Override
	public String toString() {
		String str = time + " " + floorNum + " " + direction + " " + destinationFloorNum;
		return str;
	}
	
	
	

}
