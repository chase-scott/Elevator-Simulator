package floor;

import java.util.ArrayList;

import elevator.Elevator;
import state.Direction;
import system.Observer;

/**
 * Floor class
 * 
 * @author Chase Scott - 101092194
 */
public class Floor implements Observer {

	private ArrayList<FloorButton> buttons;
	private int floorNumber;
	
	public Floor(int floorNumber, boolean topFloor, boolean bottomFloor) {
		this.floorNumber = floorNumber;
		this.buttons = new ArrayList<>();
		
		if(topFloor) {
			buttons.add(new FloorButton(Direction.DOWN));
		} else if(bottomFloor) {
			buttons.add(new FloorButton(Direction.UP));
		} else {
			buttons.add(new FloorButton(Direction.UP));
			buttons.add(new FloorButton(Direction.DOWN));
		}
	
	}
	
	public int getFloorNumber() {return this.floorNumber;}

	@Override
	public void update(byte[] data) {
		//TODO turn on and off lamps
	
	}

	@Override
	public Elevator getElevator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Floor getFloor() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
}
