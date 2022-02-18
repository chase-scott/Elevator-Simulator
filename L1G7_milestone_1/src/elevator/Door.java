package elevator;

import state.DoorState;

/**
 * @author Chase
 *
 */
public class Door {
	
	private DoorState state;

	public Door(DoorState state) {
		this.state = state;
	}
	
	public DoorState getState() {
		return this.state;
	}
	
	public void switchState() {
		state = state.switchState();
	}
	
	@Override
	public String toString() {
		return "Door is currently in the " + state.getState() + " state";
	}
	
}
