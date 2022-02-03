package system;

import java.util.ArrayList;
import java.util.List;

import event.EventFile;
import event.FloorEvent;
import floor.Floor;

public class FloorSystem implements Runnable {
	
	private List<Floor> floors;
	private Pipe pipe;
	private EventFile file;
	
	public FloorSystem(Pipe pipe, EventFile file) {
		
		this.file = file;
		this.pipe = pipe;
		
		this.floors = new ArrayList<>();
		for(int i = 1; i <= 11; i++) {
			floors.add(new Floor(i, i == 11, i == 1));
		}
		
	}
	
	/**
	 * Monitors event file for new floor events
	 */
	private void monitor() {
		
		//if there is a new floor event in the file, send floor event to scheduler
		if(file.wasModified()) {
			FloorEvent e = EventFile.readEvent(file.getFile());
			pipe.sendFloorEvent(e);
		}
		//pipe.sendFloorEvent(new FloorEvent());
		
	}
	
	
	

	@Override
	public void run() {
		//for(int i = 0; i < 3; i++) {
		while(true) {
			

			
			try {		
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			//monitor for floor events
			monitor();
			
		}
	}

}
