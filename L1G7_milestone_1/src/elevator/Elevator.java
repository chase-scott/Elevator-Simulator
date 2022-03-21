package elevator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import floor.Floor;
import state.Direction;
import state.DoorState;
import state.MotorState;
import system.Observer;

public class Elevator implements Observer, Runnable {

	private List<ElevatorButton> buttons;
	private MotorState motor;
	private DoorState door;
	private int curFloor;
    private volatile boolean exit = false;


	private LinkedList<Integer> floorQueue;

	public Elevator(int minFloor, int maxFloor) {
		this.motor = MotorState.IDLE;
		this.door = DoorState.CLOSED;

		this.floorQueue = new LinkedList<>();

		this.curFloor = minFloor;

		this.buttons = new ArrayList<ElevatorButton>();
		for (int i = minFloor; i < maxFloor; i++) {
			buttons.add(new ElevatorButton(i));
		}
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
		System.out.println("Elevator has received move information");
		System.out.println("For elevator: " + data[1]);
		System.out.println("Go to floor: " + data[2]);
		System.out.println("Wants to go to floor: " + data[3]);
		
		//TODO make this do fancy algorithm stuff
		floorQueue.add((int) data[2]);
		floorQueue.add((int) data[3]);
	}
	
	@Override
	public Elevator getElevator() {
		return this;
	}
	@Override
	public Floor getFloor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Moves the elevator
	 * 
	 * @param desfloor
	 */
	private void handleMove(int desFloor) {
		
		if(desFloor - curFloor != 0) {
			if(desFloor > curFloor) {
				this.motor = MotorState.setState(2);
				try {
					System.out.println(Thread.currentThread().getName() + " has started moving from " + curFloor + " towards " + desFloor);
					move(Direction.UP, desFloor - curFloor);
				} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				this.motor = MotorState.setState(3);
				try {
					System.out.println(Thread.currentThread().getName() + " has started moving from " + curFloor + " towards " + desFloor);
					move(Direction.DOWN, curFloor - desFloor);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
		this.motor = MotorState.setState(1);
		
	}

	private void move(Direction direction, int floorsToMove) throws InterruptedException {
		for (int i = 0; i < floorsToMove; i++) {
			Thread.sleep(6402);

			if (direction.equals(Direction.UP)) {
				curFloor++;
				System.out.println(Thread.currentThread().getName() + " has moved up to floor " + curFloor);
			} else {
				curFloor--;
				System.out.println(Thread.currentThread().getName() + " has moved down to floor " + curFloor);
			}

		}
		
		
		System.out.println(Thread.currentThread().getName() + " has reached its destination. Opening doors...");
		Thread.sleep(1000);

		this.getDoor().switchState();
		System.out.println("Doors are " + this.getDoor().getState() + "\n");
		
		System.out.println("Loading/unloading passengers...\n");
		Thread.sleep(7344);
		System.out.println("Done. Closing doors...");
		Thread.sleep(1000);
		
		this.getDoor().switchState();
		System.out.println("Door are " + this.getDoor().getState()+ "\n");
		
	}

	@Override
	public void run() {

		while (!exit) {
			
			if(floorQueue.size() != 0) {
				System.out.print("Heading to floor: " + floorQueue.get(0));
				
				handleMove(floorQueue.remove(0));

			}

			try {
				Thread.sleep((long) (Math.random() * 5000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

	}
	
	public void exit() {
		exit = true;
	}


}
