package state;

/**
 * @author Colin
 *
 */
public enum SchedulerState {

	IDLE("idle"), SENDING("sending"), RECEIVING("receiving");
	private String state;
	
	SchedulerState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
}
