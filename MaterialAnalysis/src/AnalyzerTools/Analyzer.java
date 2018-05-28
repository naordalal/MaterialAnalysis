package AnalyzerTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import MainPackage.Globals.UpdateType;
import MapFrames.ReportViewFrame;
import Reports.MrpHeader;

public class Analyzer 
{
	public static final int ConstantColumnsCount = 4;
	public static final int CategoryColumn = 3;
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private DataBase db;
	
	public Analyzer() 
	{
		new Globals();
		db = new DataBase();
	}
	
	public void addNewFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String userName , String notes)
	{
		db.addFC(customer, catalogNumber, quantity, initDate, requireDate, description , userName , notes);
		updateProductQuantities(userName , catalogNumber , FormType.FC , true);
	}
	
	public void updateFC(int id , String customer , String catalogNumber , String quantity , String initDate , String requireDate 
			, String description , String userName , String notes)
	{
		Forecast previousForecast = getForecast(id);
		MonthDate previousRequireDate = new MonthDate(previousForecast.getRequestDate());
		MonthDate newRequireDate = new MonthDate(Globals.parseDate(requireDate));
		double remainder = Double.parseDouble(previousForecast.getQuantity()) - Double.parseDouble(quantity);
		boolean successUpdate = db.updateFC(id,customer, catalogNumber, quantity, initDate, requireDate, description , userName , notes);
		
		if((remainder != 0 || !previousRequireDate.equals(newRequireDate)) && successUpdate)
		{
			MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate));
			MonthDate fcInitDate = new MonthDate(Globals.parseDate(initDate));
			boolean ignorePast = !fcInitDate.before(maximumDate);
			updateProductQuantities(userName , catalogNumber , FormType.FC , ignorePast);	
		}
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
	
	public void cleanProductQuantityPerDate(String userName , String catalogNumber)
	{
		db.cleanProductQuantityPerDate(catalogNumber , FormType.WO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.PO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.SHIPMENT);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.FC);
		
		updateProductQuantities(userName , catalogNumber , true);
	}
	
	public void addNewInitProduct(String userName , String catalogNumber, String initDate, String quantity, String requireDate, FormType type)
	{
		db.addNewInitProduct(catalogNumber, initDate, quantity, requireDate, type);
		updateProductQuantities(userName , catalogNumber , type , true);
	}
	
	public void updateProductQuantities(String userName , String catalogNumber , FormType type , boolean ignorePast)
	{
		Map<String, List<QuantityPerDate>> productQuanitiesPerDate;
		switch (type) 
		{
			case PO:
				//updateProductQuantities(db.getAllPO(catalogNumber , ignorePast), db.getAllProductsPOQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsPODates(catalogNumber) , FormType.PO);
				productQuanitiesPerDate = db.getAllProductsPOQuantityPerDate(catalogNumber , ignorePast);
				break;
			case WO:
				//updateProductQuantities(db.getAllWO(catalogNumber , ignorePast), db.getAllProductsWOQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsWODates(catalogNumber) , FormType.WO);
				productQuanitiesPerDate = db.getAllProductsWOQuantityPerDate(catalogNumber , ignorePast);
				break;
			case SHIPMENT:
				//updateProductQuantities(db.getAllShipments(catalogNumber , ignorePast), db.getAllProductsShipmentQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsShipmentsDates(catalogNumber) , FormType.SHIPMENT);
				productQuanitiesPerDate = db.getAllProductsShipmentQuantityPerDate(catalogNumber , ignorePast);
				break;
			case FC:
				//updateProductQuantities(db.getAllFC(catalogNumber, ignorePast), db.getAllProductsFCQuantityPerDate(catalogNumber , ignorePast),db.getInitProductsFCDates(catalogNumber) , FormType.FC);
				productQuanitiesPerDate = db.getAllProductsFCQuantityPerDate(catalogNumber , ignorePast);
				break;
			default:
				productQuanitiesPerDate = new HashMap<>();
				break;
		}
		
		if(productQuanitiesPerDate.isEmpty())
			productQuanitiesPerDate.put(catalogNumber, new ArrayList<>());
		
		for (String cn : productQuanitiesPerDate.keySet())
		{
			List<QuantityPerDate> quantities = db.calculateProductFormQuantityOnDate(cn , type);
			List<MonthDate> newMonths = quantities.stream().map(q -> q.getDate()).collect(Collectors.toList());
			List<MonthDate> currentMonths = productQuanitiesPerDate.get(cn).stream().map(q -> q.getDate()).collect(Collectors.toList());
			List<MonthDate> monthsToAdd = newMonths.stream().filter(m -> !currentMonths.contains(m)).collect(Collectors.toList());
			List<MonthDate> monthsToRemove = currentMonths.stream().filter(m -> !newMonths.contains(m)).collect(Collectors.toList());
			List<MonthDate> monthsToUpdate = currentMonths.stream().filter(m -> newMonths.contains(m)).collect(Collectors.toList());
			
			for (QuantityPerDate quantityPerDate : quantities) 
			{
				if(monthsToAdd.contains(quantityPerDate.getDate()))
					db.addNewProductFormQuantityPerDate(cn, quantityPerDate, type);
				else if(monthsToUpdate.contains(quantityPerDate.getDate()))
					db.updateNewProductFormQuantityPerDate(cn, quantityPerDate, type);
			}
			
			for(MonthDate dateToRemove : monthsToRemove)
				db.removeProductQuantity(cn, dateToRemove , type);
		}
		
		if(!ignorePast)
			updateLastMap(null , null);
		
		db.updateLastUpdateDate(UpdateType.ProductQuantity);
		calculateMap(userName, false, null, catalogNumber);
	}
	
	public void updateProductQuantities(String userName , String catalogNumber , boolean ignorePast)
	{
		updateProductQuantities(userName , catalogNumber , FormType.FC , ignorePast);
		updateProductQuantities(userName , catalogNumber , FormType.WO , ignorePast);
		updateProductQuantities(userName , catalogNumber , FormType.PO , ignorePast);
		updateProductQuantities(userName , catalogNumber , FormType.SHIPMENT , ignorePast);
	}
	

	private void updateProductQuantities(List<? extends Form> forms ,  Map<String, List<QuantityPerDate>> productsQuantityPerDate 
			, Map<String , Date> productsInitDates ,  FormType type)
	{
		Map<MonthDate,List<Form>> newFormsPerDate = new HashMap<>();
		
		for (Form form : forms) 
		{
			if(productsInitDates.containsKey(form.getCatalogNumber()))
				if(form.getCreateDate().before(productsInitDates.get(form.getCatalogNumber())) || form.getCreateDate().equals(productsInitDates.get(form.getCatalogNumber())))
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
	    Map<String, List<QuantityPerDate>> initProductsQuantityPerDate = new HashMap<>();
	    
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
        		removedDateList.stream().forEach(date -> db.removeProductQuantity(entry.getKey() , date , type));
	        }
	    }
	    
	    productsQuantityIterator = productsQuantityPerDate.entrySet().iterator();
	    while (productsQuantityIterator.hasNext()) 
	    {
	        Map.Entry<String,List<QuantityPerDate>> entry = (Map.Entry<String,List<QuantityPerDate>>)productsQuantityIterator.next();
	        if(!initProductsQuantityPerDate.containsKey(entry.getKey()))
	        	db.removeProductQuantity(entry.getKey() , null , type);
	    }
	    
	}
	
	public Map<MonthDate,Map<String,ProductColumn>> calculateMap(String userName , boolean forView , List<String> customers , String cn)
	{
		Date lastCalculateMapDate = db.getLastUpdateDate(UpdateType.MAP);
		Date lastCalculateProductQuantitiesDate = db.getLastUpdateDate(UpdateType.ProductQuantity);
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription(userName);
		if(customers != null)
		{
			List<String> products = customers.stream().map(customer ->  db.getAllCatalogNumberOfCustomer(customer)).reduce(new ArrayList<String>(), (x,y) -> {x.addAll(y);return x;});
			List<String> removeProdcuts = catalogNumbers.keySet().stream().filter(c -> !products.contains(c)).collect(Collectors.toList());
			removeProdcuts.forEach(c -> catalogNumbers.remove(c));	
		}
		else if(cn != null)
		{
			Set<String> sons = getAllSons(cn);
			sons.add(cn);
			
			new ArrayList<>(sons).stream().forEach(son -> sons.addAll(db.getAllDescendantCatalogNumber(son)));
			new ArrayList<>(sons).stream().forEach(son -> sons.addAll(db.getAllPatriarchsCatalogNumber(son)));
			
			List<String> removeProdcuts = catalogNumbers.keySet().stream().filter(c -> !sons.contains(c)).collect(Collectors.toList());
			removeProdcuts.forEach(c -> catalogNumbers.remove(c));
		}
		
		if(lastCalculateMapDate != null && lastCalculateProductQuantitiesDate != null && 
				(lastCalculateMapDate.equals(lastCalculateProductQuantitiesDate) || lastCalculateMapDate.after(lastCalculateProductQuantitiesDate)))
			return viewMap(userName, forView , catalogNumbers);
			
		//MonthDate lastCalculateMapDate = db.getLastCalculateMapDate();
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		if(!db.mapContainsDate(maximumDate))
			updateLastMap(userName , null);
		
		boolean containsDate = db.mapContainsDate(maximumDate);
		Map<String,ProductColumn> lastMap = (containsDate) ? db.getLastMap(userName, catalogNumbers , maximumDate) : new HashMap<String,ProductColumn>();
		
		Map<MonthDate,Map<String,ProductColumn>> map = calculateMap(lastMap , catalogNumbers);
		
		List<MonthDate> monthToCalculate = new ArrayList<>(map.keySet());
		Collections.sort(monthToCalculate);
		
		int indexOfToday = monthToCalculate.indexOf(new MonthDate(Globals.getTodayDate()));
		int endIndex = 0;
		if(indexOfToday != -1)
		{
			if(indexOfToday >= Globals.monthsBackToView)
				endIndex = indexOfToday - Globals.monthsBackToView;
		}
		
		List<MonthDate> notMonthsToView = monthToCalculate.subList(0, endIndex);
		
		for(MonthDate date : map.keySet())
		{
			db.updateMap(map.get(date), date);	
		}
		
		if(forView)
		{
			for (MonthDate month : notMonthsToView)
			{
				for(String catalogNumber : map.get(month).keySet())
				{
					ProductColumn pc = map.get(month).get(catalogNumber);
					if(pc.getWorkOrderAfterCustomerOrderAndParentWorkOrder() < 0)
						pc.setWorkOrderAfterCustomerOrderAndParentWorkOrder(0);
				}
				
				map.remove(month);	
			}	
		}
		
		db.updateLastUpdateDate(UpdateType.MAP);

		return map;
	}
	
	private Set<String> getAllSons(String cn) 
	{
		Set<String> sons = new HashSet<>();
		for (String son : db.getSons(cn)) 
		{
			sons.add(son);
			sons.addAll(getAllSons(son));
		}
		
		return sons;
	}

	private Map<MonthDate,Map<String,ProductColumn>> calculateMap(Map<String,ProductColumn> lastMap , Map<String,String> catalogNumbers)
	{
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		Map<MonthDate,Map<String,ProductColumn>> helpedMap = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		
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
			
			List<QuantityPerDate> initFCProductsQuantityPerDate = db.getInitProductsFCQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initWOProductsQuantityPerDate = db.getInitProductsWOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initPOProductsQuantityPerDate = db.getInitProductsPOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initShipmentProductsQuantityPerDate = db.getInitProductsShipmentsQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			
			List<MonthDate> initFCProductsDates = initFCProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initWOProductsDates = initWOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initPOProductsDates = initPOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initShipmentProductsDates = initShipmentProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				int indexOfFC = initFCProductsDates.indexOf(monthDate);
				int indexOfWO = initWOProductsDates.indexOf(monthDate);
				int indexOfPO = initPOProductsDates.indexOf(monthDate);
				int indexOfShipment = initShipmentProductsDates.indexOf(monthDate);
				
				double initFC = (indexOfFC < 0) ? 0 : initFCProductsQuantityPerDate.get(indexOfFC).getQuantity();
				double initWO = (indexOfWO < 0) ? 0 : initWOProductsQuantityPerDate.get(indexOfWO).getQuantity();
				double initPO = (indexOfPO < 0) ? 0 : initPOProductsQuantityPerDate.get(indexOfPO).getQuantity();
				double initShipment = (indexOfShipment < 0) ? 0 : initShipmentProductsQuantityPerDate.get(indexOfShipment).getQuantity();
				
				forecast.addQuantity(initFC);
				workOrder.addQuantity(initWO);
				customerOrders.addQuantity(initPO);
				supplied.addQuantity(initShipment);
				
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
				List<String> descendantsCatalogNumbers = db.getAllDescendantCatalogNumber(catalogNumber);
				List<String> fathersOfDescendantsCatalogNumbers = descendantsCatalogNumbers.stream().map(cn -> db.getFathers(cn).stream().map(pair -> pair.getLeft())
																		.collect(Collectors.toList())).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
				List<String> descendantsFathersOfDescendantsCatalogNumbers = fathersOfDescendantsCatalogNumbers.stream().map(el -> { List<String> desCN = db.getAllDescendantCatalogNumber(el); desCN.add(el); return desCN;}).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
				
				double materialAvailabilityFix = calculateMaterialAvailabilityFix(catalogNumber , monthDate);
				double initFatherWO = 0;
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> descendantsFatherCatalogNumbers = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					descendantsFatherCatalogNumbers.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : descendantsFatherCatalogNumbers) 
					{
						if(descendantsFathersOfDescendantsCatalogNumbers.contains(fatherCatalogNumber))
							continue;
						
						QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
						QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
						
						List<QuantityPerDate> initWOProductsQuantityPerDateOfFather = db.getInitProductsWOQuantityPerDate(fatherCatalogNumber).getOrDefault(fatherCatalogNumber, new ArrayList<>());
						List<MonthDate> initWOProductsDatesOfFather = initWOProductsQuantityPerDateOfFather.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
						int indexOfWOFather = initWOProductsDatesOfFather.indexOf(monthDate);
						initFatherWO += (indexOfWOFather < 0) ? 0 : initWOProductsQuantityPerDateOfFather.get(indexOfWOFather).getQuantity();
						
						int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
						//customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
						//supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
						//materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
						
						parentWorkOrder += quantityToAssociate * fatherWorkOrder.getQuantity();
						parentWorkOrderSupplied += quantityToAssociate * fatherSupplied.getQuantity(); 
					}
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() - parentWorkOrderSupplied + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				workOrderAfterCustomerOrderAndParentWorkOrder = previousWorkOrderAfterCustomerOrderAndParentWorkOrder + workOrder.getQuantity()
																					- customerOrders.getQuantity() - parentWorkOrder - initFatherWO;
				
				//workOrderAfterCustomerOrderAndParentWorkOrder = (workOrderAfterCustomerOrderAndParentWorkOrder < 0) ? 0 : workOrderAfterCustomerOrderAndParentWorkOrder;
				
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
			
		return map;
		
	}
	
	private Map<MonthDate,Map<String,ProductColumn>> viewMap(String userName , boolean forView, Map<String,String> catalogNumbers)
	{		
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<>();
		
		MonthDate maximumDate = db.getMaximumMapDate();
		MonthDate minimumDate = db.getMinimumMapDate();
		
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		
		int indexOfToday = monthToCalculate.indexOf(new MonthDate(Globals.getTodayDate()));
		int startIndex = 0;
		if(indexOfToday != -1)
		{
			if(indexOfToday >= Globals.monthsBackToView)
				startIndex = indexOfToday - Globals.monthsBackToView;
		}
		
		if(forView)
			monthToCalculate = monthToCalculate.subList(startIndex, monthToCalculate.size());
		if(maximumDate == null || maximumDate.before(minimumDate))
			return map;
		
		for (MonthDate monthDate : monthToCalculate) 
			map.put(monthDate, db.getLastMap(userName, catalogNumbers , monthDate));
				
		
		for (String catalogNumber : catalogNumbers.keySet()) 
		{
			String descendantCatalogNumber = db.getDescendantCatalogNumber(catalogNumber);
			
			List<QuantityPerDate> initFCProductsQuantityPerDate = db.getInitProductsFCQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initWOProductsQuantityPerDate = db.getInitProductsWOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initPOProductsQuantityPerDate = db.getInitProductsPOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initShipmentProductsQuantityPerDate = db.getInitProductsShipmentsQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			
			List<MonthDate> initFCProductsDates = initFCProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initWOProductsDates = initWOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initPOProductsDates = initPOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initShipmentProductsDates = initShipmentProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				int indexOfFC = initFCProductsDates.indexOf(monthDate);
				int indexOfWO = initWOProductsDates.indexOf(monthDate);
				int indexOfPO = initPOProductsDates.indexOf(monthDate);
				int indexOfShipment = initShipmentProductsDates.indexOf(monthDate);
				
				double initFC = (indexOfFC < 0) ? 0 : initFCProductsQuantityPerDate.get(indexOfFC).getQuantity();
				double initWO = (indexOfWO < 0) ? 0 : initWOProductsQuantityPerDate.get(indexOfWO).getQuantity();
				double initPO = (indexOfPO < 0) ? 0 : initPOProductsQuantityPerDate.get(indexOfPO).getQuantity();
				double initShipment = (indexOfShipment < 0) ? 0 : initShipmentProductsQuantityPerDate.get(indexOfShipment).getQuantity();
				
				forecast.addQuantity(initFC);
				workOrder.addQuantity(initWO);
				customerOrders.addQuantity(initPO);
				supplied.addQuantity(initShipment);
				
				ProductColumn currentProductColumn = map.get(monthDate).get(descendantCatalogNumber);
				currentProductColumn.setForecast(currentProductColumn.getForecast() + forecast.getQuantity());
				currentProductColumn.setWorkOrder(currentProductColumn.getWorkOrder() + workOrder.getQuantity());
				currentProductColumn.setCustomerOrders(currentProductColumn.getCustomerOrders() + customerOrders.getQuantity());
				currentProductColumn.setSupplied(currentProductColumn.getSupplied() + supplied.getQuantity());
				
				if(forView && currentProductColumn.getWorkOrderAfterCustomerOrderAndParentWorkOrder() < 0)
					currentProductColumn.setWorkOrderAfterCustomerOrderAndParentWorkOrder(0);
				
				List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
				List<String> descendantsCatalogNumbers = db.getAllDescendantCatalogNumber(catalogNumber);
				List<String> fathersOfDescendantsCatalogNumbers = descendantsCatalogNumbers.stream().map(cn -> db.getFathers(cn).stream().map(pair -> pair.getLeft())
																		.collect(Collectors.toList())).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
				List<String> descendantsFathersOfDescendantsCatalogNumbers = fathersOfDescendantsCatalogNumbers.stream().map(el -> { List<String> desCN = db.getAllDescendantCatalogNumber(el); desCN.add(el); return desCN;}).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
				
				double parentWorkOrder = 0 , parentWorkOrderSupplied = 0;
				
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> descendantsFatherCatalogNumbers = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					descendantsFatherCatalogNumbers.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : descendantsFatherCatalogNumbers) 
					{
						if(descendantsFathersOfDescendantsCatalogNumbers.contains(fatherCatalogNumber))
							continue;
						
						QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
						QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
						
						int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
						
						parentWorkOrder += quantityToAssociate * fatherWorkOrder.getQuantity();
						parentWorkOrderSupplied += quantityToAssociate * fatherSupplied.getQuantity(); 
					}
				}
				
				currentProductColumn.setParentWorkOrder(currentProductColumn.getParentWorkOrder() + parentWorkOrder);
				currentProductColumn.setParentWorkOrderSupplied(currentProductColumn.getParentWorkOrderSupplied() + parentWorkOrderSupplied);
			}
		}
		
		return map;
	}
	
	private double calculateMaterialAvailabilityFix(String catalogNumber , MonthDate monthDate) 
	{
		List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
		List<String> descendantsCatalogNumbers = db.getAllDescendantCatalogNumber(catalogNumber);
		List<String> fathersOfDescendantsCatalogNumbers = descendantsCatalogNumbers.stream().map(cn -> db.getFathers(cn).stream().map(pair -> pair.getLeft())
																.collect(Collectors.toList())).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
		List<String> descendantsFathersOfDescendantsCatalogNumbers = fathersOfDescendantsCatalogNumbers.stream().map(el -> { List<String> desCN = db.getAllDescendantCatalogNumber(el); desCN.add(el); return desCN;}).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
		
		double materialAvailabilityFix = 0;
		
		for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
		{
			List<String> descendantsFatherCatalogNumbers = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
			descendantsFatherCatalogNumbers.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
			
			for (String fatherCatalogNumber : descendantsFatherCatalogNumbers) 
			{
				if(descendantsFathersOfDescendantsCatalogNumbers.contains(fatherCatalogNumber))
					continue;
		
				int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
				
				QuantityPerDate fatherForecast = db.getProductFCQuantityOnDate(fatherCatalogNumber , monthDate);
				materialAvailabilityFix += fatherForecast.getQuantity() * quantityToAssociate;
				
				if(isSon(fatherCatalogNumber))
					materialAvailabilityFix += calculateMaterialAvailabilityFix(fatherCatalogNumber, monthDate) * quantityToAssociate;
			}
		}
		
		return materialAvailabilityFix;
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
				db.clearLastMap(maximumDate);
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
			
			List<QuantityPerDate> initFCProductsQuantityPerDate = db.getInitProductsFCQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initWOProductsQuantityPerDate = db.getInitProductsWOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initPOProductsQuantityPerDate = db.getInitProductsPOQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			List<QuantityPerDate> initShipmentProductsQuantityPerDate = db.getInitProductsShipmentsQuantityPerDate(catalogNumber).getOrDefault(catalogNumber, new ArrayList<>());
			
			List<MonthDate> initFCProductsDates = initFCProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initWOProductsDates = initWOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initPOProductsDates = initPOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			List<MonthDate> initShipmentProductsDates = initShipmentProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
			
			
			for (MonthDate monthDate : monthToCalculate) 
			{
				QuantityPerDate supplied = db.getProductShipmentQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate customerOrders = db.getProductPOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate workOrder = db.getProductWOQuantityOnDate(catalogNumber , monthDate);
				QuantityPerDate forecast = db.getProductFCQuantityOnDate(catalogNumber , monthDate);
				
				int indexOfFC = initFCProductsDates.indexOf(monthDate);
				int indexOfWO = initWOProductsDates.indexOf(monthDate);
				int indexOfPO = initPOProductsDates.indexOf(monthDate);
				int indexOfShipment = initShipmentProductsDates.indexOf(monthDate);
				
				double initFC = (indexOfFC < 0) ? 0 : initFCProductsQuantityPerDate.get(indexOfFC).getQuantity();
				double initWO = (indexOfWO < 0) ? 0 : initWOProductsQuantityPerDate.get(indexOfWO).getQuantity();
				double initPO = (indexOfPO < 0) ? 0 : initPOProductsQuantityPerDate.get(indexOfPO).getQuantity();
				double initShipment = (indexOfShipment < 0) ? 0 : initShipmentProductsQuantityPerDate.get(indexOfShipment).getQuantity();
				
				forecast.addQuantity(initFC);
				workOrder.addQuantity(initWO);
				customerOrders.addQuantity(initPO);
				supplied.addQuantity(initShipment);
				
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
				
				List<String> descendantsCatalogNumbers = db.getAllDescendantCatalogNumber(catalogNumber);
				List<String> fathersOfDescendantsCatalogNumbers = descendantsCatalogNumbers.stream().map(el -> db.getFathers(el).stream().map(pair -> pair.getLeft())
																		.collect(Collectors.toList())).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());				
				List<String> descendantsFathersOfDescendantsCatalogNumbers = fathersOfDescendantsCatalogNumbers.stream().map(el ->{ List<String> desCN = db.getAllDescendantCatalogNumber(el); desCN.add(el); return desCN;}).reduce((a,b) -> {a.addAll(b) ; return a;}).orElse(new ArrayList<>());
				
				double materialAvailabilityFix = calculateMaterialAvailabilityFix(catalogNumber , monthDate);
				double initFatherWO = 0;
				
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					List<String> descendantsFatherCatalogNumbers = db.getAllDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					descendantsFatherCatalogNumbers.add(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					
					for (String fatherCatalogNumber : descendantsFatherCatalogNumbers) 
					{
						if(descendantsFathersOfDescendantsCatalogNumbers.contains(fatherCatalogNumber))
							continue;
						
						QuantityPerDate fatherSupplied = db.getProductShipmentQuantityOnDate(fatherCatalogNumber , monthDate);
						QuantityPerDate fatherWorkOrder = db.getProductWOQuantityOnDate(fatherCatalogNumber , monthDate);
						
						List<QuantityPerDate> initWOProductsQuantityPerDateOfFather = db.getInitProductsWOQuantityPerDate(fatherCatalogNumber).getOrDefault(fatherCatalogNumber, new ArrayList<>());
						List<MonthDate> initWOProductsDatesOfFather = initWOProductsQuantityPerDateOfFather.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
						int indexOfWOFather = initWOProductsDatesOfFather.indexOf(monthDate);
						initFatherWO += (indexOfWOFather < 0) ? 0 : initWOProductsQuantityPerDateOfFather.get(indexOfWOFather).getQuantity();
						
						int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
						//customerOrders.setQuantity(customerOrders.getQuantity() + quantityToAssociate * fatherWorkOrder.getQuantity());
						//supplied.setQuantity(supplied.getQuantity() + quantityToAssociate * fatherSupplied.getQuantity());
						//materialAvailabilityFix += quantityToAssociate * fatherWorkOrder.getQuantity();
						
						parentWorkOrder += quantityToAssociate * fatherWorkOrder.getQuantity();
						parentWorkOrderSupplied += quantityToAssociate * fatherSupplied.getQuantity();
					}
					
				}
				
				materialAvailability = forecast.getQuantity() + previousMaterialAvailability - workOrder.getQuantity() + materialAvailabilityFix;
				workOrderAfterSupplied = workOrder.getQuantity() - supplied.getQuantity() - parentWorkOrderSupplied + previousWorkOrderAfterSupplied;
				openCustomerOrder = customerOrders.getQuantity() - supplied.getQuantity() + previousOpenCustomerOrder;
				workOrderAfterCustomerOrderAndParentWorkOrder = previousWorkOrderAfterCustomerOrderAndParentWorkOrder + workOrder.getQuantity()
																				- customerOrders.getQuantity() - parentWorkOrder - initFatherWO;
				
				//workOrderAfterCustomerOrderAndParentWorkOrder = (workOrderAfterCustomerOrderAndParentWorkOrder < 0) ? 0 : workOrderAfterCustomerOrderAndParentWorkOrder;
				
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
					row.add(Integer.toString(productColumn.getColumnValue(i)));
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

	public CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth , String userName , ReportViewFrame mapFrame , Map<MonthDate, Map<String, ProductColumn>> map , List<String> customers) 
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
				if(column < Analyzer.CategoryColumn)
					return null;
				else if(column == Analyzer.CategoryColumn)
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
				
				ReportViewFrame reportViewFrame = getFormsReportView(forms , email , auth);

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
							updateForm.updateValue(column , newValue , userName);
							mapFrame.updateRows(getRows(calculateMap(userName , true , null, updateForm.getCatalogNumber())));
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

	public static ReportViewFrame getFormsReportView(List<? extends Form> forms , String email ,Authenticator auth) 
	{
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
		
		return reportViewFrame;

	}

	public CallBack<Object> getRightClickAction(String email , Authenticator auth , String userName , ReportViewFrame mapFrame , Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		return null;
	}

	public List<Integer> getInvalidEditableCoulmns(String[] columns) 
	{
		return IntStream.rangeClosed(0, columns.length - 1).boxed().collect(Collectors.toList());
	}
	
	public List<MrpHeader> getMrpHeaders(String userName , List<String> customers)
	{
		Map<MonthDate , Map<String,ProductColumn>> map = calculateMap(userName , false , customers , null);
		List<MrpHeader> mrpHeaders = new ArrayList<>();
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription(userName);
		
		MonthDate maximumDate = db.getMaximumMapDate();
		MonthDate minimumDate = db.getMinimumMapDate();
		List<MonthDate> monthToCalculate = createDates(minimumDate , maximumDate);
		
		int indexOfCurrentMonth = monthToCalculate.indexOf(new MonthDate(Globals.getTodayDate()));
				
		for (String catalogNumber : getCatalogNumbersFromMap(map)) 
		{
			String description = catalogNumbers.get(catalogNumber);
			String customer = db.getCustomerOfCatalogNumber(catalogNumber);
			Map<MonthDate , Double> quantityPerMonth = new HashMap<>();
			
			for (int index = monthToCalculate.size() - 1 ; index >= 0 ; index--) 
			{
				MonthDate monthDate = monthToCalculate.get(index);
				ProductColumn productColumn = map.get(monthDate).get(catalogNumber);
				double materialAvailability = productColumn.getMaterialAvailability();
				
				double previousMaterialAvailability = 0;
				if(index != 0)
				{
					ProductColumn previousProductColumn = map.get(monthToCalculate.get(index - 1)).get(catalogNumber);
					previousMaterialAvailability = previousProductColumn.getMaterialAvailability();
				}
				
				double quantity = materialAvailability - previousMaterialAvailability;
				
				List<Pair<String, Integer>> fathersCatalogNumberAndQuantityToAssociate = db.getFathers(catalogNumber);
				
				for (Pair<String, Integer> fatherCatalogNumberAndQuantityToAssociate : fathersCatalogNumberAndQuantityToAssociate) 
				{
					String fatherCatalogNumber = db.getDescendantCatalogNumber(fatherCatalogNumberAndQuantityToAssociate.getLeft());
					List<QuantityPerDate> initFCProductsQuantityPerDate = db.getInitProductsFCQuantityPerDate(fatherCatalogNumber).getOrDefault(fatherCatalogNumber, new ArrayList<>());
					List<QuantityPerDate> initWOProductsQuantityPerDate = db.getInitProductsWOQuantityPerDate(fatherCatalogNumber).getOrDefault(fatherCatalogNumber, new ArrayList<>());
					
					List<MonthDate> initFCProductsDates = initFCProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
					List<MonthDate> initWOProductsDates = initWOProductsQuantityPerDate.stream().map(pc -> pc.getDate()).collect(Collectors.toList());
					
					int indexOfFC = initFCProductsDates.indexOf(monthDate);
					int indexOfWO = initWOProductsDates.indexOf(monthDate);
					
					double initFC = (indexOfFC < 0) ? 0 : initFCProductsQuantityPerDate.get(indexOfFC).getQuantity();
					double initWO = (indexOfWO < 0) ? 0 : initWOProductsQuantityPerDate.get(indexOfWO).getQuantity();
					
					ProductColumn fatherProductColumn = map.get(monthDate).get(fatherCatalogNumber);
					double fatherMaterialAvailability = fatherProductColumn.getMaterialAvailability();
					double previousFatherMaterialAvailability = 0;
					if(index != 0)
					{
						ProductColumn previousFatherProductColumn = map.get(monthToCalculate.get(index - 1)).get(fatherCatalogNumber);
						previousFatherMaterialAvailability = previousFatherProductColumn.getMaterialAvailability();
					}
					
					int quantityToAssociate = fatherCatalogNumberAndQuantityToAssociate.getRight();
					double difference = fatherMaterialAvailability - previousFatherMaterialAvailability;
					difference -= initFC;
					difference += initWO;
					quantity -= difference * quantityToAssociate;
				}
				
				if(indexOfCurrentMonth >= index)
				{
					if(quantityPerMonth.containsKey(monthToCalculate.get(indexOfCurrentMonth)))
						quantity += quantityPerMonth.get(monthToCalculate.get(indexOfCurrentMonth));
					
					quantityPerMonth.put(monthToCalculate.get(indexOfCurrentMonth) , quantity);
				}
				else
					quantityPerMonth.put(monthDate, quantity);
			}
			
			MrpHeader mrpHeader = new MrpHeader(catalogNumber, customer, description, quantityPerMonth);
			mrpHeaders.add(mrpHeader);
		}
		
		return mrpHeaders;
		
	}
	
	public Map<MonthDate,Map<String,MapPrice>> calculateMapPrice(String userName , List<String> customers)
	{
		Map<MonthDate,Map<String,MapPrice>> priceOfProductPerMonth = new HashMap<>();
		Map<MonthDate , Map<String,ProductColumn>> map = calculateMap(userName , false , customers , null);
		
		for (MonthDate month : map.keySet())
		{
			Map<String,MapPrice> productsPrices = new HashMap<>();
			priceOfProductPerMonth.put(month, productsPrices);
			
			for (String cn : map.get(month).keySet()) 
			{
				String customer = db.getCustomerOfCatalogNumber(cn);
				double deposit = db.getCustomerDeposit(customer);
				double obligation = db.getCustomerObligation(customer);
				
				ProductColumn productColumn = map.get(month).get(cn);
				double priceOfProduct = db.getPriceOfProduct(cn);
				double materialAvailabilityPrice = productColumn.getMaterialAvailability() * priceOfProduct;
				double WorkOrderAfterSuppliedPrice = productColumn.getWorkOrderAfterSupplied() * priceOfProduct;
				double OpenCustomerOrderPrice = productColumn.getOpenCustomerOrder() * priceOfProduct;
				double budgetExceeded = materialAvailabilityPrice + WorkOrderAfterSuppliedPrice - OpenCustomerOrderPrice;
				budgetExceeded = (budgetExceeded > (obligation + deposit)) ? budgetExceeded - (obligation + deposit) : 0;
				
				MapPrice mapPrice = new MapPrice(cn, materialAvailabilityPrice, WorkOrderAfterSuppliedPrice, OpenCustomerOrderPrice , budgetExceeded);
				productsPrices.put(cn, mapPrice);
			}
		}
		
		return priceOfProductPerMonth;
	}
	
	public String[][] getRowsOfMapPrice(Map<MonthDate,Map<String,MapPrice>> mapPrice)
	{
		List<List<String>> rows = new ArrayList<>();
		
		List<MonthDate> months = mapPrice.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		
		for (MonthDate monthDate : months) 
		{
			Map<String , MapPrice> productsPrices = mapPrice.get(monthDate);
			List<String> products = productsPrices.keySet().stream().collect(Collectors.toList());
			Collections.sort(products);
			
			for (int index = 0 ; index < products.size() ; index ++) 
			{
				String product = products.get(index);
				MapPrice productPrices = productsPrices.get(product);
				if(months.indexOf(monthDate) == 0)
				{
					for(int i = 0 ; i < MapPrice.CategoriesCount ; i++)
					{
						List<String> row = new ArrayList<>();
						row.add(product);
						row.add(db.getCustomerOfCatalogNumber(product));
						row.add(productPrices.getColumn(i));
						rows.add(row);
					}	
				}
				
				for(int i = 0 ; i < MapPrice.CategoriesCount ; i++)
				{
					List<String> row = rows.get(index * MapPrice.CategoriesCount + i);
					row.add(Integer.toString(productPrices.getColumnValue(i)));
				}
			}
			
		}
		
		return rows.stream().map(row -> row.toArray(new String[0])).toArray(String[][]::new);
	}
	
	public String[] getColumnsOfMapPrice(Map<MonthDate, Map<String, MapPrice>> mapPrice) 
	{
		List<String> columns = new ArrayList<>();
		columns.add("Catalog Number");
		columns.add("Customer");
		columns.add("Category");
		List<MonthDate> months = mapPrice.keySet().stream().collect(Collectors.toList());
		Collections.sort(months);
		columns.addAll(months.stream().map(date -> date.shortString()).collect(Collectors.toList()));
		
		return columns.toArray(new String[0]);
	}
	
	public List<Integer> getFilterColumnsOfMapPrice() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		
		return filterColumns;
	}

	private Set<String> getCatalogNumbersFromMap(Map<MonthDate, Map<String, ProductColumn>> map) 
	{
		Set<String> catalogNumbers = new HashSet<>();
		catalogNumbers.addAll(map.values().stream().findFirst().get().keySet());
		return catalogNumbers;
	}

	private boolean isSon(String catalogNumber) 
	{
		return db.getFathers(catalogNumber).size() > 0;
	}
	
	private boolean isFather(String catalogNumber) 
	{
		return db.getSons(catalogNumber).size() > 0;
	}
}
