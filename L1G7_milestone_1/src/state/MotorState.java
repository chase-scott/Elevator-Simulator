package state;

/**
 * MotorState enumeration
 * 
 * @author Chase Scott - 101092194
 */
public enum MotorState {
	
	IDLE("id"), UP("up"), DOWN("dn");
	
	private String state;

	MotorState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
}
