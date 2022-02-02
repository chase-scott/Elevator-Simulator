package system;

import java.util.ArrayList;
import java.util.List;

import floor.Floor;
import floor.FloorEvent;

public class FloorSystem implements Runnable {
	
	private List<Floor> floors;
	private Pipe pipe;
	
	public FloorSystem(Pipe pipe) {
		
		this.pipe = pipe;
		
		this.floors = new ArrayList<>();
		for(int i = 1; i <= 11; i++) {
			floors.add(new Floor(i, i == 11, i == 1));
		}
		
	}
	
	

	@Override
	public void run() {
		for(int i = 0; i < 2; i++) {
			try {		
				Thread.sleep(6000);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			//send random floor event to system
			pipe.sendFloorEvent(new FloorEvent());
		
		}
	}

}
