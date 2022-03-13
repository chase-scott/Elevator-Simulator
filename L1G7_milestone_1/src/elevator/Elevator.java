package elevator;

import java.util.ArrayList;
import java.util.List;

import state.DoorState;
import state.MotorState;
import system.Observer;

/**
 * Elevator class
 * 
 * @author Chase Scott - 101092194
 */
public class Elevator implements Observer {
	
	private int id;
	private List<ElevatorButton> buttons;
	private Motor motor;
	private Door door;
	private int currentFloor;
	private boolean isMoving;


	public Elevator(int minFloor, int maxFloor,int startFloor,int id) {
		this.motor = new Motor(MotorState.IDLE);
		this.door = new Door(DoorState.CLOSED);
		this.currentFloor = startFloor;
		this.isMoving = false;
		this.id = id;
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
	public void moveUpFloor() {currentFloor++;}
	
	public void moveDownFloor() {currentFloor--;}
	
	public int getId() {return id;}
	
	public int getFloorNum() {return currentFloor;}
	
	public boolean getIsMoving() {return isMoving;}
	
	public void setMoving(boolean move) {isMoving = move;}
	
	public Motor getMotor() {return motor;}
	
	public Door getDoor() {return door;}

	@Override
	public void update(byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	

}
