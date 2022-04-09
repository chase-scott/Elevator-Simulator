package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import elevator.Elevator;
import floor.Floor;
import system.ElevatorSystem;
import system.FloorSystem;
import util.Constants;

/**
 * Builds out the Graphic User Interface for the system
 * 
 * @author Chase Scott - 101092194
 */
@SuppressWarnings("serial")
public class SystemGUI extends JPanel {
	
	public SystemGUI(ElevatorSystem elevatorModel, FloorSystem floorModel) {
		super();
		
		JPanel elevatorPanel = this.generateElevatorPanel(elevatorModel);
		
		JPanel floorPanel = this.generateFloorPanel(floorModel);
		
		this.setLayout(new BorderLayout());
		
		this.add(elevatorPanel, BorderLayout.CENTER);
		this.add(floorPanel, BorderLayout.LINE_END);
		
	}

	private JPanel generateFloorPanel(FloorSystem floorModel) {
		
		JPanel panel = new JPanel(new GridLayout(0, 1));
		
		for(int i = Constants.MAX_FLOOR - 1; i >= Constants.MIN_FLOOR - 1; i--) {
			FloorView view = new FloorView((Floor)floorModel.getFloors().get(i));
			panel.add(view);
		}

		return panel;
	}

	//probably pass the elevator system and for each elevator create a view?? that is my current idea
	private JPanel generateElevatorPanel(ElevatorSystem model) {
		
		JPanel panel = new JPanel(new GridLayout(0, 2));

		for(int i = 1; i < Constants.NUM_ELEVATORS + 1; i++) {
			ElevatorView view = new ElevatorView((Elevator)model.getElevators().get(i), i);
			panel.add(view);
		}
		
		return panel;
	}

}
