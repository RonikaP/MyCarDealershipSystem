package carDealership;

/**
 * Car Dealership System
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
public class IllegalCapacityException extends RuntimeException {

	private static final long serialVersionUID = 8128614141471165676L;

	/**
	 * Constructor for the IllegalCapacityException class
	 * Thrown when attempting to create a dealership with an invalid inventory capacity
	 * This exception is triggered when the specified capacity is out of the allowed range
	 */
	public IllegalCapacityException() {
		super();
	}
}
