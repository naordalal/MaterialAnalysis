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
			
			connect();
			stmt = c.prepareStatement("INSERT INTO WorkOrder (WOId , customer , CN , description ,quantity , date ) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, woNumber);
			stmt.setString(2, customer);
			stmt.setString(3, catalogNumber);
			stmt.setString(4, description);
			stmt.setString(5, quantity);
			stmt.setString(6, date);
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
	
	public boolean addCustomerOrder(String customer , String orderNumber , String catalogNumber , String description , String quantity , String price , String orderDate , String guaranteedDate) 
	{
		try
		{
			
			connect();
			stmt = c.prepareStatement("INSERT INTO CustomerOrders (orderNumber , customer , orderDate , CN , description ,quantity , price , guaranteedDate) VALUES (?,?,?,?,?,?,?,?)");
			stmt.setString(1, orderNumber);
			stmt.setString(2, customer);
			stmt.setString(3, orderDate);
			stmt.setString(4, catalogNumber);
			stmt.setString(5, description);
			stmt.setString(6, quantity);
			stmt.setString(7, price);
			stmt.setString(8, guaranteedDate);
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
			
			connect();
			stmt = c.prepareStatement("INSERT INTO Shipments (orderId , orderCustomerId , customer , CN , description , quantity , shipmentDate) VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, orderId);
			stmt.setString(2, orderCustomerId);
			stmt.setString(3, customer);
			stmt.setString(4, catalogNumber);
			stmt.setString(5, description);
			stmt.setString(6, quantity);
			stmt.setString(7, shipmentDate);
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
			stmt = c.prepareStatement("DELETE FROM WorkOrder Where date(date) > ?");
			stmt.setString(1, Globals.dateToSqlFormatString(Globals.addMonths(Globals.getTodayDate() , -months)));
			
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
			stmt = c.prepareStatement("DELETE FROM CustomerOrders Where date(orderDate) >= ?");
			stmt.setString(1, Globals.dateToSqlFormatString(Globals.addMonths(Globals.getTodayDate() , -months)));
			
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
			stmt = c.prepareStatement("INSERT INTO ? (CN , quantity , date) VALUES (?,?,?)");
			stmt.setString(1, tableName);
			stmt.setString(2, product);
			stmt.setString(3, Integer.toString(quantityPerDate.getQuantity()));
			stmt.setString(4, Globals.dateToSqlFormatString(quantityPerDate.getDate()));
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
			stmt = c.prepareStatement("UPDATE ? SET quantity = ? where CN = ? AND date = ?");
			stmt.setString(1, tableName);
			stmt.setString(2, Integer.toString(quantityPerDate.getQuantity()));
			stmt.setString(3, product);
			stmt.setString(4, Globals.dateToSqlFormatString(quantityPerDate.getDate()));
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
			stmt = c.prepareStatement("SELECT * FROM ?");		
			stmt.setString(1, tableName);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				String catalogNumber = rs.getString("CN");
				String quantity = rs.getString("quantity");
				MonthDate shipmentDate = new MonthDate(Globals.parseDate(rs.getString("date")));
				
				QuantityPerDate quantityPerDate = new QuantityPerDate(shipmentDate, Integer.parseInt(quantity));
				
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
				String orderId = rs.getString("orderId");
				String orderCustomerId = rs.getString("orderCustomerId");
				String customer = rs.getString("customer");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String shipmentDate = rs.getString("shipmentDate");
				
				Shipment shipment = new Shipment(customer, orderId, orderCustomerId , catalogNumber, quantity, Globals.parseDate(shipmentDate), description);
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
				String customer = rs.getString("customer");
				String orderNumber = rs.getString("orderNumber");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				String price = rs.getString("price");
				String orderDate = rs.getString("orderDate");
				Date guaranteedDate = Globals.parseDate(rs.getString("guaranteedDate"));
				
				CustomerOrder customerOrder = new CustomerOrder(customer, orderNumber, catalogNumber, description, quantity, price, orderDate, guaranteedDate);
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
				String customer = rs.getString("customer");
				String woNumber = rs.getString("WOId");
				String catalogNumber = rs.getString("CN");
				String description = rs.getString("description");
				String quantity = rs.getString("quantity");
				Date orderDate = Globals.parseDate(rs.getString("date"));
				
				WorkOrder customerOrder = new WorkOrder(woNumber, catalogNumber, quantity, customer, orderDate, description);
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
}
