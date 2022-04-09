package floor;

import java.util.ArrayList;
import GUI.IView;
import state.Direction;
import state.MotorState;
import system.Observer;
import util.Constants;

/**
 * Floor class
 * 
 * @author Chase Scott - 101092194
 */
public class Floor implements Observer {

	private ArrayList<IView> views; // the views for this floor
	private ArrayList<FloorButton> buttons;
	private int floorNumber;

	public Floor(int floorNumber, boolean bottomFloor, boolean topFloor) {

		this.floorNumber = floorNumber;
		this.views = new ArrayList<>();
		this.buttons = new ArrayList<>();

		if (topFloor) {
			buttons.add(new FloorButton(Direction.DOWN));
		} else if (bottomFloor) {
			buttons.add(new FloorButton(Direction.UP));
		} else {
			buttons.add(new FloorButton(Direction.UP));
			buttons.add(new FloorButton(Direction.DOWN));
		}

	}

	public void addView(IView view) {
		views.add(view);
	}

	public void updateViews(String message) {
		views.forEach(e -> e.updateView(message));
	}

	public int getFloorNumber() {
		return this.floorNumber;
	}

	public ArrayList<FloorButton> getButtons() {
		return this.buttons;
	}
	
	public void toggleLamps(Direction direction) {
		for(FloorButton b : buttons) {
			if(b.getDirection() == direction)
				b.toggleLamp();
		}
	}

	@Override
	public void notify(byte[] data) {
		
		// create view string
		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < (Constants.NUM_ELEVATORS * 5 + 1); i += 5) {
			// if elevator data[i] is on this floor, add to the string
			if (data[i + 1] == this.floorNumber) {
				sb.append(" [ | ] ");
				
				//if elevator is on this floor and idle and handling up requests --> meaning an elevator going up has stopped on this floor, turn off the lamp
				if(data[i + 2] == MotorState.IDLE.getState() && data[i + 3] == Direction.UP.getState()) {
					for(FloorButton b : buttons) {
						if(b.getLamp())
							toggleLamps(Direction.UP);
					}
				}
				
				//if elevator is on this floor and idle and handling down requests --> meaning an elevator going down has stopped on this floor, turn off the lamp
				if(data[i + 2] == MotorState.IDLE.getState() && data[i + 3] == Direction.DOWN.getState()) {
					for(FloorButton b : buttons) {
						if(b.getLamp())
							toggleLamps(Direction.DOWN);
					}
				}
				
			} else {
				sb.append(" ----- ");
			}
			
		}
				
		//deactivate lamps for dead elevator
		for(int i = Constants.NUM_ELEVATORS * 5 + 1; i < data.length; i++) {
			if(this.floorNumber == data[i]) {
				for(FloorButton b : buttons) {
					if(b.getLamp())
						toggleLamps(Direction.DOWN);
						toggleLamps(Direction.UP);
				}
			}
		}

		updateViews(sb.toString());

	}

}
