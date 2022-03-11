package system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import elevator.ElevatorData;
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
	
	@Test
	void decodePacketTest() {
		Pipe buffer = new Pipe();
		ElevatorSystem es = new ElevatorSystem(1, 11, buffer);
		Scheduler s = new Scheduler(buffer);
		byte[] data = es.buildPacketData(es.getElevator(0));
		ElevatorData ed = s.decodePacketData(data);
		for(int i = 0;i<data.length;i++) {
			System.out.print(data[i] +" ");
		}
		System.out.print("\n");
		System.out.println(ed.getId());
		System.out.println(ed.getFloor());
		System.out.println(ed.getDoorState());
		System.out.println(ed.getMotorState());
		System.out.println(ed.getMoving());

	}

}
