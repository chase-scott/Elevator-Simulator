package floor;

import java.util.ArrayList;

import event.FloorEvent;
import state.Direction;

/**
 * @author Chase
 *
 */

public class Floor {

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
		
		new FloorEvent();
	
	}
	
	public int getFloorNumber() {return this.floorNumber;}
	
	@Override
	public String toString() {
		String str = String.valueOf(floorNumber);
		for(FloorButton b : buttons) {
			str += b.toString();
		}
		return str;
		
	}
	
	
	
	
	
}
