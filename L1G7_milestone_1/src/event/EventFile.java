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
	public static String EVENT_FILEPATH = "eventfolder/eventFile.txt";
	
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
	 * @return	FloorEvent, the parsed event
	 */
	public static FloorEvent readTextFile(File file) {
		 StringBuilder contentBuilder = new StringBuilder();
	        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILEPATH))) 
	        {
	 
	            String sCurrentLine;
	            while ((sCurrentLine = br.readLine()) != null) 
	            {
	                contentBuilder.append(sCurrentLine);
	            }
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        
	        
	        FloorEvent event = null;
	        try {
				event = new FloorEvent(contentBuilder.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
	        return event;
	}


}
