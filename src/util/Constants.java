package util;

/**
 * All of the constants to be used in the project.
 * 
 * @author Chase
 *
 */
public class Constants {
	
	public static final boolean DEBUG = false; //debug mode on or off
	
	public static final int MIN_FLOOR = 1; //bottom floor of the building
	
	public static final int MAX_FLOOR = 20; //top floor of the building
	
	public static final int NUM_ELEVATORS = 4; //number of elevators
	
	public static final int DATA_REQUEST = 1; //request from a subsystem for data, will be the first bit of the packet
	
	public static final int ELEVATOR_INFO = 2; //elevator state info, will be first bit of packet
	
	public static final int MOVE_DATA = 15; //elevator move data, will be first bit of packet
	
	public static final int ERROR_DATA = 16; //elevator error data, will be first bit of packet
	
	public static final byte[] ACK_DATA = {97, 99, 107}; //packet for an acknowledgment 
	
	public static final int DOOR_ERROR = 21;
	
	public static final int FLOOR_ERROR = 22;
	
}
