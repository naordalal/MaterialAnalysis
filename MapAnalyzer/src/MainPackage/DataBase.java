package MainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.sqlite.SQLiteConfig;

import Forms.CustomerOrder;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.Globals.FormType;
import MainPackage.Globals.UpdateType;


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
			case SHIPMENT:
				removeHistoryOfShipments(months);
			default:
				return;
		}
		
		
	}
	
	private void removeHistoryOfWO(int months) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM WorkOrder Where date(date) >= date(?)");
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
	
	private void removeHistoryOfShipments(int months) 
	{
		try
		{
			connect();
			stmt = c.prepareStatement("DELETE FROM Shipments Where date(shipmentDate) >= date(?)");
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
			stmt = c.prepareStatement("DELETE FROM CustomerOrders Where date(orderDate) >= date(?)");
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
	
	public Map<String, List<QuantityPerDate>> getInitProductsPOQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.PO , catalogNumber);
	}

	public Map<String, List<QuantityPerDate>> getInitProductsWOQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.WO , catalogNumber);
	}

	public Map<String, List<QuantityPerDate>> getInitProductsShipmentsQuantityPerDate(String catalogNumber) 
	{
		return getInitProductsFormQuantityPerDate(FormType.SHIPMENT , catalogNumber);
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

	public Map<String, String> getAllCatalogNumbersPerDescription() 
	{
		Map<String, String> catalogNumbers = new HashMap<String,String>();
		try{
			
			connect();
			stmt = c.prepareStatement("SELECT distinct CN,description FROM Tree");
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

	public Map<String, ProductColumn> getLastMap(Date lastCalculateMapDate) 
	{
		Map<String,ProductColumn> lastMap = new HashMap<String,ProductColumn>();
		Map<String, String> catalogNumbers = getAllCatalogNumbersPerDescription();
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
					
				ProductColumn productColumn = new ProductColumn(descendantCatalogNumber, catalogNumbers.get(catalogNumber), 0, materialAvailability
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
	
	private void addNewOpenCustomerOrder(String catalogNumber, Date newCalculateMapDate, double openCustomerOrder) 
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

	private void addNewWorkOrderAfterSupplied(String catalogNumber, Date newCalculateMapDate, double workOrderAfterSupplied) 
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

	private void addNewMaterialAvailability(String catalogNumber, Date newCalculateMapDate, double materialAvailability) 
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
}
