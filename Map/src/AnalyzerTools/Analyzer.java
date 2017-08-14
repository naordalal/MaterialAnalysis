package AnalyzerTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import Forms.Forecast;
import Forms.Form;
import mainPackage.DataBase;
import mainPackage.Globals;
import mainPackage.Globals.FormType;

public class Analyzer 
{
	private Globals globals;
	private DataBase db;

	public Analyzer() 
	{
		globals = new Globals();
		db = new DataBase();
	}
	
	public void addNewFC(String customer , String catalogNumber , String quantity , String initDate , String requireDate , String description , String notes)
	{
		db.addFC(customer, catalogNumber, quantity, initDate, requireDate, description , notes);
	}
	
	public void removeFC(String catalogNumber)
	{
		db.removeFC(catalogNumber);
	}
	
	public void cleanProductQuantityPerDate(String catalogNumber)
	{
		db.cleanProductQuantityPerDate(catalogNumber , FormType.WO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.PO);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.SHIPMENT);
		db.cleanProductQuantityPerDate(catalogNumber , FormType.FC);
	}
	
	public void updateProductQuantities()
	{
		updateProductQuantities(db.getAllFC(), db.getAllProductsFCQuantityPerDate(),db.getInitProductsFCQuantityPerDate(),db.getInitProductsFCDates() , FormType.FC);
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
