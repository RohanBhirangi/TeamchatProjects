package com.statuspage.bot;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Aniruddha Varshney
 *
 */

public class Database_Handler
{
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	
	private static String DB_URL = "jdbc:mysql://localhost:3306/StatusPage?user=root&password=";
	//private static String DB_URL = "jdbc:mysql://localhost:3306/Bot?user=tcinterns&password=PakyovBosh7";

	
	public StatusPage_basics GetBasicStuff(String email)
	{
		StatusPage_basics bb = new StatusPage_basics();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from authorized_statuspage where email = '" + email + "'");
			resultSet.next();
			bb.setAccess_token(resultSet.getString("access_token"));
			bb.setEmail(resultSet.getObject("email").toString());
			return bb;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			close();
		}
	}

	// check if data exists in the server against that email
	public boolean isAuthorized(String email)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select email from authorized_statuspage where email='" + email + "'");
			// check if result set is empty or not
			while (resultSet.next())
			{
				return true;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			close();
		}
		// default case
		return false;
	}

	// You need to close the resultSet
	private void close()
	{
		try
		{
			if (resultSet != null)
			{
				resultSet.close();
			}

			if (statement != null)
			{
				statement.close();
			}

			if (connect != null)
			{
				connect.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// storing data handler
	public boolean StorageHandler(String email, String token)
	{
		// check if token already exists or not
		if (CheckToken(email))
		{
			return StoreToken(email, token);
		} else
		{
			return UpdateToken(email, token);
		}
	}

	// check if data exists in the server against that email
	// true means that data doesn;t exist
	private boolean CheckToken(String email)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select email from authorized_statuspage where email='" + email + "'");
			// check if result set is empty or not
			while (resultSet.next())
			{
				return false;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			close();
		}
		// default case
		return true;
	}

	// set the authentication data into the table
	private boolean StoreToken(String email, String token)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("insert into authorized_statuspage values (default, ?, ?, ?)");
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, token);
			preparedStatement.setBoolean(3, true);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			close();
		}
	}

	// update the authentication data into the table
	private boolean UpdateToken(String email, String token)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("update authorized_statuspage set token = ?, state = ? where email = ?");
			preparedStatement.setString(1, token);
			preparedStatement.setBoolean(2, true);
			preparedStatement.setString(3, email);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public void DeleteData(String email){
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			statement.executeUpdate("DELETE FROM authorized_statuspage WHERE email='" + email + "'");
		
			
		
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			close();
		}
		// default case
	}
	

}