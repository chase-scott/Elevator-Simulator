package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import floor.Floor;
import floor.FloorButton;
import state.Direction;

/**
 * FloorView class
 * 
 * @author Chase Scott - 101092194
 */
@SuppressWarnings("serial")
public class FloorView extends JPanel implements IView  {

	private Floor model;
	
	private JLabel elevators;	//label showing the elevators on this floor
	private JLabel upButton;	//up button label
	private JLabel downButton;	//down button label
	
	public FloorView(Floor model) {
		
		super();
		this.model = model;
		this.createLayout();
		model.addView(this);
		
	}
	
	/**
	 * Creates the layout for this view
	 */
	private void createLayout() {
		this.setLayout(new GridBagLayout());

		this.setPreferredSize(new Dimension(250, 50));
		
		this.setBorder(BorderFactory.createTitledBorder("Floor " + model.getFloorNumber()));
		
		for(FloorButton b : model.getButtons()) {
			if(b.getDirection() == Direction.UP) {
				upButton = new JLabel(" " + (char)8593 + " "); //up arrow
				upButton.setOpaque(true);
				upButton.setBackground(Color.BLACK);
				
				this.add(upButton);
			} 
			if(b.getDirection() == Direction.DOWN) {
				downButton = new JLabel(" " + (char)8595 + " "); //down arrow
				downButton.setOpaque(true);
				downButton.setBackground(Color.BLACK);
				this.add(downButton);
			}
		}
		
		
		elevators = new JLabel();
		this.add(elevators);
	
	}

	/**
	 * Updates the information for this view
	 */
	@Override
	public void updateView(String message) {
		
		elevators.setText(message);
		
		for(FloorButton b : model.getButtons()) {
			if(b.getLamp()) {
				
				if(b.getDirection() == Direction.UP) {
					upButton.setBackground(Color.YELLOW);
				} else {
					downButton.setBackground(Color.YELLOW);
				}
			} else {
				if(b.getDirection() == Direction.UP) {
					upButton.setBackground(Color.BLACK);
				} else {
					downButton.setBackground(Color.BLACK);
				}
			}
		}
		
		
	}
	

}
