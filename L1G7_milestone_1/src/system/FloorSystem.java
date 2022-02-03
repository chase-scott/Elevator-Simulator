package system;

import java.text.ParseException;
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
	private synchronized void monitor() {

		
		//if there is a new floor event in the file, send floor event to schedule
		FloorEvent e = null;
		if(file.wasModified())
			try {
				e = EventFile.readEvent(file.getFile());
			} catch (ParseException e1) {
				return;
			}
		try {
			pipe.sendFloorEvent(e);
		} catch (NullPointerException e1) {
			return;
		}
		
	}
	
	
	

	@Override
	public void run() {

		while(true) {
		
			//monitor for floor events
			monitor();
			
			
		}
	}

}
