package floor;

import state.Direction;

/**
 * FloorButton class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorButton {
	
	private Direction direction;
	private boolean isLit;

	public FloorButton(Direction direction) {
		this.direction = direction;
		this.isLit = false;
	}
	
	public void toggleLamp() {
		this.isLit = !isLit;
	}
	
	public boolean getLamp() {
		return this.isLit;
	}
	
	public Direction getDirection() {
		return direction;
	}
	


}
