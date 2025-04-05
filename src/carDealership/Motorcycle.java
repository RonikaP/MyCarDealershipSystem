package carDealership;

import java.io.Serializable;

/**
 * Car Dealership System
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
public class Motorcycle extends Vehicle implements Serializable {
	private static final long serialVersionUID = 1L;
	private String handlebarType;

	/**
	 * Constructor for the Motorcycle class
	 *
	 * @param make - the manufacturer of the motorcycle
	 * @param model - the model name of the motorcycle
	 * @param color - the color of the motorcycle
	 * @param year - the manufacturing year of the motorcycle
	 * @param price - the price of the motorcycle in SAR (Saudi Arabian Riyal)
	 * @param handlebarType - the type of handlebars on the motorcycle
	 */
	public Motorcycle(String make, String model, String color, int year, double price, String handlebarType) {
		super(make, model, color, year, price);
		this.handlebarType = handlebarType;
	}

	/**
	 * Copy constructor that creates a new Motorcycle instance with the same attributes as another Motorcycle
	 *
	 * @param m - the Motorcycle object to copy
	 */
	public Motorcycle(Motorcycle m) {
		this(m.make, m.model, m.color, m.year, m.price, m.handlebarType);
	}

	/**
	 * Display detailed information about this motorcycle to the console
	 * This method is used for providing vehicle information to users
	 * based on role-based permissions as specified in project requirements
	 */
	public void displayInfo() {
		String motorcycleName = make + " " + model + " " + year;
		System.out.println("ID: " + id);
		System.out.println("Motorcycle: " + motorcycleName);
		System.out.println("Color: " + color);
		System.out.println("Handlebar type: " + handlebarType);
		System.out.println("Price: " + price + " SAR");
	}

	/**
	 * Getter method for the motorcycle handlebar type
	 *
	 * @return the handlebar type of the motorcycle
	 */
	public String getHandlebarType() {
		return handlebarType;
	}

	/**
	 * Setter method for the motorcycle handlebar type
	 *
	 * @param handlebarType - the new handlebar type to set
	 */
	public void setHandlebarType(String handlebarType) {
		this.handlebarType = handlebarType;
	}

	/**
	 * Return a string representation of this motorcycle, including all attributes
	 * This method is helpful for vehicle grouping and display functionality
	 * 
	 * @return a string containing all motorcycle information
	 */
	public String toString() {
		return super.toString() + "\nHandelbarType: " + handlebarType;
	}
}
