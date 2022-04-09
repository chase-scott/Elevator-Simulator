package util;

/**
 * All of the constants to be used in the project.
 * 
 * @author Chase
 *
 */
public class Constants {
	
	public static final boolean DEBUG = true; //debug mode on or off
	
	public static final int MIN_FLOOR = 1; //bottom floor of the building
	
	public static final int MAX_FLOOR = 22; //top floor of the building
	
	public static final int NUM_ELEVATORS = 4; //number of elevators
	
	public static final int DATA_REQUEST = 1; //request from a subsystem for data, will be the first bit of the packet
	
	public static final int ELEVATOR_INFO = 2; //elevator state info, will be first bit of packet
	
	public static final int MOVE_DATA = 15; //elevator move data, will be first bit of packet
	
	public static final byte[] ACK_DATA = {97, 99, 107}; //packet for an acknowledgment 
	
	public static final int FLOOR_ERROR = -69; //error code for a floor_error
	
	public static final int DOOR_ERROR = -23; //error code for a door_error
	
	public static final int NO_ERROR = -1; //error code for no_error
	
	
	
	
}
