package persistance;

import java.io.Serializable;
import java.sql.SQLException;

public class DealershipLayer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String m_name;
	private String m_location;
	private int m_capacity;

	public DealershipLayer() {
	}

	public DealershipLayer(String name, String location, int capacity) throws SQLException {
		m_name = name;
		m_location = location;
		m_capacity = capacity;

		DBManager.getInstance().runInsert("INSERT INTO dealerships " + "(name, location, capacity) " + "VALUES ('"
				+ name + "', '" + location + "', " + Integer.toString(capacity) + ");");
	}

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

	public String getNname() {
		return m_name;
	}

	public String getLocation() {
		return m_location;
	}

	public int getCapacity() {
		return m_capacity;
	}

}
