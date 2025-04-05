package carDealership;

import java.io.Serializable;

/**
 * Vehicle Class
 *
 * @author Ronika Patel (40156217)
 * @author Nazim Chaib Cherif-Baza (40017992)
 * @author Andrea Delgado Anderson (40315869)
 * @author Grace Pan (40302283)
 * @author Bao Tran Nguyen (40257379)
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public abstract class Vehicle implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String make, model, color;
	protected int year;
	protected double price;
	protected int id;
	// private static int nextId;

	/**
	 * Constructor for the Vehicle class
	 * Creates a vehicle with the specified attributes
	 *
	 * @param make - the manufacturer of the vehicle
	 * @param model - the model name of the vehicle
	 * @param color - the color of the vehicle
	 * @param year - the manufacturing year of the vehicle
	 * @param price - the price of the vehicle
	 */
	public Vehicle(String make, String model, String color, int year, double price) {
		this.make = make;
		this.model = model;
		this.color = color;
		this.year = year;
		setPrice(price);
	}

	/**
	 * Abstract method to display vehicle information
	 * Must be implemented by all vehicle subclasses
	 */
	public abstract void displayInfo();

	/**
	 * Return a string representation of this vehicle
	 * Includes basic vehicle information (ID, make, model, color, year, price)
	 *
	 * @return a string containing basic vehicle information
	 */
	public String toString() {
		return "ID: " + id + "\nMake: " + make + "\nModel: " + model + "\nColor: " + color + "\nYear: " + year
				+ "\nPrice: " + price;
	}

	/**
	 * Getter method for the vehicle make
	 *
	 * @return the manufacturer of the vehicle
	 */
	public String getMake() {
		return make;
	}

	/**
	 * Setter method for the vehicle make
	 *
	 * @param make - the new manufacturer to set
	 */
	public void setMake(String make) {
		this.make = make;
	}

	/**
	 * Getter method for the vehicle model
	 *
	 * @return the model name of the vehicle
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Setter method for the vehicle model
	 *
	 * @param model - the new model name to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Getter method for the vehicle color
	 *
	 * @return the color of the vehicle
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Setter method for the vehicle color
	 *
	 * @param color - the new color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Getter method for the vehicle year
	 *
	 * @return the manufacturing year of the vehicle
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Setter method for the vehicle year
	 *
	 * @param year - the new manufacturing year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Getter method for the vehicle price
	 *
	 * @return the price of the vehicle
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Setter method for the vehicle price
	 * Ensures the price is not negative, setting it to 0 if a negative value is provided
	 *
	 * @param price - the new price to set
	 */
	public void setPrice(double price) {
		if (price < 0) {
			price = 0;
		}
		this.price = price;
	}

	/**
	 * Getter method for the vehicle ID
	 *
	 * @return the unique identifier of the vehicle
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter method for the vehicle ID
	 *
	 * @param id - the new ID to set
	 */
	public void setId(int id) {
		this.id = id;
	}
}
