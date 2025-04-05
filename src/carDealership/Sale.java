package carDealership;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Car Dealership System
 *
 * @author Ronika Patel (40156217)
 * @author Nazim Chaib Cherif-Baza (40017992)
 * @author Andrea Delgado Anderson (40315869)
 * @author Grace Pan (40302283)
 * @author Bao Tran Nguyen (40257379)
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public class Sale implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vehicle vehicle;
	private String buyerName;
	private String buyerContact;
	private LocalDate saleDate;

	/**
	 * Constructor for the Sale class
	 * Creates a record of a vehicle sale transaction
	 *
	 * @param vehicle - the vehicle that was sold
	 * @param buyerName - the name of the buyer
	 * @param buyerContact - the contact information of the buyer
	 * @param saleDate - the date when the sale was completed
	 */
	public Sale(Vehicle vehicle, String buyerName, String buyerContact, LocalDate saleDate) {
		this.vehicle = vehicle;
		this.buyerName = buyerName;
		this.buyerContact = buyerContact;
		this.saleDate = saleDate;
	}

	/**
	 * Getter method for the vehicle
	 *
	 * @return the vehicle associated with this sale
	 */
	public Vehicle getVehicle() {
		return vehicle;
	}

	/**
	 * Setter method for the vehicle
	 *
	 * @param vehicle - the new vehicle to associate with this sale
	 */
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * Getter method for the buyer's name
	 *
	 * @return the name of the buyer
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * Setter method for the buyer's name
	 *
	 * @param buyerName - the new buyer name to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * Getter method for the buyer's contact information
	 *
	 * @return the contact information of the buyer
	 */
	public String getBuyerContact() {
		return buyerContact;
	}

	/**
	 * Setter method for the buyer's contact information
	 *
	 * @param buyerContact - the new buyer contact information to set
	 */
	public void setBuyerContact(String buyerContact) {
		this.buyerContact = buyerContact;
	}

	/**
	 * Getter method for the sale date
	 *
	 * @return the date when the sale was completed
	 */
	public LocalDate getSaleDate() {
		return saleDate;
	}

	/**
	 * Setter method for the sale date
	 *
	 * @param saleDate - the new sale date to set
	 */
	public void setSaleDate(LocalDate saleDate) {
		this.saleDate = saleDate;
	}
}
