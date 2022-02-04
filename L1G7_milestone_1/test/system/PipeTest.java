/**
 * 
 */
package system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import floor.FloorEvent;
import state.Direction;

/**
 * @author Colin
 *
 */
class PipeTest {

	@Test
	void pipeTest() {
		Pipe p = new Pipe();
		FloorEvent e = new FloorEvent("00:00:00.00000000",1,Direction.UP,5);
		assertEquals(p.getNextEvent(),e);

	}

}
