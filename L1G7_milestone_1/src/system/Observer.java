package system;

import elevator.Elevator;
import floor.Floor;

public interface Observer {

	public void update(byte[] data);
	public Elevator getElevator();
	public Floor getFloor();

}
