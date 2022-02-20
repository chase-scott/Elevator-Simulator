package system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import event.EventFile;
import floor.FloorEvent;
import state.Direction;
import state.SchedulerState;

class SchedulerTest {

	@Test
	void schedulerTest() {
		Pipe buffer = new Pipe();
		Scheduler s = new Scheduler(buffer);
		assertEquals(s.getSchedulerState(),SchedulerState.IDLE);
		Thread schedulingSubsystem = new Thread(s, "Scheduler subsystem");
		schedulingSubsystem.start();
		event.FloorEvent fe = new event.FloorEvent("15:20:43.997771100", 4, Direction.UP, 7);
		s.handleFloorEvent(fe);
		assertEquals(s.getSchedulerState(),SchedulerState.SENDING);

	}

}
