package elevator;

import java.util.ArrayList;
import java.util.List;

import state.DoorState;
import state.MotorState;

/**
 * @author Chase
 *
 */
public class Elevator {
	
	private List<ElevatorButton> buttons;
	private Motor motor;
	private Door door;

	public Elevator(int minFloor, int maxFloor) {
		this.motor = new Motor(MotorState.IDLE);
		this.door = new Door(DoorState.CLOSED);
		
		this.buttons = new ArrayList<ElevatorButton>();
		for(int i = minFloor; i < maxFloor; i++) {
			buttons.add(new ElevatorButton(i));
		}
	}
	
	public void pressButton(int targetFloor) {
		for(ElevatorButton b : buttons) {
			if(b.getTargetFloor() == targetFloor) {
				//TODO turn on lamp
			}
		}
		
	}
	
	public Motor getMotor() {return motor;}
	
	public Door getDoor() {return door;}
	
	

}

