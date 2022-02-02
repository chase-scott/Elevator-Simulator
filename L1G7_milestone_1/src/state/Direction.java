package state;

public enum Direction {
	
	UP("up"), DOWN("down");
	
	private String state;
	
	Direction(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}

}

