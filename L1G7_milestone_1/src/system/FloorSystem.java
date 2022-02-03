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

	private void monitorFile() {
		
		if(file.isModified()) {
			FloorEvent e = EventFile.readEvent(file.getFile());
			
			pipe.sendFloorEvent(e);
			

		}
		
		
		
		
	}
	
	
	

	@Override
	public void run() {

		while(true) {
			monitorFile();
			
		}
		
		
		//for(int i = 0; i < 3; i++) {
		
			//monitor for floor events
			//monitor();
			//pipe.sendFloorEvent(new FloorEvent());
			
			
			
		//}
	}

}
