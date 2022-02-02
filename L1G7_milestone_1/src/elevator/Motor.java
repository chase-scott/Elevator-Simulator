package elevator;

import state.MotorState;

/**
 * @author Chase
 *
 */
public class Motor {
	
	private MotorState state;
	
	public Motor(MotorState state) {
		this.state = state;
	}

	public MotorState getState() {
		return this.state;
	}

	public void setState(MotorState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "Motor is currently in the " + state.getState() + " state";
	}
	
}

