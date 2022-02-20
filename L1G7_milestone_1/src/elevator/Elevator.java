package elevator;

import java.util.ArrayList;
import java.util.List;

import state.DoorState;
import state.MotorState;

/**
 * Elevator class
 * 
 * @author Chase Scott - 101092194
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
	
	/**
	 * When a button is pressed, lit up the lamp of that button.
	 * 
	 * @param targetFloor	int, the target floor
	 */
	public void pressButton(int targetFloor) {
		for(ElevatorButton b : buttons) {
			if(b.getTargetFloor() == targetFloor) {
				if(!b.getLamp().isLit()) {
					b.getLamp().switchState();
				}
			}
		}
		
	}
	
	public List<ElevatorButton> getPressedButtons(){
		List<ElevatorButton> pressed = new ArrayList<ElevatorButton>();
		for(ElevatorButton b: buttons) {
			if(b.getLamp().isLit()) {
				pressed.add(b);
			}
		}
		return pressed;
	}
	
	public Motor getMotor() {return motor;}
	
	public Door getDoor() {return door;}
	
	

}
