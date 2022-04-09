package elevator;

/**
 * ElevatorButton class
 * 
 * @author Chase Scott - 101092194
 */
public class ElevatorButton {
	
	private int targetFloor;

	public ElevatorButton(int floorNumber) {
		this.targetFloor = floorNumber;
	}
	
	public int getTargetFloor() {return this.targetFloor;}

}
