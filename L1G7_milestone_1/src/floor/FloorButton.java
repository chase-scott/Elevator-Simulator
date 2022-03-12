package floor;

import state.Direction;

/**
 * FloorButton class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorButton {
	
	private Direction direction;
	private boolean is_on;

	public FloorButton(Direction direction) {
		this.direction = direction;
		this.is_on = false;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void turnOn() {
		this.is_on = true;
	}
	public void turnOff() {
		this.is_on = false;
	}
	
	public boolean isOn() {
		return this.is_on;
	}

}
