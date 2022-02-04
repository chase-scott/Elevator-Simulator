package floor;

import state.Direction;

/**
 * FloorButton class
 * 
 * @author Chase Scott - 101092194
 */
public class FloorButton {
	
	private Direction direction;

	public FloorButton(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}

}
