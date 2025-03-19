package carDealership;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Frame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4235592661347719465L;
	private JFrame jf1;
	// ---------Buttons--------
	private JButton m_displayAllButton, m_addVehicleButton, m_sellVehicleButton, m_removeVehicleButton,
			m_editVehicleButton, m_salesHistoryButton, m_searchCarButton, m_dealershipInfoButton;

	// =========Files==========
	private JTextArea textArea;
	private JScrollPane scrollPane;
	// ---------JMenu -----------------
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem save;
	private JMenuItem deleteDealership;

	// -----------Labels---------
	private JLabel jl1;

	public Frame() {
		jf1 = new JFrame();

		// ------------- Jmenu ------------
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		save = new JMenuItem("save");
		deleteDealership = new JMenuItem("delete dealership");

		fileMenu.add(save);
		fileMenu.add(deleteDealership);

		menuBar.add(fileMenu);

		jf1.setJMenuBar(menuBar);

		// ----------jbuttons stuff-------------
		m_displayAllButton = new JButton("Display all");
		m_addVehicleButton = new JButton("Add a vehicle");
		m_sellVehicleButton = new JButton("Sell a vehicle");
		m_removeVehicleButton = new JButton("Remove a vehicle");
		m_editVehicleButton = new JButton("Edit a vehicle");
		m_salesHistoryButton = new JButton("Sales history");
		m_searchCarButton = new JButton("Search car (Budget)");
		m_dealershipInfoButton = new JButton("Dealership info");

		m_displayAllButton.setForeground(new java.awt.Color(12, 101, 72));
		m_sellVehicleButton.setForeground(new java.awt.Color(1, 121, 72));
		m_removeVehicleButton.setForeground(Color.red);

		m_displayAllButton.setBackground(Color.decode("#F09EA7"));
		m_addVehicleButton.setBackground(Color.decode("#F6CA94"));
		m_sellVehicleButton.setBackground(Color.decode("#FAFABE"));
		m_removeVehicleButton.setBackground(Color.decode("#C1EBC0"));
		m_editVehicleButton.setBackground(Color.decode("#C7CAFF"));
		m_salesHistoryButton.setBackground(Color.decode("#CDABEB"));
		m_searchCarButton.setBackground(Color.decode("#F6C2F3"));
		m_dealershipInfoButton.setBackground(Color.decode("#FFD700"));

		m_displayAllButton.setBounds(20, 40, 150, 70);
		m_addVehicleButton.setBounds(180, 40, 150, 70);
		m_sellVehicleButton.setBounds(340, 40, 150, 70);
		m_removeVehicleButton.setBounds(500, 40, 150, 70);
		m_editVehicleButton.setBounds(660, 40, 150, 70);
		m_salesHistoryButton.setBounds(820, 40, 150, 70);
		m_searchCarButton.setBounds(980, 40, 150, 70);

		m_dealershipInfoButton.setBounds(500, 120, 150, 50);

		// ------------Labels-----------
		jl1 = new JLabel("");

		// ----------MENU------------
		jf1.setJMenuBar(menuBar);
		// -------------------------

		jf1.setTitle("Dealership system");
		jf1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf1.getContentPane().setBackground(Color.decode("#ADD8E6"));

		jf1.setVisible(true);
		jf1.setBounds(0, 0, 1170, 600);
		jf1.setResizable(false);
		jf1.setIconImage(Toolkit.getDefaultToolkit().getImage(FirstLaunchPage.class.getResource("/images/icon.jpg")));

		jf1.add(m_displayAllButton);
		jf1.add(m_addVehicleButton);
		jf1.add(m_sellVehicleButton);
		jf1.add(m_removeVehicleButton);
		jf1.add(m_editVehicleButton);
		jf1.add(m_salesHistoryButton);
		jf1.add(m_searchCarButton);
		jf1.add(m_dealershipInfoButton);

		jf1.add(jl1); // do not remove!

		// ---------------------------
		m_displayAllButton.addActionListener(this);
		m_addVehicleButton.addActionListener(this);
		m_sellVehicleButton.addActionListener(this);
		m_removeVehicleButton.addActionListener(this);
		m_editVehicleButton.addActionListener(this);
		m_salesHistoryButton.addActionListener(this);
		m_searchCarButton.addActionListener(this);
		m_dealershipInfoButton.addActionListener(this);

		save.addActionListener(this);
		deleteDealership.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == m_displayAllButton) {
			displayInventory();
		}
		if (e.getSource() == m_addVehicleButton) {
			SwingUtilities.invokeLater(() -> {
				if (!(Main.m_dealership.isFull())) {
					VehicleMenu VehicleMenu = new VehicleMenu(Main.m_dealership);
					VehicleMenu.setVisible(true);
				} else
					JOptionPane.showMessageDialog(null, "Sorry, your inventory is Full!");
			});
			System.out.println("Adding...");
		}
		if (e.getSource() == m_sellVehicleButton) {
			sellVehicleMenu();
		}
		if (e.getSource() == m_removeVehicleButton) {
			removeVehicleMenu();
		}
		if (e.getSource() == m_editVehicleButton) {
			editVehicleMenu();
		}
		if (e.getSource() == m_salesHistoryButton) {
			displaySalesHistory();
		}
		if (e.getSource() == m_searchCarButton) {
			budgetCarGUI();
		}

		if (e.getSource() == save) {
			try {
				Main.save();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if (e.getSource() == deleteDealership) {
			int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the dealership?",
					"Confirmation", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				System.out.println("On bro I'm speaking fax");
				File saveFile = new File("save.data");
				if (saveFile.exists()) {
					saveFile.delete();
				}

				jf1.dispose();
			}
		}
		if (e.getSource() == m_dealershipInfoButton) {
			displayDealerInfo();
		}

	}

	private void displayDealerInfo() {
		JTextArea textArea = new JTextArea();
		textArea.setText(Main.m_dealership.getInfoGUI());
		textArea.setEditable(false);

		// Wrap the text area in a scroll pane
		JScrollPane scrollPane = new JScrollPane(textArea);

		// Create a dialog to display the information
		JOptionPane.showMessageDialog(null, scrollPane, "All Information", JOptionPane.PLAIN_MESSAGE);

	}

	private void displayInventory() {
		if (Main.m_dealership.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Inventory is empty!");
		} else {
			textArea = new JTextArea(Main.m_dealership.displayAlls());
			textArea.setEditable(false);
			scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(400, 300));
			JOptionPane.showMessageDialog(null, scrollPane, "Inventory", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void sellVehicleMenu() {
		try {
			String idString = JOptionPane.showInputDialog(null, "Enter the id of the vehicle:");
			if (idString == null) { // checks if no input
				return;
			}
			int id = Integer.parseInt(idString);
			if (Main.m_dealership.getIndexFromId(id) == -1) {
				JOptionPane.showMessageDialog(null, "Vehicle not found!");
				return;
			}
			String buyerName = JOptionPane.showInputDialog(null, "Enter the buyer's name:");
			String buyerContact = JOptionPane.showInputDialog(null, "Enter the buyer's contact:");
			Vehicle vehicle = Main.m_dealership.getVehicleFromId(id);

			if (Main.m_dealership.sellVehicle(vehicle, buyerName, buyerContact)) {
				JOptionPane.showMessageDialog(null, "Vehicle sold successfully.");
			} else {
				JOptionPane.showMessageDialog(null, "Couldn't sell vehicle.");
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
		}
	}

	private void displaySalesHistory() {
		textArea = new JTextArea(Main.m_dealership.showSalesHistory());
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		JOptionPane.showMessageDialog(null, scrollPane, "Sales History", JOptionPane.PLAIN_MESSAGE);
	}

	private void removeVehicleMenu() {
		try {
			String idString = JOptionPane.showInputDialog(null, "Enter the id of the vehicle:");
			if (idString == null) { // checks if no input
				return;
			}
			int id = Integer.parseInt(idString);

			if (Main.m_dealership.getIndexFromId(id) == -1) {
				JOptionPane.showMessageDialog(null, "Vehicle not found!");
			} else {
				Vehicle vehicle = Main.m_dealership.getVehicleFromId(id);
				int confirm = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete this vehicle\nwith id: " + id, "Confirm Deletion",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					if (Main.m_dealership.removeVehicle(vehicle)) {
						JOptionPane.showMessageDialog(null, "Vehicle removed successfully.");
					} else {
						JOptionPane.showMessageDialog(null, "Couldn't remove vehicle.");
					}
				}
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
		}
	}

	private void editVehicleMenu() {
		try {
			String idString = JOptionPane.showInputDialog(null, "Enter the id of the vehicle:");

			if (idString == null) { // checks if no input
				return;
			}

			int id = Integer.parseInt(idString);

			if (Main.m_dealership.getIndexFromId(id) == -1) { // check if exist
				JOptionPane.showMessageDialog(null, "Vehicle not found!");
				return;
			}
			Vehicle vehicle = Main.m_dealership.getVehicleFromId(id);

			JTextField makeField = new JTextField();
			JTextField modelField = new JTextField();
			JTextField colorField = new JTextField();
			JTextField yearField = new JTextField();
			JTextField priceField = new JTextField();
			JTextField typeField = new JTextField();
			JTextField handlebarField = new JTextField();

			JPanel editPanel = new JPanel();
			editPanel.setLayout(new GridLayout(0, 2));

			if (vehicle instanceof Car) {
				Car car = (Car) vehicle;

				editPanel.add(new JLabel("Make:"));
				makeField.setText(car.getMake());
				editPanel.add(makeField);

				editPanel.add(new JLabel("Model:"));
				modelField.setText(car.getModel());
				editPanel.add(modelField);

				editPanel.add(new JLabel("Color:"));
				colorField.setText(car.getColor());
				editPanel.add(colorField);

				editPanel.add(new JLabel("Year:"));
				yearField.setText(String.valueOf(car.getYear()));
				editPanel.add(yearField);

				editPanel.add(new JLabel("Price:"));
				priceField.setText(String.valueOf(car.getPrice()));
				editPanel.add(priceField);

				editPanel.add(new JLabel("Type:"));
				typeField.setText(car.getType());
				editPanel.add(typeField);

			} else if (vehicle instanceof Motorcycle) {
				Motorcycle motorcycle = (Motorcycle) vehicle;

				editPanel.add(new JLabel("Make:"));
				makeField.setText(motorcycle.getMake());
				editPanel.add(makeField);

				editPanel.add(new JLabel("Model:"));
				modelField.setText(motorcycle.getModel());
				editPanel.add(modelField);

				editPanel.add(new JLabel("Color:"));
				colorField.setText(motorcycle.getColor());
				editPanel.add(colorField);

				editPanel.add(new JLabel("Year:"));
				yearField.setText(String.valueOf(motorcycle.getYear()));
				editPanel.add(yearField);

				editPanel.add(new JLabel("Price:"));
				priceField.setText(String.valueOf(motorcycle.getPrice()));
				editPanel.add(priceField);

				editPanel.add(new JLabel("Handlebar Type:"));
				handlebarField.setText(motorcycle.getHandlebarType());
				editPanel.add(handlebarField);
			}

			int option = JOptionPane.showConfirmDialog(null, editPanel, "Edit Vehicle", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) { // edit and set
				if (vehicle instanceof Car) {
					Car car = (Car) vehicle;
					car.setMake(makeField.getText());
					car.setModel(modelField.getText());
					car.setColor(colorField.getText());
					car.setYear(Integer.parseInt(yearField.getText()));
					car.setPrice(Double.parseDouble(priceField.getText()));
					car.setType(typeField.getText());
				} else if (vehicle instanceof Motorcycle) {
					Motorcycle motorcycle = (Motorcycle) vehicle;
					motorcycle.setMake(makeField.getText());
					motorcycle.setModel(modelField.getText());
					motorcycle.setColor(colorField.getText());
					motorcycle.setYear(Integer.parseInt(yearField.getText()));
					motorcycle.setPrice(Double.parseDouble(priceField.getText()));
					motorcycle.setHandlebarType(handlebarField.getText());
				}
				JOptionPane.showMessageDialog(null, "Vehicle edited successfully.");
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid input. Year and price must be numeric values.");
		}
	}

	public void budgetCarGUI() { // Search a CAR WITHIN a specific BUDGET.

		String budgetText = JOptionPane.showInputDialog(null, "Enter Budget:");
		if (budgetText == null) {
			return;
		}

		try {

			double budget = Double.parseDouble(budgetText);
			if (budget < 0) {
				JOptionPane.showMessageDialog(null, "Invalid input. Please enter a positive number.");
				return;
			}

			Car[] carsWithinBudget = Main.m_dealership.carsWithinBudget(budget);
			if (carsWithinBudget.length == 0) {
				JOptionPane.showMessageDialog(null, "No cars found within the budget of " + budget + " SAR.");
			} else {
				StringBuilder message = new StringBuilder();
				for (Car car : carsWithinBudget) {
					if (car != null && car.getPrice() <= budget) {
						message.append(car.toString() + "\n--------------------").append("\n");
					}
				}
				if (message.length() == 0) {
					JOptionPane.showMessageDialog(null, "No cars found within the budget of " + budget);
				} else {
					JTextArea resultArea = new JTextArea(message.toString());
					scrollPane = new JScrollPane(resultArea);
					scrollPane.setPreferredSize(new Dimension(400, 300));
					JOptionPane.showMessageDialog(null, scrollPane, "Cars within Budget", JOptionPane.PLAIN_MESSAGE);
				}
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
		}
	}

}
