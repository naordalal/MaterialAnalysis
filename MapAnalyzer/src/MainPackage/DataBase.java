package MainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import org.sqlite.SQLiteConfig;

import Forms.CustomerOrder;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.Globals.FormType;


public class DataBase 
{

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
	
	private void closeConnection()
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
	
	public boolean addWO(String woNumber , String catalogNumber , String quantity , String customer , String date , String description) 
	{
		try
		{
			if(quantity.trim().equals(""))
				quantity = "0";
			connect();
			stmt = c.prepareStatement("INSERT INTO WorkOrder (WOId , customer , CN , description ,quantity , date ) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, woNumber);
			stmt.setString(2, customer);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, description);
			stmt.setString(5, quantity);
			stmt.setString(6, Globals.parseDateToSqlFormatString(date));
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
	
	public boolean addCustomerOrder(String customer , String orderNumber , String customerOrderNumber , String catalogNumber , String description , String quantity , String price , String orderDate , String guaranteedDate) 
	{
		try
		{
			if(quantity.trim().equals(""))
				quantity = "0";
			connect();
			stmt = c.prepareStatement("INSERT INTO CustomerOrders (orderNumber , customerOrderNumber , customer , orderDate , CN , description ,quantity , price , guaranteedDate) VALUES (?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, orderNumber);
			stmt.setString(2, customerOrderNumber);
			stmt.setString(3, customer);
			stmt.setString(4, Globals.parseDateToSqlFormatString(orderDate));
			stmt.setString(5, catalogNumber);
			stmt.setString(6, description);
			stmt.setString(7, quantity);
			stmt.setString(8, price);
			stmt.setString(9, Globals.parseDateToSqlFormatString(guaranteedDate));
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
	
	
	public boolean addShipment(String customer , String orderId , String orderCustomerId , String catalogNumber , String quantity , String shipmentDate , String description) 
	{
		try
		{
			if(quantity.trim().equals(""))
				quantity = "0";
			connect();
			stmt = c.prepareStatement("INSERT INTO Shipments (orderId , orderCustomerId , customer , CN , description , quantity , shipmentDate) VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, orderId);
			stmt.setString(2, orderCustomerId);
			stmt.setString(3, customer);
			stmt.setString(4, catalogNumber);
			stmt.setString(5, description);
			stmt.setString(6, quantity);
			stmt.setString(7, Globals.parseDateToSqlFormatString(shipmentDate));
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

	public void removeHistoryOfForm(FormType type , int months)
	{
		
		switch (type) 
		{
			case PO:
				removeHistoryOfPO(months);
				break;
			case WO:
				removeHistoryOfWO(months);
				break;
			default:
				return;
		}
		
		
	}
	
	private void removeHistoryOfWO(int months) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM WorkOrder Where date(date) > date(?)");
			stmt.setString(1, Globals.dateToSqlFormatString(Globals.setFirstDayOfMonth(Globals.addMonths(Globals.getTodayDate() , -months))));
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

	private void removeHistoryOfPO(int months) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM CustomerOrders Where date(orderDate) > date(?)");
			stmt.setString(1, Globals.dateToSqlFormatString(Globals.setFirstDayOfMonth(Globals.addMonths(Globals.getTodayDate() , -months))));
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
			default:
				return;
		}
		
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO " + tableName + " (CN , quantity , date) VALUES (?,?,?)");
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

	public Map<String, List<QuantityPerDate>> getAllProductsFormQuantityPerDate(FormType type)
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
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM " + tableName);		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
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
	
	public Map<String, List<QuantityPerDate>> getAllProductsShipmentQuantityPerDate() 
	{
		return getAllProductsFormQuantityPerDate(FormType.SHIPMENT);
	}

	public List<Shipment> getAllShipments() 
	{
		List<Shipment> shipments = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM Shipments");		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				String customer = rs.getString("customer");
				String catalogNumber = rs.getString("CN");
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

	public List<CustomerOrder> getAllPO() 
	{
		List<CustomerOrder> customerOrders = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM CustomerOrders");		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String orderNumber = rs.getString("orderNumber");
				String customerOrderNumber = rs.getString("customerOrderNumber");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("orderDate"));
				Date guaranteedDate = Globals.parseDateFromSqlFormat(rs.getString("guaranteedDate"));
				
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

	public Map<String, List<QuantityPerDate>> getAllProductsPOQuantityPerDate() 
	{
		return getAllProductsFormQuantityPerDate(FormType.PO);
	}

	public List<WorkOrder> getAllWO() 
	{
		List<WorkOrder> workOrders = new ArrayList<>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM WorkOrder");		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String customer = rs.getString("customer");
				String woNumber = rs.getString("WOId");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				Date orderDate = Globals.parseDateFromSqlFormat(rs.getString("date"));
				
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

	public Map<String, List<QuantityPerDate>> getAllProductsWOQuantityPerDate() 
	{
		return getAllProductsFormQuantityPerDate(FormType.WO);
	}

	public Map<String,List<QuantityPerDate>> getInitProductsFormQuantityPerDate(FormType type)
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
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM " + tableName);		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
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
	
	public Map<String, List<QuantityPerDate>> getInitProductsPOQuantityPerDate() 
	{
		return getInitProductsFormQuantityPerDate(FormType.PO);
	}

	public Map<String, List<QuantityPerDate>> getInitProductsWOQuantityPerDate() 
	{
		return getInitProductsFormQuantityPerDate(FormType.WO);
	}

	public Map<String, List<QuantityPerDate>> getInitProductsShipmentQuantityPerDate() 
	{
		return getInitProductsFormQuantityPerDate(FormType.SHIPMENT);
	}

	public Map<String, Date> getInitProductsFormDates(FormType type)
	{
		Map<String, Date> productFormQuantityPerDate = new HashMap<>();
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
			default:
				return new HashMap<>();
		}
		
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT * FROM " + tableName);		
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
				Date initDate = Globals.parseDateFromSqlFormat(rs.getString("initDate"));
				
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
	
	public Map<String, Date> getInitProductsPODates() 
	{
		return getInitProductsFormDates(FormType.PO);
	}

	public Map<String, Date> getInitProductsWODates() 
	{
		return getInitProductsFormDates(FormType.WO);
	}

	public Map<String, Date> getInitProductsShipmentsDates() 
	{
		return getInitProductsFormDates(FormType.SHIPMENT);
	}

	public void removeProductQuantity(String CatalogNumber, MonthDate date)
	{
		String[] tablesName = {"productShipments" ,"productCustomerOrders" ,"productWorkOrder" , "productForecast"}  ;

		for (String tableName : tablesName) 
		{
			try{
				
				connect();
				stmt = (date == null) ? c.prepareStatement("DELETE FROM " + tableName +" Where CN = ?") : c.prepareStatement("DELETE FROM " + tableName +" Where CN = ? AND date(date) = date(?)");
				stmt.setString(1, CatalogNumber);
				if(date != null)
					stmt.setString(2, Globals.dateToSqlFormatString(date));
				
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
		
	}

	public Date getMaximumShipmentDate() 
	{
		Date shipmentDate = null;
		try{
			
			connect();
			stmt =  c.prepareStatement("SELECT Max(date(shipmentDate)) AS date FROM Shipments");
			ResultSet rs = stmt.executeQuery();

			if(rs.next())
			{
				String date = rs.getString("date");
				if(date != null && !date.trim().equals(""))
					shipmentDate = Globals.parseDateFromSqlFormat(date);
			}
			
			closeConnection();
			
			return shipmentDate;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			closeConnection();
			return null;
		}
	}
}
