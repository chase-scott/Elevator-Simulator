package state;

/**
 * Direction enumeration
 * 
 * @author Chase Scott - 101092194
 */
public enum Direction {
	
	UP(2), DOWN(3), INVALID(-1);
	
	private int state;
	
	Direction(int state) {
		this.state = state;
	}
	
	public int getState() {
		return this.state;
	}
	
	/**
	 * Parses a string to see if it is a valid Direction
	 * 
	 * @param direction		String, the direction string
	 * @return				Direction, the direction it matches
	 */
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
