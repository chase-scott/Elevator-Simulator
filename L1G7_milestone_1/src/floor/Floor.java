package floor;

import java.util.ArrayList;

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
		// TODO Auto-generated method stub
		byte[] floors = decodeByteData(data);
		for (int i = 0 ; i < floors.length;i++) {
			int floornum = floors[i];
			if (floornum == this.floorNumber) {
				for(FloorButton b:this.buttons) {
					if(b.isOn())b.turnOff();
				}
				
			}
		}
		
		
	}
	
	public byte[] decodeByteData(byte[] data) {
		int zero_count = 0;
		int num_elevators = (data.length -1)/4;
		byte[] id_bytes = new byte[num_elevators];
		byte[] floor_bytes = new byte[num_elevators]; 
		byte[] motor_bytes = new byte[num_elevators]; 
		int j=0;
		for(int i = 1; i < data.length;i++) {
			if(data[i]==0) {
				zero_count++;
				j=0;
			}else {
				if(j==0) {
					id_bytes[zero_count] = data[i];
					j++;
				}else if(j==1) {
					floor_bytes[zero_count] = data[i];
					j++;
				}else{
					motor_bytes[zero_count] = data[i];
				}
			}
		}
		return floor_bytes;
		
	}
	
	
	
	
	
}
