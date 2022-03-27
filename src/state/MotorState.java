package state;

/**
 * MotorState enumeration
 * 
 * @author Chase Scott - 101092194
 */
public enum MotorState {
	
	IDLE(1), UP(2), DOWN(3), INVALID(-1);
	
	private int state;

	MotorState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
	
	public static MotorState setState(int state) {
		switch(state) {
		case 1:
			return IDLE;
		case 2:
			return UP;
		case 3:
			return DOWN;
		default:
			return INVALID;
		}
	}
	
}
