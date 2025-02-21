package com.integration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class Db_handlerbot
{
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	String fileName = "com/integration/config.properties";
	public Properties configProps;
	private static String DB_URL = "";

	Db_handlerbot()
	{
		
		try
		{
			configProps = loadPropertyFromClasspath(fileName, this.getClass());
			DB_URL = "jdbc:mysql://localhost/Bot?user="+configProps.getProperty("sql_username") +"&password="+configProps.getProperty("sql_password");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		DB_URL="jdbc:mysql://localhost/"+configProps.getProperty("sql_dbname")+"/?user="+configProps.getProperty("sql_username").trim()+"/&password="+configProps.getProperty("sql_password").trim();
	}

	// get HIpchatapi's basic stuff
	public Hipchat_basiccheckbot GetBasicStuff(String hipchatemail)
	{
//		System.out.println(configProps.getProperty("sql_dbname"));
//		System.out.println(configProps.getProperty("sql_username"));

		Hipchat_basiccheckbot bb = new Hipchat_basiccheckbot();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " +configProps.getProperty("dbtablename")+ " where hipchatemail = '" + hipchatemail + "'");
			resultSet.next();
			bb.setAccess_token(resultSet.getString("token"));
			bb.setEmail(resultSet.getObject("email").toString());
			bb.setnotify_token(resultSet.getString("notifytoken"));
			bb.sethipchatEmail(resultSet.getString("hipchatemail"));
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
	public boolean isAuthorized(String email, String hipchatemail)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("select * from "+configProps.getProperty("dbtablename")+" where email = ? and hipchatemail = ?");
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, hipchatemail);
			resultSet = preparedStatement.executeQuery();
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
	public boolean StorageHandler(String email, String token, String notifytoken, String hipchatemail)
	{
		// check if token already exists or not
		if (CheckToken(email))
		{
			return StoreToken(email, token, notifytoken, hipchatemail);
		} else
		{
			return UpdateToken(email, token, notifytoken, hipchatemail);
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
			resultSet = statement.executeQuery("select email from "+configProps.getProperty("dbtablename")+" where email='" + email + "'");
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
	private boolean StoreToken(String email, String token, String notifytoken, String hipchatemail)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("insert into "+configProps.getProperty("dbtablename")+" values (default, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, token);
			preparedStatement.setBoolean(3, true);
			preparedStatement.setString(4, notifytoken);
			preparedStatement.setString(5, hipchatemail);
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
	private boolean UpdateToken(String email, String token, String notifytoken, String hipchatemail)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("default, update "+configProps.getProperty("dbtablename")+" set token = ?, state = ?, notifytoken = ?, hipchatemail = ? where email=?");
			preparedStatement.setString(1, token);
			preparedStatement.setBoolean(2, true);
			preparedStatement.setString(3, notifytoken);
			preparedStatement.setString(4, hipchatemail);
			preparedStatement.setString(5, email);
			preparedStatement.executeUpdate();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// delete user data
	public boolean Delete(String email)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(DB_URL);
			statement = connect.createStatement();
			preparedStatement = connect.prepareStatement("DELETE FROM "+configProps.getProperty("dbtablename")+" WHERE email = ?");
			preparedStatement.setString(1, email);
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
	public static Properties loadPropertyFromClasspath(String fileName, Class<?> type) throws IOException
	{

		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;

	}
}