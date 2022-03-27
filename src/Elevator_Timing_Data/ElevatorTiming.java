package Elevator_Timing_Data;
/*
 * 
 * @author: Kenny Deng
 * 
 * WARNINGS: 
 * 		- avgRideTime is average of floor to floor ride times AND normalized floor1->floor7 (and f7->f1 times), 
 * 			issue with using f1->f7 (and f7->f1) is there is only one accel and one decel event for all floors
 * 			vs 6 accel and 6 decel if travelling floor to floor.
 * 
 * 			Will continue to use both in calculation as in the real life elevators often skip floors (not always 
 * 			sequential travelling).
 */


public class ElevatorTiming {
	
	
	public static void main(String[] args) {
			
			/*
			 * Your estimated values for the rate of acceleration and deceleration of an elevator car, and the maximum 
			 * speed of the car. To estimate the distance between floors, count the number of steps.  A typical riser is 
			 * seven inches in height.  Show him you estimate how you come up with acceleration.  You can assume that 
			 * the elevator decelerates at the same rate. 
			 */
			
			/*
			 * Your estimated time it takes to load and unload a car.  You should probably take note whether there is a 
			 * significant difference in the loading time of a car if there are a lot of people getting in and out or not (Hint: 
			 * the Dunton elevators may be your best bet here!).  This may not be an average, but a function of the 
			 * number of people moving in and out of the elevator.   You can include this information in your excel 
			 * spreadsheet, or submit it as a separate PDF file.
			 */
			
			/*
			 * Finally, answer the three questions in the quiz for Iteration 0.  You will need to submit the maximum speed 
			 * for the elevator, the rate of acceleration for the elevator, and the average loading/unloading time.  Only one 
			 * person per group needs to do this (assuming I have figured out how to correctly configure this in CU Learn). 
			 */
			
			// import all-floors data (floor to floor time)
			double[] loadUnloadTimeAllFloors = {8.0, 9.9, 11.0, 7.8};
			double[] rideTimeAllFloors = {17.6, 19.6, 19.6, 22.5};
			
			// import per-floor data
			double[] loadUnloadTimePerFloor = {6.2, 9.7, 10.6, 10.7, 10.2, 11.0, 8.2};
			double[] rideTimePerFloor = {6.5, 8.5, 7.7, 12.7, 8.4, 13.2};
			
			
			// average loading/unloading time of "All Floors" and "Per Floor"
			// 0.5 * (avgAllFloors + avgPerFloor)
			double avgLoadUnloadTime = averageAllFloorsPerFloor(simpleAverage(loadUnloadTimeAllFloors), simpleAverage(loadUnloadTimePerFloor));
			System.out.println("Average Elevator Load/Unload: " + avgLoadUnloadTime + " seconds");
			
			// average ride time of "All Floors" and "Per Floor"
			// 0.5 * (avgAllFloors + avgPerFloor)
			// BIG thing to watch our for, AllFloors doesnt have to slow down for each floor (only 1 accel and 1 decel)
			double avgRideTime = averageAllFloorsPerFloor(normalizeAverageAllFloors(rideTimeAllFloors, 6), simpleAverage(rideTimePerFloor));
			System.out.println("Average Elevator Ride Time Between Floors: " + avgRideTime + " seconds");
			
			// average elevator speed
			// velocity = distance/time
			double avgElevatorSpeed = averageElevatorSpeed(avgRideTime, 4);	// 4 metres between each floor as shown on "Project" file Version 3.0, 
																			// 2022/01/10, pg. 7 of 12, paragraph 1
			System.out.println("Average Elevator Speed (all-floors + per-floor data): " + avgElevatorSpeed + " metres/second");
			
			// average all-floors elevator speed of f1->f7 and f7->f1
			// using these values as there are only 1 accel and 1 decel events in each measurement
			// velocity = distance/time
			double avgElevatorSpeedF1toF7 = (6*4)/simpleAverage(rideTimeAllFloors);
			System.out.println("Average Elevator Speed (all-floors): " + avgElevatorSpeedF1toF7 + " metres/second");
			
			/*
			// max velocity = average/0.90
			// ask Kenny for the math, he will explain (I drew it out on paper, will transfer to electronic later)
			// jist of it is 10% of time accel to max speed, 80% cruising at max speed, 10% of time decel to stop
			// a lot of assumptions are being made
			// assuming ramped square velocity characteristic
			double maxSpeedCalculated = avgElevatorSpeed/0.9;
			System.out.println("Max Elevator Speed (Calculated): " + maxSpeedCalculated + " metres/second");
			
			// acceleration = d/dt (velocity)
			// velocity = [ (y1 - y0)/(x1 - x0) ]x + b
			// vel = [(Vmax-0)/((0.1*time) - 0)]x + 0
			// vel = (Vmax/0.1time)x
			// accel = vel' = Vmax/0.1time
			double accelCalculated = maxSpeedCalculated/(0.1*avgRideTime);
			System.out.println("Elevator Acceleration (Calculated): " + accelCalculated + " metres/second^2");
			System.out.println("Time Elevator Spent Accelerating/Decelerating (Calculated): " + 0.1*avgRideTime + " seconds");
			*/
			double maxSpeedCalculatedAllFloors = avgElevatorSpeedF1toF7/0.8;
			System.out.println("Max Elevator Speed (Calculated, all-floors): " + maxSpeedCalculatedAllFloors + " metres/second");
			double accelCalculatedAllFloors = maxSpeedCalculatedAllFloors/(0.2*avgRideTime);
			System.out.println("Elevator Acceleration (Calculated): " + accelCalculatedAllFloors + " metres/second^2");
			System.out.println("Time Elevator Spent Accelerating/Decelerating (Calculated): " + 0.2*avgRideTime + " seconds");
			
		}
	
	/*
	 * Calculate simple average of array of double data. No extrapolation or manipulating/curving.
	 * 
	 * Parameter double[] with elevator data
	 * 
	 * Return average with type double
	 */
	public static double simpleAverage(double[] arr) {
		double sum = 0;
		for (int i = 0; i < arr.length; i++) {
			sum += arr[i];
		}
		
		return sum/arr.length;
	}
	
	/*
	 * Convert floor1->floor7 time into average of floor to floor times
	 */
	public static double normalizeAverageAllFloors(double[] arr, int floorsTravelled) {
		double normSum = 0;
		for (int i = 0; i < arr.length; i++) {
			normSum += arr[i]/floorsTravelled;
		}
		
		return normSum/arr.length;
	}
	
	/*
	 * Calculate average of "AllFloors" and "PerFloor" from return value of simpleAverage()
	 * 
	 * Average = 0.5 * (sum of all data)
	 */
	public static double averageAllFloorsPerFloor(double allFloors, double perFloor) {
		return 0.5 * (allFloors + perFloor);
	}

	/*
	 * Returns average elevator speed.
	 * 
	 * Parameters: average (AllFloors & PerFloor) and floor to floor distance (metres)
	 */
	public static double averageElevatorSpeed(double averageTime, double floorToFloorDistance) {
		return floorToFloorDistance/averageTime;
	}

}
