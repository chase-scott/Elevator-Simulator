package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import elevator.Elevator;
import util.Timer;

/**
 * ElevatorView class
 * 
 * @author Chase Scott - 101092194
 */
@SuppressWarnings("serial")
public class ElevatorView extends JPanel implements IView {

	private Elevator model;
	private int elevatorID;
	
	JLabel doorLabel;	//label containing the door state
	JLabel lampLabel;	//label containing the current floor
	JLabel statusLabel;	//label containing the status
	JLabel errorLabel;	//label containing the error code
	
	JTextArea elevatorText; //text containing the log info
	

	public ElevatorView(Elevator model, int elevatorID) {
		super();
		this.model = model;
		this.elevatorID = elevatorID;
		this.createLayout();
		model.addView(this);

	}

	/**
	 * Creates the layout for this view
	 */
	private void createLayout() {

		this.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(400, 300));
		this.setBorder(BorderFactory.createTitledBorder("Elevator " + elevatorID));
		
		lampLabel = new JLabel(model.getLamp(), SwingConstants.CENTER);
		lampLabel.setFont(new Font("", Font.PLAIN, 30));
		
		doorLabel = new JLabel("Door: " + model.getDoor());
		
		statusLabel = new JLabel("Status: "  + (model.getActive() ? "Active" : "Disabled"));
		errorLabel = new JLabel("Error code: " + model.getError());
		
		JPanel headerPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		headerPanel.add(doorLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 2;
		
		headerPanel.add(lampLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 0.5;
		c.gridy = 1;
		headerPanel.add(statusLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;       
		c.weightx = 0.5;
		c.gridy = 2;       
		headerPanel.add(errorLabel, c);

		elevatorText = new JTextArea("[" + Timer.formatTime() + "] Begin log.\n");
		elevatorText.setEditable(false);
		JScrollPane elevatorInfo = new JScrollPane(elevatorText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//this makes the text area auto scroll
		elevatorInfo.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				elevatorText.select(elevatorText.getHeight() + 1000, 0);
			}
		});


		this.add(headerPanel, BorderLayout.PAGE_START);
		

		this.add(elevatorInfo, BorderLayout.CENTER);

	}

	/**
	 * Updates the information for this view
	 */
	@Override
	public void updateView(String message) {

		doorLabel.setText("Door: " + model.getDoor());
		lampLabel.setText(model.getLamp());
		statusLabel.setText("Status: "  + (model.getActive() ? "Active" : "Disabled"));
		errorLabel.setText("Error code: " + model.getError());
		
		elevatorText.append("[" + Timer.formatTime() + "] " + message + "\n");
		
		
	}

}
