package event;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import util.Timer;

public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	private String time;
	
	public Event(String time) {
		this.time = time;
	}
	
	/**
	 * Marshal this event to a byte array.
	 * 
	 * @param e
	 * @return
	 */
	public static byte[] marshal(Event e) {
		// Getting byte arrays of FloorEvent attributes
		e.time = Timer.formatTime(); // set time to when event is marshaled to packet
		try {
			ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(new BufferedOutputStream(baoStream));
			ooStream.flush();
			ooStream.writeObject(e);
			ooStream.flush();

			return baoStream.toByteArray();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;

	}

	/**
	 * Unmarshals the data if it is a event, otherwise throws exception
	 * 
	 * @param data byte[], the data to unmarshal
	 * @return FloorEvent, the unmarshalled floor event
	 */
	public static Event unmarshal(byte[] data) {

		// decode floor event
		Event event = null;
		try {
			// Retrieve the ElevatorData object from the receive packet
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is;
			is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();

			event = (Event) o;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Invalid packet recieved");
			e.printStackTrace();
		}

		return event;

	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
