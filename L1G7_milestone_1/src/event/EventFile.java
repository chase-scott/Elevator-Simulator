package event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;


public class EventFile {
	
	public static String EVENT_FILEPATH = "eventfolder/eventFile.txt";
	
	private File file;
	private long time;
	
	public EventFile() {
		this.file = new File(EVENT_FILEPATH);
		this.time = file.lastModified();
	}
	
	
	
	public static void writeEvent(FloorEvent event) {
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(EventFile.EVENT_FILEPATH);
			writer.write(event.toString());
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
	public static FloorEvent readEvent(File file) throws ParseException {
		
		FloorEvent event = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String eventStr;
			while ((eventStr = br.readLine()) != null) {
				event = new FloorEvent(eventStr);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return event;
		
	}
	
	public boolean wasModified() {
		
		long timeStamp = file.lastModified();
		
		if(timeStamp != this.time) {
			this.time = timeStamp;
			return true;
		}
		return false;
		
	}



	public File getFile() {
		return file;
	}


}
