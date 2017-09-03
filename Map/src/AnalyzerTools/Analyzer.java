package AnalyzerTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import Components.TableCellListener;
import Forms.CustomerOrder;
import Forms.Forecast;
import Forms.Form;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Pair;
import MainPackage.Globals.FormType;
import MapFrames.ReportViewFrame;
import Reports.MrpHeader;

public class Analyzer 
{
	public static final int ConstantColumnsCount = 4;
	public static final int CatalogColumn = 3;
	private DataBase db;
	
	public Analyzer() 
	{
		new Globals();
		db = new DataBase();
	}
	
	public void addNewFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		db.addFC(customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		updateProductQuantities(catalogNumber , FormType.FC , true);
	}
	
	public void updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		double remainder = Double.parseDouble(getForecast(id).getQuantity()) - Double.parseDouble(quantity);
		boolean successUpdate = db.updateFC(id,customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		if(remainder != 0 && successUpdate)
			updateProductQuantities(catalogNumber , FormType.FC , false);
	}
	
	public void removeFC(int id)
	{
		db.removeFC(id);
	}
	
	public Forecast getForecast(int id)
	{
		return db.getForecast(id);
	}
	
	public List<Forecast> getAllForecastOnMonth(String catalogNumber , MonthDate date)
	{
		List<Forecast> allForecastOnMonth = new ArrayList<Forecast>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allForecastOnMonth.addAll(db.getAllFCOnMonth(cn , date)));
		return allForecastOnMonth;
	}
	
	public List<Shipment> getAllShipmentsOnMonth(String catalogNumber , MonthDate date)
	{
		List<Shipment> allShipmentsOnMonth = new ArrayList<Shipment>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allShipmentsOnMonth.addAll(db.getAllShipmentsOnMonth(cn , date)));
		return allShipmentsOnMonth;
	}
	
	public List<WorkOrder> getAllWorkOrderOnMonth(String catalogNumber , MonthDate date)
	{
		List<WorkOrder> allWorkOrdersOnMonth = new ArrayList<WorkOrder>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allWorkOrdersOnMonth.addAll(db.getAllWOOnMonth(cn , date)));
		return allWorkOrdersOnMonth;
	}
	
	public List<CustomerOrder> getAllCustomerOrdersOnMonth(String catalogNumber , MonthDate date)
	{
		List<CustomerOrder> allCustomerOrdersOnMonth = new ArrayList<CustomerOrder>();
		List<String> familyCatalogNumber = db.getAllPatriarchsCatalogNumber(catalogNumber);
		familyCatalogNumber.stream().forEach(cn -> allCustomerOrdersOnMonth.addAll(db.getAllPOOnMonth(cn , date)));
		return allCustomerOrdersOnMonth;
	}
	
	public void updateAlias(String catalogNumber , String alias)
	{
		db.updateAlias(catalogNumber, alias);
	}
	
	public void cleanProductQuantityPerDate(String catalogNumber)
	{
		db.cleanProductQuantityPerDate(catalogNumber , FormType.WO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.PO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.SHIPMENT);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.FC);
		
		updateProductQuantities(catalogNumber , true);
	}
	
	public void addNewInitProduct(String catalogNumber, String initDate, String quantity, String requireDate, FormType type)
	{
		db.addNewInitProduct(catalogNumber, initDate, quantity, requireDate, type);
		updateProductQuantities(catalogNumber , type , true);
	}
	
	public void updateProductQuantities(String catalogNumber , FormType type , boolean ignorePast)
	{
		switch (type) 
		{
		case PO:
			updateProductQuantities(db.getAllPO(catalogNumber , ignorePast), db.getAllProductsPOQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsPOQuantityPerDate(catalogNumber),db.getInitProductsPODates(catalogNumber) , FormType.PO);
			break;
		case WO:
			updateProductQuantities(db.getAllWO(catalogNumber , ignorePast), db.getAllProductsWOQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsWOQuantityPerDate(catalogNumber),db.getInitProductsWODates(catalogNumber) , FormType.WO);
			break;
		case SHIPMENT:
			updateProductQuantities(db.getAllShipments(catalogNumber , ignorePast), db.getAllProductsShipmentQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsShipmentsQuantityPerDate(catalogNumber),db.getInitProductsShipmentsDates(catalogNumber) , FormType.SHIPMENT);
			break;
		case FC:
			updateProductQuantities(db.getAllFC(catalogNumber, ignorePast), db.getAllProductsFCQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsFCQuantityPerDate(catalogNumber),db.getInitProductsFCDates(catalogNumber) , FormType.FC);
			break;
		default:
			break;
		}
		
		if(!ignorePast)
			updateLastMap(null , null);
		
	}
	
	public void updateProductQuantities(String catalogNumber , boolean ignorePast)
	{
		updateProductQuantities(catalogNumber , FormType.FC , ignorePast);
		updateProductQuantities(catalogNumber , FormType.WO , ignorePast);
		updateProductQuantities(catalogNumber , FormType.PO , ignorePast);
		updateProductQuantities(catalogNumber , FormType.SHIPMENT , ignorePast);
	}
	

	private void updateProductQuantities(List<? extends Form> forms ,  Map<String, List<QuantityPerDate>> productsQuantityPerDate 
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
	        	QuantityPerDate quantityPerDate = new QuantityPerDate(entry.getKey(), new Double(form.getQuantity()));
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
	        	
        		List<MonthDate> currentDateList = currentQuantityPerDateList.stream().map(el -> el.getDate()).collect(Collectors.toList());
        		List<MonthDate> newDateList = entry.getValue().stream().map(el -> el.getDate()).collect(Collectors.toList());
        		
	        	for (QuantityPerDate quantityPerDate : changedQuantityPerDateList) 
	        	{
					if(currentDateList.contains(quantityPerDate.getDate()))
						db.updateNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
					else
						db.addNewProductFormQuantityPerDate(entry.getKey() , quantityPerDate , type);
				}
	        	
        		List<MonthDate> removedDateList = currentDateList.stream().filter(date -> !newDateList.contains(date)).collect(Collectors.toList());
        		removedDateList.stream().forEach(date -> db.removeProductQuantity(entry.getKey() , date));
	        }
	    }
	    
	    productsQuantityIterator = productsQuantityPerDate.entrySet().iterator();
	    while (productsQuantityIterator.hasNext()) 
	    {
	        Map.Entry<String,List<QuantityPerDate>> entry = (Map.Entry<String,List<QuantityPerDate>>)productsQuantityIterator.next();
	        if(!initProductsQuantityPerDate.containsKey(entry.getKey()))
	        	db.removeProductQuantity(entry.getKey() , null);
	    }
	    
	}
	
	public Map<MonthDate,Map<String,ProductColumn>> calculateMap(String userName)
	{
		MonthDate lastCalculateMapDate = db.getLastCalculateMapDate();
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		if(lastCalculateMapDate != null && !lastCalculateMapDate.equals(maximumDate))
			updateLastMap(userName , null);
		
		lastCalculateMapDate = db.getLastCalculateMapDate();
		Map<String,ProductColumn> lastMap = (lastCalculateMapDate != null) ? db.getLastMap(userName, lastCalculateMapDate) : new HashMap<String,ProductColumn>();
		return calculateMap(userName , lastMap);
	}
	
	private Map<MonthDate,Map<String,ProductColumn>> calculateMap(String userName , Map<String,ProductColumn> lastMap)
	{
		
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		Map<MonthDate,Map<String,ProductColumn>> helpedMap = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription(userName);
		
		MonthDate maximumDate = db.getMaximumMapDate();
		MonthDate minimumDate = db.getMinimumMapDate();
		if(maximumDate == null || maximumDate.before(minimumDate))
			return map;
		
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		List<String> catalogNumbersSorted = new ArrayList<>(catalogNumbers.keySet());
		catalogNumbersSorted = Globals.topologicalSort(catalogNumbersSorted, (objects) ->{
			String catalogNumber = (String) objects[0];
			List<String> sons = db.getSons(catalogNumber);
			
			return sons;
		});
		
		if(catalogNumbersSorted == null)
			return null;
		
		
		for (String catalogNumber : catalogNumbersSorted) 
		{
			String descendantCatalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				double materialAvailability = 0 ,workOrderAfterSupplied = 0 , openCustomerOrder = 0 , workOrderAfterCustomerOrderAndParentWorkOrder= 0 ,
						parentWorkOrder = 0 , parentWorkOrderSupplied = 0;
				
				double previousOpenCustomerOrder = 0 , previousWorkOrderAfterSupplied = 0 , previousMaterialAvailability = 0 ,
						previousWorkOrderAfterCustomerOrderAndParentWorkOrder = 0;
				int indexOfCurrentMonth = monthToCalculate.indexOf(monthDate);
				if(indexOfCurrentMonth != 0)
				{
					ProductColumn previousProductColumn = helpedMap.get(monthToCalculate.get(indexOfCurrentMonth - 1)).get(catalogNumber);
					
					previousOpenCustomerOrder = previousProductColumn.getOpenCustomerOrder();
					previousWorkOrderAfterSupplied = previousProductColumn.getWorkOrderAfterSupplied();
					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
					previousWorkOrderAfterCustomerOrderAndParentWorkOrder = previousProductColumn.getWorkOrderAfterCustomerOrderAndParentWorkOrder();
				}
				else if(lastMap.containsKey(catalogNumber))
				{
					ProductColumn previousProductColumn = lastMap.get(catalogNumber);
					
					previousOpenCustomerOrder = previousProductColumn.getOpenCustomerOrder();
					previousWorkOrderAfterSupplied = previousProductColumn.getWorkOrderAfterSupplied();
					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
					previousWorkOrderAfterCustomerOrderAndParentWorkOrder = previousProductColumn.getWorkOrderAfterCustomerOrderAndParentWorkOrder();
				}

				List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
								
				double materialAvailabilityFix = 0;
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> patriarchsFatherCatalogNumber = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					patriarchsFatherCatalogNumber.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : patriarchsFatherCatalogNumber) 
					{
						QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
						QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
						
						int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
						//customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
						//supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
						materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
						
						parentWorkOrder += quantityToAssociate * fatherWorkOrder.getQuantity();
						parentWorkOrderSupplied += quantityToAssociate * fatherSupplied.getQuantity(); 
					}
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				workOrderAfterCustomerOrderAndParentWorkOrder = previousWorkOrderAfterCustomerOrderAndParentWorkOrder + workOrder.getQuantity()
																					- customerOrders.getQuantity() - parentWorkOrder;
				
				ProductColumn productColumn = new ProductColumn(descendantCatalogNumber, catalogNumbers.get(descendantCatalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, workOrderAfterCustomerOrderAndParentWorkOrder , customerOrders.getQuantity(), 
						parentWorkOrder , parentWorkOrderSupplied , supplied.getQuantity(), openCustomerOrder);
				
				ProductColumn patriarchsProductColumn = new ProductColumn(catalogNumber, catalogNumbers.get(catalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, workOrderAfterCustomerOrderAndParentWorkOrder , customerOrders.getQuantity(),
						parentWorkOrder , parentWorkOrderSupplied , supplied.getQuantity(), openCustomerOrder);
				
				if(map.containsKey(monthDate))
				{
					if(map.get(monthDate).containsKey(descendantCatalogNumber))
						map.get(monthDate).get(descendantCatalogNumber).addProductColumn(productColumn);
					else
						map.get(monthDate).put(descendantCatalogNumber, productColumn);
				}
				else
				{
					Map<String,ProductColumn> productPerProductColumn = new HashMap<String,ProductColumn>();
					productPerProductColumn.put(descendantCatalogNumber, productColumn);
					map.put(monthDate, productPerProductColumn);
				}
				
				if(helpedMap.containsKey(monthDate))
				{
					if(helpedMap.get(monthDate).containsKey(catalogNumber))
						helpedMap.get(monthDate).get(catalogNumber).addProductColumn(patriarchsProductColumn);
					else
						helpedMap.get(monthDate).put(catalogNumber, patriarchsProductColumn);
				}
				else
				{
					Map<String,ProductColumn> productPerProductColumn = new HashMap<String,ProductColumn>();
					productPerProductColumn.put(catalogNumber, patriarchsProductColumn);
					helpedMap.put(monthDate, productPerProductColumn);
				}
			}
		}
		
		int indexOfToday = monthToCalculate.indexOf(new MonthDate(Globals.getTodayDate()));
		int endIndex = 0;
		if(indexOfToday != -1)
		{
			if(indexOfToday >= Globals.monthsBackToView)
				endIndex = indexOfToday - Globals.monthsBackToView;
		}
		
		List<MonthDate> notMonthsToView = monthToCalculate.subList(0, endIndex);
		
		for (MonthDate month : notMonthsToView)
				map.remove(month);
		
		return map;
		
	}
	
	public void updateLastMap(String userName , String cn) 
	{
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		MonthDate minimumDate = db.getMinimumInitDate();
		
		if(cn != null)
		{
			Map<String, Date> inits = db.getInitProductsFCDates(cn);
			if(inits.containsKey(cn))
				minimumDate = new MonthDate(inits.get(cn));
		}
		
		if(minimumDate == null || maximumDate.before(minimumDate))
		{
			if(cn == null)
				db.clearLastMap();
			return;
		}
		
		Map<String,String> catalogNumbers = (cn == null) ? db.getAllCatalogNumbersPerDescription(userName) : db.getDescription(cn);
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		
		List<String> catalogNumbersSorted = new ArrayList<>(catalogNumbers.keySet());
		catalogNumbersSorted = Globals.topologicalSort(catalogNumbersSorted, (objects) ->{
			String catalogNumber = (String) objects[0];
			List<String> sons = db.getSons(catalogNumber);
			
			return sons;
		});
		
		if(catalogNumbersSorted == null)
			return;
		
		
		for (String catalogNumber : catalogNumbersSorted) 
		{
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				double materialAvailability = 0 ,workOrderAfterSupplied = 0 , openCustomerOrder = 0 , workOrderAfterCustomerOrderAndParentWorkOrder= 0 ,
						parentWorkOrder = 0 , parentWorkOrderSupplied = 0;
				
				double previousOpenCustomerOrder = 0 , previousWorkOrderAfterSupplied = 0 , previousMaterialAvailability = 0 ,
						previousWorkOrderAfterCustomerOrderAndParentWorkOrder = 0;
				int indexOfCurrentMonth = monthToCalculate.indexOf(monthDate);
				if(indexOfCurrentMonth != 0)
				{
					ProductColumn previousProductColumn = map.get(monthToCalculate.get(indexOfCurrentMonth - 1)).get(catalogNumber);
					
					previousOpenCustomerOrder = previousProductColumn.getOpenCustomerOrder();
					previousWorkOrderAfterSupplied = previousProductColumn.getWorkOrderAfterSupplied();
					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
					previousWorkOrderAfterCustomerOrderAndParentWorkOrder = previousProductColumn.getWorkOrderAfterCustomerOrderAndParentWorkOrder();
				}

				List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
								
				double materialAvailabilityFix = 0;
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> patriarchsFatherCatalogNumber = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					patriarchsFatherCatalogNumber.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : patriarchsFatherCatalogNumber) 
					{
						QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
						QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
						
						int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
						//customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
						//supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
						materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
						
						parentWorkOrder += quantityToAssociate * fatherWorkOrder.getQuantity();
						parentWorkOrderSupplied += quantityToAssociate * fatherSupplied.getQuantity();
					}
					
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				workOrderAfterCustomerOrderAndParentWorkOrder = previousWorkOrderAfterCustomerOrderAndParentWorkOrder + workOrder.getQuantity()
																				- customerOrders.getQuantity() - parentWorkOrder;
				
				ProductColumn productColumn = new ProductColumn(catalogNumber, catalogNumbers.get(catalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, workOrderAfterCustomerOrderAndParentWorkOrder , customerOrders.getQuantity(), 
						parentWorkOrder , parentWorkOrderSupplied , supplied.getQuantity(), openCustomerOrder);
								
				if(map.containsKey(monthDate))
				{
					if(map.get(monthDate).containsKey(catalogNumber))
						map.get(monthDate).get(catalogNumber).addProductColumn(productColumn);
					else
						map.get(monthDate).put(catalogNumber, productColumn);
				}
				else
				{
					Map<String,ProductColumn> productPerProductColumn = new HashMap<String,ProductColumn>();
					productPerProductColumn.put(catalogNumber, productColumn);
					map.put(monthDate, productPerProductColumn);
				}
			}
		}
		
		Map<String,ProductColumn> newMapOnMaximumDate = map.get(maximumDate);
		db.updateMap(newMapOnMaximumDate , maximumDate);
		
	}

	private List<MonthDate> createDates(MonthDate fromDate, MonthDate toDate) 
	{
		List<MonthDate> dates = new ArrayList<>();
		MonthDate currentDate = fromDate;
		
		while(!currentDate.after(toDate))
		{
			dates.add(currentDate);
			currentDate = new MonthDate(Globals.addMonths(currentDate, 1));
		}

		return dates;
	}

	public String[][] getRows(Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		List<List<String>> rows = new ArrayList<>();
		
		List<MonthDate> months = map.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		
		for (MonthDate monthDate : months) 
		{
			Map<String , ProductColumn> productColumnPerProduct = map.get(monthDate);
			List<String> products = productColumnPerProduct.keySet().stream().collect(Collectors.toList());
			Collections.sort(products);
			
			for (int index = 0 ; index < products.size() ; index ++) 
			{
				String product = products.get(index);
				ProductColumn productColumn = productColumnPerProduct.get(product);
				if(months.indexOf(monthDate) == 0)
				{
					for(int i = 0 ; i < ProductColumn.CategoriesCount ; i++)
					{
						List<String> row = new ArrayList<>();
						row.add(product);
						row.add(db.getCustomerOfCatalogNumber(product));
						row.add(productColumn.getDescription());
						row.add(productColumn.getColumn(i));
						rows.add(row);
					}	
				}
				
				for(int i = 0 ; i < ProductColumn.CategoriesCount ; i++)
				{
					List<String> row = rows.get(index * ProductColumn.CategoriesCount + i);
					row.add(Double.toString(productColumn.getColumnValue(i)));
				}
			}
			
		}
		
		return rows.stream().map(row -> row.toArray(new String[0])).toArray(String[][]::new);
	}

	public String[] getColumns(Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		List<String> columns = new ArrayList<>();
		columns.add("Catalog Number");
		columns.add("Customer");
		columns.add("Description");
		columns.add("Category");
		List<MonthDate> months = map.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		columns.addAll(months.stream().map(date -> date.shortString()).collect(Collectors.toList()));
		
		return columns.toArray(new String[0]);
	}

	public List<? extends Form> getFormsFromCell(Map<MonthDate, Map<String, ProductColumn>> map , String product, MonthDate date , String category) 
	{
		FormType type = map.get(date).get(product).getFormType(category);
		if(type == null)
			return null;
		List<? extends Form> forms = new ArrayList<>();
		List<String> catalogNumbers = map.get(date).get(product).getCatalogNumbersOfForms(product, category);
		
		switch (type) 
		{
			case PO:
				forms = new ArrayList<CustomerOrder>();
				for (String catalogNumber : catalogNumbers) 
				{
					((ArrayList<CustomerOrder>)forms).addAll(getAllCustomerOrdersOnMonth(catalogNumber, date));
				}
				break;
			case WO:
				forms = new ArrayList<WorkOrder>();
				for (String catalogNumber : catalogNumbers) 
				{
					((ArrayList<WorkOrder>)forms).addAll(getAllWorkOrderOnMonth(catalogNumber, date));
				}
				break;
			case SHIPMENT:
				forms = new ArrayList<Shipment>();
				for (String catalogNumber : catalogNumbers) 
				{
					((ArrayList<Shipment>)forms).addAll(getAllShipmentsOnMonth(catalogNumber, date));
				}
				break;
			case FC:
				forms = new ArrayList<Forecast>();
				for (String catalogNumber : catalogNumbers) 
				{
					((ArrayList<Forecast>)forms).addAll(getAllForecastOnMonth(catalogNumber, date));
				}
				break;
			default:
				break;
		}
		
		return forms;
		
	}

	public String getProductOnRow(JTable table, int row) 
	{
		return (String) table.getValueAt(row, 0);
	}
	
	public String getCategoryOnRow(JTable table, int row) 
	{
		return (String) table.getValueAt(row, 3);
	}
	
	public String getDescriptionOfCategory(String product, String category)
	{
		String descriptionOfCategory = ProductColumn.getDescriptionOfCategory(product , category);
		
		return descriptionOfCategory;
	}

	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		filterColumns.add(3);
		
		return filterColumns;
	}
	
	public CallBack<Object> getValueCellChangeAction(String email , Authenticator auth , String userName , ReportViewFrame mapFrame , Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		return null;
	}

	public CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth , String userName , ReportViewFrame mapFrame , Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int column = tcl.getColumn();
				String product = getProductOnRow(tcl.getTable() , row);
				String category = getCategoryOnRow(tcl.getTable() , row);
				if(column < Analyzer.CatalogColumn)
					return null;
				else if(column == Analyzer.CatalogColumn)
				{
					String descriptionOfCategory = getDescriptionOfCategory(product , category);
					if(!descriptionOfCategory.trim().equals(""))
						JOptionPane.showConfirmDialog(null, descriptionOfCategory ,category + " Explanation",JOptionPane.PLAIN_MESSAGE);
					return null; 
				}
				
				String monthOnShortName = tcl.getTable().getColumnName(column);
				MonthDate monthDate = new MonthDate(monthOnShortName);
				List<? extends Form> forms = getFormsFromCell(map , product , monthDate , category);
				
				if(forms == null || forms.size() == 0)
					return null;
				
				String [] columns = forms.get(0).getColumns();
				String [][] rows = new String[forms.size()][columns.length];
				int index = 0;
				for (Form form : forms) 
				{
					rows[index] = form.getRow();
					index++;
				}
				
				boolean canEdit = forms.get(0).canEdit();
				ReportViewFrame reportViewFrame = new ReportViewFrame(email , auth , "Reports View" , columns, rows, canEdit , forms.get(0).getInvalidEditableColumns());
				
				List<Integer> filterColumns = forms.get(0).getFilterColumns();
				List<String> filterNames = new ArrayList<>();
				filterColumns.stream().forEach(col -> filterNames.add(columns[col] + ": "));
				reportViewFrame.setFilters(filterColumns, filterNames);
				

				CallBack<Object> valueCellChangeAction = new CallBack<Object>()
				{
					@Override
					public Object execute(Object... objects) 
					{
						TableCellListener tcl = (TableCellListener)objects[0];
						int row = tcl.getRow();
						int column = tcl.getColumn();
						String newValue = (String) tcl.getNewValue();
						String oldValue = (String) tcl.getOldValue();
						Form updateForm = forms.get(row);
						
						try 
						{
							updateForm.updateValue(column , newValue);
							mapFrame.refresh(getRows(calculateMap(userName)));
							reportViewFrame.setColumnWidth();
							return null;
						} catch (Exception e) 
						{
							reportViewFrame.updateCellValue(row,column,oldValue);
							JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
							return e;
						}
					}
				};
				reportViewFrame.setCallBacks(valueCellChangeAction, null, null);
				reportViewFrame.show();
		        return null;
			}
		};
		
		return doubleLeftClickAction;
	}

	public CallBack<Object> getRightClickAction(String email , Authenticator auth , String userName , ReportViewFrame mapFrame , Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		return null;
	}

	public List<Integer> getInvalidEditableCoulmns(String[] columns) 
	{
		return IntStream.rangeClosed(0, columns.length - 1).boxed().collect(Collectors.toList());
	}
	
	public List<MrpHeader> getMrpHeaders(String userName)
	{
		MonthDate lastCalculateMapDate = db.getLastCalculateMapDate();
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		if(lastCalculateMapDate != null && !lastCalculateMapDate.equals(maximumDate))
			updateLastMap(userName , null);
		
		lastCalculateMapDate = db.getLastCalculateMapDate();
		Map<String,ProductColumn> lastMap = (lastCalculateMapDate != null) ? db.getLastMap(userName, lastCalculateMapDate) : new HashMap<String,ProductColumn>();
		return getMrpHeaders(userName , lastMap);
	}
	
	private List<MrpHeader> getMrpHeaders(String userName, Map<String, ProductColumn> lastMap)
	{
		Map<MonthDate,Map<String,Double>> map = new HashMap<MonthDate,Map<String,Double>>();
		Map<MonthDate,Map<String,Double>> helpedMap = new HashMap<MonthDate,Map<String,Double>>();
		
		List<MrpHeader> mrpHeaders = new ArrayList<>();
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription(userName);
		
		MonthDate maximumDate = db.getMaximumMapDate();
		MonthDate minimumDate = db.getMinimumMapDate();
		if(maximumDate == null || maximumDate.before(minimumDate))
			return mrpHeaders;
		
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		
		for (String catalogNumber : catalogNumbers.keySet()) 
		{
			String descendantCatalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				double materialAvailability = 0;
				
				double previousMaterialAvailability = 0;
				int indexOfCurrentMonth = monthToCalculate.indexOf(monthDate);
				if(indexOfCurrentMonth != 0)
				{
					previousMaterialAvailability = helpedMap.get(monthToCalculate.get(indexOfCurrentMonth - 1)).get(catalogNumber);
				}
				else if(lastMap.containsKey(catalogNumber))
				{
					ProductColumn previousProductColumn = lastMap.get(catalogNumber);

					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
				}

				List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
								
				double materialAvailabilityFix = 0;
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> patriarchsFatherCatalogNumber = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					patriarchsFatherCatalogNumber.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : patriarchsFatherCatalogNumber) 
					{
							QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
							
							int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
							materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
					}
					
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				
				
				if(map.containsKey(monthDate))
				{
					if(map.get(monthDate).containsKey(descendantCatalogNumber))
					{
						Double value = 	map.get(monthDate).get(descendantCatalogNumber);
						value += materialAvailability;
						map.get(monthDate).put(descendantCatalogNumber, value);
					}
					else
						map.get(monthDate).put(descendantCatalogNumber, materialAvailability);
				}
				else
				{
					Map<String,Double> productPerProductColumn = new HashMap<String,Double>();
					productPerProductColumn.put(descendantCatalogNumber, materialAvailability);
					map.put(monthDate, productPerProductColumn);
				}
				
				if(helpedMap.containsKey(monthDate))
				{
					if(helpedMap.get(monthDate).containsKey(catalogNumber))
					{
						Double value = 	helpedMap.get(monthDate).get(descendantCatalogNumber);
						value += materialAvailability;
						helpedMap.get(monthDate).put(descendantCatalogNumber, value);
					}
					else
						helpedMap.get(monthDate).put(catalogNumber, materialAvailability);
				}
				else
				{
					Map<String,Double> productPerProductColumn = new HashMap<String,Double>();
					productPerProductColumn.put(catalogNumber, materialAvailability);
					helpedMap.put(monthDate, productPerProductColumn);
				}
			}
		}
		
		int indexOfToday = monthToCalculate.indexOf(new MonthDate(Globals.getTodayDate()));
		int startIndex = 0;
		if(indexOfToday != -1)
			startIndex = indexOfToday;
		
		List<MonthDate> monthsToView = monthToCalculate.subList(startIndex, monthToCalculate.size());
		
		for (String catalogNumber : catalogNumbers.keySet()) 
		{
			catalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			String description = catalogNumbers.get(catalogNumber);
			String customer = db.getCustomerOfCatalogNumber(catalogNumber);
			Map<MonthDate , Double> quantityPerMonth = new HashMap<>();
			boolean isSon = isSon(catalogNumber);
			
			for (int index = monthsToView.size() - 1 ; index >= 0 ; index--) 
			{
				MonthDate monthDate = monthsToView.get(index);
				double materialAvailability = map.get(monthDate).get(catalogNumber);
				
				double previousMaterialAvailability = 0;
				if(index != 0)
				{
					previousMaterialAvailability = map.get(monthsToView.get(index - 1)).get(catalogNumber);
				}
				
				double quantity = materialAvailability - previousMaterialAvailability;
				
				if(isSon)
				{
					if(materialAvailability < 0)
					{
						if(quantity != 0)
							quantity = materialAvailability;
						else if(previousMaterialAvailability < 0)
							quantity = 0;
						else
							quantity = materialAvailability;
					}
					else if(quantity < 0)
							quantity = materialAvailability;
				}
				else
				{
					if(previousMaterialAvailability < 0)
						quantity = materialAvailability;
					
					if(materialAvailability < 0)
					{
						quantity = 0;
					}
				}
				
				quantityPerMonth.put(monthDate, quantity);
			}
			
			MrpHeader mrpHeader = new MrpHeader(catalogNumber, customer, description, quantityPerMonth);
			mrpHeaders.add(mrpHeader);
		}
		
		return mrpHeaders;
		
	}

	private boolean isSon(String catalogNumber) 
	{
		return db.getFathers(catalogNumber).size() > 0;
	}
}
