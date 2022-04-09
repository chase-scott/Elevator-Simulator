package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Timer class
 * 
 * @author Chase Scott - 101092194
 */
public class Timer {

	public static final long DEFAULT = 500; //default sleep time for while loops

	public static final long DOOR_TIME = 1000; //time it takes to open the door

	public static final long FLOOR_TIME = 1000; //time it takes to move between floor ;; set to 1000 temporarily ;; calculated time value --> 6402 

	public static final long LOAD_TIME = 7344; //time to takes for passengers to load and unload from the elevator

	/**
	 * Format the time
	 * 
	 * @return String, the time
	 */
	public static String formatTime() {

		return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
	}

}
