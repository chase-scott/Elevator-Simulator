package event;

import java.text.ParseException;
import state.Direction;
import util.Constants;
import util.Timer;

/**
 * FloorEvent class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorEvent extends Event {
	
	private static final long serialVersionUID = 1L;
	private int floorNum;
	private Direction direction;
	private int destinationFloorNum;
	
	/**
	 * Creates a random FloorEvent
	 */
	public FloorEvent() {
		
		super(Timer.formatTime());
		
		int MAXFLOOR = Constants.MAX_FLOOR;
		int MINFLOOR = Constants.MIN_FLOOR;

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
		super(time);
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
		
		super(Timer.formatTime());
		
		String[] splitString = eventString.split(" ");
		if (splitString.length == 3) {
			
			direction = Direction.parseDirection(splitString[1]);

			try {
				floorNum = Integer.parseInt(splitString[0]);
				destinationFloorNum = Integer.parseInt(splitString[2]);
			} catch (NumberFormatException e) {
				throw new ParseException("EventFile parsing failed", 0);
			}

		} else {
			throw new ParseException("EventFile parsing failed", 0);
		}
		
	}
	
	public int getFloorNumber() { return this.floorNum; }
	
	public Direction getDirection() { return this.direction; }
	
	public int getDestinationFloor() {return this.destinationFloorNum;}
		
	@Override
	public String toString() {
		return getTime() + " " + floorNum + " " + direction + " " + destinationFloorNum;
	}
	

}
