/**
 * 
 */
package elevator;

import java.util.ArrayList;

import state.Direction;
import state.DoorState;
import state.MotorState;

/**
 * @author Colin - 101112765
 *
 */
public class ElevatorData {
	
	private final int elevatorID; //the elevator id number
	private final int currFloor; //the elevator's current floor
	private ArrayList<Integer> reqFloor; //the requested floors

	private Direction currDirection;
	private DoorState door_state; //door flag (open/closed)
	private MotorState motor_state; // flag for no response required
	private String status; //status for console messages
	private boolean is_moving;
	
	public ElevatorData(int id,int curr_floor,DoorState ds,MotorState ms,boolean moving) {
		elevatorID = id;
		currFloor = curr_floor;
		door_state = ds;
		motor_state = ms;
		is_moving = moving;
		
	}
	
	public int getId() {return elevatorID;}
	public int getFloor() {return currFloor;}
	public DoorState getDoorState() {return door_state;}
	public MotorState getMotorState() {return motor_state;}
	public boolean getMoving() {return is_moving;}




}
