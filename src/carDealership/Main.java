package carDealership;

import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import persistance.DealershipLayer;

import java.io.*;
import java.sql.SQLException;

/**
 * Car Dealership System
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
public class Main {
	public static Scanner input = new Scanner(System.in);
	public static Dealership m_dealership;

	/**
	 * Main entry point for the Car Dealership application
	 * Initializes the dealership from saved data or creates a new one
	 * Launches the appropriate GUI interface based on application state
	 *
	 * @param args - command line arguments (not used)
	 * @throws IOException if an I/O error occurs
	 * @throws ClassNotFoundException if the serialized class cannot be found
	 * @throws SQLException if a database access error occurs
	 */
	public static void main(String args[]) throws IOException, ClassNotFoundException, SQLException {
		try {
			var dealershipLayer = new DealershipLayer();
			if (!dealershipLayer.existsAndSet()) {
				SwingUtilities.invokeLater(() -> {
					new FirstLaunchPage();
				});
			} else {
				// Try to load Dealership from save.data
				File saveFile = new File("save.data");
				if (saveFile.exists()) {
					try (FileInputStream fileIn = new FileInputStream(saveFile);
						 ObjectInputStream objIn = new ObjectInputStream(fileIn)) {
						m_dealership = (Dealership) objIn.readObject();
					} catch (Exception e) {
						System.err.println("Error loading dealership from save.data: " + e.getMessage());
						// Fallback to creating a new Dealership
						m_dealership = new Dealership(dealershipLayer.getNname(), dealershipLayer.getLocation(), dealershipLayer.getCapacity());
					}
				} else {
					// If save.data doesn't exist, create a new Dealership
					m_dealership = new Dealership(dealershipLayer.getNname(), dealershipLayer.getLocation(), dealershipLayer.getCapacity());
				}
				SwingUtilities.invokeLater(() -> {
					LoginFrame loginFrame = new LoginFrame(m_dealership);
					loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					loginFrame.setVisible(true);
				});
			}
		} catch (Exception e) {
			System.err.println("Error in main: " + e.getMessage());
		}
	}

	/**
	 * Display a menu for adding vehicles to the dealership
	 * Provides options to add either a car or motorcycle
	 */
	public static void addVehicleMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Add a Vehicle");
		System.out.println("\nChoose an option:");
		System.out.println("1. Car");
		System.out.println("2. Motorcycle");
		System.out.println("3. Exit");

		String choice = input.nextLine();

		switch (choice) {
		case "1":
			addCarMenu();
			break;
		case "2":
			addMotorcycleMenu();
			break;
		case "3":
			return;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	/**
	 * Display a menu for adding a car to the dealership
	 * Collects car details from user input and adds the car to inventory
	 */
	public static void addCarMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Add a Car");

		System.out.print("\nEnter the make: ");
		String make = input.nextLine();

		System.out.print("Enter the model: ");
		String model = input.nextLine();

		System.out.print("Enter the color: ");
		String color = input.nextLine();

		System.out.print("Enter the year: ");
		int year = input.nextInt();

		System.out.print("Enter the price: ");
		double price = input.nextDouble();

		System.out.print("Enter the type: ");
		input.nextLine();
		String type = input.nextLine();

		try {
			if (m_dealership.addVehicle(new Car(make, model, color, year, price, type))) {
				System.out.println("Car added succesfully.");
			} else {
				System.out.println("Couldn't add car.");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while adding the car: " + e.getMessage());
		}
	}

	/**
	 * Display a menu for adding a motorcycle to the dealership
	 * Collects motorcycle details from user input and adds it to inventory
	 */
	public static void addMotorcycleMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Add a Motorcycle");

		System.out.print("\nEnter the make: ");
		String make = input.nextLine();

		System.out.print("Enter the model: ");
		String model = input.nextLine();

		System.out.print("Enter the color: ");
		String color = input.nextLine();

		System.out.print("Enter the year: ");
		int year = input.nextInt();

		System.out.print("Enter the price: ");
		double price = input.nextDouble();
		input.nextLine();

		System.out.print("Enter the handlebar type: ");
		String handlebarType = input.nextLine();

		try {
			if (m_dealership.addVehicle(new Motorcycle(make, model, color, year, price, handlebarType))) {
				System.out.println("Motorcycle added successfully.");
			} else {
				System.out.println("Couldn't add Motorcycle.");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while adding the motorcycle: " + e.getMessage());
		}
	}

	/**
	 * Display a menu for selling a vehicle from the dealership
	 * Collects vehicle ID and buyer information to process the sale
	 */
	public static void sellVehicleMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Sell a Vehicle");

		System.out.print("\nEnter the id of the vehicle: ");
		int id = input.nextInt();
		input.nextLine();

		if (m_dealership.getIndexFromId(id) == -1) {
			System.out.println("\nVehicle not found!");
			return;
		}

		System.out.print("Enter the buyer's name: ");
		String buyerName = input.nextLine();

		System.out.print("Enter the buyer's contact: ");
		String buyerContact = input.nextLine();

		Vehicle vehicle = m_dealership.getVehicleFromId(id);

		try {
			if (m_dealership.sellVehicle(vehicle, buyerName, buyerContact)) {
				System.out.println("Vehicle sold successfully.");
			} else {
				System.out.println("Couldn't sell vehicle");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while selling the vehicle: " + e.getMessage());
		}
	}

	/**
	 * Display a menu for removing a vehicle from the dealership
	 * Collects vehicle ID and removes the specified vehicle from inventory
	 */
	public static void removeVehicleMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Remove a Vehicle");

		System.out.print("\nEnter the id of the vehicle: ");
		int id = input.nextInt();
		input.nextLine();

		if (m_dealership.getIndexFromId(id) == -1) {
			System.out.println("\nVehicle not found!");
			return;
		}

		Vehicle vehicle = m_dealership.getVehicleFromId(id);

		try {
			if (m_dealership.removeVehicle(vehicle)) {
				System.out.println("Vehicle removed successfully.");
			} else {
				System.out.println("Couldn't remove vehicle");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while removing the vehicle: " + e.getMessage());
		}
	}

	/**
	 * Display a menu for editing a vehicle in the dealership
	 * Collects vehicle ID and redirects to appropriate edit method based on vehicle type
	 */
	public static void editVehicleMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Edit a Vehicle");

		System.out.print("\nEnter the id of the vehicle: ");
		int id = input.nextInt();
		input.nextLine();

		if (m_dealership.getIndexFromId(id) == -1) {
			System.out.println("\nVehicle not found!");
			return;
		}
		System.out.println("\nEnter the new information");
		Vehicle vehicle = m_dealership.getVehicleFromId(id);
		vehicle.displayInfo();

		if (vehicle instanceof Car) {
			carEdit((Car) vehicle);
		}
		if (vehicle instanceof Motorcycle) {
			motorcycleEdit((Motorcycle) vehicle);
		}
	}

	/**
	 * Edit the details of a car vehicle
	 * 
	 * @param c - the car to be edited
	 */
	public static void carEdit(Car c) {
		System.out.print("\nEnter the make: ");
		String make = input.nextLine();

		System.out.print("Enter the model: ");
		String model = input.nextLine();

		System.out.print("Enter the color: ");
		String color = input.nextLine();

		System.out.print("Enter the year: ");
		int year = input.nextInt();

		System.out.print("Enter the price: ");
		double price = input.nextDouble();

		System.out.print("Enter the type: ");
		input.nextLine();
		String type = input.nextLine();

		c.setMake(make);
		c.setModel(model);
		c.setColor(color);
		c.setYear(year);
		c.setPrice(price);
		c.setType(type);
	}

	/**
	 * Edit the details of a motorcycle vehicle
	 * 
	 * @param m - the motorcycle to be edited
	 */
	public static void motorcycleEdit(Motorcycle m) {
		System.out.print("\nEnter the make: ");
		String make = input.nextLine();

		System.out.print("Enter the model: ");
		String model = input.nextLine();

		System.out.print("Enter the color: ");
		String color = input.nextLine();

		System.out.print("Enter the year: ");
		int year = input.nextInt();

		System.out.print("Enter the price: ");
		double price = input.nextDouble();

		System.out.print("Enter the handlebar type: ");
		input.nextLine();
		String handlebarType = input.nextLine();

		m.setMake(make);
		m.setModel(model);
		m.setColor(color);
		m.setYear(year);
		m.setPrice(price);
		m.setHandlebarType(handlebarType);
	}

	/**
	 * Display a menu for searching cars by type
	 * Shows all cars matching the specified type
	 */
	public static void searchCarMenu() {
		if (!(m_dealership.isEmpty())) {

			System.out.println("Enter type: ");
			String s = input.nextLine();

			Car[] v = m_dealership.searchCar(s);
			int total = 0;
			if (v != null) {
				for (int i = 0; i < v.length; i++) {
					if (v[i] != null) {
						v[i].displayInfo();
						System.out.println();
						total++;
					}
				}
			}
			System.out.printf("Total found: [%d]\n", total);
		} else
			System.out.println("Sorry the inventory is empty.");
	}

	/**
	 * Display a menu for changing the console text color
	 * Provides options for different color schemes
	 */
	public static void changeColorMenu() {
		System.out.println("\n-------------------------------------------\n");
		System.out.println("Add a Vehicle");
		System.out.println("\nChoose an option:");
		System.out.println("1. Blue");
		System.out.println("2. Green");
		System.out.println("3. White");
		System.out.println("4. Exit");
		String choice = input.nextLine();

		switch (choice) {
		case "1":
			System.out.println("\u001B[36m"); // Changes color to Blue.
			break;
		case "2":
			System.out.println("\u001B[32m"); // Changes color to Green.

			break;
		case "3":
			System.out.println("\u001B[0m"); // Changes color back to White.

			break;
		case "4":
			return;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	/**
	 * Display a menu for searching cars within a specified budget
	 * Shows the count of cars that fall within the given price range
	 */
	public static void budgetCarMenu() {
		if (!(m_dealership.isEmpty())) {

			System.out.println("Enter budget: ");
			String budget = input.nextLine();

			for (int i = 0; i < budget.length(); i++) {
				if (budget.charAt(i) < 48 || budget.charAt(i) > 57) { // Ascii digits from 0 to 9.
					System.out.println("Invaild Input, Please enter postive numbers only.");
					return;
				}
			}
			{
				int total = m_dealership.carBudget(Double.parseDouble(budget)); // Calling carBudget Method.
				System.out.printf("Total [%d]\n", total);
			}

		} else
			System.out.println("Sorry the inventory is empty.");
	}

	/**
	 * Create a new dealership with the specified attributes
	 * 
	 * @param name - the name of the dealership
	 * @param location - the location of the dealership
	 * @param capacity - the inventory capacity of the dealership
	 * @throws IllegalCapacityException if the capacity is not within valid range (1-100)
	 * @throws SQLException if a database access error occurs
	 */
	public static void createDealership(String name, String location, int capacity)
			throws IllegalCapacityException, SQLException {
		if (capacity < 1 || capacity > 100) {
			throw new IllegalCapacityException();
		}
		m_dealership = new Dealership(name, location, capacity);
	}

	/**
	 * Save the current dealership state to a file
	 * Persists the dealership object to save.data file
	 * 
	 * @throws IOException if an I/O error occurs during saving
	 */
	public static void save() throws IOException {
		File saveFile = new File("save.data");
		FileOutputStream outFileStream = null;
		try {
			outFileStream = new FileOutputStream(saveFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectOutputStream outObjStream = null;
		try {
			outObjStream = new ObjectOutputStream(outFileStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		outObjStream.writeObject(m_dealership);
		outObjStream.close();
	}
}
