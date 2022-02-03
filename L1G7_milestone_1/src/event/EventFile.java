package event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;


public class EventFile {
	
	public static String EVENT_FILEPATH = "eventfolder/eventFile.txt";
	
	private File file;
	private long timeStamp;
	
	public EventFile(File file) {
		this.file = file;
		this.timeStamp = file.lastModified();
	}
	
	
	
	public static void writeEvent(FloorEvent event) {
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(EventFile.EVENT_FILEPATH);
			writer.write(event.toString());
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
	public static FloorEvent readEvent(File file) {
		
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
	
	
	
	
	public boolean isModified() {
	  long timeStamp = file.lastModified();
	  
	  if( this.timeStamp != timeStamp ) {
	    this.timeStamp = timeStamp;
	    return true;
	  }
	  return false;
	}
	
	public File getFile() {
		return file;
	}


}
