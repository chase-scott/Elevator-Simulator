package elevator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import GUI.IView;
import state.DoorState;
import state.MotorState;
import system.Observer;
import util.Constants;
import util.Timer;

/**
 * Elevator class
 * 
 * @author Chase Scott - 101092194
 */
public class Elevator implements Observer, Runnable {

	
	private List<IView> views; // the views for this elevator
	private List<ElevatorButton> buttons;
	
	private MotorState motor; 
	private DoorState door;
	private int curFloor;
	
	private boolean active; // if this elevator is active
	private int errorCode; // no current error code if -1

	//queue for floors going up
	private List<Byte> upfloorQueue;
	private boolean handlingUp;

	//queue for floors going down
	private List<Byte> downfloorQueue;
	private boolean handlingDown;

	public Elevator(int minFloor, int maxFloor) {

		this.views = new ArrayList<>();

		this.active = true;

		this.errorCode = Constants.NO_ERROR;

		this.motor = MotorState.IDLE;
		this.door = DoorState.CLOSED;

		this.upfloorQueue = new LinkedList<>();
		this.handlingUp = false;
		this.downfloorQueue = new LinkedList<>();
		this.handlingDown = false;

		this.curFloor = minFloor;

		this.buttons = new ArrayList<ElevatorButton>();
		for (int i = minFloor; i < maxFloor; i++) {
			buttons.add(new ElevatorButton(i));
		}
	}

	/**
	 * updates the elevator's floor queues
	 */
	@Override
	public void notify(byte[] data) {

		if (data[0] == Constants.MOVE_DATA) {

			if (data[3] > data[2]) {

				if (!upfloorQueue.contains( data[2])) {

					upfloorQueue.add( data[2]);

				}
				if (!upfloorQueue.contains( data[3])) {

					upfloorQueue.add( data[3]);

				}

				upfloorQueue.sort(Comparator.naturalOrder());

			} else if (data[2] > data[3]) {

				if (!downfloorQueue.contains(data[2])) {

					downfloorQueue.add( data[2]);

				}
				if (!downfloorQueue.contains( data[3])) {

					downfloorQueue.add( data[3]);

				}

				downfloorQueue.sort(Comparator.reverseOrder());
			}

			updateViews("Pickup at floor: " + data[2] + ". Requesting floor: " + data[3] + ". Error type: "
					+ (data[4] == -1 ? "none" : data[4]) + ".\n");

			// show elevator floor queues
			// if (Constants.DEBUG) {
			//System.out.println("\n-----FLOOR QUEUE for elevator " + data[1] + "-----\nup:");
			//upfloorQueue.forEach(e -> System.out.print(e + " "));
			//System.out.println("\n-----------------------\ndown:");
			//downfloorQueue.forEach(e -> System.out.print(e + " "));
			//System.out.println("\n-----------------------\n");
			// }

		}

		// TODO make the events in a list so this can be handled dynamically
		switch (data[4]) {
		case Constants.DOOR_ERROR:
			this.errorCode = Constants.DOOR_ERROR;
			break;
		case Constants.FLOOR_ERROR:
			this.errorCode = Constants.FLOOR_ERROR;
			break;
		default:
			break;
		}

	}

	/**
	 * Moves the elevator
	 * 
	 * @param desfloor
	 */
	private void handleMove(int desFloor) {

		if (desFloor > curFloor) {
			
			//if elevator is idle, tell view it is starting to move
			if(this.motor == MotorState.IDLE)
				updateViews("Moving up from " + curFloor + "\n");
			
			
			this.motor = MotorState.UP;

			try {
				Thread.sleep(Timer.FLOOR_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (checkFloorError())
				return;

			curFloor++;

			updateViews("Reached floor " + curFloor + "\n");

		}
		if (desFloor < curFloor) {
			
			//if elevator is idle, tell view it is starting to move
			if(this.motor == MotorState.IDLE)
				updateViews("Moving down from " + curFloor + "\n");
			
			this.motor = MotorState.DOWN;

			try {
				Thread.sleep(Timer.FLOOR_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (checkFloorError())
				return;

			curFloor--;

			updateViews("Reached floor " + curFloor + "\n");

		}
		if (desFloor == curFloor) {
			try {

				// set current state to idle and remove destination floor from queue of floors
				// to visit
				if (handlingUp) {
					upfloorQueue.remove(0);

					this.motor = MotorState.IDLE;

					if (upfloorQueue.size() == 0 && downfloorQueue.size() != 0) {
						if (this.curFloor == downfloorQueue.get(0)) {
							return;
						}
					}

				} else if (handlingDown) {
					downfloorQueue.remove(0);

					this.motor = MotorState.IDLE;

					if (downfloorQueue.size() == 0 && upfloorQueue.size() != 0) {
						if (this.curFloor == upfloorQueue.get(0)) {
							return;
						}
					}

				}

				updateViews("Reached its destination. Opening doors...\n");

				// System.out.println(Timer.formatTime() + " [" +
				// Thread.currentThread().getName()
				// + "] Reached its destination. Opening doors...\n");

				Thread.sleep(Timer.DOOR_TIME);
				this.door = door.switchState();
				updateViews("Doors are " + this.getDoor() + ". Loading/unloading passengers...\n");

				// System.out.println(Timer.formatTime() + " [" +
				// Thread.currentThread().getName() + "] Doors are "
				// + this.getDoor().getState() + "\n");
				// System.out.println(Timer.formatTime() + " [" +
				// Thread.currentThread().getName()
				// + "] Loading/unloading passengers...\n");

				Thread.sleep(Timer.LOAD_TIME);

				// System.out.println(
				// Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Done.
				// Closing doors...\n");

				updateViews("Done. Closing doors...\n");

				Thread.sleep(Timer.DOOR_TIME);

				// check here if there is a door fault, if so, wait 2 seconds then close doors
				// again.
				if (this.errorCode == Constants.DOOR_ERROR) {
					// System.err.println(Timer.formatTime() + " [" +
					// Thread.currentThread().getName()
					// + "] Door closing failed. Retrying in 2 seconds...\n");
					updateViews("Door closing failed. Retrying in 2 seconds...\n");
					Thread.sleep(2000);

					// System.out.println(
					// Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Closing
					// doors...\n");

					updateViews("Closing doors...\n");

					Thread.sleep(Timer.DOOR_TIME);

					this.errorCode = -1;
				}

				this.door = door.switchState();
				updateViews("Doors are " + this.getDoor() + "\n");

				// System.out.println(Timer.formatTime() + " [" +
				// Thread.currentThread().getName() + "] Doors are "
				// + this.getDoor().getState() + "\n");

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private boolean checkFloorError() {
		// check here if there is a floor fault, if so, kill the elevator thread.
		if (this.errorCode == Constants.FLOOR_ERROR) {
			active = false;
			this.motor = MotorState.IDLE;
			this.handlingUp = false;
			this.handlingDown = false;
			return true;
		}
		return false;
	}

	public void addView(IView view) {
		this.views.add(view);
	}

	public void updateViews(String message) {
		views.forEach(e -> e.updateView(message));
	}

	public boolean getActive() {
		return active;
	}

	public MotorState getMotor() {
		return motor;
	}

	public DoorState getDoor() {
		return door;
	}

	public int getCurFloor() {
		return curFloor;
	}

	public String getError() {

		switch (errorCode) {
		case Constants.FLOOR_ERROR:
			return "FLOOR_ERROR";
		case Constants.DOOR_ERROR:
			return "DOOR_ERROR";
		default:
			return "NONE";
		}

	}
	
	public boolean isHandlingUp() {
		return this.handlingUp;
	}
	
	public boolean isHandlingDown() {
		return this.handlingDown;	
	}
	
	public List<Byte> getUpfloorQueue() {
		return upfloorQueue;
	}

	public List<Byte> getDownfloorQueue() {
		return downfloorQueue;
	}
	
	/**
	 * Calculates and return the string representing this elevators lamp.
	 * 
	 * @return String, the lamp information
	 */
	public String getLamp() {

		StringBuilder sb = new StringBuilder();
		sb.append(curFloor + " ");

		if (handlingUp) {
			if (upfloorQueue.size() == 0) {
				sb.append("-- ");
			} else if (upfloorQueue.get(0) < curFloor) {
				sb.append((char)8595);
			} else if (upfloorQueue.get(0) > curFloor) {
				sb.append((char)8593);
			} else {
				sb.append((char)8593);
			}

		} else if (handlingDown) {
			if (downfloorQueue.size() == 0) {
				sb.append("-- ");
			} else if (downfloorQueue.get(0) < curFloor) {
				sb.append((char)8595);
			} else if (downfloorQueue.get(0) > curFloor) {
				sb.append((char)8593);
			} else {
				sb.append((char)8595);
			}

		} else {
			sb.append("-- ");
		}

		return sb.toString();
	}

	@Override
	public String toString() {

		return "MotorState: " + this.motor + " DoorState: " + this.door + "\nCurrent floor: " + this.curFloor
				+ " Status: " + (active ? "Active" : "Disabled") + "\nError code: "
				+ (errorCode == -1 ? "none" : errorCode);
	}

	@Override
	public void run() {

		while (active) {

			if (upfloorQueue.size() != 0 && !handlingDown) {

				handlingUp = true;

				handleMove(upfloorQueue.get(0));

				if (upfloorQueue.size() == 0)
					handlingUp = false;

			}

			if (downfloorQueue.size() != 0 && !handlingUp) {

				handlingDown = true;

				handleMove(downfloorQueue.get(0));

				if (downfloorQueue.size() == 0)
					handlingDown = false;

			}

			// while loop doesn't work without a tiny sleep for some reason so here it is...
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// System.err.println(
		// "[" + Thread.currentThread().getName() + "] Shutting down with error code: "
		// + this.errorCode + "\n");

		updateViews("Shutting down with error code: " + this.errorCode + "\n");

	}

}
