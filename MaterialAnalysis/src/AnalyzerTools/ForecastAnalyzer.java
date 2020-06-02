package AnalyzerTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import MainPackage.DataBase;
import MainPackage.Excel;
import MainPackage.Globals;

public class ForecastAnalyzer 
{
	private Globals globals;
	private DataBase db;

	public ForecastAnalyzer() 
	{
		globals = new Globals();
		db = new DataBase();
	}
	
	public String[][] getForecastQuantity(String fileName , String customer , List<String> unknownCatalogNumbers) throws Exception
	{
		XSSFWorkbook w = null;
		XSSFSheet forecastSheet = null;
	    try {      
	    	
	      w = new XSSFWorkbook(fileName);
	      forecastSheet = w.getSheetAt(0);
	      w.close();
	    }catch(Exception e)
	    {
	    	 e.printStackTrace();
	    	 return null;
	    }
	    
	    
	    Cell itemNumberColumnCell = Excel.findCell(forecastSheet, globals.itemNumberColumn);
	    if(itemNumberColumnCell == null)
	    	throw new Exception("Wrong file format");
	    
	    int itemNumberRow = itemNumberColumnCell.getRowIndex();
	    int itemNumberColumn = itemNumberColumnCell.getColumnIndex();
	    
	    int rowsNumber = Excel.rowsNumberFromRow(forecastSheet, itemNumberRow, itemNumberColumn);
	    Row TitleRow = forecastSheet.getRow(itemNumberRow);
	    int numOfDates = TitleRow.getPhysicalNumberOfCells() - 1;
	    
	    List<List<String>> rows = new ArrayList<>();
	    List<String> title = new ArrayList<>();
	    title.add(globals.itemNumberColumn);
	    
	    for(int dateIndex = 1 ; dateIndex <= numOfDates ; dateIndex++)
    	{
    		Cell dateCell = TitleRow.getCell(itemNumberColumn + dateIndex);
    		
    		Date forecastDate;
			if (dateCell.getCellType() == Cell.CELL_TYPE_STRING)
			{
				if((forecastDate = Globals.isValidDate(dateCell.getStringCellValue().trim())) == null)
					continue;
			}
			else
				forecastDate = dateCell.getDateCellValue();
			
			title.add(Globals.dateWithoutHourToString(forecastDate));
    	}
	    
	    rows.add(title);
	    	    
	    for(int rowIndex = itemNumberRow + 1 ; rowIndex < rowsNumber ; rowIndex++)
		{
	    	Row row = forecastSheet.getRow(rowIndex);
	    	
	    	Cell itemNumberCell = row.getCell(itemNumberColumn);
	    	if(itemNumberCell.getCellType() != Cell.CELL_TYPE_STRING)
	    		continue;
	    	
	    	String catalogNumber = db.getFullCatalogNumber(itemNumberCell.getStringCellValue() , customer).trim();
	    	if(catalogNumber.equals(""))
	    	{
	    		unknownCatalogNumbers.add(itemNumberCell.getStringCellValue());
	    		continue;
	    	}
	    	
	    	List<QuantityPerDate> forecastQuantities = db.calculateProductFCQuantityOnDate(catalogNumber);
	    	List<MonthDate> forecastDates = forecastQuantities.stream().map(forecastQuantity -> forecastQuantity.getDate()).collect(Collectors.toList());
	    	
	    	List<String> currentItemRow = new ArrayList<>();
	    	currentItemRow.add(catalogNumber);
	    	
	    	for(int dateIndex = 1 ; dateIndex <= numOfDates ; dateIndex++)
	    	{
	    		Cell dateCell = TitleRow.getCell(itemNumberColumn + dateIndex);
	    		
	    		Date forecastDate;
				if (dateCell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if((forecastDate = Globals.isValidDate(dateCell.getStringCellValue().trim())) == null)
						continue;
				}
				else
					forecastDate = dateCell.getDateCellValue();
				
				int indexOfDate = forecastDates.indexOf(new MonthDate(forecastDate));
				double currentForecastQuantity = (indexOfDate != -1) ? forecastQuantities.get(indexOfDate).getQuantity() : 0;
				
				Cell quantityCell = row.getCell(itemNumberColumn + dateIndex);
				
				double newForecastQuantity;
				if (quantityCell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if(!NumberUtils.isCreatable(quantityCell.getStringCellValue()))
						continue;
					else if(quantityCell.getStringCellValue().equals(""))
						newForecastQuantity = 0;
					else
						newForecastQuantity = Double.parseDouble(quantityCell.getStringCellValue());
				}
				else
					newForecastQuantity = quantityCell.getNumericCellValue();

				// Formula: NewForecast - MaterialAvailability - WorkOrder ==> NewForecast - CurrentForecast + PreviousMaterialAvailability
				double differenceForecast = newForecastQuantity - currentForecastQuantity;
				if(dateIndex == 1)
				{
					// Subtract previous MaterialAvailability
					differenceForecast -= db.getMaterialAvailability(catalogNumber, new MonthDate(Globals.addMonths(forecastDate, -1)));
					//differenceForecast += db.getOpenCustomerOrder(catalogNumber, new MonthDate(forecastDate)); // Only for Novocure
				}

				currentItemRow.add(differenceForecast + "");
	    	}
	    	
	    	rows.add(currentItemRow);
	    	
		}
	    
	    return rows.stream().map(row -> row.toArray(new String[0])).toArray(String[][]::new);
	    
	}
	
	public boolean addForecast(String fileName , String customer , String userName , List<String> unknownCatalogNumbers) throws Exception
	{
		Analyzer analyzer = new Analyzer();
		XSSFWorkbook w = null;
		XSSFSheet forecastSheet = null;
	    try {      
	    	
	      w = new XSSFWorkbook(fileName);
	      forecastSheet = w.getSheetAt(0);
	      w.close();
	    }catch(Exception e)
	    {
	    	 e.printStackTrace();
	    	 return false;
	    }
	    
	    
	    Cell itemNumberColumnCell = Excel.findCell(forecastSheet, globals.itemNumberColumn);
	    if(itemNumberColumnCell == null)
	    	throw new Exception("Wrong file format");
	    
	    int itemNumberRow = itemNumberColumnCell.getRowIndex();
	    int itemNumberColumn = itemNumberColumnCell.getColumnIndex();
	    
	    int rowsNumber = Excel.rowsNumberFromRow(forecastSheet, itemNumberRow, itemNumberColumn);
	    Row TitleRow = forecastSheet.getRow(itemNumberRow);
	    int numOfDates = TitleRow.getPhysicalNumberOfCells() - 1;
	    	    
	    for(int rowIndex = itemNumberRow + 1 ; rowIndex < rowsNumber ; rowIndex++)
		{
	    	Row row = forecastSheet.getRow(rowIndex);
	    	
	    	Cell itemNumberCell = row.getCell(itemNumberColumn);
	    	if(itemNumberCell.getCellType() != Cell.CELL_TYPE_STRING)
	    		continue;
	    	
	    	String catalogNumber = db.getFullCatalogNumber(itemNumberCell.getStringCellValue() , customer).trim();
	    	if(catalogNumber.equals(""))
	    	{
	    		unknownCatalogNumbers.add(itemNumberCell.getStringCellValue());
	    		continue;
	    	} 
	    		    	
	    	for(int dateIndex = 1 ; dateIndex <= numOfDates ; dateIndex++)
	    	{
	    		Cell dateCell = TitleRow.getCell(itemNumberColumn + dateIndex);
	    		
	    		Date forecastDate;
				if (dateCell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if((forecastDate = Globals.isValidDate(dateCell.getStringCellValue().trim())) == null)
						continue;
				}
				else
					forecastDate = dateCell.getDateCellValue();
				
				Cell quantityCell = row.getCell(itemNumberColumn + dateIndex);
				
				double newForecastQuantity;
				if (quantityCell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if(!NumberUtils.isCreatable(quantityCell.getStringCellValue()))
						continue;
					else if(quantityCell.getStringCellValue().equals(""))
						newForecastQuantity = 0;
					else
						newForecastQuantity = Double.parseDouble(quantityCell.getStringCellValue());
				}
				else
					newForecastQuantity = quantityCell.getNumericCellValue();
				
				if(newForecastQuantity == 0)
					continue;
				
				Date initDate = Globals.getTodayDate();
				String description = db.getDescription(catalogNumber).get(catalogNumber);
				String quantity = newForecastQuantity + "";
				String notes = "added automatically by " + userName;
				
				analyzer.addNewFC(customer, catalogNumber, quantity, Globals.dateWithoutHourToString(initDate), Globals.dateWithoutHourToString(forecastDate), description, userName , notes);
				
	    	}
	    	
		}
	    
	    return true;
	    
	}

}
