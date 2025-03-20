package persistance;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DealershipLayer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String m_name;
	private String m_location;
	private int m_capacity;

	private int dealershipId;


	public DealershipLayer() {
	}

	public DealershipLayer(String name, String location, int capacity) throws SQLException {
		m_name = name;
		m_location = location;
		m_capacity = capacity;
		this.dealershipId = 1;

		String query = "INSERT INTO dealerships (name, location, capacity) VALUES (?, ?, ?)";
        PreparedStatement statement = DBManager.getInstance().getConnection().prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, name);
        statement.setString(2, location);
        statement.setInt(3, capacity);
        statement.executeUpdate();

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.dealershipId = generatedKeys.getInt(1);
        }
    }

    public int getDealershipId() {
        return dealershipId;
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
