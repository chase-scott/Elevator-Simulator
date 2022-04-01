package elevator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import event.ErrorEvent;
import state.Direction;
import state.DoorState;
import state.MotorState;
import system.Observer;
import util.Constants;
import util.Timer;

public class Elevator implements Observer, Runnable {

	private List<ElevatorButton> buttons;
	private MotorState motor;
	private DoorState door;
	private int curFloor;
	private boolean active; // if this elevator is active

	private int errorCode; // no current error code if -1
	private LinkedList<Integer> floorQueue;

	public Elevator(int minFloor, int maxFloor) {

		this.active = true;

		this.errorCode = -1;

		this.motor = MotorState.IDLE;
		this.door = DoorState.CLOSED;

		this.floorQueue = new LinkedList<>();

		this.curFloor = minFloor;

		this.buttons = new ArrayList<ElevatorButton>();
		for (int i = minFloor; i < maxFloor; i++) {
			buttons.add(new ElevatorButton(i));
		}
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

	@Override
	public void update(byte[] data) {

		// TODO make this do fancy algorithm stuff
		if (data[0] == Constants.MOVE_DATA) {
			floorQueue.add((int) data[2]);
			floorQueue.add((int) data[3]);
		}
		if (data[0] == Constants.ERROR_DATA) {

			// TODO make the events in a list so this can be handled dynamically
			switch (data[2]) {
			case ErrorEvent.DOOR_ERROR:
				this.errorCode = ErrorEvent.DOOR_ERROR;
				break;
			case ErrorEvent.FLOOR_ERROR:
				this.errorCode = ErrorEvent.FLOOR_ERROR;
				break;
			default:
				this.errorCode = -1;
				break;
			}
		}

	}

	/**
	 * Moves the elevator
	 * 
	 * @param desfloor
	 */
	private void handleMove(int desFloor) {

		if (desFloor - curFloor != 0) {
			if (desFloor > curFloor) {
				this.motor = MotorState.setState(2);
				try {
					System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName()
							+ "] Started moving from " + curFloor + " towards " + desFloor + "\n");
					if (!move(Direction.UP, desFloor - curFloor)) {
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				this.motor = MotorState.setState(3);
				try {
					System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName()
							+ "] Started moving from " + curFloor + " towards " + desFloor + "\n");
					if (!move(Direction.DOWN, curFloor - desFloor)) {
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		this.motor = MotorState.setState(1);

	}

	private boolean move(Direction direction, int floorsToMove) throws InterruptedException {
		for (int i = 0; i < floorsToMove; i++) {
			Thread.sleep(Timer.FLOOR_TIME);

			// check here if there is a floor fault, if so, kill the elevator thread.
			if (this.errorCode == ErrorEvent.FLOOR_ERROR) {
				active = false;
				return false;
			}

			if (direction.equals(Direction.UP)) {
				curFloor++;
				System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Moved up to floor "
						+ curFloor + "\n");
			} else {
				curFloor--;
				System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName()
						+ "] Moved down to floor " + curFloor + "\n");
			}

		}

		System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName()
				+ "] Reached its destination. Opening doors...\n");
		Thread.sleep(Timer.DOOR_TIME);

		this.door = door.switchState();
		System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Doors are "
				+ this.getDoor().getState() + "\n");

		System.out.println(
				Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Loading/unloading passengers...\n");
		Thread.sleep(Timer.LOAD_TIME);
		
		
		
		System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Done. Closing doors...\n");
		Thread.sleep(Timer.DOOR_TIME);
		
		// check here if there is a door fault, if so, wait 2 seconds then close doors again.
		if(this.errorCode == ErrorEvent.DOOR_ERROR) {
			System.err.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Door closing failed. Retrying in 2 seconds...\n");
			Thread.sleep(2000);
			this.errorCode = -1;
		}
		
		System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Closing doors...\n");
		Thread.sleep(Timer.DOOR_TIME);
		
		this.door = door.switchState();
		System.out.println(Timer.formatTime() + " [" + Thread.currentThread().getName() + "] Door are "
				+ this.getDoor().getState() + "\n");

		return true;
	}

	@Override
	public void run() {

		while (active) {

			if (floorQueue.size() != 0) {
				handleMove(floorQueue.remove(0));
			}

			try {
				Thread.sleep((long) (Math.random() * Timer.DEFAULT));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		System.err.println("[" + Thread.currentThread().getName() + "] Shutting down with error code: " + this.errorCode + "\n");

	}

}
