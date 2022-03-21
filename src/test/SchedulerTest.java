package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.FloorEvent;
import event.EventFile;
import state.Direction;
import state.SchedulerState;
import system.ElevatorSystem;
import system.Scheduler;

class SchedulerTest {

	@Test
	void schedulerTest() {
		byte[] data = new byte[]{1, 0, 1, 0, 0, 1, 1, 0, 0, 2, 1, 0, 0, 3, 1, 0, 0};
		FloorEvent fe = new FloorEvent("15:20:43.997771100", 4, Direction.UP, 7);
		Scheduler s = new Scheduler();
		byte[] choose;
		choose= s.chooseElevator(data, fe);
		for (int i = 0 ; i < choose.length; i++) {
			System.out.println(choose[i]);
		}
		assertEquals(choose[0],"reply()");
		assertEquals(choose[1],"reply2()");

	}
	
}
