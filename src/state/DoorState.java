package state;

/**
 * DoorState enumeration
 * 
 * @author Chase Scott - 101092194
 */
public enum DoorState {
	
	OPEN("open"), CLOSED("closed");
	
	private String state;

	DoorState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
	public DoorState switchState() {
		switch(this) {
		case OPEN:
			return CLOSED;
		case CLOSED:
			return OPEN;
		default:
			return null;
		}
	}
	
}
