package carDealership;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * GUI interface for adding vehicles to the dealership inventory
 *
 * @author Ronika Patel (40156217)
 * @author Nazim Chaib Cherif-Baza (40017992)
 * @author Andrea Delgado Anderson (40315869)
 * @author Grace Pan (40302283)
 * @author Bao Tran Nguyen (40257379)
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public class VehicleMenu extends JFrame implements ActionListener { // this class for adding a vehicle gui..
	private JButton carButton, motorcycleButton, exitButton;
	private Dealership dealership;
	
	/**
	 * Constructor for the VehicleMenu class
	 * Creates a GUI window with buttons for adding different vehicle types
	 *
	 * @param dealership - the dealership instance to add vehicles to
	 */
	public VehicleMenu(Dealership dealership) {
		this.dealership = dealership;
		setTitle("Add a Vehicle");
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(2, 1));
		setLocationRelativeTo(null);
		
		// Load and scale icons
		// Use file paths instead of resources to ensure images are found
		ImageIcon carIcon = new ImageIcon("src/images/auto.png");
		ImageIcon motoIcon = new ImageIcon("src/images/moto.png");


		Image carImg = carIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon scaledCarIcon = new ImageIcon(carImg);

		Image motoImg = motoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon scaledMotoIcon = new ImageIcon(motoImg);

		carButton = new JButton("Add Car", scaledCarIcon);
		carButton.setBackground(Color.decode("#333333"));
		carButton.setForeground(Color.decode("#333333"));
		carButton.addActionListener(this);
		add(carButton);

		motorcycleButton = new JButton("Add Motorcycle", scaledMotoIcon);
		motorcycleButton.setBackground(Color.decode("#333333"));
		motorcycleButton.setForeground(Color.decode("#333333"));
		motorcycleButton.addActionListener(this);
		add(motorcycleButton);
	}

	/**
	 * Handle action events from GUI components
	 * Routes button clicks to appropriate handler methods
	 *
	 * @param e - the action event to be processed
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == carButton) {
			addCarMenu();
		} else if (e.getSource() == motorcycleButton) {
			addMotorcycleMenu();
		} else if (e.getSource() == exitButton) {
			dispose(); // closes the GUI windw.
		}
	}

	/**
	 * Display a dialog for adding a car to the dealership
	 * Collects car details from user input and adds the car to inventory
	 */
	private void addCarMenu() {
		JTextField makeField = new JTextField();
		JTextField modelField = new JTextField();
		JTextField colorField = new JTextField();
		JTextField yearField = new JTextField();
		JTextField priceField = new JTextField();
		JTextField carTypeField = new JTextField();

		JPanel panel = new JPanel(new GridLayout(0, 1));

		panel.add(new JLabel("Make:"));
		panel.add(makeField);
		panel.add(new JLabel("Model:"));
		panel.add(modelField);
		panel.add(new JLabel("Color:"));
		panel.add(colorField);
		panel.add(new JLabel("Year:"));
		panel.add(yearField);
		panel.add(new JLabel("Price:"));
		panel.add(priceField);
		panel.add(new JLabel("Car Type:"));
		panel.add(carTypeField);

		int result = JOptionPane.showConfirmDialog(null, panel, "Add a Car", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String make = makeField.getText();
			String model = modelField.getText();
			String color = colorField.getText();
			int year;
			double price;

			try {
				year = Integer.parseInt(yearField.getText());
				price = Double.parseDouble(priceField.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid input. Year and price must be numeric values.");
				return;
			}

			String type = carTypeField.getText();

			try {
				if (dealership.addVehicle(new Car(make, model, color, year, price, type)))
					JOptionPane.showMessageDialog(null, "Car has been added successfully.");
				else
					JOptionPane.showMessageDialog(null, "Sorry, the car has not been added.");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "An error occurred while adding the car: " + e.getMessage());
			}
		}
	}

	/**
	 * Display a dialog for adding a motorcycle to the dealership
	 * Collects motorcycle details from user input and adds the motorcycle to inventory
	 */
	private void addMotorcycleMenu() {
		JTextField makeField = new JTextField();
		JTextField modelField = new JTextField();
		JTextField colorField = new JTextField();
		JTextField yearField = new JTextField();
		JTextField priceField = new JTextField();
		JTextField handlebarTypeField = new JTextField();

		JPanel panel = new JPanel(new GridLayout(0, 1));

		panel.add(new JLabel("Make:"));
		panel.add(makeField);
		panel.add(new JLabel("Model:"));
		panel.add(modelField);
		panel.add(new JLabel("Color:"));
		panel.add(colorField);
		panel.add(new JLabel("Year:"));
		panel.add(yearField);
		panel.add(new JLabel("Price:"));
		panel.add(priceField);
		panel.add(new JLabel("Handlebar Type:"));
		panel.add(handlebarTypeField);

		int result = JOptionPane.showConfirmDialog(null, panel, "Add a Motorcycle", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String make = makeField.getText();
			String model = modelField.getText();
			String color = colorField.getText();
			int year;
			double price;

			try {
				year = Integer.parseInt(yearField.getText());
				price = Double.parseDouble(priceField.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid input. Year and price must be numeric values.");
				return;
			}

			String handlebarType = handlebarTypeField.getText();

			try {
				if (dealership.addVehicle(new Motorcycle(make, model, color, year, price, handlebarType)))
					JOptionPane.showMessageDialog(null, "Motorcycle has been added successfully.");
				else
					JOptionPane.showMessageDialog(null, "Sorry, the motorcycle has not been added.");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "An error occurred while adding the motorcycle: " + e.getMessage());
			}
		}
	}
}
