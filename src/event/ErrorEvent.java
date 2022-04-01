package event;

import java.text.ParseException;
import util.Timer;

public class ErrorEvent extends Event {
	
	private static final long serialVersionUID = 1L;
	private int elevatorID; //the elevator the error is for
	private int errorType; //the type of error
	
	public static final int FLOOR_ERROR = -69;
	public static final int DOOR_ERROR = -23;
	
	public ErrorEvent(int elevatorID, int errorType) {
		super(Timer.formatTime());
		this.elevatorID = elevatorID;
		this.errorType = errorType;
	}

	public ErrorEvent(String eventString) throws ParseException {
	
		super(Timer.formatTime());
		
		String[] splitString = eventString.split(" ");
		if (splitString.length == 2) {
			
			try {
				
				elevatorID = Integer.parseInt(splitString[0]);
				
				if(splitString[1].equals("FLOOR_ERROR")) {
					errorType = FLOOR_ERROR;
				} else if (splitString[1].equals("DOOR_ERROR")) {
					errorType = DOOR_ERROR;
				}
				
			} catch (NumberFormatException e) {
				throw new ParseException("EventFile parsing failed", 0);
			}

		} else {
			throw new ParseException("EventFile parsing failed", 0);
		}
		
	}
	
	public int getElevatorID() {
		return elevatorID;
	}

	public int getErrorType() {
		return errorType;
	}
	
	@Override
	public String toString() {
		
		return getTime() + " " + elevatorID + " " + errorType;
	}
	
	

}
