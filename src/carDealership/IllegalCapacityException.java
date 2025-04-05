package carDealership;

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
