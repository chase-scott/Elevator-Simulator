package elevator;

import state.Lamp;

/**
 * ElevatorButton class
 * 
 * @author Chase Scott - 101092194
 */
public class ElevatorButton {
	
	private int targetFloor;
	private Lamp lamp;

	public ElevatorButton(int floorNumber) {
		this.targetFloor = floorNumber;
		this.lamp = new Lamp();
	}
	
	public int getTargetFloor() {return this.targetFloor;}
	
	public Lamp getLamp() {return this.lamp;}

}
