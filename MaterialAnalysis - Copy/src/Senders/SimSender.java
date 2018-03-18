package Senders;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.Authenticator;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import MainPackage.Excel;
import MainPackage.Globals;
import MainPackage.Pair;
import MainPackage.SimGlobals;
import MainPackage.noValidEmailException;

public class SimSender extends Sender{

	private List<File> supplierOrdersFile;
	private XSSFSheet ordersSheet;
	private String from;
	private FileOutputStream fos;
	private List<String> untilDate;
	private Authenticator auth;
	private SimGlobals globals;
	private ArrayList<Pair<Integer, Point>> expensiveOrders;
	
	public SimSender(String from , Authenticator auth, List<File> suppliersOrdersFiles) 
	{
		
		super(from, auth);
		this.from = from;
		this.auth = auth;
		this.expensiveOrders = new ArrayList<Pair<Integer,Point>>();
		this.supplierOrdersFile = suppliersOrdersFiles;
		this.globals = new SimGlobals();
	}

	@Override
	public List<String> send() {
		List<String> suppliers = new ArrayList<String>();
		List<Map<String, List<Pair<Integer,Point>>>> listOfSuppliersOrders = new ArrayList<Map<String,List<Pair<Integer,Point>>>>();
		List<Map<String, Pair<Double,Boolean>>> shortagesItemsList = new ArrayList<Map<String,Pair<Double,Boolean>>>();
		Map<Pair<Integer,Point>,Double> mapOfOpenOrders = new HashMap<Pair<Integer,Point>, Double>();
		Map<String,String> mapOfDescriptions = new HashMap<String,String>();
		
		XSSFWorkbook w = null;
	    
	    
		for(int i = 0 ; i < supplierOrdersFile.size() ; i++)
		{
		    try {
		    	//-Xmx1024M
		      //FileInputStream fis = new FileInputStream(supplierOrdersFile.get(i));

		      w = new XSSFWorkbook(supplierOrdersFile.get(i));
		      /*OPCPackage p = OPCPackage.open(supplierOrdersFile.get(i) , PackageAccess.READ);
		      XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(w);
		      evaluator.evaluateAll();*/
		      // Get the first sheet
		      ordersSheet = w.getSheetAt(0);
		      w.close();
		      //p.revert();
		      //fis.close();
		      
		      
		    }catch(Exception e)
		    {
		    	 e.printStackTrace();
		    	 return null;
		    }
		    		
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date toExpediteDate;
		try {
			toExpediteDate = sourceFormat.parse(untilDate.get(i));
			String toExp = outsourceFormat.format(toExpediteDate);
			toExpediteDate = outsourceFormat.parse(toExp);
			Calendar c = Calendar.getInstance();
			c.setTime(toExpediteDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			toExpediteDate = c.getTime();
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
		
			
		Map<String, List<Pair<Integer,Point>>> suppliersOrders = new HashMap<String, List<Pair<Integer,Point>>>();
		Map<String, Pair<Double,Boolean>> shortagesItems = new HashMap<String, Pair<Double,Boolean>>();
		
		
		shortagesItemsList.add(shortagesItems);
		
		Cell polarizerCell = Excel.findCell(ordersSheet, globals.itemNumberColumn);
		Cell shortageCell = Excel.findCell(ordersSheet, globals.shortageColumn);
		Cell priceCell = Excel.findCell(ordersSheet, globals.priceColumn);
		Cell descCell = Excel.findCell(ordersSheet, globals.descriptionColumn);
		
		if(polarizerCell == null || shortageCell == null)
		{
			JOptionPane.showConfirmDialog(null, "missing 'מקטב' or 'חוסר נוכחי' column","",JOptionPane.PLAIN_MESSAGE);
			return null;
		}
		
		int polarizerColumn = polarizerCell.getColumnIndex();
		int shortageColumn = shortageCell.getColumnIndex();
		int priceColumn = priceCell.getColumnIndex();
		int polarizerRow = polarizerCell.getRowIndex();
		//TODO int rowsNumber = ordersSheet.getPhysicalNumberOfRows();
		int rowsNumber = Excel.rowsNumberFromRow(ordersSheet, polarizerRow, polarizerColumn);
		List<Cell> datesCell = new ArrayList<Cell>();
		
		
		for (int index = 1 ; index <= globals.maxOrderIndex ; index++) 
		{
			Cell cell = Excel.findCellInRow(polarizerCell.getRow(), globals.dateColumn + index);
			if(cell == null)
			{
				JOptionPane.showConfirmDialog(null, "wrong format , missing orders rounds , have to be " + globals.maxOrderIndex + " rounds","",JOptionPane.PLAIN_MESSAGE);
				return null;
			}
			datesCell.add(cell);
		}

		for(int index = polarizerRow +1 ; index < rowsNumber ; index++)
		{
			Row row = ordersSheet.getRow(index);
			Cell currentShortage = row.getCell(shortageColumn);
			
			if(currentShortage == null)
				break;	
			
			if(currentShortage.getCellType() != Cell.CELL_TYPE_NUMERIC && currentShortage.getCellType() != Cell.CELL_TYPE_FORMULA)
			{
				JOptionPane.showConfirmDialog(null, "wrong file format , please remove make items","",JOptionPane.PLAIN_MESSAGE);
				return null;
			}
			
			if(currentShortage.getNumericCellValue() >= 0)
				continue;
			
			int maxOrderIndex = globals.maxOrderIndex;
			int orderIndex = 0;
			
			do 
			{
				Cell currentShortageAfterOrder = row.getCell(shortageColumn + orderIndex);
				if(currentShortageAfterOrder.getNumericCellValue() >= 0)
					break;

				Cell titleDateCell = datesCell.get(orderIndex);
				Cell currentDateCell = row.getCell(titleDateCell.getColumnIndex());
				
				if(currentDateCell == null)
				{
					orderIndex++;
					continue;
				}
				
				if (currentDateCell.getCellType() == Cell.CELL_TYPE_STRING) 
				{
					if(currentDateCell.getStringCellValue().trim().equals("00/00/0000") || currentDateCell.getStringCellValue().trim().equals(""))
					{
						orderIndex++;
						continue;
					}
				}
				else if(currentDateCell.getCellType() == Cell.CELL_TYPE_BLANK)
				{
					orderIndex++;
					continue;
				}
				
				Date cellDate;
				if (currentDateCell.getCellType() == Cell.CELL_TYPE_STRING)
				{
					if((cellDate = Globals.isValidDate(currentDateCell.getStringCellValue().trim())) == null)
					{
						orderIndex++;
						continue;
					}
				}
				else
					cellDate = currentDateCell.getDateCellValue();
				
				if(cellDate == null)
				{
					orderIndex++;
					continue;
				}
				
				int distanceFromDateToSupplier = globals.dateOffset - globals.supplierOffset;
				Cell supplierCell = row.getCell(currentDateCell.getColumnIndex() - distanceFromDateToSupplier);
				
				Calendar c = Calendar.getInstance();
				c.setTime(cellDate);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.add(Calendar.DAY_OF_MONTH, -1);
				if(supplierCell.getStringCellValue().trim().matches(".*[a-zA-Z].*"))
					c.add(Calendar.DAY_OF_MONTH, 45);
				cellDate = c.getTime();
				
				if(cellDate.before(toExpediteDate))
				{
					orderIndex++;
					continue;
				}
				else
				{
					Cell nextShortageAfterOrder = row.getCell(shortageColumn + orderIndex + 1);
					Cell currentPriceCell = row.getCell(priceColumn);
					
					int distanceFromDateToStart = globals.dateOffset - globals.orderOffset;
					int distanceFromDateToend = globals.quantityOffset - globals.dateOffset;
					
					if(suppliersOrders.containsKey(supplierCell.getStringCellValue().trim()))
					{
						Point point = new Point(currentDateCell.getColumnIndex() - distanceFromDateToStart, 
								currentDateCell.getColumnIndex() + distanceFromDateToend);
						Pair<Integer, Point> pair = new Pair<Integer, Point>(row.getRowNum(), point);
						if(!orderExistsInPreviousMap(supplierCell.getStringCellValue().trim() , pair , listOfSuppliersOrders))
						{
							
							suppliersOrders.get(supplierCell.getStringCellValue().trim()).add(pair);
							mapOfOpenOrders.put(pair, currentShortageAfterOrder.getNumericCellValue());
							
							if(nextShortageAfterOrder.getNumericCellValue() > 0)
							{
								double count = nextShortageAfterOrder.getNumericCellValue();
								double price = currentPriceCell.getNumericCellValue();
								double total = count * price;
								
								if(total > Globals.highPrice)
								{
									expensiveOrders.add(pair);
								}
							}
						}
							
					}
					else
					{
						List<Pair<Integer,Point>> list = new ArrayList<Pair<Integer,Point>>();
						Point point = new Point(currentDateCell.getColumnIndex() - distanceFromDateToStart
								, currentDateCell.getColumnIndex() + distanceFromDateToend);
						Pair<Integer, Point> pair = new Pair<Integer, Point>(row.getRowNum(), point);
						list.add(pair);
						if(!orderExistsInPreviousMap(supplierCell.getStringCellValue().trim()  , pair , listOfSuppliersOrders))
						{
							
							suppliersOrders.put(supplierCell.getStringCellValue().trim(), list);
							mapOfOpenOrders.put(pair, currentShortageAfterOrder.getNumericCellValue());
							
							if(nextShortageAfterOrder.getNumericCellValue() > 0)
							{
								double count = nextShortageAfterOrder.getNumericCellValue();
								double price = currentPriceCell.getNumericCellValue();
								double total = count * price;
								
								if(total > Globals.highPrice)
								{
									expensiveOrders.add(pair);
								}
							}
						}
							
					}
				}
				orderIndex++;
				
			}while(orderIndex < maxOrderIndex);
			
			Cell currentShortageAfterOrder = row.getCell(shortageColumn + maxOrderIndex);
			
			if(currentShortageAfterOrder.getNumericCellValue() < 0)
			{
				Cell itemCell = row.getCell(polarizerColumn);
				Cell openOrders = row.getCell(shortageColumn + maxOrderIndex + globals.openOrdersIndex);
				Cell description = null;
				
				if(descCell != null)
				{
					description = row.getCell(descCell.getColumnIndex());
					
					if(!mapOfDescriptions.containsKey(row.getRowNum()))
						mapOfDescriptions.put(itemCell.getStringCellValue() , description.getStringCellValue());
				}
				
				if(openOrders != null && openOrders.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
					boolean open = openOrders.getNumericCellValue() > 0;
					
					if(open)
					{
						Point point = new Point(0, -1);
						Pair<Integer,Point> pair = new Pair<Integer, Point>(index, point);
						mapOfOpenOrders.put(pair,currentShortageAfterOrder.getNumericCellValue());
						if(suppliersOrders.containsKey(null))
							suppliersOrders.get(null).add(pair);
						else
						{
							List<Pair<Integer,Point>> list = new ArrayList<Pair<Integer,Point>>();
							list.add(pair);
							suppliersOrders.put(null, list);
						}
					}
					double count = currentShortageAfterOrder.getNumericCellValue() + openOrders.getNumericCellValue();
					if(count < 0)
						shortagesItems.put(itemCell.getStringCellValue(), new Pair <Double , Boolean>(count , open));
				}
				else
					shortagesItems.put(itemCell.getStringCellValue(), new Pair <Double , Boolean>(currentShortageAfterOrder.getNumericCellValue() , false));
				
			}
			
		}
		
		listOfSuppliersOrders.add(suppliersOrders);
		
		}
		
		int size = listOfSuppliersOrders.stream().map(el -> ((Map<String,List<Pair<Integer,Point>>>)el).size()).mapToInt(Integer::intValue).sum();
		if(size > 0)
			sendAllExpediteOrders(listOfSuppliersOrders , mapOfOpenOrders);
		size = shortagesItemsList.stream().map(el -> ((Map<String,Pair<Double,Boolean>>)el).size()).mapToInt(Integer::intValue).sum();
		if(size > 0)
		{
			sendShortagesItems(shortagesItemsList , mapOfDescriptions);
		}
		
		return suppliers;
}
	
	
	
	private Map<Pair<Integer, Point>, Integer> createOrderPerDateMap(
			List<Map<String, List<Pair<Integer, Point>>>> listOfSuppliersOrders) {
		
		Map<Pair<Integer, Point>, Integer> returnMap = new HashMap<Pair<Integer,Point>, Integer>();
		int indexDate = 0;
		for (Map<String, List<Pair<Integer, Point>>> map : listOfSuppliersOrders) 
		{
			for (Map.Entry<String, List<Pair<Integer, Point>>> entry : map.entrySet()) 
			{
				for (Pair<Integer, Point> pair : entry.getValue()) 
				{
					returnMap.put(pair, indexDate);
				}
				
			}
			
			indexDate ++;
		}
		return returnMap;
	}

	private boolean orderExistsInPreviousMap(String stringCellValue,
			Pair<Integer, Point> checkPair,
			List<Map<String, List<Pair<Integer, Point>>>> listOfSuppliersOrders) {
		
		for (Map<String, List<Pair<Integer, Point>>> map : listOfSuppliersOrders) 
		{
			List<Pair<Integer,Point>> list = map.get(stringCellValue);
			
			if(list == null)
				continue;
			
			for (Pair<Integer, Point> pair : list) 
			{
				if(pair.getLeft().intValue() == checkPair.getLeft().intValue() && (int)checkPair.getRight().getX() == (int)pair.getRight().getX() 
						&& (int) checkPair.getRight().getY() == (int)pair.getRight().getY())
					return true;
			}
		}
		return false;
	}
	
	private void sendAllExpediteOrders(
			List<Map<String, List<Pair<Integer, Point>>>> listOfSuppliersOrders, Map<Pair<Integer, Point>, Double> mapOfOpenOrders) 
	{
		File file = new File(globals.expediteOrdersPath);
		int index = 1;
		while(file.exists())
		{
			file = new File(globals.expediteOrdersPath.split("\\.")[0]+" (" + index + ")."+globals.expediteOrdersPath.split("\\.")[1]);
    		index++;
		}
	    /*WorkbookSettings wbSettings = new WorkbookSettings();

	    wbSettings.setLocale(new Locale("en", "EN"));*/

		XSSFWorkbook w = null;
	    w = new XSSFWorkbook();
	    
	    w.createSheet("Report");
	    XSSFSheet excelSheet = w.getSheetAt(0);
	    
    	
    	XSSFCellStyle style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
	    Row newRow = excelSheet.createRow(0);
	    
	    Cell cell = newRow.createCell(0);	    
	    cell.setCellValue("Part number");
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(1);	    
	    cell.setCellValue("Description");
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(2);	    
	    cell.setCellValue("Order number");
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(3);	    
	    cell.setCellValue("Supplier");
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(4);	    
	    cell.setCellValue("Delivery date");
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(5);	    
	    cell.setCellValue("Quantity");
    	cell.setCellStyle(style);
	    
    	style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
    	cell = newRow.createCell(6);	    
	    cell.setCellValue("Request expedite date");
    	cell.setCellStyle(style);
    	
    	XSSFCellStyle style2 = w.createCellStyle();
    	style2.setFillForegroundColor(IndexedColors.RED.index);
    	style2.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	
    	cell = newRow.createCell(7);	    
	    cell.setCellValue("חוסר לסבב");
    	cell.setCellStyle(style2);
    	
    	cell = newRow.createCell(8);	    
	    cell.setCellValue("הזמנות פתוחות לאחר 3 סבבים");
    	cell.setCellStyle(style2);
    	
	    int rowStart = 1;
	    
	    Map<Pair<Integer,Point>,Integer> mapOfItemPerDate = createOrderPerDateMap(listOfSuppliersOrders);
	    
	    createExpediteContent(w , excelSheet , listOfSuppliersOrders , rowStart , mapOfItemPerDate , true , mapOfOpenOrders);

	    for(int i = 0 ; i < 9 ; i++)
	    	excelSheet.autoSizeColumn(i);  
	    
	    List<Integer> columns = new ArrayList<Integer>();
	    columns.add(6);
	    columns.add(0);
	    columns.add(4);
	    
	    List<Globals.Sort> sortMethods = new ArrayList<Globals.Sort>();
	    sortMethods.add(Globals.Sort.ASC);
	    sortMethods.add(Globals.Sort.ASC);
	    sortMethods.add(Globals.Sort.ASC);
	    
	    //Excel.sortSheet(excelSheet, columns, sortMethods , 1);
	    
	    try {
			fos = new FileOutputStream(file);
			w.write(fos);
			fos.close();
			w.close();
			
			List <String> to = new ArrayList<String>();
			to.add(from);
			SendEmail senderMail = new SendEmail(from, to, auth);
		    boolean success;
			try {
				success = senderMail.send("expedite orders file", "", file , globals.expediteOrdersPath);
			} catch (noValidEmailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			}
		     
		    file.delete();
		    
		    if(!success)
		    {
		    	 JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
		    }
		     
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendShortagesItems(List<Map<String, Pair<Double, Boolean>>> shortagesItemsList, Map<String, String> mapOfDescriptions) 
	{
		File file = new File(globals.ShortagesItemsPath);
		int index = 1;
		while(file.exists())
		{
			file = new File(globals.ShortagesItemsPath.split("\\.")[0]+" (" + index + ")."+globals.ShortagesItemsPath.split("\\.")[1]);
    		index++;
		}
	    /*WorkbookSettings wbSettings = new WorkbookSettings();

	    wbSettings.setLocale(new Locale("en", "EN"));*/
		XSSFWorkbook w = null;
	    w = new XSSFWorkbook();
	    
	    w.createSheet("Report");
	    XSSFSheet excelSheet = w.getSheetAt(0);
	        
	    Row newRow = excelSheet.createRow(0);
	    
	    Cell cell = newRow.createCell(0);
	    
	    cell.setCellValue("מקטב");
    	
    	XSSFCellStyle style = w.createCellStyle();
    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    	cell.setCellStyle(style);
    	
    	cell = newRow.createCell(1);
	    cell.setCellValue("תיאור");
	    cell.setCellStyle(style);	
	    
    	cell = newRow.createCell(2);
	    cell.setCellValue("כמות חסרה");
	    cell.setCellStyle(style);	    
	    
	    cell = newRow.createCell(3);
	    cell.setCellValue("כמות נדרשת לסבב");
	    cell.setCellStyle(style);
	    
    	cell = newRow.createCell(4);
	    cell.setCellValue("תאריך נדרש");
	    cell.setCellStyle(style);
	    
	    
    	Map<Pair<Double,Boolean> , Double> listOfItemQuantity = updateItemsAmount(shortagesItemsList);
    	
	    int newRowIndex = 1;
	    int dateIndex = 0;
	    for (Map<String, Pair<Double, Boolean>> map : shortagesItemsList) 
	    {	
			for (Entry<String, Pair<Double, Boolean>> entry : map.entrySet())
			{
				
				double neededCount = listOfItemQuantity.get(entry.getValue());
				if(neededCount == 0)
					continue;
				style = w.createCellStyle();
				
		    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				
				if(entry.getValue().getRight())
				{
					style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
			    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				}
				else
				{
					style.setFillForegroundColor(IndexedColors.WHITE.index);
				}
				
				newRow = excelSheet.createRow(newRowIndex);
				Cell partNumberCell = newRow.createCell(0);
				partNumberCell.setCellValue(entry.getKey());
				partNumberCell.setCellStyle(style);
				
				Cell newDescriptionCell = newRow.createCell(1);
				String description = (mapOfDescriptions.containsKey(entry.getKey())) ? mapOfDescriptions.get(entry.getKey()) : "";
				newDescriptionCell.setCellValue(description);
				newDescriptionCell.setCellStyle(style);
				
				Cell quantityCell = newRow.createCell(2);
				quantityCell.setCellValue(entry.getValue().getLeft());
				quantityCell.setCellStyle(style);
				
				Cell currentQuantityCell = newRow.createCell(3);
				currentQuantityCell.setCellValue(listOfItemQuantity.get(entry.getValue()));
				currentQuantityCell.setCellStyle(style);
				
				Cell dateCell = newRow.createCell(4);
				dateCell.setCellValue(untilDate.get(dateIndex));
				dateCell.setCellStyle(style);
				
				newRowIndex++;
			}
			dateIndex++;
	    }
		
	    for(int i =0 ; i <= 4 ; i++)
	    	excelSheet.autoSizeColumn(i); 
		
		 try {
				fos = new FileOutputStream(file);
				w.write(fos);
				fos.close();
				w.close();
				
				List <String> to = new ArrayList<String>();
				to.add(from);
				SendEmail senderMail = new SendEmail(from, to, auth);
			    boolean success;
				try {
					success = senderMail.send("items without orders", "", file , globals.ShortagesItemsPath);
				} catch (noValidEmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				}
			     
			    file.delete();
			    
			    if(!success)
			    {
			    	 JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
			    }
			     
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	
	private Map<Pair<Double, Boolean>, Double> updateItemsAmount(
			List<Map<String, Pair<Double, Boolean>>> shortagesItemsList) 
	{
		Map<Pair<Double, Boolean> , Double> itemsQuantity = new HashMap<Pair<Double,Boolean>, Double>();
		for(int i = 0 ; i < shortagesItemsList.size() ; i++)
		{
			Map<String, Pair<Double, Boolean>> currentMap = shortagesItemsList.get(i);
			
			for (Map.Entry<String, Pair<Double, Boolean>> currentEntry : currentMap.entrySet()) 
			{
				String partNumber = currentEntry.getKey();
				
				for(int j = i - 1 ; j >= 0 ; j--)
				{
					Map<String, Pair<Double, Boolean>> secondMap = shortagesItemsList.get(j);
					Pair<Double,Boolean> pair = secondMap.get(partNumber);
					
					if(pair == null)
						continue;
				
					double count = currentEntry.getValue().getLeft() - pair.getLeft();
					
					itemsQuantity.put(currentEntry.getValue(), Math.abs(count));
					break;
				}
				
				if(!itemsQuantity.containsKey(currentEntry.getValue()))
					itemsQuantity.put(currentEntry.getValue(), Math.abs(currentEntry.getValue().getLeft()));
			}
		}
		
		return itemsQuantity;
		
	}
	
	private void createExpediteContent(XSSFWorkbook workbook, XSSFSheet excelSheet,
			List<Map<String, List<Pair<Integer, Point>>>> listOfSuppliersOrders , int startNewRow, Map<Pair<Integer, Point>, Integer> expediteDateMap
			,boolean viewFile, Map<Pair<Integer, Point>, Double> MapOfOpenOrders) 
	{
		
		int newRowNumber = startNewRow;
		Cell polarizerCell = Excel.findCell(ordersSheet, globals.itemNumberColumn);
		Cell descriptionCell = Excel.findCell(ordersSheet, globals.descriptionColumn);
		Cell openOrdersCell = Excel.findCell(ordersSheet, globals.openOrdersColumn);
		
		for (Map<String, List<Pair<Integer,Point>>> map : listOfSuppliersOrders) 
	    {	
			List<Pair<Integer,Point>> sortedRows = map.values().stream().reduce((l1 , l2) -> {l1.addAll(l2); return l1;}).orElse(new ArrayList<>());
			Collections.sort(sortedRows);
			
	    	for (Pair<Integer, Point> pair : sortedRows) 
			{
				Row row = ordersSheet.getRow(pair.getLeft());
				if(!viewFile)
				{
					Cell cellDate = row.getCell((int)(pair.getRight().getX() + globals.dateOffset));
					try {
						DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
						Date date = sourceFormat.parse(globals.frozenDate);
						
						if(sourceFormat.format(date).equals(sourceFormat.format(cellDate.getDateCellValue())))
							continue;
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				int newColumnNumber = 0;
				Row newRow = excelSheet.createRow(newRowNumber);
			
				Cell itemCell = row.getCell(polarizerCell.getColumnIndex());
				
				Cell newItemCell = newRow.createCell(newColumnNumber);
				String partNumber = itemCell.getStringCellValue();
				newItemCell.setCellValue(partNumber);
		    	
		    	XSSFCellStyle Itemstyle = workbook.createCellStyle();
		    	
		    	Itemstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    	
		    	if(expensiveOrders.contains(pair))
		    	{
		    		Itemstyle.setFillForegroundColor(IndexedColors.YELLOW.index);
		    		Itemstyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    	}
		    	
		    	newItemCell.setCellStyle(Itemstyle);
		    	
		    	newColumnNumber++;
		    	
		    	newItemCell = newRow.createCell(newColumnNumber);
		    	Cell descCell = row.getCell(descriptionCell.getColumnIndex());
		    	newItemCell.setCellValue(descCell.getStringCellValue());
		    	
		    	Itemstyle = workbook.createCellStyle();
		    	
		    	Itemstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    	Itemstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    	newItemCell.setCellStyle(Itemstyle);
		    	
				for (int column = (int) pair.getRight().getX() ; column <= pair.getRight().getY() ; column++) 
				{
					newColumnNumber++;
					Cell cell = row.getCell(column);
					
					if (cell.getCellType() == Cell.CELL_TYPE_STRING) 
					{					
						Cell newCell = newRow.createCell(newColumnNumber);
						String content = cell.getStringCellValue();
				    	newCell.setCellValue(content);
				    	
				    	XSSFCellStyle style = workbook.createCellStyle();
				    	
				    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				    	
				    	newCell.setCellStyle(style);
					}
					else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell))
					{
						Cell newCell = newRow.createCell(newColumnNumber);
						DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");	 
						DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
						
						Date content = cell.getDateCellValue();
						
						Date tomorrow = new Date();
						Date today = new Date();
						
						Calendar c = Calendar.getInstance();
						c.setTime(content);
						c.set(Calendar.HOUR_OF_DAY, 0);
						c.set(Calendar.MINUTE, 0);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						content = c.getTime();
						
						c.setTime(tomorrow);
						c.set(Calendar.HOUR_OF_DAY, 0);
						c.set(Calendar.MINUTE, 0);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						c.add(Calendar.DAY_OF_MONTH, 1);
						tomorrow = c.getTime();
						
						c.setTime(today);
						c.set(Calendar.HOUR_OF_DAY, 0);
						c.set(Calendar.MINUTE, 0);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						today = c.getTime();
						
						try {
							Date date = sourceFormat.parse(globals.noDate);
							
							Date dayAfterExpedite = sourceFormat.parse(untilDate.get(expediteDateMap.get(pair)));
							c.setTime(dayAfterExpedite);
							c.set(Calendar.HOUR_OF_DAY, 0);
							c.set(Calendar.MINUTE, 0);
							c.set(Calendar.SECOND, 0);
							c.set(Calendar.MILLISECOND, 0);
							c.add(Calendar.DAY_OF_MONTH, 1);
							dayAfterExpedite = c.getTime();
							
							if(sourceFormat.format(date).equals(sourceFormat.format(content)))
							{
								newCell.setCellValue("");
								XSSFCellStyle style = workbook.createCellStyle();
						    	style.setFillForegroundColor(IndexedColors.RED.index);
						    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
						    	
						    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
						    	
						    	newCell.setCellStyle(style);
							}
							else
							{
								date = sourceFormat.parse(globals.frozenDate);
								newCell.setCellValue(outsourceFormat.format(globals.convertDateToYYYY(content)));
								XSSFCellStyle style = workbook.createCellStyle();
								
								if(sourceFormat.format(date).equals(sourceFormat.format(content)))
								{
									style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
									style.setFillPattern(CellStyle.SOLID_FOREGROUND);
								}
								else if(content.before(tomorrow))
								{
									style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
									style.setFillPattern(CellStyle.SOLID_FOREGROUND);
								}
								else if(content.after(today) && content.before(dayAfterExpedite))
								{
									style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.index);
									style.setFillPattern(CellStyle.SOLID_FOREGROUND);
								}
								
						    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
						    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
						    	
						    	newCell.setCellStyle(style);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
				    	
				    	
					}
					else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
					{
						Cell newCell = newRow.createCell(newColumnNumber);
						double content = cell.getNumericCellValue();
				    	newCell.setCellValue(content);
				    	
				    	XSSFCellStyle style = workbook.createCellStyle();
				    	
				    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				    	
				    	newCell.setCellStyle(style);
					}
					else if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
					{
						Cell newCell = newRow.createCell(newColumnNumber);
						
						newCell.setCellValue("");
						XSSFCellStyle style = workbook.createCellStyle();
						
				    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
				    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				    	
				    	newCell.setCellStyle(style);
					}
					
				}
				
		    	Double openOrders = null;
		    	boolean noOrder = false;
		    	
				if((int) pair.getRight().getX() == 0 && (int) pair.getRight().getY() == -1)
				{
					noOrder = true;
			    	XSSFCellStyle style = workbook.createCellStyle();
			    	
			    	style.setFillForegroundColor(IndexedColors.ORANGE.index);
					style.setFillPattern(CellStyle.SOLID_FOREGROUND);
					
			    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			    	
					for(int i = globals.orderOffset ; i <= globals.quantityOffset ; i++)
					{
						newColumnNumber++;
						Cell newCell = newRow.createCell(newColumnNumber);
				    	newCell.setCellStyle(style);
				    	newCell.setCellValue("");
					}
					
					openOrders = row.getCell(openOrdersCell.getColumnIndex()).getNumericCellValue();
				}
				newColumnNumber++;
				
	
		    	XSSFCellStyle style = workbook.createCellStyle();
		    	
		    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    	
		    	int supplierColumn = (int) pair.getRight().getX() + globals.supplierOffset;
		    	String supplierName = row.getCell(supplierColumn).getStringCellValue();
		    	
		    	Date expediteDate;
		    	if(supplierName.trim().matches(".*[a-zA-Z].*") && !noOrder)
		    		expediteDate = new Date(Math.max(globals.addDays(Globals.parseDate(untilDate.get(expediteDateMap.get(pair))) , -45).getTime() , Globals.getTodayDate().getTime()));
		    	else
		    		expediteDate = Globals.parseDate(untilDate.get(expediteDateMap.get(pair)));
		    		
				Cell newCell = newRow.createCell(newColumnNumber);
				LocalDateTime ldt = LocalDateTime.ofInstant(expediteDate.toInstant(), ZoneId.systemDefault());
		    	newCell.setCellValue(Globals.dateWithoutHourToString(ldt));
		    	newCell.setCellStyle(style);
		    	
		    	newColumnNumber++;
		    	newCell = newRow.createCell(newColumnNumber);
		    	Double shortage = MapOfOpenOrders.get(pair);
		    	if(shortage != null)
		    		newCell.setCellValue(shortage);
		    	else
		    		newCell.setCellValue("");
		    	newCell.setCellStyle(style);
				
		    	newColumnNumber++;
		    	newCell = newRow.createCell(newColumnNumber);
		    	if(openOrders != null)
		    		newCell.setCellValue(openOrders);
		    	else
		    		newCell.setCellValue("");
		    	newCell.setCellStyle(style);
		    	
				newRowNumber++;
			
			}	
	    
	    }
		
	}
	
	public void setDatesFilter(List<String> untilDate) 
	{
		this.untilDate = untilDate;
		
		/*if(untilDate.split("/")[2].length() < 4)
		{
			int number = Integer.parseInt(untilDate.split("/")[2]);
			number += 2000;
			this.untilDate = untilDate.substring(0,untilDate.lastIndexOf("/")).concat("/"+number);
		}*/
		
	}

}
