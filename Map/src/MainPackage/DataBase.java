package MainPackage;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.sqlite.SQLiteConfig;

import AnalyzerTools.MonthDate;
import AnalyzerTools.QuantityPerDate;
import Forms.CustomerOrder;
import Forms.Forecast;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.Globals;
import MainPackage.Globals.FormType;

public class DataBase {

	private Connection c = null;
	private PreparedStatement stmt = null;
	private Globals globals;
	
	public DataBase() 
	{
		globals = new Globals();
	}
	public boolean connect()
	{
		try {
			Class.forName("org.sqlite.JDBC");
		    SQLiteConfig config = new SQLiteConfig(); 
		    config.enforceForeignKeys(true);  
		    c = DriverManager.getConnection("jdbc:sqlite:"+globals.con , config.toProperties());
		    c.setAutoCommit(false);
		    return true;
			}catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      JOptionPane.showConfirmDialog(null, "Can't find DB file \nThe file should be in : " + globals.con,"",JOptionPane.PLAIN_MESSAGE);
			      //System.exit(0);
			      return false;
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
	
	
	public boolean addFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Forecast (customer , CN , description ,quantity , initDate , requireDate , notes) VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, customer);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, description);
			stmt.setString(4, quantity);
			stmt.setString(5, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(6, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(7, notes);
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
	
	public boolean updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("UPDATE Forecast SET customer = ? , CN = ? , description = ? ,quantity = ? , initDate = ? , requireDate = ? , notes = ? where ID = ?");
			stmt.setString(1, customer);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, description);
			stmt.setString(4, quantity);
			stmt.setString(5, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(6, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(7, notes);
			stmt.setInt(8, id);
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
	
	public void removeFC(int id)
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM Forecast Where ID = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
		}
		
	}
	
	public void addNewProductFormQuantityPerDate(String product, QuantityPerDate quantityPerDate , FormType type) 
	{
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "productShipments";
				break;
			case PO:
				tableName = "productCustomerOrders";
				break;
			case WO:
				tableName = "productWorkOrder";
				break;
			case FC:
				tableName = "productForecast";
				break;
			default:
				return;
		}
		
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO " + tableName +" (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, product);
			stmt.setString(2, Double.toString(quantityPerDate.getQuantity()));
			stmt.setString(3, Globals.dateToSqlFormatString(quantityPerDate.getDate()));
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
	
	public void updateNewProductFormQuantityPerDate(String product, QuantityPerDate quantityPerDate , FormType type) 
	{
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "productShipments";
				break;
			case PO:
				tableName = "productCustomerOrders";
				break;
			case WO:
				tableName = "productWorkOrder";
				break;
			case FC:
				tableName = "productForecast";
				break;
			default:
				return;
		}
		
		try
		{
			
			connect();
			stmt = c.prepareStatement("UPDATE " + tableName +" SET quantity = ? where CN = ? AND date = ?");
			stmt.setString(1, Double.toString(quantityPerDate.getQuantity()));
			stmt.setString(2, product);
			stmt.setString(3, Globals.dateToSqlFormatString(quantityPerDate.getDate()));
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
	
	public List<Forecast> getAllFC(String catalogNumber) 
	{
		List<Forecast> forecasts = new ArrayList<>();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM Forecast") : c.prepareStatement("SELECT * FROM Forecast where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber); 	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("ID");
				String customer = rs.getString("customer");
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
				String notes = rs.getString("notes");
						
				Forecast forecast = new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, notes);
				forecasts.add(forecast);
			}
			
			closeConnection();
			return forecasts;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Forecast>();
		}
	}
	
	
	public void cleanProductQuantityPerDate(String catalogNumber, FormType type) 
	{
		String tableName1 , tableName2;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName1 = "productShipments";
				tableName2 = "InitProductShipments";
				break;
			case PO:
				tableName1 = "productCustomerOrders";
				tableName2 = "InitProductCustomerOrders";
				break;
			case WO:
				tableName1 = "productWorkOrder";
				tableName2 = "InitProductWorkOrder";
				break;
			case FC:
				tableName1 = "productForecast";
				tableName2 = "InitProductForecast";
				break;
			default:
				return;
		}
		
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM " + tableName1 +" Where CN = ?");
			stmt.setString(1, catalogNumber);
			stmt.executeUpdate();
			
			stmt = c.prepareStatement("DELETE FROM " + tableName2 +" Where CN = ?");
			stmt.setString(1, catalogNumber);
			stmt.executeUpdate();
			
			stmt = c.prepareStatement("INSERT INTO " + tableName2 +" (CN , initDate) VALUES(?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(Globals.getTodayDate()));
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
		}
		
		
		
	}
	
	public Forecast getForecast(int id) 
	{
		Forecast forecast = null;
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM Forecast where ID = ?");	
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String customer = rs.getString("customer");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
				String notes = rs.getString("notes");
						
				forecast =  new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, notes);
			}
			
			closeConnection();
			return forecast;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}

	public MonthDate getMaximumForecastDate() 
	{
		MonthDate requireDate = new MonthDate(Globals.getTodayDate());
		
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT date FROM productForecast where date(date) = (SELECT MAX(date(date)) FROM productForecast)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
					requireDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
			}
			
			closeConnection();
			return requireDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
	
	public MonthDate getMinimumInitDate() 
	{
		MonthDate requireDate = new MonthDate(Globals.getTodayDate());
		
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT Min(date(initDate)) AS date FROM (SELECT initDate FROM InitProductCustomerOrders UNION "
					+ "SELECT initDate FROM InitProductForecast UNION SELECT initDate FROM InitProductWorkOrder UNION SELECT initDate FROM InitProductShipments)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
					requireDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
			}
				
			
			closeConnection();
			return requireDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
	
	public void removeProductQuantity(String CatalogNumber, MonthDate date)
	{
		String[] tablesName = {"productShipments" ,"productCustomerOrders" ,"productWorkOrder" , "productForecast"}  ;

		for (String tableName : tablesName) 
		{
			try{
				
				connect();
				stmt = (date == null) ? c.prepareStatement("DELETE FROM " + tableName + " Where CN = ?") : c.prepareStatement("DELETE FROM " + tableName +" Where CN = ? AND date(date) = date(?)");		
				stmt.setString(1, CatalogNumber);
				if(date != null)
					stmt.setString(2, Globals.dateToSqlFormatString(date));
				
				stmt.executeQuery();	
				closeConnection();
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				closeConnection();
			}
		}
		
	}
	public Map<String, String> getAllCatalogNumbers() 
	{
		Map<String, String> catalogNumbers = new HashMap<String,String>();
		try{
			
			connect();
			//stmt = c.prepareStatement("SELECT CN,description FROM shipments UNION SELECT CN,description FROM customerOrders"
				//	+ "SELECT CN,description FROM UNION workOrder UNION SELECT CN,description FROM forecast");
			stmt = c.prepareStatement("SELECT CN,description FROM Tree");
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				catalogNumbers.put(catalogNumber,description);
			}
			
			closeConnection();
			return catalogNumbers;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new HashMap<String,String>();
		}

	}
	
	private QuantityPerDate getProductFormQuantityOnDate(String catalogNumber , MonthDate monthDate , FormType type)
	{
		QuantityPerDate quantityPerDate = null;
		
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "productShipments";
				break;
			case PO:
				tableName = "productCustomerOrders";
				break;
			case WO:
				tableName = "productWorkOrder";
				break;
			case FC:
				tableName = "productForecast";
				break;
			default:
				return null;
		}
		
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT quantity FROM " + tableName + " where CN = ? AND date(date) = date(?)");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(monthDate));
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
			{
				String quantity = rs.getString("quantity");
				quantityPerDate = new QuantityPerDate(monthDate, Double.parseDouble(quantity));
			}
			else
				quantityPerDate = new QuantityPerDate(monthDate, 0);
			
			closeConnection();
			return quantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
	
	public QuantityPerDate getProductShipmentQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.SHIPMENT);
	}
	public QuantityPerDate getProductPOQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.PO);
	}
	public QuantityPerDate getProductWOQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.WO);
	}
	public QuantityPerDate getProductFCQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.FC);
	}
	
	public Pair<String,Integer> getFather(String catalogNumber) 
	{
		Pair<String,Integer> father;
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT fatherCN,quantity FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
			{
				String fatherCatalogNumber = rs.getString("fatherCN");
				if(fatherCatalogNumber == null || fatherCatalogNumber.trim().equals(""))
					father = new Pair<String,Integer>(null, null);
				else
				{
					String quantity = rs.getString("quantity");
					if(StringUtils.isNumeric(quantity))
						father = new Pair<String,Integer>(fatherCatalogNumber, Integer.parseInt(quantity));
					else
						father = new Pair<String,Integer>(fatherCatalogNumber, 0);
				}
			}
			else
				father = new Pair<String,Integer>(null, null);
			
			closeConnection();
			return father;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new Pair<String,Integer>(null, null);
		}
	}
	
	public void addNewProduct(String catalogNumber , String customer , String description , String father , String quantity) 
	{
		try{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Tree (CN , customer , description , fatherCN , quantity) VALUES(?,?,?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, customer);
			stmt.setString(3, description);
			stmt.setString(4, father);
			stmt.setString(5, quantity);
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
	
	public void updateAlias(String catalogNumber , String alias)
	{
		try{
			
			connect();
			stmt = c.prepareStatement("UPDATE Tree SET alias = ? where CN = ?");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, alias);
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
	
	public void updateQuantityToAssociate(String catalogNumber , String father , String quantity)
	{
		try{
			
			connect();
			stmt = c.prepareStatement("UPDATE Tree SET quantity = ? where CN = ? and fatherCN = ?");
			stmt.setString(1, quantity);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, father);
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
	
	public List<Shipment> getAllShipmentsOnMonth(String catalogNumber , MonthDate date) 
	{
		List<Shipment> shipments = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM Shipments where CN = ? AND date(shipmentDate) >= date(?) AND date(shipmentDate) < date(?) "
					+ "AND date(shipmentDate) >= (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductShipments) order by shipmentDate");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				String customer = rs.getString("customer");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String shipmentDate = rs.getString("shipmentDate");
				
				Shipment shipment = new Shipment(id,customer, orderId, orderCustomerId , catalogNumber, quantity, Globals.parseDateFromSqlFormat(shipmentDate), description);
				shipments.add(shipment);
			}
			
			closeConnection();
			return shipments;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Shipment>();
		}
		
	}

	public List<CustomerOrder> getAllPOOnMonth(String catalogNumber , MonthDate date) 
	{
		List<CustomerOrder> customerOrders = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM CustomerOrders where CN = ? AND date(guaranteedDate) >= date(?) AND date(guaranteedDate) < date(?) "
					+ "AND date(orderDate) >= (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductCustomerOrders) order by guaranteedDate");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String orderNumber = rs.getString("orderNumber");
				String customerOrderNumber = rs.getString("customerOrderNumber");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("orderDate"));
				java.util.Date guaranteedDate = Globals.parseDateFromSqlFormat(rs.getString("guaranteedDate"));
				
				CustomerOrder customerOrder = new CustomerOrder(id,customer, orderNumber, customerOrderNumber , catalogNumber, description, quantity, price, orderDate, guaranteedDate);
				customerOrders.add(customerOrder);
			}
			
			closeConnection();
			return customerOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<CustomerOrder>();
		}
	}

	public List<WorkOrder> getAllWOOnMonth(String catalogNumber , MonthDate date) 
	{
		List<WorkOrder> workOrders = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM WorkOrder where CN = ? AND date(date) >= date(?) AND date(date) < date(?) "
					+ "AND date(date) >= (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductWorkOrder) order by date");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String woNumber = rs.getString("WOId");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("date"));
				
				WorkOrder customerOrder = new WorkOrder(id, woNumber, catalogNumber, quantity, customer, orderDate, description);
				workOrders.add(customerOrder);
			}
			
			closeConnection();
			return workOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<WorkOrder>();
		}
	}
	
	public List<Forecast> getAllFCOnMonth(String catalogNumber , MonthDate date) 
	{
		List<Forecast> forecasts = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM Forecast where CN = ? AND date(requireDate) >= date(?) AND date(requireDate) < date(?) "
					+ "AND date(initDate) >= (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductForecast) order by requireDate");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("ID");
				String customer = rs.getString("customer");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
				String notes = rs.getString("notes");
						
				Forecast forecast = new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, notes);
				forecasts.add(forecast);
			}
			
			closeConnection();
			return forecasts;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Forecast>();
		}
	}
	
	public String getDescendantCatalogNumber(String catalogNumber)
	{
		String descendantCatalogNumber = catalogNumber;
		boolean done = false;
		try{
			
			connect();
			while(!done)
			{
				stmt = c.prepareStatement("SELECT alias FROM Tree where CN = ?");
				stmt.setString(1, descendantCatalogNumber);
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next())
				{
					String alias = rs.getString("alias");
					if(alias == null || alias.trim().equals(""))
						done = true;
					else
						descendantCatalogNumber = alias;
				}
				else
					done = true;
			}
			
			
			closeConnection();
			return descendantCatalogNumber;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
	
	public List<String> getAllPatriarchsCatalogNumber(String catalogNumber) 
	{
		List<String> patriarchsCatalogNumber = new ArrayList<>();
		for (String cn : getAllCatalogNumbers().keySet()) 
		{
			if(getDescendantCatalogNumber(cn).trim().equals(catalogNumber))
				patriarchsCatalogNumber.add(cn);
		}
		
		return patriarchsCatalogNumber;
	}
	
	public void addNewInitProductCustomerOrders(String catalogNumber, String initDate , String quantity , String requireDate , FormType type)
	{
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "InitProductShipments";
				break;
			case PO:
				tableName = "InitProductCustomerOrders";
				break;
			case WO:
				tableName = "InitProductWorkOrder";
				break;
			case FC:
				tableName = "InitProductForecast";
				break;
			default:
				return;
		}
		
		try
		{
			connect();			
			stmt = c.prepareStatement("UPDATE " + tableName + " SET quantity = ? , requireDate = ? where CN = ? AND initDate = ?");
			stmt.setString(1, quantity);
			stmt.setString(2, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(3, catalogNumber);
			stmt.setString(4, Globals.parseDateToSqlFormatString(initDate));
			stmt.executeUpdate();
			
			c.commit();
			
			closeConnection();
			
		}
		catch(SQLException e)
		{
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
		}
	}
	
	public List<WorkOrder> getAllWO(String catalogNumber) 
	{
		List<WorkOrder> workOrders = new ArrayList<>();
		try{
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM WorkOrder") : c.prepareStatement("SELECT * FROM WorkOrder where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String woNumber = rs.getString("WOId");
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("date"));
				
				WorkOrder customerOrder = new WorkOrder(id, woNumber, catalogNumber, quantity, customer, orderDate, description);
				workOrders.add(customerOrder);
			}
			
			closeConnection();
			return workOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<WorkOrder>();
		}
	}
	
	public List<CustomerOrder> getAllPO(String catalogNumber) 
	{
		List<CustomerOrder> customerOrders = new ArrayList<>();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM CustomerOrders") : c.prepareStatement("SELECT * FROM CustomerOrders where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String orderNumber = rs.getString("orderNumber");
				String customerOrderNumber = rs.getString("customerOrderNumber");
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("orderDate"));
				java.util.Date guaranteedDate = Globals.parseDateFromSqlFormat(rs.getString("guaranteedDate"));
				
				CustomerOrder customerOrder = new CustomerOrder(id,customer, orderNumber, customerOrderNumber , catalogNumber, description, quantity, price, orderDate, guaranteedDate);
				customerOrders.add(customerOrder);
			}
			
			closeConnection();
			return customerOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<CustomerOrder>();
		}
	}
	
	public List<Shipment> getAllShipments(String catalogNumber) 
	{
		List<Shipment> shipments = new ArrayList<>();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM Shipments") : c.prepareStatement("SELECT * FROM Shipments where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				String customer = rs.getString("customer");
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String shipmentDate = rs.getString("shipmentDate");
				
				Shipment shipment = new Shipment(id,customer, orderId, orderCustomerId , catalogNumber, quantity, Globals.parseDateFromSqlFormat(shipmentDate), description);
				shipments.add(shipment);
			}
			
			closeConnection();
			return shipments;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Shipment>();
		}
		
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsFormQuantityPerDate(FormType type , String catalogNumber)
	{
		Map<String, List<QuantityPerDate>> productFormQuantityPerDate = new HashMap<>();
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "productShipments";
				break;
			case PO:
				tableName = "productCustomerOrders";
				break;
			case WO:
				tableName = "productWorkOrder";
				break;
			case FC:
				tableName = "productForecast";
				break;
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM " + tableName) : c.prepareStatement("SELECT * FROM " + tableName + " where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String quantity = rs.getString("quantity");
				MonthDate requireDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("date")));
				
				QuantityPerDate quantityPerDate = new QuantityPerDate(requireDate, Double.parseDouble(quantity));
				
				if(productFormQuantityPerDate.containsKey(catalogNumber))
					productFormQuantityPerDate.get(catalogNumber).add(quantityPerDate);
				else
				{
					List<QuantityPerDate> quantityPerDates = new ArrayList<>();
					quantityPerDates.add(quantityPerDate);
					productFormQuantityPerDate.put(catalogNumber, quantityPerDates);
				}
				
			}
			
			closeConnection();
			return productFormQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new HashMap<>();
		}
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsWOQuantityPerDate(String catalogNumber) 
	{
		return getAllProductsFormQuantityPerDate(FormType.WO , catalogNumber);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsPOQuantityPerDate(String catalogNumber) 
	{
		return getAllProductsFormQuantityPerDate(FormType.PO , catalogNumber);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsShipmentQuantityPerDate(String catalogNumber) 
	{
		return getAllProductsFormQuantityPerDate(FormType.SHIPMENT , catalogNumber);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsFCQuantityPerDate(String catalogNumber) 
	{
		return getAllProductsFormQuantityPerDate(FormType.FC , catalogNumber);
	}
	
	public Map<String,List<QuantityPerDate>> getInitProductsFormQuantityPerDate(FormType type , String catalogNumber)
	{
		Map<String, List<QuantityPerDate>> productFormQuantityPerDate = new HashMap<>();
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "InitProductShipments";
				break;
			case PO:
				tableName = "InitProductCustomerOrders";
				break;
			case WO:
				tableName = "InitProductWorkOrder";
				break;
			case FC:
				tableName = "InitProductForecast";
				break;
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM " + tableName) : c.prepareStatement("SELECT * FROM " + tableName +" where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				String quantity = rs.getString("quantity");
				MonthDate requireDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("requireDate")));
				
				QuantityPerDate quantityPerDate = new QuantityPerDate(requireDate, Double.parseDouble(quantity));
				
				if(productFormQuantityPerDate.containsKey(catalogNumber))
					productFormQuantityPerDate.get(catalogNumber).add(quantityPerDate);
				else
				{
					List<QuantityPerDate> quantityPerDates = new ArrayList<>();
					quantityPerDates.add(quantityPerDate);
					productFormQuantityPerDate.put(catalogNumber, quantityPerDates);
				}
				
			}
			
			closeConnection();
			return productFormQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new HashMap<>();
		}
	}
	public Map<String, List<QuantityPerDate>> getInitProductsFCQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.FC , catalogNumber);
	}
	public Map<String, List<QuantityPerDate>> getInitProductsWOQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.WO , catalogNumber);
	}
	public Map<String, List<QuantityPerDate>> getInitProductsPOQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.PO , catalogNumber);
	}
	public Map<String, List<QuantityPerDate>> getInitProductsShipmentsQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.SHIPMENT , catalogNumber);
	}
	
	public Map<String, java.util.Date> getInitProductsFormDates(FormType type , String catalogNumber)
	{
		Map<String, java.util.Date> productFormQuantityPerDate = new HashMap<>();
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "InitProductShipments";
				break;
			case PO:
				tableName = "InitProductCustomerOrders";
				break;
			case WO:
				tableName = "InitProductWorkOrder";
				break;
			case FC:
				tableName = "InitProductForecast";
				break;
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM " + tableName) : c.prepareStatement("SELECT * FROM " + tableName + " where CN = ?");
			if(catalogNumber != null)
				stmt.setString(1, catalogNumber);		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				catalogNumber = (catalogNumber == null) ? rs.getString("CN") : catalogNumber;
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				
				productFormQuantityPerDate.put(catalogNumber, initDate);
			}
			
			closeConnection();
			return productFormQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new HashMap<>();
		}
	}
	
	public Map<String, java.util.Date> getInitProductsPODates(String catalogNumber) 
	{
		return getInitProductsFormDates(FormType.PO , catalogNumber);
	}

	public Map<String, java.util.Date> getInitProductsWODates(String catalogNumber) 
	{
		return getInitProductsFormDates(FormType.WO , catalogNumber);
	}

	public Map<String, java.util.Date> getInitProductsShipmentsDates(String catalogNumber) 
	{
		return getInitProductsFormDates(FormType.SHIPMENT , catalogNumber);
	}
	
	public Map<String, java.util.Date> getInitProductsFCDates(String catalogNumber) 
	{
		return getInitProductsFormDates(FormType.FC , catalogNumber);
	}
	
	public String getCustomerOfCatalogNumber(String catalogNumber) 
	{
		String customer = "";
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT customer FROM Tree");
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
				customer = rs.getString("customer");
			
			closeConnection();
			return customer;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return customer;
		}
	}
	
	
	

}
