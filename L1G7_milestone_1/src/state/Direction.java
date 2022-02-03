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

	public static Direction parseDirection(String direction) {
		switch(direction.toLowerCase()) {
			case "up":
				return UP;
			case "down":
				return DOWN;
			default:
				return null;
		}
	}

}

