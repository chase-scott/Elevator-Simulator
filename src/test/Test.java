package test;



import java.util.Arrays;

import event.FloorEvent;

public class Test {

	/*
	* parameters: byte array of elevator state data, floor data of user waiting on elevator
	* return: byte array [1, elevator#, pickupFloor, destinationFloorNum]
	*			1: send_request
	*/
	public byte[] chooseElevator(byte[] elevatorStateData, FloorEvent floorEvent) { 
			//elevatorStateData = [1, 0, 1, 0, 0, 1, 1, 0, 0, 2, 1, 0, 0, 3, 1, 0, 0]
				//first bit is 1 denoting that it is a send_request, then it repeats every 4 bits
				//0, 1, 0, 0 --> elevatorID = 0, curFloor = 1, motorState = IDLE, 0 to separate elevators
			

			// algorithm to find which elevator to send to user
				// find if same floor, send elevator that is on same floor and travel direction that has least current users
				// find closest same direction, send closest elevator that has least current users
				// find if idle, send closest idle elevator

			byte[] chosenElevator = new byte[4];
			boolean foundElevator = false;				
			
			
			// TODO: balance # of users per elevator
			/* same floor, send first elevator that loop finds */
			if (foundElevator == false) {
				byte direction;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					if (elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == direction || elevatorStateData[i+1] == floorEvent.getFloorNumber() && elevatorStateData[i+2] == 1) { // check if on same floor, motorState == direction, traveling in same direction OR motor idle
						// set chosenElevator data																
						chosenElevator[0] = 1; // send_request
						chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
						chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
						chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
						foundElevator = true;
					}
	
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find an elevator in the same direction, elevator must be heading to pickupFloor */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				byte direction;
				boolean firstPass = true;
				
				// get direction of floorEvent
				if (floorEvent.getDirection().parseDirection("up") == floorEvent.getDirection()) {
					direction = 2; // direction UP
				} else if (floorEvent.getDirection().parseDirection("down") == floorEvent.getDirection()) {
					direction = 3; // direction DOWN
				} else {
					direction = -1; // direction INVALID
				}

				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest elevator in same direction 
					if (elevatorStateData[i+2] == direction) { // motorState == direction, traveling in same direction
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									// set chosenElevator data
									chosenElevator[0] = 1; // send_request
									chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
									chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
									chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
									foundElevator = true; // found elevator in same direction
								}
							}
							*/
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (elevatorStateData[i+2] == 2) { // UP
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							if (elevatorStateData[i+2] == 3) { // DOWN
								if (elevatorStateData[i+1] < floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							
							/*
							if (elevatorStateData[i+2] == -1) { // INVALID
								if (elevatorStateData[i+1] > floorEvent.getFloorNumber()) {
									// don't send elevator
								} else { 
									// send elevator
									if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
										numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
										// set chosenElevator data
										chosenElevator[0] = 1; // send_request
										chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
										chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
										chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
										foundElevator = true; // found elevator in same direction
									}
								}
							}
							*/	
						}
					}
				}
				
				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}
			}
			
			
			// TODO: balance number of users per elevator
			/* find closest idle elevator */
			if (foundElevator == false) {
				byte numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[2]); // #floor difference of first entry
				boolean firstPass = true;
				
				for (byte i = 1; i < elevatorStateData.length; i+=4) { // loop through all elevators
					// find closest idle elevator
					if (elevatorStateData[i+2] == 1) { // MotorState IDLE(1)
						if (firstPass == true) { // don't update numFloorsCloser as it has already been initialized
							// set chosenElevator data
							chosenElevator[0] = 1; // send_request
							chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
							chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
							chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							firstPass = false;
						} else { // compare and update numFloorsCloser
							if (numFloorsCloser > Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1])) { // current elevator is closer
								numFloorsCloser = (byte) Math.abs(floorEvent.getFloorNumber() - elevatorStateData[i+1]);
								// set chosenElevator data
								chosenElevator[0] = 1; // send_request
								chosenElevator[1] = (byte) elevatorStateData[i]; // elevator# chosen
								chosenElevator[2] = (byte) floorEvent.getFloorNumber(); // pickupFloor# 
								chosenElevator[3] = (byte) floorEvent.getDestinationFloor(); // destinationFloor #
							}
						}
						foundElevator = true; // found idle elevator 
					}	
				}

				if (foundElevator == true) {
					return chosenElevator; // [1, elevator#, pickupFloor, destinationFloorNum]
				}	
			}	
			
			return chosenElevator; // error, return empty, no elevator can take user
					
	}	
	
	public static void main(String[] args) {
		
		Test test = new Test();
		
		FloorEvent fe = new FloorEvent();
		System.out.println(fe.toString());
		
		/* test direction */
		/*
		byte direction;
		if (fe.getDirection().parseDirection("up") == fe.getDirection()) {
			direction = 2; // direction UP
		} else if (fe.getDirection().parseDirection("down") == fe.getDirection()) {
			direction = 3; // direction DOWN
		} else {
			direction = -1; // direction INVALID
		}
		System.out.println(direction);
		*/
		
		byte[] data = test.chooseElevator(new byte[] {1, 1, 1, 1, 0, 2, 3, 3, 0, 3, 5, 2, 0, 4, 7, 1, 0}, fe);
		//first bit is 1 denoting that it is a send_request, then it repeats every 4 bits
		//0, 1, 0, 0 --> elevatorID = 0, curFloor = 1, motorState = IDLE, 0 to separate elevators
		// return: byte array [1, elevator#, pickupFloor, destinationFloorNum]
		
		System.out.println("Containing Bytes: " + Arrays.toString(data) + "\n");
		
	}
	
	
	
}
