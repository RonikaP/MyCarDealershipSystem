package carDealership;

import java.io.Serializable;

/**
 * Car Class
 *
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public class Car extends Vehicle implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;

	/**
	 * Constructor for the Car class
	 *
	 * @param make  - the manufacturer of the car
	 * @param model - the model name of the car
	 * @param color - the exterior color of the car
	 * @param year  - the manufacturing year of the car
	 * @param price - the selling price of the car
	 * @param type  - the car type (sedan, SUV, coupe, etc.)
	 */
	public Car(String make, String model, String color, int year, double price, String type) {
		super(make, model, color, year, price);
		this.type = type;
	}

	/**
	 * Copy constructor for the Car class
	 *
	 * @param c - the Car object to copy
	 */
	public Car(Car c) {
		this(c.make, c.model, c.color, c.year, c.price, c.type);
	}

	/**
	 * Getter method for the car type
	 *
	 * @return the car type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter method for the car type
	 *
	 * @param type - the new car type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Display detailed information about the car
	 * Prints the car's ID, name, color, type, and price to the console
	 */
	public void displayInfo() {
		String carName = make + " " + model + " " + year;
		System.out.println("ID: " + id);
		System.out.println("Car: " + carName);
		System.out.println("Color: " + color);
		System.out.println("Type: " + type);
		System.out.println("Price: " + price + " SAR");
	}

	/**
	 * Return a string representation of the car
	 * 
	 * @return formatted string with all car details
	 */
	public String toString() {
		return super.toString() + "\nType: " + type;
	}
}
