package MainPackage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import MainPackage.Globals.FormType;

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
		db.removeHistoryOfForm(FormType.PO, globals.monthsToIgnore);
		db.removeHistoryOfForm(FormType.WO, globals.monthsToIgnore);
		
		analyzeWO(globals.WOFilePath);
		analyzeCustomerOrders(globals.customerOrdersFilePath);
		analyzeShipments(globals.shipmentsFilePath);
		
		updateProductQuantities(db.getAllPO(), db.getAllProductsPOQuantityPerDate(), db.getInitProductsPOQuantityPerDate() , db.getInitProductsPODates(),  FormType.PO);
		updateProductQuantities(db.getAllWO(), db.getAllProductsWOQuantityPerDate(),db.getInitProductsWOQuantityPerDate(),db.getInitProductsWODates() , FormType.WO);
		updateProductQuantities(db.getAllShipments(), db.getAllProductsShipmentQuantityPerDate(),db.getInitProductsShipmentQuantityPerDate(),db.getInitProductsShipmentsDates() , FormType.SHIPMENT);
	}

	private void analyzeWO(String filePath) throws IOException 
	{
		int woNumberColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , customerColumn = -1 , dateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName(globals.charsetName)))
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
				Date date = Globals.parseDate(columns.get(dateColumn));
				if(Globals.addMonths(Globals.getTodayDate(), -globals.monthsToIgnore).before(date))
					db.addWO(columns.get(woNumberColumn), columns.get(catalogNumberColumn), columns.get(quantityColumn)
							, columns.get(customerColumn), columns.get(dateColumn), columns.get(descriptionColumn));
			}
		}	
	}
	
	private void analyzeCustomerOrders(String filePath) throws IOException 
	{
		int customerColumn = -1 , orderNumberColumn = -1 , catalogNumberColumn = -1 , descriptionColumn = -1 , quantityColumn = -1 , priceColumn = -1,
				orderDateColumn = - 1 , guaranteedDateColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName(globals.charsetName)))
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
				Date date = Globals.parseDate(columns.get(orderDateColumn));
				if(Globals.addMonths(Globals.getTodayDate(), -globals.monthsToIgnore).before(date))
					db.addCustomerOrder(columns.get(customerColumn), columns.get(orderNumberColumn), columns.get(catalogNumberColumn)
							, columns.get(descriptionColumn), columns.get(quantityColumn), columns.get(priceColumn) 
							, columns.get(orderDateColumn) , columns.get(guaranteedDateColumn));
			}
		}
	}
	
	private void analyzeShipments(String filePath) throws IOException 
	{
		int customerColumn = -1 , orderIdColumn = -1 , orderCustomerIdColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , shipmentDateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(Paths.get(filePath),Charset.forName(globals.charsetName)))
		{
			List<String> columns = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
			if(customerColumn == -1) 
				customerColumn = columns.indexOf(globals.customerIdColumn);
			if(orderIdColumn == -1) 
				orderIdColumn = columns.indexOf(globals.orderIdColumn);
			if(orderCustomerIdColumn == -1) 
				orderCustomerIdColumn = columns.indexOf(globals.orderCustomerIdColumn);
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
				Date date = Globals.parseDate(columns.get(shipmentDateColumn));
				if(Globals.addDays(Globals.getTodayDate(), -2).before(date))
					db.addShipment(columns.get(customerColumn), columns.get(orderIdColumn), columns.get(orderCustomerIdColumn) ,columns.get(catalogNumberColumn)
							, columns.get(quantityColumn), columns.get(shipmentDateColumn), columns.get(descriptionColumn));
			}
		}
	}
	
	public void updateProductQuantities(List<? extends Form> forms ,  Map<String, List<QuantityPerDate>> productsQuantityPerDate 
			, Map<String , List<QuantityPerDate>> initProductsQuantityPerDate, Map<String , Date> productsInitDates ,  FormType type)
	{
		Map<MonthDate,List<Form>> newFormsPerDate = new HashMap<>();
		
		for (Form form : forms) 
		{
			if(productsInitDates.containsKey(form.getCatalogNumber()))
				if(form.getCreateDate().before(productsInitDates.get(form.getCatalogNumber())))
					continue;
			MonthDate monthDate = new MonthDate(form.getRequestDate());
			if(newFormsPerDate.containsKey(monthDate))
				newFormsPerDate.get(monthDate).add(form);
			else
			{
				List<Form> formOfMonth = new ArrayList<Form>();
				formOfMonth.add(form);
				newFormsPerDate.put(monthDate , formOfMonth);
			}
		}

		
		Iterator<Entry<MonthDate, List<Form>>> it = newFormsPerDate.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<MonthDate,List<Form>> entry = (Map.Entry<MonthDate,List<Form>>)it.next();
	        for (Form form : entry.getValue()) 
	        {
	        	QuantityPerDate quantityPerDate = new QuantityPerDate(entry.getKey(), new Integer(form.getQuantity()));
	        	if(initProductsQuantityPerDate.containsKey(form.getCatalogNumber()))
	        	{
	        		List<QuantityPerDate> quantityPerDateList = initProductsQuantityPerDate.get(form.getCatalogNumber());
	        		List<MonthDate> datesList = quantityPerDateList.stream().map(el -> el.getDate()).collect(Collectors.toList());
	        		
	        		int indexOfQuantity = datesList.indexOf(quantityPerDate.getDate());
	        		if(indexOfQuantity != -1)
	        			quantityPerDateList.get(indexOfQuantity).addQuantity(quantityPerDate.getQuantity());
	        		else
	        			quantityPerDateList.add(quantityPerDate);
	        			
	        	}
	        	else
	        	{
	        		List<QuantityPerDate> quantityPerDateList = new ArrayList<>();
	        		quantityPerDateList.add(quantityPerDate);
	        		initProductsQuantityPerDate.put(form.getCatalogNumber(), quantityPerDateList);
	        	}
			}
	    }
	    
	    Iterator<Entry<String, List<QuantityPerDate>>> productsQuantityIterator = initProductsQuantityPerDate.entrySet().iterator();
	    while (productsQuantityIterator.hasNext()) 
	    {
	        Map.Entry<String,List<QuantityPerDate>> entry = (Map.Entry<String,List<QuantityPerDate>>)productsQuantityIterator.next();
	        if(!productsQuantityPerDate.containsKey(entry.getKey()))
	        	entry.getValue().stream().forEach(date -> db.addNewProductFormQuantityPerDate(entry.getKey() , date , type));
	        else
	        {
	        	List<QuantityPerDate> currentQuantityPerDateList = productsQuantityPerDate.get(entry.getKey());
	        	List<QuantityPerDate> changedQuantityPerDateList = entry.getValue().stream().filter(el -> !currentQuantityPerDateList.contains(el)).collect(Collectors.toList());
	        	
	        	for (QuantityPerDate quantityPerDate : changedQuantityPerDateList) 
	        	{
	        		List<MonthDate> currentDateList = currentQuantityPerDateList.stream().map(el -> el.getDate()).collect(Collectors.toList());
					if(currentDateList.contains(quantityPerDate.getDate()))
						db.updateNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
					else
						db.addNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
				}
	        }
	    }
	    
		
		
	}

}
