package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer {
	
	public static final long DEFAULT = 2000;
	
	public static final long DOOR_TIME = 1000;
	
	public static final long FLOOR_TIME = 6402;
	
	public static final long LOAD_TIME = 7344;
	
	/**
	 * Format the time
	 * 
	 * @return	String, the time
	 */
	public static String formatTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }
	
}
