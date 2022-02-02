package floor;

import state.Direction;

public class FloorEvent {
	
	//TODO make a time class which formats this nicely
	private String time;
	private int floorNum;
	private Direction direction;
	private int floorDestinationNum;
	
	public FloorEvent() {
		int MAXFLOOR = 11;
		int MINFLOOR = 1;
		
		this.time = java.time.LocalTime.now().toString();
		this.floorNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		
		do {
			this.floorDestinationNum = ((int) (Math.random()*(MAXFLOOR - MINFLOOR))) + MINFLOOR;
		} while(this.floorDestinationNum == this.floorNum);
		
		
		if(this.floorNum > this.floorDestinationNum) {
			this.direction = Direction.DOWN;
		} else {
			this.direction = Direction.UP;
		}
	}
	
	public FloorEvent(String time, int floorNum, Direction direction, int floorDestinationNum) {
		this.time = time;
		this.floorNum = floorNum;
		this.direction = direction;
		this.floorDestinationNum = floorDestinationNum;
		
	}
	
	
	@Override
	public String toString() {
		return "floorevent={" + this.time + ", " + String.valueOf(floorNum) + ", " + direction.toString() + ", " + String.valueOf(floorDestinationNum) + "}";
	}

}
