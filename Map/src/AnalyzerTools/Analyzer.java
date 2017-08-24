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

import javax.swing.JTable;

import Forms.CustomerOrder;
import Forms.Forecast;
import Forms.Form;
import Forms.Shipment;
import Forms.WorkOrder;
import MainPackage.DataBase;
import MainPackage.Globals;
import MainPackage.Pair;
import MainPackage.Globals.FormType;

public class Analyzer 
{
	public static final int ConstantColumnsCount = 3;
	private DataBase db;
	
	public Analyzer() 
	{
		new Globals();
		db = new DataBase();
	}
	
	public void addNewFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		db.addFC(customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		updateProductQuantities(catalogNumber , FormType.FC);
	}
	
	public void updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		int remainder = Integer.parseInt(getForecast(id).getQuantity()) - Integer.parseInt(quantity);
		boolean successUpdate = db.updateFC(id,customer, catalogNumber, quantity, initDate, requireDate, description , notes);
		if(remainder != 0 && successUpdate)
			updateProductQuantities(catalogNumber , FormType.FC);
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
		
		updateProductQuantities(catalogNumber);
	}
	
	public void addNewInitProductCustomerOrders (String catalogNumber, String initDate, String quantity, String requireDate, FormType type)
	{
		db.addNewInitProductCustomerOrders(catalogNumber, initDate, quantity, requireDate, type);
		updateProductQuantities(catalogNumber , type);
	}
	
	public void updateProductQuantities(String catalogNumber , FormType type)
	{
		switch (type) 
		{
		case PO:
			updateProductQuantities(db.getAllPO(catalogNumber), db.getAllProductsPOQuantityPerDate(catalogNumber),db.getInitProductsPOQuantityPerDate(catalogNumber),db.getInitProductsPODates(catalogNumber) , FormType.PO);
			return;
		case WO:
			updateProductQuantities(db.getAllWO(catalogNumber), db.getAllProductsWOQuantityPerDate(catalogNumber),db.getInitProductsWOQuantityPerDate(catalogNumber),db.getInitProductsWODates(catalogNumber) , FormType.WO);
			return;
		case SHIPMENT:
			updateProductQuantities(db.getAllShipments(catalogNumber), db.getAllProductsShipmentQuantityPerDate(catalogNumber),db.getInitProductsShipmentsQuantityPerDate(catalogNumber),db.getInitProductsShipmentsDates(catalogNumber) , FormType.SHIPMENT);
			return;
		case FC:
			updateProductQuantities(db.getAllFC(catalogNumber), db.getAllProductsFCQuantityPerDate(catalogNumber),db.getInitProductsFCQuantityPerDate(catalogNumber),db.getInitProductsFCDates(catalogNumber) , FormType.FC);
			return;
		default:
			return;
		}

	}
	
	public void updateProductQuantities(String catalogNumber)
	{
		updateProductQuantities(catalogNumber , FormType.FC);
		updateProductQuantities(catalogNumber , FormType.WO);
		updateProductQuantities(catalogNumber , FormType.PO);
		updateProductQuantities(catalogNumber , FormType.SHIPMENT);
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
		
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		Map<MonthDate,Map<String,ProductColumn>> helpedMap = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription(userName);
		
		MonthDate maximumDate = db.getMaximumMapDate();
		MonthDate minimumDate = db.getMinimumInitDate();
		if(maximumDate == null || maximumDate.before(minimumDate))
			return map;
		
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		
		for (String catalogNumber : catalogNumbers.keySet()) 
		{
			String descendantCatalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				double materialAvailability = 0 ,workOrderAfterSupplied = 0 , openCustomerOrder = 0;
				
				double previousOpenCustomerOrder = 0 , previousWorkOrderAfterSupplied = 0 , previousMaterialAvailability = 0;
				int indexOfCurrentMonth = monthToCalculate.indexOf(monthDate);
				if(indexOfCurrentMonth != 0)
				{
					ProductColumn previousProductColumn = helpedMap.get(monthToCalculate.get(indexOfCurrentMonth - 1)).get(catalogNumber);
					
					previousOpenCustomerOrder = previousProductColumn.getOpenCustomerOrder();
					previousWorkOrderAfterSupplied = previousProductColumn.getWorkOrderAfterSupplied();
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
						if(fatherCatalogNumberAndQuantityToAssociate.getLeft() != null)
						{
							QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
							QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
							
							int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
							customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
							supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
							materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
						}
					}
					
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				
				ProductColumn productColumn = new ProductColumn(descendantCatalogNumber, catalogNumbers.get(descendantCatalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, customerOrders.getQuantity(), supplied.getQuantity(), openCustomerOrder);
				
				ProductColumn patriarchsProductColumn = new ProductColumn(catalogNumber, catalogNumbers.get(catalogNumber), forecast.getQuantity(), materialAvailability, workOrder.getQuantity()
						, workOrderAfterSupplied, customerOrders.getQuantity(), supplied.getQuantity(), openCustomerOrder);
				
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
		
		
		return map;
		
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
					for(int i = 0 ; i < productColumn.getCategoriesCount() ; i++)
					{
						List<String> row = new ArrayList<>();
						row.add(product);
						row.add(db.getCustomerOfCatalogNumber(product));
						row.add(productColumn.getDescription());
						row.add(productColumn.getColumn(i));
						rows.add(row);
					}	
				}
				
				for(int i = 0 ; i < productColumn.getCategoriesCount() ; i++)
				{
					List<String> row = rows.get(index * productColumn.getCategoriesCount() + i);
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
		
		switch (type) 
		{
		case PO:
			return getAllCustomerOrdersOnMonth(product, date);
		case WO:
			return getAllWorkOrderOnMonth(product, date);
		case SHIPMENT:
			return getAllShipmentsOnMonth(product, date);
		case FC:
			return getAllForecastOnMonth(product, date);
		default:
			return null;
		}
	}

	public String getProductOnRow(JTable table, int row) 
	{
		return (String) table.getValueAt(row, 0);
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
}
