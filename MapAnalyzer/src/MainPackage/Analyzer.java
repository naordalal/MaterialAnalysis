package MainPackage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

import Forms.Form;
import MainPackage.Globals.FormType;
import MainPackage.Globals.UpdateType;

public class Analyzer 
{
	private Globals globals;
	private DataBase db;
	private String woFilePath;
	private String customerOrdersFilePath;
	private String shipmentsFilePath;

	public Analyzer() 
	{
		globals = new Globals();
		db = new DataBase();
	}
	
	public Analyzer(String woFilePath , String customerOrdersFilePath , String shipmentsFilePath) 
	{
		globals = new Globals();
		db = new DataBase();
		this.woFilePath = woFilePath;
		this.customerOrdersFilePath = customerOrdersFilePath;
		this.shipmentsFilePath = shipmentsFilePath;
	}

	public void analyze() throws IOException
	{
		db.removeHistoryOfForm(FormType.PO, Globals.monthsToIgnore);
		db.removeHistoryOfForm(FormType.WO, Globals.monthsToIgnore);
		db.removeHistoryOfForm(FormType.SHIPMENT, Globals.monthsToIgnore);
		
		if(woFilePath == null)
		{
			analyzeWO(globals.WOFilePath);
			analyzeCustomerOrders(globals.customerOrdersFilePath);
			analyzeShipments(globals.shipmentsFilePath);
		}
		else
		{
			analyzeWO(woFilePath);
			analyzeCustomerOrders(customerOrdersFilePath);
			analyzeShipments(shipmentsFilePath);
		}

		
		updateProductQuantities(db.getAllPO(), db.getAllProductsPOQuantityPerDate(), db.getInitProductsPODates(),  FormType.PO);
		updateProductQuantities(db.getAllWO(), db.getAllProductsWOQuantityPerDate(),db.getInitProductsWODates() , FormType.WO);
		updateProductQuantities(db.getAllShipments(), db.getAllProductsShipmentQuantityPerDate(),db.getInitProductsShipmentsDates() , FormType.SHIPMENT);
		
		db.updateLastUpdateDate(UpdateType.ProductQuantity);
		
		updateMap();
	}

	private void analyzeWO(String filePath) throws IOException 
	{
		String fileDir = filePath.substring(0,filePath.lastIndexOf("\\"));
		String filePrefix = filePath.substring(filePath.lastIndexOf("\\") + 1);
		
		int woNumberColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , customerColumn = -1 , dateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(getFilePath(fileDir , filePrefix),Charset.forName(globals.charsetName)))
		{
			List<String> columns = Arrays.asList(line.split("\\|" , -1)).stream().map(s -> s.trim()).collect(Collectors.toList());
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
				if(date == null || columns.get(catalogNumberColumn).trim().equals("") || !NumberUtils.isCreatable(columns.get(quantityColumn)))
				{
					continue;	
				}
				if(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToIgnore - 1).before(date))
					db.addWO(columns.get(woNumberColumn), columns.get(catalogNumberColumn), columns.get(quantityColumn)
							, columns.get(customerColumn), columns.get(dateColumn), columns.get(descriptionColumn));
			}
		}	
	}

	private void analyzeCustomerOrders(String filePath) throws IOException 
	{
		String fileDir = filePath.substring(0,filePath.lastIndexOf("\\"));
		String filePrefix = filePath.substring(filePath.lastIndexOf("\\") + 1);
		
		int customerColumn = -1 , orderNumberColumn = -1 , customerOrderNumberColumn = -1 , catalogNumberColumn = -1 , descriptionColumn = -1 , quantityColumn = -1 , priceColumn = -1,
				orderDateColumn = - 1 , guaranteedDateColumn = -1;
		for (String line : Files.readAllLines(getFilePath(fileDir , filePrefix),Charset.forName(globals.charsetName)))
		{
			List<String> columns = Arrays.asList(line.split("\\|" , -1)).stream().map(s -> s.trim()).collect(Collectors.toList());
			if(customerColumn == -1) 
				customerColumn = columns.indexOf(globals.customerIdColumn);
			if(orderNumberColumn == -1) 
				orderNumberColumn = columns.indexOf(globals.orderNumberColumn);
			if(customerOrderNumberColumn == -1) 
				customerOrderNumberColumn = columns.indexOf(globals.customerOrderNumberColumn);
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
				Date orderDate = Globals.parseDate(columns.get(orderDateColumn));
				Date guaranteedDate = Globals.parseDate(columns.get(guaranteedDateColumn));
				if(orderDate == null || guaranteedDate == null || columns.get(catalogNumberColumn).trim().equals("") || !NumberUtils.isCreatable(columns.get(quantityColumn)))
					continue;
				if(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToIgnore - 1).before(orderDate))
					db.addCustomerOrder(columns.get(customerColumn), columns.get(orderNumberColumn), columns.get(customerOrderNumberColumn), columns.get(catalogNumberColumn)
							, columns.get(descriptionColumn), columns.get(quantityColumn), columns.get(priceColumn) 
							, columns.get(orderDateColumn) , columns.get(guaranteedDateColumn));
			}
		}
	}
	
	private void analyzeShipments(String filePath) throws IOException 
	{
		String fileDir = filePath.substring(0,filePath.lastIndexOf("\\"));
		String filePrefix = filePath.substring(filePath.lastIndexOf("\\") + 1);
		
		int customerColumn = -1 , orderIdColumn = -1 , orderCustomerIdColumn = -1 , catalogNumberColumn = -1 , quantityColumn = -1 , shipmentDateColumn = -1 , descriptionColumn = -1;
		for (String line : Files.readAllLines(getFilePath(fileDir , filePrefix),Charset.forName(globals.charsetName)))
		{
			List<String> columns = Arrays.asList(line.split("\\|" , -1)).stream().map(s -> s.trim()).collect(Collectors.toList());
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
				if(date == null || columns.get(catalogNumberColumn).trim().equals("") || !NumberUtils.isCreatable(columns.get(quantityColumn)))
					continue;
				if(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToIgnore - 1).before(date))
					db.addShipment(columns.get(customerColumn), columns.get(orderIdColumn), columns.get(orderCustomerIdColumn) ,columns.get(catalogNumberColumn)
							, columns.get(quantityColumn), columns.get(shipmentDateColumn), columns.get(descriptionColumn));
			}
		}
	}
	
	
	private Path getFilePath(String fileDir , String filePrefix) 
	{
		File dir = new File(fileDir);
		File[] foundFiles = dir.listFiles(new FilenameFilter() 
		{
		    public boolean accept(File dir, String name) 
		    {
		        return name.toLowerCase().startsWith(filePrefix.toLowerCase());
		    }
		});

		if(foundFiles.length > 0)
		{
			File lastModifiedFile = foundFiles[0];
			long lastModified = foundFiles[0].lastModified();
			for(int i = 1 ; i < foundFiles.length ; i++)
			{
				File currentFile = foundFiles[i];
				if(currentFile.lastModified() > lastModified)
				{
					lastModifiedFile = currentFile;
					lastModified = currentFile.lastModified();
				}
			}
			
			return lastModifiedFile.toPath();
		}
		
		return null;
	}
	
	private void updateProductQuantities(List<? extends Form> forms ,  Map<String, List<QuantityPerDate>> productsQuantityPerDate 
												,Map<String , Date> productsInitDates ,  FormType type)
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
		Map<String , List<QuantityPerDate>> initProductsQuantityPerDate = new HashMap<>(); 
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
	
	public void updateLastMap()
	{
		
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		MonthDate lastCalculateMapDate = db.getLastCalculateMapDate();
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToIgnore - 1));
		
		if(lastCalculateMapDate != null && maximumDate.equals(lastCalculateMapDate))
			return;
		
		MonthDate minimumDate;
		if(lastCalculateMapDate == null)
			minimumDate = db.getMinimumInitDate();
		else
			minimumDate = new MonthDate(Globals.addMonths(lastCalculateMapDate, 1));
				
		if(minimumDate == null || maximumDate.before(minimumDate))
		{
			db.clearLastMap(maximumDate);
			return;
		}
				
		Map<String,ProductColumn> lastMap = (lastCalculateMapDate != null) ? db.getLastMap(lastCalculateMapDate) : new HashMap<String,ProductColumn>();
				
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription();
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
						//QuantityPerDate fatherForecast = db.getProductFCQuantityOnDate(fatherCatalogNumber , monthDate);
						
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
	
	private boolean isSon(String catalogNumber) 
	{
		return db.getFathers(catalogNumber).size() > 0;
	}
	
	public void updateMap()
	{		
		MonthDate maximumDate = new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate - 1));
		if(!db.mapContainsDate(maximumDate))
			updateLastMap();
		
		boolean containsDate = db.mapContainsDate(maximumDate);
		Map<String,ProductColumn> lastMap = (containsDate) ? db.getLastMap(maximumDate) : new HashMap<String,ProductColumn>();
		Map<MonthDate,Map<String,ProductColumn>> map = calculateMap(lastMap);
		
		for(MonthDate date : map.keySet())
		{
			db.updateMap(map.get(date), date);	
		}
		
		db.updateLastUpdateDate(UpdateType.MAP);
	}

	private Map<MainPackage.MonthDate, Map<String, ProductColumn>> calculateMap(Map<String, ProductColumn> lastMap) 
	{
		Map<MonthDate,Map<String,ProductColumn>> map = new HashMap<MonthDate,Map<String,ProductColumn>>();
		Map<MonthDate,Map<String,ProductColumn>> helpedMap = new HashMap<MonthDate,Map<String,ProductColumn>>();
		
		Map<String,String> catalogNumbers = db.getAllCatalogNumbersPerDescription();
		
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

}
