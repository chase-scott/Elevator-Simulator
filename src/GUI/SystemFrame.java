package GUI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import system.ElevatorSystem;
import system.FloorSystem;
import system.Scheduler;
import util.Constants;

/**
 * SystemFrame class --> run this to start the program with the GUI
 * 
 * @author Chase Scott - 101092194
 *
 */
@SuppressWarnings("serial")
public class SystemFrame extends JFrame {

	@SuppressWarnings("unused")
	public SystemFrame(ElevatorSystem elevatorModel, FloorSystem floorModel) {
		
		super("L1G7 Visual Interface");
	
		JPanel contents = new SystemGUI(elevatorModel, floorModel);
		
		this.setContentPane(contents);
		
		this.setSize(new Dimension((Constants.NUM_ELEVATORS > 2 ? 1000 : 600), (Constants.NUM_ELEVATORS > 2 ? ((Constants.NUM_ELEVATORS / 2) * 325) : 350)));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	//MAIN METHOD, starts all 3 subsystems and displays the GUI
	public static void main(String[] args) {
		
		Scheduler s = new Scheduler();
		FloorSystem f = new FloorSystem();
		ElevatorSystem e = new ElevatorSystem();
		
		new SystemFrame(e, f);
		
		new Thread(s, "Scheduler").start();
		new Thread(f, "FloorSystem").start();
		new Thread(e, "ElevatorSystem").start();
		
		
	}


}
