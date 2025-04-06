package persistance;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access layer for dealership information
 * Manages persistence of dealership data in the database
 *
 * @author Ronika Patel (40156217)
 * @author Nazim Chaib Cherif-Baza (40017992)
 * @author Andrea Delgado Anderson (40315869)
 * @author Grace Pan (40302283)
 * @author Bao Tran Nguyen (40257379)
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public class DealershipLayer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String m_name;
	private String m_location;
	private int m_capacity;

	private int dealershipId;

	/**
	 * Default constructor for the DealershipLayer class
	 */
	public DealershipLayer() {
	}

	/**
	 * Constructor for the DealershipLayer class
	 * Creates a new dealership record in the database
	 *
	 * @param name - the name of the dealership
	 * @param location - the location of the dealership
	 * @param capacity - the inventory capacity of the dealership
	 * @throws SQLException if a database access error occurs
	 */
	public DealershipLayer(String name, String location, int capacity) throws SQLException {
		m_name = name;
		m_location = location;
		m_capacity = capacity;
		this.dealershipId = 1;

		String query = "INSERT INTO dealerships (name, location, capacity) VALUES (?, ?, ?)";
        PreparedStatement statement = DBManager.getInstance().Connection().prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        statement.setString(2, location);
        statement.setInt(3, capacity);
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.dealershipId = generatedKeys.getInt(1);
        }
    }

    /**
     * Getter method for the dealership ID
     *
     * @return the unique identifier of the dealership in the database
     */
    public int getDealershipId() {
        return dealershipId;
    }

	/**
	 * Check if a dealership record exists in the database
	 * If found, sets the local attributes to match the database record
	 *
	 * @return true if a dealership record exists, false otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public boolean existsAndSet() throws SQLException {
		var resultSet = DBManager.getInstance().runQuery("SELECT * FROM dealerships");
		boolean dealershipFound = false;

		while (resultSet.next()) {
			dealershipFound = true;
			m_name = resultSet.getString("name");
			m_location = resultSet.getString("location");
			m_capacity = resultSet.getInt("capacity");
		}

		return dealershipFound;
	}

	/**
	 * Getter method for the dealership name
	 *
	 * @return the name of the dealership
	 */
	public String getNname() {
		return m_name;
	}

	/**
	 * Getter method for the dealership location
	 *
	 * @return the location of the dealership
	 */
	public String getLocation() {
		return m_location;
	}

	/**
	 * Getter method for the dealership capacity
	 *
	 * @return the inventory capacity of the dealership
	 */
	public int getCapacity() {
		return m_capacity;
	}
}
