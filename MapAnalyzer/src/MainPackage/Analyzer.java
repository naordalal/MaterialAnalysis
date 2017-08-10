package MainPackage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Analyzer 
{
	private Globals globals;
	private DataBase db;

	public Analyzer() 
	{
		globals = new Globals();
		db = new DataBase();
	}
	
	public void analyze() throws IOException
	{
		analyzeWO(globals.WOFilePath);
		analyzeCustomerOrders(globals.customerOrdersFilePath);
		analyzeShipments(globals.shipmentsFilePath);
	}
	
	private void analyzeWO(String filePath) throws IOException 
	{
		int woNumberColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , customerColumn = -1 , dateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName("IBM862")))
		{
			List<String> columns = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
			if(woNumberColumn == -1) 
				woNumberColumn = columns.indexOf(globals.woNumberColumn);
			if(catalogNumberColumn == -1) 
				catalogNumberColumn = columns.indexOf(globals.catalogNumberColumn);
			if(quantityColumn == -1) 
				quantityColumn = columns.indexOf(globals.quantityColumn);
			if(customerColumn == -1) 
				customerColumn = columns.indexOf(globals.customerColumn);
			if(dateColumn == -1) 
				dateColumn = columns.indexOf(globals.dateColumn);
			if(descriptionColumn == -1) 
				descriptionColumn = columns.indexOf(globals.descriptionColumn);
			else
			{
				db.addWO(columns.get(woNumberColumn), columns.get(catalogNumberColumn), columns.get(quantityColumn)
						, columns.get(customerColumn), columns.get(dateColumn), columns.get(descriptionColumn));
			}
		}	
	}
	
	private void analyzeCustomerOrders(String filePath) throws IOException 
	{
		int customerColumn = -1 , orderNumberColumn = -1 , catalogNumberColumn = -1 , descriptionColumn = -1 , quantityColumn = -1 , priceColumn = -1,
				orderDateColumn = - 1 , guaranteedDateColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName("IBM862")))
		{
			List<String> columns = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
			if(customerColumn == -1) 
				customerColumn = columns.indexOf(globals.customerIdColumn);
			if(orderNumberColumn == -1) 
				orderNumberColumn = columns.indexOf(globals.orderNumberColumn);
			if(catalogNumberColumn == -1) 
				catalogNumberColumn = columns.indexOf(globals.catalogNumberColumn);
			if(descriptionColumn == -1) 
				descriptionColumn = columns.indexOf(globals.descriptionColumn);
			if(quantityColumn == -1) 
				quantityColumn = columns.indexOf(globals.quantityOrderColumn);
			if(priceColumn == -1) 
				priceColumn = columns.indexOf(globals.priceColumn);
			if(orderDateColumn == -1) 
				orderDateColumn = columns.indexOf(globals.orderDateColumn);
			if(guaranteedDateColumn == -1) 
				guaranteedDateColumn = columns.indexOf(globals.guaranteedDateColumn);
			else
			{
				db.addCustomerOrder(columns.get(customerColumn), columns.get(orderNumberColumn), columns.get(catalogNumberColumn)
						, columns.get(descriptionColumn), columns.get(quantityColumn), columns.get(priceColumn) 
						, columns.get(orderDateColumn) , columns.get(guaranteedDateColumn));
			}
		}
	}
	
	private void analyzeShipments(String filePath) throws IOException 
	{
		int customerColumn = -1 , shipmentIdColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , shipmentDateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName("IBM862")))
		{
			List<String> columns = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
			if(customerColumn == -1) 
				customerColumn = columns.indexOf(globals.customerIdColumn);
			if(shipmentIdColumn == -1) 
				shipmentIdColumn = columns.indexOf(globals.shipmentIdColumn);
			if(catalogNumberColumn == -1) 
				catalogNumberColumn = columns.indexOf(globals.catalogNumberColumn);
			if(quantityColumn == -1) 
				quantityColumn = columns.indexOf(globals.quantityColumn);
			if(shipmentDateColumn == -1) 
				shipmentDateColumn = columns.indexOf(globals.shipmentDateColumn);
			if(descriptionColumn == -1) 
				descriptionColumn = columns.indexOf(globals.descriptionColumn);
			else
			{
				db.addShipment(columns.get(customerColumn), columns.get(shipmentIdColumn), columns.get(catalogNumberColumn)
						, columns.get(quantityColumn), columns.get(shipmentDateColumn), columns.get(descriptionColumn));
			}
		}
	}

}
