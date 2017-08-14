package mainPackage;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.sqlite.SQLiteConfig;

public class DataBase {

	private Connection c = null;
	private PreparedStatement stmt = null;
	
	private void connect()
	{
		try {
			Class.forName("org.sqlite.JDBC");
		    SQLiteConfig config = new SQLiteConfig(); 
		    config.enforceForeignKeys(true);  
		    c = DriverManager.getConnection("jdbc:sqlite:"+Globals.con , config.toProperties());
		    c.setAutoCommit(false);
			}catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      JOptionPane.showConfirmDialog(null, "Can't find DB file \nThe file should be in : " + Globals.con,"",JOptionPane.PLAIN_MESSAGE);
			      System.exit(0);
			}
	}
	public void closeConnection()
	{
		try {
			if(stmt != null && !c.isClosed())
				stmt.close();
			if(c != null && !c.isClosed())
				c.close();
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	    
	}
	
	public boolean checkConnectPermission(String ID , String password)
	{
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("SELECT * FROM Permissions where nickName = ? and Password = ?");
			stmt.setString(1, ID);
			stmt.setString(2, encryptPassword);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return false;
			}
			
			if(!Globals.decrypt(rs.getString("password")).equals(password))
			{
				closeConnection();
				return false;
			}
			
			closeConnection();
			return true;
		}
		catch(Exception e)
		{
			closeConnection();
			return false;
		}
		
	}
	
	public boolean checkPurchasingPermission(String ID , String password)
	{
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("SELECT purchasing FROM Permissions where nickName = ? and Password = ?");
			stmt.setString(1, ID);
			stmt.setString(2, encryptPassword);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return false;
			}
				
			boolean purchasing = rs.getBoolean("purchasing");
			closeConnection();
			return purchasing;
		}
		catch(Exception e)
		{
			closeConnection();
			return false;
		}
		
	}
	
	
	public boolean checkAddOrDeletePermission(String ID , String password)
	{
		
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("SELECT * FROM Permissions where nickName = ? and password = ?");
			stmt.setString(1, ID);
			stmt.setString(2, encryptPassword);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return false;
			}
			
			boolean permission = rs.getBoolean("permission");
			closeConnection();
			return permission;
			
		}
		catch(Exception e)
		{
			closeConnection();
			return false;
		}
	}
	
	public boolean addUser(String nickName , String ID , String password , boolean addOrDeletePermission , boolean purchasing , String signature )
	{
		
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("INSERT INTO Permissions (ID , nickName ,password , permission , purchasing) VALUES (?,?,?,?,?)");
			stmt.setString(1, ID);
			stmt.setString(2, nickName);
			stmt.setString(3, encryptPassword);
			stmt.setBoolean(4, addOrDeletePermission);
			stmt.setBoolean(5, purchasing);
			stmt.executeUpdate();
			
			stmt = c.prepareStatement("INSERT INTO Signatures (user , signature) VALUES (?,?)");
			stmt.setString(1, nickName);
			stmt.setString(2, signature);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			return true;
			
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			closeConnection();
			return false;
		} 
	}
	
	public boolean deleteUser (String ID)
	{
		
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM Permissions Where nickName = ?");
			stmt.setString(1, ID);
			int rowsNumber = stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			return rowsNumber > 0;
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
			return false;
		}
	}
	
	
	public String getSignature(String ID)
	{
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT signature FROM Signatures where user = ?");
			stmt.setString(1, ID);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return "";
			}
			
			String signature = rs.getString("signature");
			closeConnection();
			return signature;
			
		}
		catch(SQLException e)
		{
			return "";
		}
	}
	
	public boolean addSubjectAndBody (int ID , String subject , String body)
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Bodys (ID , subject , body) VALUES (?,?,?)");
			stmt.setInt(1, ID);
			stmt.setString(2, subject);
			stmt.setString(3, body);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			return true;
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
			return false;
		}
	}
	
	public boolean deleteSubjectAndBody(int ID)
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM Bodys Where ID = ?");
			stmt.setInt(1, ID);
			int rows = stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			if(rows == 0)
				return false;
			
			return true;
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			closeConnection();
			return false;
		}
	}
	
	public String[] getSubjectAndBody(int ID)
	{
		String [] subjectAndBody = new String [2];
		
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT subject , body FROM Bodys where ID = ?");
			stmt.setInt(1, ID);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				subjectAndBody[0] = "";
				subjectAndBody[1] = "";
				
				closeConnection();
				return subjectAndBody;
			}
			
			subjectAndBody[0] = rs.getString("subject");
			subjectAndBody[1] = rs.getString("body");
			
			closeConnection();
			return subjectAndBody;
			
		}
		catch(SQLException e)
		{
			subjectAndBody[0] = "";
			subjectAndBody[1] = "";
			closeConnection();
			return subjectAndBody;
		}
		
	}
	
	public Object [][] getAllUsers()
	{
		List<Object[]> usersList = new ArrayList<Object[]>();
		
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT * FROM Permissions");
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				Object [] user = new Object [4];
				user [0] = rs.getString("nickName");
				String admin =  rs.getBoolean("permission") ? "V" : "";
				String purchasing =  (!rs.getBoolean("permission") && rs.getBoolean("purchasing")) ? "V" : "";
				String PPC =  (!rs.getBoolean("permission") && !rs.getBoolean("purchasing"))? "V" : "";
				user [1] = admin;
				user [2] = purchasing;
				user [3] = PPC;
				usersList.add(user);
			}
			
			Object users [][] = new Object [usersList.size()][4];
			int i = 0;
			for (Object[] user : usersList) {
				users[i] = user;
				i++;
			}
			closeConnection();
			return users;
		
		}catch(SQLException e)
		{
			closeConnection();
			return null;
		}
	}

	public String getEmail(String nickName, String password) 
	{
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("SELECT ID FROM Permissions where nickName = ? and password = ?");
			stmt.setString(1, nickName);
			stmt.setString(2, encryptPassword);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return "";
			}
			
			String email = rs.getString("ID");
			
			closeConnection();
			return email;
			
		}
		catch(Exception e)
		{
			closeConnection();
			return "";
		}
	}
	
	public boolean addUses(String name , boolean acceptOrder , boolean withoutDueDate , boolean pastDueDate , boolean supplyOnTime, boolean beyondRequestDate ,
			boolean importExpediteReport , boolean exportExpediteReport, String project)
	{
		boolean followUp = acceptOrder || withoutDueDate || pastDueDate || supplyOnTime || beyondRequestDate;
		boolean expediteReport = importExpediteReport || exportExpediteReport;
		
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Uses (name , followUp ,acceptOrder , withoutDueDate , pastDueDate , supplyOnTime , beyondRequestDate "
				+ ",expediteReport , importExpediteReport , exportExpediteReport , project , Date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
		
			stmt.setString(1, name);
			stmt.setBoolean(2, followUp);
			stmt.setBoolean(3, acceptOrder);
			stmt.setBoolean(4, withoutDueDate);
			stmt.setBoolean(5, pastDueDate);
			stmt.setBoolean(6, supplyOnTime);
			stmt.setBoolean(7, beyondRequestDate);
			stmt.setBoolean(8, expediteReport);
			stmt.setBoolean(9, importExpediteReport);
			stmt.setBoolean(10, exportExpediteReport);
			stmt.setString(11, project);
			//Date date = Date.valueOf(LocalDate.now());
			String date = LocalDateTime.now().toString();
			stmt.setString(12, date.toString());
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
			return true;
		
	}
	catch(Exception e)
	{
		try {
			c.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		e.printStackTrace();
		
		closeConnection();
		return false;
	}
		
	}
	
	public List<Activity> getUses(Date fromDate , Date untilDate)
	{
		List<Activity> uses = new ArrayList<Activity>();
		String name;
		boolean followUp;
		boolean acceptOrder;
		boolean withoutDueDate; 
		boolean pastDueDate;
		boolean supplyOnTime;
		boolean beyondRequestDate;
		boolean expediteReport;
		boolean importExpediteReport;
		boolean exportExpediteReport;
		String project;
		LocalDateTime date;
		
		DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT * from Uses where date(Date) Between date(?) AND date(?)");
			stmt.setString(1, fromDate.toString());
			stmt.setString(2, untilDate.toString());
			ResultSet rs = stmt.executeQuery();
			if(rs.isClosed())
			{
				closeConnection();
				return new ArrayList<Activity>();
			}
				
			
			while(rs.next())
			{
				name = rs.getString("name");
				followUp = rs.getBoolean("followUp");
				acceptOrder = rs.getBoolean("acceptOrder");
				withoutDueDate = rs.getBoolean("withoutDueDate");
				pastDueDate = rs.getBoolean("pastDueDate");
				supplyOnTime = rs.getBoolean("supplyOnTime");
				beyondRequestDate = rs.getBoolean("beyondRequestDate");
				expediteReport = rs.getBoolean("expediteReport");
				importExpediteReport = rs.getBoolean("importExpediteReport");
				exportExpediteReport = rs.getBoolean("exportExpediteReport");
				project = rs.getString("project");
			    date = LocalDateTime.from(df.parse(rs.getString("Date"))); 
				Activity use = new Activity(name, followUp, acceptOrder, withoutDueDate, pastDueDate, supplyOnTime, beyondRequestDate ,
						expediteReport, importExpediteReport, exportExpediteReport, project , date);
				uses.add(use);
			}
			
			closeConnection();
			return uses;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Activity>();
		}
		
	}

	public boolean addProject(String projectName) 
	{

		try
		{
			connect();
			stmt = c.prepareStatement("INSERT INTO Projects (projectName) VALUES (?)");
			
			stmt.setString(1, projectName.trim());
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			closeConnection();
			return false;
		}
	}

	public boolean removeProject(String projectName) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("DELETE FROM Projects where projectName = ?");
			
			stmt.setString(1, projectName.trim());
			stmt.executeUpdate();
			
			c.commit();
			closeConnection();
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			closeConnection();
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getAllProjects()
	{
		List<String> projects = new ArrayList<String>();
		try
		{
		
		connect();
		stmt = c.prepareStatement("SELECT * FROM Projects");		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next())
		{
			String name = rs.getString("projectName");
			projects.add(name);
		}
		
		closeConnection();
		return projects;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<String>();
		}
	}
	public boolean updatePassword(String userName, String password) {
		try
		{
			connect();
			String encryptPassword = Globals.encrypt(password);
			stmt = c.prepareStatement("UPDATE Permissions SET Password = ? WHERE nickName = ?");
			
			stmt.setString(1, encryptPassword);
			stmt.setString(2, userName);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			closeConnection();
			return false;
		}
		
	}
	public boolean addCC(String cc) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO CCEmails (email) VALUES (?)");
			stmt.setString(1, cc);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
			return true;
		
	}
	catch(Exception e)
	{
		try {
			c.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		e.printStackTrace();
		
		closeConnection();
		return false;
	}
		
	}
	public boolean removeCC(String cc) 
	{
		
		try
		{
			
			connect();
			stmt = c.prepareStatement("DELETE FROM CCEmails where email = ?");
			
			stmt.setString(1, cc);
			stmt.executeUpdate();
			
			c.commit();
			closeConnection();
			return true;
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			closeConnection();
			e.printStackTrace();
			return false;
		}
	}
	public List<String> getAllCC() 
	{
		List<String> cc = new ArrayList<String>();
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT * FROM CCEmails");
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				cc.add(rs.getString("email"));
			}
			
			closeConnection();
			return cc;
		
		}catch(SQLException e)
		{
			closeConnection();
			return new ArrayList<String>();
		}
	}
	public void setDirectory(String project , String directory) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("UPDATE Projects SET directory = ? WHERE projectName = ?");
			
			stmt.setString(1, directory);
			stmt.setString(2, project);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			closeConnection();
		}
		
	}
	
	public String getDirectory(String project) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT directory FROM Projects where projectName = ?");
			stmt.setString(1, project);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return "";
			}
			
			String directory = rs.getString("directory");
			directory = (directory == null) ? "" : directory;
			
			closeConnection();
			
			return directory;
		
		}catch(SQLException e)
		{
			closeConnection();
			return "";
		}
	}
	
	public void setFollowUpDirectory(String directory) 
	{
		try
		{
			if(getFollowUpDirectory().equals(""))
			{
				connect();
				stmt = c.prepareStatement("INSERT INTO Directories (directory) VALUES (?)");
			}
			else
			{
				connect();
				stmt = c.prepareStatement("UPDATE Directories SET directory = ?");	
			}
			
			stmt.setString(1, directory);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
		
		}
		catch(Exception e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			closeConnection();
		}
		
	}
	
	public String getFollowUpDirectory() 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT directory FROM Directories");
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.isClosed())
			{
				closeConnection();
				return "";
			}
			
			String directory = rs.getString("directory");
			directory = (directory == null) ? "" : directory;
			
			closeConnection();
			
			return directory;
		
		}catch(SQLException e)
		{
			closeConnection();
			return "";
		}
	}
}
