package elevator;

import state.MotorState;

/**
 * Motor class
 * 
 * @author Chase Scott - 101092194
 */
public class Motor {
	
	private MotorState state;
	
	public Motor(MotorState state) {
		this.state = state;
	}

	public MotorState getState() {
		return state;
	}

	public void setState(MotorState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "Motor is currently in the " + state.getState() + " state";
	}
	
}
