package MainPackage;
import java.awt.Component;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sqlite.SQLiteConfig;

import AnalyzerTools.MonthDate;
import AnalyzerTools.ProductColumn;
import AnalyzerTools.QuantityPerDate;
import Forms.CustomerOrder;
import Forms.Forecast;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.Globals;
import MainPackage.Globals.FormType;
import MainPackage.Globals.UpdateType;
import Reports.ProductInit;
import Reports.ProductInitHistory;
import Reports.Tree;

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
		    c = DriverManager.getConnection("jdbc:sqlite:"+Globals.con , config.toProperties());
		    c.setAutoCommit(false);
		    return true;
			}catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      JOptionPane.showConfirmDialog(null, "Can't find DB file \nThe file should be in : " + Globals.con,"",JOptionPane.PLAIN_MESSAGE);
			      System.exit(0);
			      return false;
			}
	}
	public void closeConnection()
	{
		try {
			if(stmt != null && !c.isClosed())
			{
				stmt.close();
				stmt = null;
			}
			if(c != null && !c.isClosed())
			{
				c.close();
				c = null;
			}
			
		} catch (SQLException e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}finally {
			stmt = null;
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
	
	public boolean addCustomersToUser(String nickName , List<String> customers)
	{
		try
		{
			connect();
			
			for (String customer : customers) 
			{
				stmt = c.prepareStatement("INSERT INTO UsersPerCustomer (user , customer) VALUES (?,?)");
				stmt.setString(1, nickName);
				stmt.setString(2, customer);
				stmt.executeUpdate();
			}
			
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
			e.printStackTrace();
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
	
	
	public boolean addFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String userName , String notes) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Forecast (customer , CN , description ,quantity , initDate , requireDate , userName , notes) VALUES (?,?,?,?,?,?,?,?)");
			stmt.setString(1, customer);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, description);
			stmt.setString(4, quantity);
			stmt.setString(5, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(6, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(7, userName);
			stmt.setString(8, notes);
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
	
	public boolean updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String userName , String notes) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("UPDATE Forecast SET customer = ? , CN = ? , description = ? ,quantity = ? , initDate = ? , requireDate = ? , userName = ? ,notes = ? where ID = ?");
			stmt.setString(1, customer);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, description);
			stmt.setString(4, quantity);
			stmt.setString(5, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(6, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(7, userName);
			stmt.setString(8, notes);
			stmt.setInt(9, id);
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
	
	public List<Forecast> getAllFC(String catalogNumber, boolean ignorePast) 
	{
		List<Forecast> forecasts = new ArrayList<>();
		MonthDate firstMonth;
		if(ignorePast)
			firstMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
		else
			firstMonth = getMinimumInitDate();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM Forecast where date(initDate) >= date(?)") 
					: c.prepareStatement("SELECT * FROM Forecast where CN = ? AND date(initDate) >= date(?)");
			if(catalogNumber != null)
			{
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(firstMonth));
			}
			else
				stmt.setString(1, Globals.dateToSqlFormatString(firstMonth));	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("ID");
				String customer = rs.getString("customer");
				catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
				String userName = rs.getString("userName");
				String notes = rs.getString("notes");
						
				Forecast forecast = new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, userName , notes);
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
			stmt = c.prepareStatement("DELETE FROM " + tableName +" Where CN = ?");
			stmt.setString(1, catalogNumber);
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
				String userName = rs.getString("userName");
				String notes = rs.getString("notes");
						
				forecast =  new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, userName, notes);
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

	public MonthDate getMaximumMapDate() 
	{
		MonthDate requireDate = new MonthDate(Globals.getTodayDate());
		MonthDate maxInitDate = getMaximumInitDate();
		
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT Max(date(date)) AS date FROM (SELECT date FROM productForecast UNION SELECT date FROM productCustomerOrders)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
					requireDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
			}
			
			if(maxInitDate.after(requireDate))
				requireDate = maxInitDate;
			
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
	
	public MonthDate getMinimumMapDate() 
	{
		MonthDate requireDate = new MonthDate(Globals.addMonths(Globals.getTodayDate() , -Globals.monthsToCalculate));
		MonthDate minInitDate = getMinimumInitDate();
		
		if(minInitDate.after(requireDate))
			requireDate = minInitDate;
		
		return requireDate;
	}
	
	public MonthDate getMinimumInitDate() 
	{		
		MonthDate requireDate = null;
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT Min(date(initDate)) AS date FROM (SELECT initDate FROM InitProductCustomerOrders UNION "
					+ "SELECT initDate FROM InitProductForecast UNION SELECT initDate FROM InitProductWorkOrder UNION SELECT initDate FROM InitProductShipments)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
				{
					MonthDate minInitMonthDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
					requireDate =  minInitMonthDate;
					
				}
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
	
	public MonthDate getMaximumInitDate() 
	{
		MonthDate requireDate = new MonthDate(Globals.getTodayDate());
		
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT MAX(date(requireDate)) AS date FROM (SELECT requireDate FROM InitProductCustomerOrders UNION "
					+ "SELECT requireDate FROM InitProductForecast UNION SELECT requireDate FROM InitProductWorkOrder UNION SELECT requireDate FROM InitProductShipments)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
				{
					MonthDate maxInitMonthDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
					if(maxInitMonthDate.before(requireDate))
						requireDate = maxInitMonthDate;
					
				}
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
	
	public void removeProductQuantity(String CatalogNumber, MonthDate date , FormType type)
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

		try{
			
			connect();
			stmt = (date == null) ? c.prepareStatement("DELETE FROM " + tableName + " Where CN = ?") : c.prepareStatement("DELETE FROM " + tableName +" Where CN = ? AND date(date) = date(?)");		
			stmt.setString(1, CatalogNumber);
			if(date != null)
				stmt.setString(2, Globals.dateToSqlFormatString(date));
			
			stmt.executeUpdate();
			c.commit();
			closeConnection();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
		}
		
	}
	public Map<String, String> getAllCatalogNumbersPerDescription(String userName) 
	{
		Map<String, String> catalogNumbers = new HashMap<String,String>();
		List<String> customers = (userName == null) ? getAllProjects() : getCustomersOfUser(userName);
		try{
			
			connect();
			for (String customer : customers) 
			{
				stmt = c.prepareStatement("SELECT distinct CN,description FROM Tree where customer = ?");
				stmt.setString(1, customer);
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next())
				{
					String catalogNumber = rs.getString("CN");
					String description = rs.getString("description");
					catalogNumbers.put(catalogNumber,description);
				}
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
	
	public List<QuantityPerDate> calculateProductFormQuantityOnDate(String catalogNumber , FormType type) 
	{
		switch (type) 
		{
			case SHIPMENT:
				return calculateProductShipmentQuantityOnDate(catalogNumber);
			case PO:
				return calculateProductPOQuantityOnDate(catalogNumber);
			case WO:
				return calculateProductWOQuantityOnDate(catalogNumber);
			case FC:
				return calculateProductFCQuantityOnDate(catalogNumber);
			default:
				return null;
		}
	}
	
	public QuantityPerDate getProductShipmentQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{	
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.SHIPMENT);
	}
	
	public List<QuantityPerDate> calculateProductShipmentQuantityOnDate(String catalogNumber) 
	{
		try{
			
			List<QuantityPerDate> productQuantityPerDate = new ArrayList<>();
			connect();
			stmt = c.prepareStatement("select month , sum(quantity) as quantity from "
					+ "(select strftime('%Y-%m-01',shipmentDate) as month ,cast(quantity as decimal) as quantity "
					+ "from Shipments where cn = ? AND date(shipmentDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) "
					+ "FROM InitProductShipments where CN = ?)) group by month order by month");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				MonthDate monthDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("month")));
				double quantity = rs.getDouble("quantity");
				QuantityPerDate quantityPerDate = new QuantityPerDate(monthDate, quantity);
				productQuantityPerDate.add(quantityPerDate);
			}
			
			closeConnection();
			return productQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	
	public QuantityPerDate getProductPOQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.PO);
	}
	
	public List<QuantityPerDate> calculateProductPOQuantityOnDate(String catalogNumber) 
	{
		try{
			
			List<QuantityPerDate> productQuantityPerDate = new ArrayList<>();
			connect();
			stmt = c.prepareStatement("select month , sum(quantity) as quantity from "
					+ "(select strftime('%Y-%m-01',guaranteedDate) as month ,cast(quantity as decimal) as quantity "
					+ "from CustomerOrders where cn = ? AND date(orderDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) "
					+ "FROM InitProductCustomerOrders where CN = ?)) group by month order by month");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				MonthDate monthDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("month")));
				double quantity = rs.getDouble("quantity");
				QuantityPerDate quantityPerDate = new QuantityPerDate(monthDate, quantity);
				productQuantityPerDate.add(quantityPerDate);
			}
			
			closeConnection();
			return productQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	public QuantityPerDate getProductWOQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.WO);
	}
	
	public List<QuantityPerDate> calculateProductWOQuantityOnDate(String catalogNumber) 
	{
		try{
			
			List<QuantityPerDate> productQuantityPerDate = new ArrayList<>();
			connect();
			stmt = c.prepareStatement("select month , sum(quantity) as quantity from "
					+ "(select strftime('%Y-%m-01',date) as month ,cast(quantity as decimal) as quantity "
					+ "from WorkOrder where cn = ? AND date(date) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) "
					+ "FROM InitProductWorkOrder where CN = ?)) group by month order by month");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				MonthDate monthDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("month")));
				double quantity = rs.getDouble("quantity");
				QuantityPerDate quantityPerDate = new QuantityPerDate(monthDate, quantity);
				productQuantityPerDate.add(quantityPerDate);
			}
			
			closeConnection();
			return productQuantityPerDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	public QuantityPerDate getProductFCQuantityOnDate(String catalogNumber, MonthDate monthDate) 
	{
		return getProductFormQuantityOnDate(catalogNumber , monthDate , FormType.FC);
	}
		
	public List<QuantityPerDate> calculateProductFCQuantityOnDate(String catalogNumber) 
	{
		try{
			
			List<QuantityPerDate> productQuantityPerDate = new ArrayList<>();
			connect();
			stmt = c.prepareStatement("select month , sum(quantity) as quantity from "
					+ "(select strftime('%Y-%m-01',requireDate) as month ,cast(quantity as decimal) as quantity "
					+ "from Forecast where cn = ? AND date(initDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) "
					+ "FROM InitProductForecast where CN = ?)) group by month order by month");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				MonthDate monthDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("month")));
				double quantity = rs.getDouble("quantity");
				QuantityPerDate quantityPerDate = new QuantityPerDate(monthDate, quantity);
				productQuantityPerDate.add(quantityPerDate);
			}
			
			closeConnection();
			return productQuantityPerDate;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	public List<Pair<String,Integer>> getFathers(String catalogNumber) 
	{
		List<Pair<String,Integer>> fathers = new ArrayList<>();
		Pair<String,Integer> father;
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT fatherCN,quantity FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String fatherCatalogNumber = rs.getString("fatherCN");
				if(fatherCatalogNumber == null || fatherCatalogNumber.trim().equals(""))
					continue;
				else
				{
					String quantity = rs.getString("quantity");
					if(StringUtils.isNumeric(quantity))
						father = new Pair<String,Integer>(fatherCatalogNumber, Integer.parseInt(quantity));
					else
						father = new Pair<String,Integer>(fatherCatalogNumber, 0);
				}
				
				fathers.add(father);
			}
			
			closeConnection();
			return fathers;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	public void addNewProduct(String catalogNumber , String customer , String userName , String description , String father , String quantity) 
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
			
			String initDate = Globals.dateWithoutHourToString(Globals.getTodayDate());
			if(!getInitProductsFCDates(catalogNumber).containsKey(catalogNumber))
			{
				insertNewInitProduct(catalogNumber, "0", initDate , initDate, FormType.FC);
				insertNewInitProduct(catalogNumber, "0", initDate , initDate, FormType.WO);
				insertNewInitProduct(catalogNumber, "0", initDate , initDate, FormType.PO);
				insertNewInitProduct(catalogNumber, "0", initDate , initDate, FormType.SHIPMENT);	
				
				String changeDate = Globals.parseDateToSqlFormatString(Globals.dateWithoutHourToString(Globals.getTodayDate()));
				addNewInitProductHistory(catalogNumber, quantity, initDate, initDate, changeDate, "init", userName, FormType.FC);
				addNewInitProductHistory(catalogNumber, quantity, initDate, initDate, changeDate, "init", userName, FormType.WO);
				addNewInitProductHistory(catalogNumber, quantity, initDate, initDate, changeDate, "init", userName, FormType.PO);
				addNewInitProductHistory(catalogNumber, quantity, initDate, initDate, changeDate, "init", userName, FormType.SHIPMENT);
			}
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
			stmt.setString(1, alias);
			stmt.setString(2, catalogNumber);
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
			stmt = c.prepareStatement("UPDATE Tree SET quantity = ? where CN = ? AND fatherCN = ?");
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
					+ "AND date(shipmentDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductShipments where CN = ?) order by shipmentDate");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			stmt.setString(4, catalogNumber);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				String quantity = rs.getString("quantity");
				String shipmentDate = rs.getString("shipmentDate");
				
				Shipment shipment = new Shipment(id,null, orderId, orderCustomerId , catalogNumber, quantity, Globals.parseDateFromSqlFormat(shipmentDate), null);
				shipments.add(shipment);
			}
			
			closeConnection();
			
			for (Shipment shipment : shipments) 
			{				
				String customer = getCustomerOfCatalogNumber(shipment.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(shipment.getCatalogNumber());
				shipment.setCustomer(customer);
				shipment.setDescription(description);
			}
			
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
					+ "AND date(orderDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductCustomerOrders WHERE CN = ?) order by guaranteedDate");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			stmt.setString(4, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customerOrderNumber = rs.getString("customerOrderNumber");
				String orderNumber = rs.getString("orderNumber");
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("orderDate"));
				java.util.Date guaranteedDate = Globals.parseDateFromSqlFormat(rs.getString("guaranteedDate"));
				
				CustomerOrder customerOrder = new CustomerOrder(id,null, orderNumber, customerOrderNumber , catalogNumber, null, quantity, price, orderDate, guaranteedDate);
				customerOrders.add(customerOrder);
			}
			
			closeConnection();
			
			for (CustomerOrder customerOrder : customerOrders) 
			{				
				String customer = getCustomerOfCatalogNumber(customerOrder.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(customerOrder.getCatalogNumber());
				customerOrder.setCustomer(customer);
				customerOrder.setDescription(description);
			}
			
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
					+ "AND date(date) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductWorkOrder where CN = ?) order by date");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			stmt.setString(4, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String woNumber = rs.getString("WOId");				
				String quantity = rs.getString("quantity");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("date"));
				
				WorkOrder customerOrder = new WorkOrder(id, woNumber, catalogNumber, quantity, null, orderDate, null);
				workOrders.add(customerOrder);
			}
			
			closeConnection();
			
			for (WorkOrder customerOrder : workOrders) 
			{				
				String customer = getCustomerOfCatalogNumber(customerOrder.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(customerOrder.getCatalogNumber());
				customerOrder.setCustomer(customer);
				customerOrder.setDescription(description);
			}
			
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
					+ "AND date(initDate) > (SELECT COALESCE(MAX(date(initDate)), date('0001-01-01')) FROM InitProductForecast where CN = ?) order by requireDate");
			
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.dateToSqlFormatString(date));
			stmt.setString(3, Globals.dateToSqlFormatString(Globals.addMonths(date, 1)));
			stmt.setString(4, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("ID");
				String customer = rs.getString("customer");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
				String userName = rs.getString("userName");
				String notes = rs.getString("notes");
						
				Forecast forecast = new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, userName ,notes);
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
				stmt = c.prepareStatement("SELECT distinct alias FROM Tree where CN = ?");
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
	
	public List<String> getAllDescendantCatalogNumber(String catalogNumber)
	{
		List<String> catalogNumbers = new ArrayList<>();
		String descendantCatalogNumber = catalogNumber;
		boolean done = false;
		try{
			
			connect();
			while(!done)
			{
				stmt = c.prepareStatement("SELECT distinct alias FROM Tree where CN = ?");
				stmt.setString(1, descendantCatalogNumber);
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next())
				{
					String alias = rs.getString("alias");
					if(alias == null || alias.trim().equals(""))
						done = true;
					else
					{
						descendantCatalogNumber = alias;
						catalogNumbers.add(descendantCatalogNumber);
					}
						
				}
				else
					done = true;
			}
			
			
			closeConnection();
			return catalogNumbers;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	
	public List<String> getAllPatriarchsCatalogNumber(String catalogNumber) 
	{
		List<String> patriarchsCatalogNumber = new ArrayList<>();
		for (String cn : getAllCatalogNumbersPerDescription(null).keySet()) 
		{
			if(getDescendantCatalogNumber(cn).trim().equals(catalogNumber))
				patriarchsCatalogNumber.add(cn);
		}
		
		return patriarchsCatalogNumber;
	}
	
	public void addNewInitProduct(String catalogNumber, String initDate , String quantity , String requireDate , FormType type)
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
			
			double currentQuantity = 0;
			stmt = c.prepareStatement("SELECT quantity FROM " + tableName +" where CN = ? AND initDate = ? AND requireDate = ?");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(3, Globals.parseDateToSqlFormatString(requireDate));
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
			{
				currentQuantity = Double.parseDouble(rs.getString("quantity"));
				double newQuantity = currentQuantity + Double.parseDouble(quantity);
				stmt = c.prepareStatement("UPDATE " + tableName +" SET quantity = ? where CN = ? AND initDate = ? AND requireDate = ?");
				stmt.setString(1, Double.toString(newQuantity));
				stmt.setString(2, catalogNumber);
				stmt.setString(3, Globals.parseDateToSqlFormatString(initDate));
				stmt.setString(4, Globals.parseDateToSqlFormatString(requireDate));
				stmt.executeUpdate();
				
				c.commit();
			}
			else
			{
				stmt = c.prepareStatement("SELECT quantity FROM " + tableName +" where CN = ? AND initDate = ?");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.parseDateToSqlFormatString(initDate));
				rs = stmt.executeQuery();
				
				if(!rs.next())
				{
					closeConnection();
					cleanInitProduct(catalogNumber, type);
					connect();
				}
				
				closeConnection();
				insertNewInitProduct(catalogNumber , quantity , initDate , requireDate , type);
				connect();
			}

			
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
	
	private void insertNewInitProduct(String catalogNumber, String quantity, String initDate,String requireDate ,FormType type) 
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
			stmt = c.prepareStatement("INSERT INTO " + tableName +" (CN , quantity , initDate ,requireDate) VALUES(?,?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, quantity);
			stmt.setString(3, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(4, Globals.parseDateToSqlFormatString(requireDate));
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
	public void cleanInitProduct(String catalogNumber, FormType type) 
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
			stmt = c.prepareStatement("DELETE FROM " + tableName +" Where CN = ?");
			stmt.setString(1, catalogNumber);
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
	public List<WorkOrder> getAllWO(String catalogNumber, boolean ignorePast) 
	{
		List<WorkOrder> workOrders = new ArrayList<>();
		MonthDate firstMonth;
		if(ignorePast)
			firstMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
		else
			firstMonth = getMinimumInitDate();
		try{
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM WorkOrder where date(date) >= date(?)") 
					: c.prepareStatement("SELECT * FROM WorkOrder where CN = ? AND date(date) >= date(?)");
			if(catalogNumber != null)
			{
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(firstMonth));
			}
			else
				stmt.setString(1, Globals.dateToSqlFormatString(firstMonth));
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String woNumber = rs.getString("WOId");
				catalogNumber = rs.getString("CN");
				String quantity = rs.getString("quantity");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("date"));
				
				WorkOrder customerOrder = new WorkOrder(id, woNumber, catalogNumber, quantity, null, orderDate, null);
				workOrders.add(customerOrder);
			}
			
			closeConnection();
			
			for (WorkOrder workOrder : workOrders) 
			{				
				String customer = getCustomerOfCatalogNumber(workOrder.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(workOrder.getCatalogNumber());
				workOrder.setCustomer(customer);
				workOrder.setDescription(description);
			}
			
			return workOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<WorkOrder>();
		}
	}
	
	public List<CustomerOrder> getAllPO(String catalogNumber, boolean ignorePast) 
	{
		List<CustomerOrder> customerOrders = new ArrayList<>();
		MonthDate firstMonth;
		if(ignorePast)
			firstMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
		else
			firstMonth = getMinimumInitDate();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM CustomerOrders where date(orderDate) >= date(?)") 
					: c.prepareStatement("SELECT * FROM CustomerOrders where CN = ? AND date(orderDate) >= date(?)");
			if(catalogNumber != null)
			{
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(firstMonth));
			}
			else
				stmt.setString(1, Globals.dateToSqlFormatString(firstMonth));
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customerOrderNumber = rs.getString("customerOrderNumber");
				String orderNumber = rs.getString("orderNumber");
				catalogNumber = rs.getString("CN");
				
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				java.util.Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("orderDate"));
				java.util.Date guaranteedDate = Globals.parseDateFromSqlFormat(rs.getString("guaranteedDate"));
				
				CustomerOrder customerOrder = new CustomerOrder(id,null, orderNumber, customerOrderNumber , catalogNumber, null, quantity, price, orderDate, guaranteedDate);
				customerOrders.add(customerOrder);
			}
			
			closeConnection();
			
			for (CustomerOrder customerOrder : customerOrders) 
			{				
				String customer = getCustomerOfCatalogNumber(customerOrder.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(customerOrder.getCatalogNumber());
				customerOrder.setCustomer(customer);
				customerOrder.setDescription(description);
			}
			
			return customerOrders;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<CustomerOrder>();
		}
	}
	
	private String getDescriptionOfCatalogNumber(String catalogNumber) 
	{
		String description = "";
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT description FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
				description = rs.getString("description");
			
			closeConnection();
			return description;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return description;
		}
	}
	public List<Shipment> getAllShipments(String catalogNumber, boolean ignorePast) 
	{
		List<Shipment> shipments = new ArrayList<>();
		MonthDate firstMonth;
		if(ignorePast)
			firstMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
		else
			firstMonth = getMinimumInitDate();
		try{
			
			connect();
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM Shipments where date(shipmentDate) >= date(?)") 
					: c.prepareStatement("SELECT * FROM Shipments where CN = ? AND date(shipmentDate) >= date(?)");
			if(catalogNumber != null)
			{
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(firstMonth));
			}
			else
				stmt.setString(1, Globals.dateToSqlFormatString(firstMonth));	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				catalogNumber = rs.getString("CN");			
				String quantity = rs.getString("quantity");
				String shipmentDate = rs.getString("shipmentDate");
				
				Shipment shipment = new Shipment(id,null, orderId, orderCustomerId , catalogNumber, quantity, Globals.parseDateFromSqlFormat(shipmentDate), null);
				shipments.add(shipment);
			}
			
			closeConnection();
			
			for (Shipment shipment : shipments) 
			{				
				String customer = getCustomerOfCatalogNumber(shipment.getCatalogNumber());
				String description = getDescriptionOfCatalogNumber(shipment.getCatalogNumber());
				shipment.setCustomer(customer);
				shipment.setDescription(description);
			}
			
			return shipments;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<Shipment>();
		}
		
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsFormQuantityPerDate(FormType type , String catalogNumber, boolean ignorePast)
	{
		Map<String, List<QuantityPerDate>> productFormQuantityPerDate = new HashMap<>();
		MonthDate firstMonth;
		if(ignorePast)
			firstMonth = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
		else
			firstMonth = getMinimumInitDate();
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
			stmt = (catalogNumber == null) ? c.prepareStatement("SELECT * FROM " + tableName + " where date(date) >= date(?)") 
					: c.prepareStatement("SELECT * FROM " + tableName + " where CN = ? AND date(date) >= date(?)");
			if(catalogNumber != null)
			{
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(firstMonth));
			}
			else
				stmt.setString(1, Globals.dateToSqlFormatString(firstMonth));	
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				catalogNumber = rs.getString("CN");
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
	
	public Map<String, List<QuantityPerDate>> getAllProductsWOQuantityPerDate(String catalogNumber, boolean ignorePast) 
	{
		return getAllProductsFormQuantityPerDate(FormType.WO , catalogNumber , ignorePast);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsPOQuantityPerDate(String catalogNumber, boolean ignorePast) 
	{
		return getAllProductsFormQuantityPerDate(FormType.PO , catalogNumber , ignorePast);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsShipmentQuantityPerDate(String catalogNumber, boolean ignorePast) 
	{
		return getAllProductsFormQuantityPerDate(FormType.SHIPMENT , catalogNumber , ignorePast);
	}
	
	public Map<String, List<QuantityPerDate>> getAllProductsFCQuantityPerDate(String catalogNumber, boolean ignorePast) 
	{
		return getAllProductsFormQuantityPerDate(FormType.FC , catalogNumber , ignorePast);
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
				catalogNumber = rs.getString("CN");
				String quantity = rs.getString("quantity");
				MonthDate requireDate = new MonthDate(Globals.parseDateFromSqlFormat(rs.getString("requireDate")));
				
				if(!NumberUtils.isCreatable(quantity))
					continue;
				QuantityPerDate quantityPerDate = new QuantityPerDate(requireDate, Double.parseDouble(quantity));
				
				if(productFormQuantityPerDate.containsKey(catalogNumber))
				{
					List<MonthDate> months = productFormQuantityPerDate.get(catalogNumber).stream().map(productQuantity -> productQuantity.getDate()).collect(Collectors.toList());
					if(months.contains(requireDate))
					{
						int index = months.indexOf(requireDate);
						productFormQuantityPerDate.get(catalogNumber).get(index).addQuantity(quantityPerDate.getQuantity());
					}
					else
						productFormQuantityPerDate.get(catalogNumber).add(quantityPerDate);	
				}
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
				catalogNumber = rs.getString("CN");
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
			stmt = c.prepareStatement("SELECT distinct customer FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
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
	
	public List<String> getCustomersOfUser(String userName) 
	{
		List<String> customers = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT customer FROM UsersPerCustomer where user = ?");
			stmt.setString(1, userName);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String customer = rs.getString("customer");
				customers.add(customer);
			}
			
			closeConnection();
			return customers;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	public List<String> getAllCatalogNumbers(String userName)
	{
		List<String> catalogNumbers = new ArrayList<>();
		List<String> customers = getCustomersOfUser(userName);
		
		for (String customer : customers) 
			catalogNumbers.addAll(getAllCatalogNumberOfCustomer(customer));
		
		return catalogNumbers;
	}
	
	public List<String> getAllCatalogNumberOfCustomer(String customer) 
	{
		List<String> catalogNumbers = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT distinct CN FROM Tree where customer = ?");
			stmt.setString(1, customer);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
				catalogNumbers.add(catalogNumber);
			}
			
			closeConnection();
			return catalogNumbers;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	public List<Tree> getAllTrees(String userName , String cn) 
	{
		List<Tree> trees = new ArrayList<>();
		List<String> catalogNumbers;
		if(cn != null)
		{
			catalogNumbers = new ArrayList<>();
			catalogNumbers.add(cn);
		}
		else
			catalogNumbers = getAllCatalogNumbers(userName);
		
		for (String catalogNumber : catalogNumbers) 
		{
			try{
				
				connect();
				stmt = c.prepareStatement("SELECT distinct * FROM Tree where CN = ?");
				stmt.setString(1, catalogNumber);
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next())
				{
					String customer = rs.getString("customer");
					String description = rs.getString("description");
					String fatherCN = rs.getString("fatherCN");
					String quantity = rs.getString("quantity"); 
					String alias = rs.getString("alias");
					Tree tree = new Tree(catalogNumber, customer, description, fatherCN, quantity, alias);
					
					trees.add(tree);
				}
				
				closeConnection();
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				closeConnection();
				return new ArrayList<>();
			}
		}
		
		return trees;
	}
	
	public void updateDescription(String catalogNumber, String description) 
	{
		try{
			
			connect();
			stmt = c.prepareStatement("UPDATE Tree SET description = ? where CN = ?");
			stmt.setString(1, description);
			stmt.setString(2, catalogNumber);
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

	
	public void updateTree(String catalogNumber, String customer , String description, String fatherCN, String newFatherCN , String quantity, String alias) 
	{
		updateAlias(catalogNumber, alias);
		updateQuantityToAssociate(catalogNumber, fatherCN, quantity);
		updateDescription(catalogNumber , description);
		updateFather(catalogNumber, fatherCN, newFatherCN);
		updateCustomer(catalogNumber , customer);
		
	}
	
	private void updateCustomer(String catalogNumber, String customer) 
	{
		try{
			
			connect();
			stmt = c.prepareStatement("UPDATE Tree SET customer = ? where CN = ?");
			stmt.setString(1, customer);
			stmt.setString(2, catalogNumber);
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
	
	public void updateFather(String catalogNumber, String fatherCN , String newFatherCN) 
	{
		try{
			
			connect();
			stmt = c.prepareStatement("UPDATE Tree SET fatherCN = ? WHERE CN = ? AND fatherCN = ?");
			stmt.setString(1, newFatherCN);
			stmt.setString(2, catalogNumber);
			stmt.setString(3, fatherCN);
			stmt.executeUpdate();
			
			c.commit();
			
			if(newFatherCN == null || newFatherCN.equals(""))
			{
				stmt = c.prepareStatement("SELECT COUNT(*) AS rowsCount FROM Tree where CN = ?");
				stmt.setString(1, catalogNumber);
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next())
				{
					int count = rs.getInt("rowsCount");
					if(count > 1)
					{
						closeConnection();
						removeCatalogNumber(catalogNumber , newFatherCN);
						connect();
					}
				}
			}
			
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
	
	public void removeCatalogNumber(String catalogNumber, String fatherCN) 
	{
		try{
			
			connect();
			stmt = c.prepareStatement("DELETE FROM Tree WHERE CN = ? AND fatherCN = ?");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, fatherCN);
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
	public User getUser(String nickName) 
	{
		User user = null;
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT * FROM Permissions where nickName = ?");
			stmt.setString(1, nickName);
			
			ResultSet rs = stmt.executeQuery();
			
			String email;
			boolean adminPermission , purchasingPermission;
			if(rs.next())
			{
				email = rs.getString("ID");
				adminPermission =  rs.getBoolean("permission");
				purchasingPermission =  adminPermission || rs.getBoolean("purchasing");
			}
			else 
			{
				closeConnection();
				return null;
			}
				
			closeConnection();
			
			List<String> customers = getCustomersOfUser(nickName);
			String signature = getSignature(nickName);
			user = new User(nickName, email, adminPermission, purchasingPermission, signature , customers);

			return user;
		
		}catch(SQLException e)
		{
			closeConnection();
			return null;
		}
		
	}
	public boolean updateUser(String nickName, String email, boolean adminPermission, boolean purchasingPermission, String signature
			, List<String> customers) 
	{
		String password = getPassword(email);
		if(password == null)
			return false;
		if(deleteUser(nickName))
		{
			if(addUser(nickName, email, password, adminPermission, purchasingPermission, signature))
				return addCustomersToUser(nickName, customers);
		}
		
		return false;
	}
	
	private String getPassword(String email) 
	{
		String password = null;
		try
		{
			connect();
			stmt = c.prepareStatement("SELECT password FROM Permissions where ID = ?");
			stmt.setString(1, email);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
			{
				String encryptedPassword = rs.getString("password");
				try 
				{
					password = Globals.decrypt(encryptedPassword);
				} 
				catch (Exception e) 
				{
					password = null;
				}
			}
			else 
				password =  null;
			
			closeConnection();
			
			return password;
		}catch(SQLException e)
		{
			return null;
		}

	}
	
	public void updateInitProduct(String catalogNumber, String quantity, String initDate,String previousRequireDate ,  String requireDate , FormType type)
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
			
			stmt = c.prepareStatement("UPDATE " + tableName +" SET quantity = ? , requireDate = ? where CN = ? AND initDate = ? AND requireDate = ?");
			stmt.setString(1, quantity);
			stmt.setString(2, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(3, catalogNumber);
			stmt.setString(4, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(5, Globals.parseDateToSqlFormatString(previousRequireDate));
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
	public List<ProductInit> getAllProductsInit(String userName) 
	{
		List<ProductInit> productsInit = new ArrayList<>();
		List<String> catalogNumbers = getAllCatalogNumbers(userName);
		List<FormType> formTypes = globals.getAllFormTypes();
		
		try
		{
			connect();
			for (String catalogNumber : catalogNumbers) 
			{
				for (FormType formType : formTypes) 
				{
					String tableName = null;
					
					switch (formType) 
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
							break;
					}
					
					stmt = c.prepareStatement("SELECT * FROM " + tableName +" where CN = ?");
					stmt.setString(1, catalogNumber);
					ResultSet rs = stmt.executeQuery();
					
					while(rs.next())
					{
						String quantity = rs.getString("quantity");
						String initDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("initDate")));
						String requireDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("requireDate")));
						
						ProductInit productInit = new ProductInit(catalogNumber, quantity, initDate, requireDate, formType);
						
						productsInit.add(productInit);
					}
					
				}
			}
			
			closeConnection();
			return productsInit;
		
		}catch(SQLException e)
		{
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	
	public List<ProductInitHistory> getAllProductsInitHistory(String userName) 
	{
		List<ProductInitHistory> productsInitHistory = new ArrayList<>();
		List<String> catalogNumbers = getAllCatalogNumbers(userName);
		List<FormType> formTypes = globals.getAllFormTypes();
		
		try
		{
			connect();
			for (String catalogNumber : catalogNumbers) 
			{
				for (FormType formType : formTypes) 
				{
					String tableName = null;
					
					switch (formType) 
					{
						case SHIPMENT:
							tableName = "InitProductShipmentsHistory";
							break;
						case PO:
							tableName = "InitProductCustomerOrdersHistory";
							break;
						case WO:
							tableName = "InitProductWorkOrderHistory";
							break;
						case FC:
							tableName = "InitProductForecastHistory";
							break;
						default:
							break;
					}
					
					stmt = c.prepareStatement("SELECT * FROM " + tableName +" where CN = ? ORDER BY changeDate desc");
					stmt.setString(1, catalogNumber);
					ResultSet rs = stmt.executeQuery();
					
					while(rs.next())
					{
						String quantity = rs.getString("quantity");
						String initDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("initDate")));
						String requireDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("requireDate")));
						String changeDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("changeDate")));
						String userUpdate = rs.getString("userUpdate");
						String note = rs.getString("note");
						ProductInitHistory productInitHistory = new ProductInitHistory(catalogNumber, quantity, initDate, requireDate
								, changeDate, userUpdate, note, formType);
						
						productsInitHistory.add(productInitHistory);
					}
					
				}
			}
			
			closeConnection();
			return productsInitHistory;
		
		}catch(SQLException e)
		{
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	
	public List<ProductInitHistory> getProductInitHistory(String catalogNumber) 
	{
		List<ProductInitHistory> productsInitHistory = new ArrayList<>();
		List<FormType> formTypes = globals.getAllFormTypes();
		
		try
		{
			connect();
			for (FormType formType : formTypes) 
			{
				String tableName = null;
				
				switch (formType) 
				{
					case SHIPMENT:
						tableName = "InitProductShipmentsHistory";
						break;
					case PO:
						tableName = "InitProductCustomerOrdersHistory";
						break;
					case WO:
						tableName = "InitProductWorkOrderHistory";
						break;
					case FC:
						tableName = "InitProductForecastHistory";
						break;
					default:
						break;
				}
				
				stmt = c.prepareStatement("SELECT * FROM " + tableName +" where CN = ? ORDER BY changeDate desc");
				stmt.setString(1, catalogNumber);
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next())
				{
					String quantity = rs.getString("quantity");
					String initDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("initDate")));
					String requireDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("requireDate")));
					String changeDate = Globals.dateWithoutHourToString(Globals.parseDateFromSqlFormat(rs.getString("changeDate")));
					String userUpdate = rs.getString("userUpdate");
					String note = rs.getString("note");
					ProductInitHistory productInitHistory = new ProductInitHistory(catalogNumber, quantity, initDate, requireDate
							, changeDate, userUpdate, note, formType);
					
					productsInitHistory.add(productInitHistory);
				}
				
			}
			
			closeConnection();
			return productsInitHistory;
		
		}catch(SQLException e)
		{
			closeConnection();
			return new ArrayList<>();
		}
		
	}
	
	public MonthDate getLastCalculateMapDate() 
	{
		MonthDate requireDate = null;
		
		try{
			
			connect();	
			stmt =  c.prepareStatement("SELECT MAX(date(date)) AS date FROM (SELECT date FROM MaterialAvailability UNION "
					+ "SELECT date FROM WorkOrderAfterSupplied UNION SELECT date FROM OpenCustomerOrder UNION "
					+ "SELECT date FROM WorkOrderAfterCustomerOrderAndParentWorkOrder)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
				{
					MonthDate maxMapCalculateDate = new MonthDate(Globals.parseDateFromSqlFormat(date));
					requireDate = maxMapCalculateDate;
				}
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
	
	public Map<String, ProductColumn> getLastMap(String userName , Map<String, String> catalogNumbers , MonthDate lastCalculateMapDate) 
	{
		Map<String,ProductColumn> lastMap = new HashMap<String,ProductColumn>();
		
		try{
			
			connect();
			for (String catalogNumber : catalogNumbers.keySet())
			{	
				closeConnection();
				String descendantCatalogNumber = getDescendantCatalogNumber(catalogNumber);
				connect();
				
				
				double materialAvailability = 0;
				double workOrderAfterSupplied = 0;
				double openCustomerOrder = 0;
				double workOrderAfterCustomerOrderAndParentWorkOrder = 0;
	
				stmt =  c.prepareStatement("SELECT * FROM MaterialAvailability where CN = ? AND date(date) = date(?)");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(lastCalculateMapDate));
				ResultSet rs = stmt.executeQuery();

				if(rs.next())
					materialAvailability = rs.getDouble("quantity");
				
				stmt =  c.prepareStatement("SELECT * FROM WorkOrderAfterSupplied where CN = ? AND date(date) = date(?)");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(lastCalculateMapDate));
				rs = stmt.executeQuery();
		
				if(rs.next())
					workOrderAfterSupplied = rs.getDouble("quantity");
				
				stmt =  c.prepareStatement("SELECT * FROM OpenCustomerOrder where CN = ? AND date(date) = date(?)");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(lastCalculateMapDate));
				rs = stmt.executeQuery();
		
				if(rs.next())
					openCustomerOrder = rs.getDouble("quantity");
				
				stmt =  c.prepareStatement("SELECT * FROM WorkOrderAfterCustomerOrderAndParentWorkOrder where CN = ? AND date(date) = date(?)");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(lastCalculateMapDate));
				rs = stmt.executeQuery();
		
				if(rs.next())
					workOrderAfterCustomerOrderAndParentWorkOrder = rs.getDouble("quantity");
					
				ProductColumn productColumn = new ProductColumn(descendantCatalogNumber, catalogNumbers.get(descendantCatalogNumber), 0, materialAvailability
						, 0, workOrderAfterSupplied, workOrderAfterCustomerOrderAndParentWorkOrder , 0, 0, 0 , 0 ,openCustomerOrder);
				
				if(lastMap.containsKey(descendantCatalogNumber))
					lastMap.get(descendantCatalogNumber).addProductColumn(productColumn);
				else
					lastMap.put(descendantCatalogNumber, productColumn);
			}
			
			closeConnection();
			return lastMap;	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new HashMap<String,ProductColumn>();
		}
	}
	
	public void updateMap(Map<String, ProductColumn> newMap , java.util.Date newCalculateMapDate) 
	{
		try{
			connect();
			for (String catalogNumber : newMap.keySet())
			{	
				ProductColumn productColumn = newMap.get(catalogNumber);
				double materialAvailability = productColumn.getMaterialAvailability();
				double workOrderAfterSupplied = productColumn.getWorkOrderAfterSupplied();
				double openCustomerOrder = productColumn.getOpenCustomerOrder();
				double workOrderAfterCustomerOrderAndParentWorkOrder = productColumn.getWorkOrderAfterCustomerOrderAndParentWorkOrder();
	
				stmt = c.prepareStatement("SELECT quantity from MaterialAvailability where CN = ? AND date = ?");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next())
				{
					
					stmt =  c.prepareStatement("UPDATE MaterialAvailability SET quantity = ? where date = ? AND CN = ?");
					stmt.setDouble(1, materialAvailability);
					stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
					stmt.setString(3, catalogNumber);
					stmt.executeUpdate();
					c.commit();
				}
				else
				{
					closeConnection();
					addNewMaterialAvailability(catalogNumber , newCalculateMapDate , materialAvailability);
					connect();
				}
				
				stmt = c.prepareStatement("SELECT quantity from WorkOrderAfterSupplied where CN = ? AND date = ?");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
				rs = stmt.executeQuery();
				
				if(rs.next())
				{
					stmt =  c.prepareStatement("UPDATE WorkOrderAfterSupplied SET quantity = ? where date = ? AND CN = ?");
					stmt.setDouble(1, workOrderAfterSupplied);
					stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
					stmt.setString(3, catalogNumber);
					stmt.executeUpdate();
					c.commit();	
				}
				else
				{
					closeConnection();
					addNewWorkOrderAfterSupplied(catalogNumber , newCalculateMapDate , workOrderAfterSupplied);
					connect();
				}
				
				stmt = c.prepareStatement("SELECT quantity from OpenCustomerOrder where CN = ? AND date = ?");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
				rs = stmt.executeQuery();
				
				if(rs.next())
				{
					stmt =  c.prepareStatement("UPDATE OpenCustomerOrder SET quantity = ? where date = ? AND CN = ?");
					stmt.setDouble(1, openCustomerOrder);
					stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
					stmt.setString(3, catalogNumber);
					stmt.executeUpdate();
					c.commit();	
				}
				else
				{
					closeConnection();
					addNewOpenCustomerOrder(catalogNumber , newCalculateMapDate , openCustomerOrder);
					connect();
				}
				
				stmt = c.prepareStatement("SELECT quantity from WorkOrderAfterCustomerOrderAndParentWorkOrder where CN = ? AND date = ?");
				stmt.setString(1, catalogNumber);
				stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
				rs = stmt.executeQuery();
				
				if(rs.next())
				{
					stmt =  c.prepareStatement("UPDATE WorkOrderAfterCustomerOrderAndParentWorkOrder SET quantity = ? where date = ? AND CN = ?");
					stmt.setDouble(1, workOrderAfterCustomerOrderAndParentWorkOrder);
					stmt.setString(2, Globals.dateToSqlFormatString(newCalculateMapDate));
					stmt.setString(3, catalogNumber);
					stmt.executeUpdate();
					c.commit();	
				}
				else
				{
					closeConnection();
					addNewWorkOrderAfterCustomerOrderAndParentWorkOrder(catalogNumber , newCalculateMapDate , workOrderAfterCustomerOrderAndParentWorkOrder);
					connect();
				}
				
			}
			
			closeConnection();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			closeConnection();
		}
		
	}
	
	private void addNewWorkOrderAfterCustomerOrderAndParentWorkOrder(String catalogNumber,java.util.Date newCalculateMapDate
			, double workOrderAfterCustomerOrderAndParentWorkOrder) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("INSERT INTO WorkOrderAfterCustomerOrderAndParentWorkOrder (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setDouble(2, workOrderAfterCustomerOrderAndParentWorkOrder);
			stmt.setString(3, Globals.dateToSqlFormatString(newCalculateMapDate));
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
	
	private void addNewOpenCustomerOrder(String catalogNumber, java.util.Date newCalculateMapDate, double openCustomerOrder) 
	{
		
		try
		{
			connect();
			stmt = c.prepareStatement("INSERT INTO OpenCustomerOrder (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setDouble(2, openCustomerOrder);
			stmt.setString(3, Globals.dateToSqlFormatString(newCalculateMapDate));
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

	private void addNewWorkOrderAfterSupplied(String catalogNumber, java.util.Date newCalculateMapDate, double workOrderAfterSupplied) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("INSERT INTO WorkOrderAfterSupplied (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setDouble(2, workOrderAfterSupplied);
			stmt.setString(3, Globals.dateToSqlFormatString(newCalculateMapDate));
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

	private void addNewMaterialAvailability(String catalogNumber, java.util.Date newCalculateMapDate, double materialAvailability) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("INSERT INTO MaterialAvailability (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setDouble(2, materialAvailability);
			stmt.setString(3, Globals.dateToSqlFormatString(newCalculateMapDate));
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
	public void clearLastMap(java.util.Date date) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM MaterialAvailability where date = ?");
			stmt.setString(1, Globals.dateToSqlFormatString(date));
			stmt.executeUpdate();
			
			c.commit();
			
			stmt = c.prepareStatement("DELETE FROM WorkOrderAfterSupplied where date = ?");
			stmt.setString(1, Globals.dateToSqlFormatString(date));
			stmt.executeUpdate();
			
			c.commit();
			
			stmt = c.prepareStatement("DELETE FROM OpenCustomerOrder where date = ?");
			stmt.setString(1, Globals.dateToSqlFormatString(date));
			stmt.executeUpdate();
			
			c.commit();
			
			stmt = c.prepareStatement("DELETE FROM WorkOrderAfterCustomerOrderAndParentWorkOrder where date = ?");
			stmt.setString(1, Globals.dateToSqlFormatString(date));
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
	public List<String> getSons(String catalogNumber) 
	{
		List<String> sons = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT CN FROM Tree where fatherCN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String sonCatalogNumber = rs.getString("CN");
				sons.add(sonCatalogNumber);
			}
			
			closeConnection();
			
			return sons;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	private boolean hisFather(String catalogNumber, String fatherCatalogNumber) 
	{
		boolean hisFather;
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT fatherCN,quantity FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			hisFather = rs.next();
			
			closeConnection();
			
			return hisFather;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return false;
		}
	}
	
	public Map<String, String> getDescription(String catalogNumber) 
	{
		Map<String, String> catalogNumbers = new HashMap<String,String>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT distinct description FROM Tree where CN = ?");
			stmt.setString(1, catalogNumber);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next())
			{
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
	
	public java.util.Date getLastLoad() 
	{
		java.util.Date lastLoadDate = null;
		
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT Max(date(date)) AS date FROM (SELECT shipmentDate AS date FROM Shipments UNION SELECT date FROM WorkOrder UNION SELECT orderDate AS date FROM CustomerOrders)");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
					lastLoadDate = Globals.parseDateFromSqlFormat(date);
			}
			
			closeConnection();
			return lastLoadDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
	public void addNewInitProductHistory(String catalogNumber, String quantity, String initDate, String requireDate,String changeDate
			, String note,String userName,FormType type) 
	{
		
		String tableName;
		
		switch (type) 
		{
			case SHIPMENT:
				tableName = "InitProductShipmentsHistory";
				break;
			case PO:
				tableName = "InitProductCustomerOrdersHistory";
				break;
			case WO:
				tableName = "InitProductWorkOrderHistory";
				break;
			case FC:
				tableName = "InitProductForecastHistory";
				break;
			default:
				return;
		}
		
		try
		{
			connect();		
			
			stmt = c.prepareStatement("INSERT INTO " + tableName +" (CN , quantity , initDate ,requireDate, changeDate , userUpdate , note) VALUES(?,?,?,?,?,?,?)");
			stmt.setString(1, catalogNumber);
			stmt.setString(2, quantity);
			stmt.setString(3, Globals.parseDateToSqlFormatString(initDate));
			stmt.setString(4, Globals.parseDateToSqlFormatString(requireDate));
			stmt.setString(5, Globals.parseDateToSqlFormatString(changeDate));
			stmt.setString(6, userName);
			stmt.setString(7, note);
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
	
	public List<Forecast> getForecastBetweenDates(String userName , java.util.Date from , java.util.Date to)
	{
		try{
			List<Forecast> forecasts = new ArrayList<>();
			
			List<String> customers = getCustomersOfUser(userName);
			
			connect();
			
			for (String customer : customers) 
			{
				stmt = c.prepareStatement("SELECT * from Forecast where customer = ? AND date(initDate) >= date(?) AND date(initDate) <= date(?) order by CN");
				stmt.setString(1, customer);
				stmt.setString(2, Globals.dateToSqlFormatString(from));
				stmt.setString(3, Globals.dateToSqlFormatString(to));
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next())
				{
					int id = rs.getInt("id");
					String catalogNumber = rs.getString("CN");
					String description = rs.getString("description");
					String quantity = rs.getString("quantity");
					java.util.Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
					java.util.Date requireDate = Globals.parseDateFromSqlFormat(rs.getString("requireDate"));
					String user = rs.getString("userName");
					String notes = rs.getString("notes");
							
					Forecast forecast =  new Forecast(id,customer, catalogNumber, quantity, initDate, requireDate, description, user , notes);
					forecasts.add(forecast);
				}


			}
						
			closeConnection();
			return forecasts;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return new ArrayList<>();
		}
	}
	
	public boolean deleteProduct(String catalogNumber)
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM Tree Where CN = ?");
			stmt.setString(1, catalogNumber);
			int rowsNumber = stmt.executeUpdate();
			
			c.commit();
			closeConnection();
			
			List<String> sons = getSons(catalogNumber);	
			
			for (String son : sons) 
				updateFather(son, catalogNumber, "");	
			
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
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean addConnectingComputers(String  computerName)
	{
		try
		{
			connect();		
			
			stmt = c.prepareStatement("INSERT INTO ConnectingComputers (ComputerName) VALUES(?)");
			stmt.setString(1, computerName);
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
	
	public boolean deleteConnectingComputers(String computerName)
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM ConnectingComputers where ComputerName = ? AND "
					+ "rowid IN (Select rowid from ConnectingComputers where ComputerName = ? limit 1);");
			stmt.setString(1, computerName);
			stmt.setString(2, computerName);
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
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getConnectingComputers()
	{
		List<String> computers = new ArrayList<>();
		
		try
		{
			connect();		
			
			stmt = c.prepareStatement("SELECT distinct ComputerName FROM ConnectingComputers");
			ResultSet rs = stmt.executeQuery();
						
			while(rs.next())
			{
				String computerName = rs.getString("ComputerName");
				computers.add(computerName);
			}
			
			
			closeConnection();
			
			return computers;
			
		}
		catch(SQLException e)
		{
			closeConnection();
			
			return new ArrayList<>();
		}
	}
	public boolean mapContainsDate(MonthDate maximumDate) 
	{
		try{
			
			connect();	
			stmt =  c.prepareStatement("SELECT date FROM (SELECT date FROM MaterialAvailability UNION "
					+ "SELECT date FROM WorkOrderAfterSupplied UNION SELECT date FROM OpenCustomerOrder UNION "
					+ "SELECT date FROM WorkOrderAfterCustomerOrderAndParentWorkOrder) where date(date) = date(?)");
			stmt.setString(1, Globals.dateToSqlFormatString(maximumDate));
			ResultSet rs = stmt.executeQuery();

			boolean result = rs.next();
			
			closeConnection();
			return result;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return false;
		}
	}
	
	public boolean updateLastUpdateDate(UpdateType type)
	{
		try
		{
			connect();		
			
			stmt = c.prepareStatement("INSERT INTO UpdateDates (updateType , date) VALUES(?,?)");
			stmt.setString(1, type.toString());
			stmt.setString(2, LocalDateTime.now().toString());
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
	
	public java.util.Date getLastUpdateDate(UpdateType type) 
	{
		DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		
		try{
			
			connect();	
			stmt =  c.prepareStatement("SELECT MAX(strftime('%Y-%m-%dT%H:%M:%S',date)) AS date FROM UpdateDates where updateType = ?");
			stmt.setString(1, type.toString());
			ResultSet rs = stmt.executeQuery();

			java.util.Date out = null;
			
			if(rs.next() && rs.getString("date") != null)
			{
				LocalDateTime ldt = LocalDateTime.from(df.parse(rs.getString("date")));
				out = java.util.Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			}
			
			closeConnection();
			
			return out;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
		
	}
	
	public double getPriceOfProduct(String cn)
	{
		try{
			
			connect();	
			stmt =  c.prepareStatement("select CN , price , MAX(orderDate) as orderDate from CustomerOrders where CN = ? Group by CN");
			stmt.setString(1, cn);
			ResultSet rs = stmt.executeQuery();
			
			double price = 0;
			
			while(rs.next())
			{
				String priceString = rs.getString("price");
				if(NumberUtils.isCreatable(priceString))
				{
					price = Double.parseDouble(priceString);
					break;
				}
			}
			
			closeConnection();
			
			return price;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return -1;
		}
		
	}
	
	public double getCustomerDesposite(String customer) 
	{
		try
		{
		
			connect();
			stmt = c.prepareStatement("SELECT deposite FROM Projects where projectName = ?");
			stmt.setString(1, customer);
			ResultSet rs = stmt.executeQuery();
			
			double deposite = 0;
			
			if(rs.next())
				deposite =  rs.getDouble("deposite");
			
			closeConnection();
			return deposite;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return 0;
		}
	}
	
	public double getCustomerObligation(String customer) 
	{
		try
		{
		
			connect();
			stmt = c.prepareStatement("SELECT obligation FROM Projects where projectName = ?");
			stmt.setString(1, customer);
			ResultSet rs = stmt.executeQuery();
			
			double obligation = 0;
			
			if(rs.next())
				obligation =  rs.getDouble("obligation");
			
			closeConnection();
			return obligation;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return 0;
		}
	}
	
	
}
