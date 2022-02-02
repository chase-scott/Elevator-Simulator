package floor;

import state.Direction;

/**
 * @author Chase
 *
 */
public class FloorButton {
	
	private Direction direction;

	public FloorButton(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	@Override
	public String toString() {
		return this.direction.getState();
	}

}
