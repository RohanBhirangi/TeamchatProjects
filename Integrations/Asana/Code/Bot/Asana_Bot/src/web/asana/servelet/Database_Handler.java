package web.asana.servelet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



/**
 * @author Aniruddha varshney
 *
 */
public class Database_Handler {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private static final String DB_URL = "jdbc:mysql://localhost:3306/Asana?user=webaroo&password=webar00";

	// storing data handler
	public boolean StorageHandler(Token token, String email) {
		// check if token already exists or not
		if (CheckToken(email, token)) {
			return StoreToken(token, email);
		} else {
			return UpdateToken(email, token);
		}
	}

	// check if data exists in the server against that email
	private boolean CheckToken(String email_id, Token token) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			resultSet = statement
					.executeQuery("select email from asana_authorized where email='"
							+ email_id + "'");
			// check if result set is empty or not
			while (resultSet.next()) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		// default case
		return true;
	}

	// set the authentication data into the table
	private boolean StoreToken(Token token, String email_id) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect
					.prepareStatement("insert into asana_authorized values (default, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, email_id);
			preparedStatement.setString(2, token.getAccess_token());
			preparedStatement.setString(3, token.getRefresh_token());
			preparedStatement.setInt(4, token.getExpires_in());
			preparedStatement.setBoolean(5, true);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}
	}

	// update the authentication data into the table
	private boolean UpdateToken(String email, Token token) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect
					.prepareStatement("update asana_authorized set access_token = ?, refresh_token = ?, expires_in = ?, authenticated = ? where email = ?");
			preparedStatement.setString(1, token.getAccess_token());
			preparedStatement.setString(2, token.getRefresh_token());
			preparedStatement.setInt(3, token.getExpires_in());
			preparedStatement.setBoolean(4, true);
			preparedStatement.setString(5, email);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}