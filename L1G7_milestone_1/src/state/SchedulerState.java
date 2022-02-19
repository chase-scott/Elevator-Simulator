/**
 * 
 */
package state;

/**
 * @author Colin
 *
 */
public enum SchedulerState {

	IDLE("idle"), FLOOR_TO_ELEVATOR("floor to elevator"), ELEVATOR_TO_FLOOR("elevator to floor");
	private String state_text;
	
	SchedulerState(String state) {
		this.state_text = state;
	}
	
	public SchedulerState getState() {
		return this;
	}
	
	public String getStateString() {
		return this.state_text;
	}
	
	public SchedulerState switchStateTo(SchedulerState s) {
		switch(s) {
		case IDLE:
			this.state_text = IDLE.getStateString();
			return IDLE;
		case FLOOR_TO_ELEVATOR:
			this.state_text = FLOOR_TO_ELEVATOR.getStateString();
			return FLOOR_TO_ELEVATOR;
		case ELEVATOR_TO_FLOOR:
			this.state_text = ELEVATOR_TO_FLOOR.getStateString();
			return ELEVATOR_TO_FLOOR;
		default:
			return null;
		}
	}
}
