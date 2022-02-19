package event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

/**
 * EventFile class
 * 
 * @author Chase Scott - 101092194
 */
public class EventFile {
	
	//file path for event file
	public static String EVENT_FILEPATH = "L1G7_milestone_1/eventfolder/eventFile.txt";
	
	private long timeStamp;
	private File file;

	/**
	 * Constructor for EventFile class
	 */
	public EventFile() {

		this.file = new File(EVENT_FILEPATH);
		this.timeStamp = file.lastModified();
	}

	public boolean isFileUpdated() {
		if (timeStamp != file.lastModified()) {
			timeStamp = file.lastModified();
			return true;
		} else {
			return false;
		}
	}

	public File getFile() {
		return file;
	}

	/**
	 * Write an event to the EventFile
	 * 
	 * @param event	FloorEvent, the event to write
	 */
	public static void writeEvent(FloorEvent event) {
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(EventFile.EVENT_FILEPATH);
			writer.write(event.toString());
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
	/**
	 * Parses the EventFile for an event
	 * 
	 * @param file	File, the file to parse
	 * @return	FloorEvent[], the parsed events
	 */
	public static FloorEvent[] readTextFile(File file) {
		 StringBuilder contentBuilder = new StringBuilder();
	        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILEPATH))) 
	        {
	 
	            String sCurrentLine;
	            while ((sCurrentLine = br.readLine()) != null) 
	            {
	                contentBuilder.append(sCurrentLine + "\n");
	            }
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        
	        
        	String[] eventStrings = contentBuilder.toString().split("\n");
        	FloorEvent[] events = new FloorEvent[eventStrings.length];
	        
        	try {
	   
				for(int i = 0; i < events.length; i++) {
					events[i] = new FloorEvent(eventStrings[i]);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
	        return events;
	}


}
