package Senders;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import mainPackage.Excel;
import mainPackage.Globals;
import mainPackage.Pair;
import mainPackage.noValidEmailException;

public class FollowUpSender extends Sender
{
	private File supplierEmailsFile;
	private List<File> supplierOrdersFile;
	private XSSFSheet ordersSheet;
	private XSSFSheet emailsSheet;
	private String subject;
	private String body;
	private String from;
	private FileOutputStream fos;
	private boolean acceptOrder;
	private boolean noDate;
	private boolean passDate;
	private boolean futureDate;
	private List<String> untilDate;
	private Authenticator auth;
	private Globals globals;
	private List<String> ccList;
	private boolean purchasingPermission;
	private boolean beyondRequestDate;
	private int daysBeyondRequestDate;

	
	public FollowUpSender(File supplierEmailsFile , List<File> supplierOrdersFile, String subject, String body, String from 
			, Authenticator auth)
	{
		super(from , auth);
		this.supplierEmailsFile = supplierEmailsFile;
		this.supplierOrdersFile = supplierOrdersFile;
		this.subject = subject;
		this.body = body;
		this.from = from;
		this.auth = auth;
		this.globals = new Globals();
	}
	
	public List<String> send()
	{
		List<String> suppliers = new ArrayList<String>();
		
		XSSFWorkbook w = null;
	    try {
	    	
	      FileInputStream fis = new FileInputStream(supplierOrdersFile.get(0));
	      w = new XSSFWorkbook(fis);
	      // Get the first sheet
	      ordersSheet = w.getSheetAt(0);
	      w.close();
	      fis.close();
	      
	      if(purchasingPermission)
	      {
	    	  fis = new FileInputStream(supplierEmailsFile);
		      w = new XSSFWorkbook(fis);
		      // Get the first sheet
		      emailsSheet = w.getSheetAt(0);
			  w.close();
			  fis.close();
		      
	      }
	    }catch(Exception e)
	    {
	    	 e.printStackTrace();
	    	 return null;
	    }
	    	    
	 Cell commentsCell = Excel.findCell(ordersSheet, globals.notesColumn);
	 if(commentsCell == null && acceptOrder)
	 {
		 JOptionPane.showConfirmDialog(null, "missing 'הערות לספק' column","",JOptionPane.PLAIN_MESSAGE);
		 return null;
	 }
		
	 Cell supplyDateCell = Excel.findCell(ordersSheet, globals.promisedDateInHebrewColumn);
	 if(supplyDateCell == null)
		 supplyDateCell = Excel.findCell(ordersSheet, globals.promisedDateInEnglishColumn);
	 
	 Cell suppliersCell = Excel.findCell(ordersSheet, globals.supplierInHebrewColumn);
	 if(suppliersCell == null)
		 suppliersCell = Excel.findCell(ordersSheet, globals.supplierInEnglishColumn);
	 
	 if(supplyDateCell == null || suppliersCell == null)
	 {
		 JOptionPane.showConfirmDialog(null, "missing 'ספק' or 'תאריך מובטח' column","",JOptionPane.PLAIN_MESSAGE);
		 return null;
	 }
		 
	 
	 int column = suppliersCell.getColumnIndex();
	 Row row = suppliersCell.getRow();
	 int rowIndex = row.getRowNum();
	 //TODO int rowsNumber = ordersSheet.getPhysicalNumberOfRows();
	 int rowsNumber = Excel.rowsNumberFromRow(ordersSheet, rowIndex, column);
	 Map<String, List<Integer>> suppliersOrders = new HashMap <String, List<Integer>>();
	 for(int i = rowIndex + 1 ; i < rowsNumber; i++)
	 {
		 if(!needBeAdded(ordersSheet.getRow(i),supplyDateCell))
			 continue;
		 
		 Cell currentCell = Excel.getCell(ordersSheet , i, column);
		 String content = currentCell.getStringCellValue();
		 
		 //Cell currentCell = ordersSheet.getCell(column , i);
		 if(!suppliersOrders.containsKey(content))
		 {
			 List<Integer> rows = new ArrayList<Integer>();
			 rows.add(i);
			 suppliersOrders.put(content, rows);
		 }
		 else
		 {
			 suppliersOrders.get(currentCell.toString()).add(i);
		 }
		 
	 }
	 
	 if(suppliersOrders.size() == 0)
	 {
		 JOptionPane.showConfirmDialog(null, "no email was sent","",JOptionPane.PLAIN_MESSAGE);
		 return null;
	 }
	 
	 for (Map.Entry<String, List<Integer>> entry : suppliersOrders.entrySet())
	 {
	     Cell supplierEmail = Excel.findCell(emailsSheet , entry.getKey());
	     
	     if(supplierEmail == null)
	     {
	    	 suppliers.add(entry.getKey());
	    	 continue;
	     }
	     
	     Row cells = supplierEmail.getRow();
	     
	     List<String> mails = new ArrayList<String>();
	     
	     for (Cell cell : cells) 
	     {
			if(cell.getColumnIndex() > supplierEmail.getColumnIndex())
			{
				mails.add(cell.toString());
			}
		 }
	     
	     if(mails.stream().allMatch(s -> ((String)s).equals("")))
	     {
	    	 suppliers.add(entry.getKey());
	    	 continue;
	     }
	     
	     File tempFile = null;
	     try {
	    	 tempFile = createExcelFile(entry , suppliersCell);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	     if(tempFile != null)
	     {
		     /*try {
				Desktop.getDesktop().open(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		     
	    	 
		     mails.addAll(ccList);
		     SendEmail senderMail = new SendEmail(from, mails, auth);
		     boolean success;
			try {
				success = senderMail.send(entry.getKey() + "-" + subject, body, tempFile , globals.supplierOrdersPath);
			} catch (noValidEmailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				suppliers.add(entry.getKey());
			    tempFile.delete();
				continue;
			}
		     tempFile.delete();
		     
		     if(!success)
		     {
		    	 JOptionPane.showConfirmDialog(null, "Wrong User/Password OR there is no internet connection","",JOptionPane.PLAIN_MESSAGE);
		    	 return null;
		     }
	     }
	 }
		

		return suppliers;

	}
	
	private boolean needBeAdded(XSSFRow row , Cell dateSupplyCell) 
	{
		Cell cell = row.getCell(dateSupplyCell.getColumnIndex());
		if(cell == null)
			return false;
		Cell commentsCell = Excel.findCell(ordersSheet, globals.notesColumn);
		//if (cell.getColumnIndex() != dateSupplyCell.getColumnIndex())
			//return true;
		
		DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");
		if(cell.getCellType() == Cell.CELL_TYPE_STRING)
		{
			try {
				Date date = sourceFormat.parse(globals.frozenDate);
				if(sourceFormat.format(date).equals(cell.getStringCellValue()))
				{
					return false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell))
		{
			try {
				Date date = sourceFormat.parse(globals.frozenDate);
				if(sourceFormat.format(date).equals(sourceFormat.format(cell.getDateCellValue())))
				{
					return false;
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		else if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
		{
			cell.setCellValue("");
		}
		else 
			return false;
			
		if(noDate)
		{
			
			sourceFormat = new SimpleDateFormat("dd/MM/yy");		
			try {
				Date date = sourceFormat.parse(globals.noDate);
				
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) 
				{
					if(cell.getStringCellValue().trim().equals(""))
						return true;
				}
				else if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
					return true;
				else if(DateUtil.isCellDateFormatted(cell))
				{
					if(sourceFormat.format(date).equals(sourceFormat.format(cell.getDateCellValue())))
					{
						return true;
					}
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(passDate)
		{
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return false;
			
			Date date = cell.getDateCellValue();
			
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			date = c.getTime();
			
			c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			if(date.before(c.getTime()))
			{
				return true;
			}
		}
		if(futureDate)
		{
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return false;
			
			sourceFormat = new SimpleDateFormat("dd/MM/yy");		
			try {
				Date date = cell.getDateCellValue();
				Date until = sourceFormat.parse(untilDate.get(0));
				
				Calendar c = Calendar.getInstance();
				c.setTime(until);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				c.add(Calendar.DAY_OF_MONTH, 1);
				Date until2 = c.getTime();
				
				c.setTime(date);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				date = c.getTime();
				
				c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				c.add(Calendar.DAY_OF_MONTH, -1);
				
				
				if(date.after(c.getTime()) && date.before(until2))
				{
					return true;
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if(beyondRequestDate)
		{
			sourceFormat = new SimpleDateFormat("dd/MM/yy");		
			try {
				Date date = sourceFormat.parse(globals.noDate);
				
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) 
				{
					if(cell.getStringCellValue().trim().equals(""))
						return false;
				}
				else if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
					return false;
				else if(DateUtil.isCellDateFormatted(cell))
				{
					if(sourceFormat.format(date).equals(sourceFormat.format(cell.getDateCellValue())))
					{
						return false;
					}
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Cell requestDateCell = Excel.findCell(ordersSheet, globals.requestDateInHebrewColumn);
			if(requestDateCell == null)
				requestDateCell = Excel.findCell(ordersSheet, globals.requestDateInEnglishColumn);
			
			requestDateCell = row.getCell(requestDateCell.getColumnIndex());
				
			if (cell.getCellType() == Cell.CELL_TYPE_STRING || requestDateCell.getCellType() == Cell.CELL_TYPE_STRING)
				return false;
			
			if(DateUtil.isCellDateFormatted(cell) && DateUtil.isCellDateFormatted(requestDateCell))
			{
				Date date = cell.getDateCellValue();
				Date requestDate = requestDateCell.getDateCellValue();
				Date twoWeeksFromToday = new Date();
				
				Calendar c = Calendar.getInstance();
				c.setTime(requestDate);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.add(Calendar.DAY_OF_MONTH, daysBeyondRequestDate);
				
				requestDate = c.getTime();
				
				c.setTime(date);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				date = c.getTime();	
				
				c.setTime(twoWeeksFromToday);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.add(Calendar.DAY_OF_MONTH, 14);
				
				twoWeeksFromToday = c.getTime();
				
				if(date.after(twoWeeksFromToday) && date.after(requestDate))
					return true;
			}
		}
		
		if(commentsCell != null && acceptOrder)
		{
			
			int CommentsColumn = commentsCell.getColumnIndex();
			Cell commentCell = row.getCell(CommentsColumn);
						
			if (commentCell != null && commentCell.getCellType() == Cell.CELL_TYPE_STRING) 
			{
				if(commentCell.getStringCellValue().toLowerCase().contains(globals.acceptNotes.toLowerCase()))
					return true;
			}
		}	
				
		return false;

	}
	
	
	private File createExcelFile(Entry<String, List<Integer>> entry, Cell mainCell) throws IOException {

		File file = new File(globals.supplierOrdersPath);
		int index = 1;
		while(file.exists())
		{
			file = new File(globals.supplierOrdersPath.split("\\.")[0]+" (" + index + ")."+globals.supplierOrdersPath.split("\\.")[1]);
    		index++;
		}
	    /*WorkbookSettings wbSettings = new WorkbookSettings();

	    wbSettings.setLocale(new Locale("en", "EN"));*/
		
		XSSFWorkbook w = null;
	    w = new XSSFWorkbook();
	    
	    w.createSheet("Report");
	    XSSFSheet excelSheet = w.getSheetAt(0);
	    
	    Row row = mainCell.getRow();
	    Iterator<Cell> iterator = row.iterator();
	    
	    Row newRow = excelSheet.createRow(0);
	    
	    while (iterator.hasNext()) 
	    {
	    	Cell cell = iterator.next();
	    	
	    	if(cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().equalsIgnoreCase("הערות לספק"))
	    		continue;
	    	
	    	Cell newCell = newRow.createCell(cell.getColumnIndex());
	    	newCell.setCellValue((String) cell.getStringCellValue());
	    	
	    	XSSFCellStyle style = w.createCellStyle();
	    	style.setFillForegroundColor(IndexedColors.TURQUOISE.index);
	    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);

	    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	newCell.setCellStyle(style);
		}
	    
	    fos = new FileOutputStream(file);
	    
	    createContent(w , excelSheet , entry);
	    
	    for(int i = 0 ; i < newRow.getPhysicalNumberOfCells(); i++)
	    	excelSheet.autoSizeColumn(i);
	    
	    w.write(fos);
	    fos.close();
	    w.close();
	    return file;
		
	}

	private boolean createContent(XSSFWorkbook workbook , XSSFSheet excelSheet, Entry<String, List<Integer>> entry) 
	{
		boolean followUp = true;
		int newRow = 1;
		Cell Comments = Excel.findCell(ordersSheet, globals.notesColumn);
		int CommentsColumn = -1;
		if(followUp && acceptOrder && Comments == null)
			return false;
		else if(followUp && acceptOrder)
			CommentsColumn = Comments.getColumnIndex();
		
		Cell supplyDateCell = Excel.findCell(ordersSheet, globals.promisedDateInHebrewColumn);
		if(supplyDateCell == null)
			supplyDateCell = Excel.findCell(ordersSheet, globals.promisedDateInEnglishColumn);
		
		
		for (Integer row : entry.getValue()) 
		{
			Row r = excelSheet.createRow(newRow);
			for (Cell cell : ordersSheet.getRow(row)) 
			{
				if(followUp && cell.getColumnIndex() == CommentsColumn)
		    		continue;
				
				int column = cell.getColumnIndex();
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) 
				{					
					Cell newCell = r.createCell(column);
					String content = cell.getStringCellValue();
			    	newCell.setCellValue(content);
			    	
			    	XSSFCellStyle style = workbook.createCellStyle();
			    	
			    	if(cell.getColumnIndex() == supplyDateCell.getColumnIndex())
					{
						if(cell.getStringCellValue().equals(""))
				    	{
				    		style.setFillForegroundColor(IndexedColors.RED.index);
					    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				    	}
				    	else if(cell.getStringCellValue().equals(globals.noDate))
				    	{
				    		style.setFillForegroundColor(IndexedColors.RED.index);
					    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
					    	newCell.setCellValue("");
				    	}
				    	else
				    	{
				    		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
					    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				    	}
					}
			    	
			    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			    	
			    	XSSFFont font = workbook.createFont();
			    	XSSFFont seconfFont = ((XSSFCellStyle)(cell.getCellStyle())).getFont();
			    	font.setBold(seconfFont.getBold());
			    	font.setColor(seconfFont.getXSSFColor());
			    	style.setFont(font);
			    	
			    	newCell.setCellStyle(style);
				}
				else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell))
				{
					Cell newCell = r.createCell(column);
					DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yy");	 
					DateFormat outsourceFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date content = cell.getDateCellValue();
						
					try {
						Date date = sourceFormat.parse(globals.noDate);
						
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
					    	
					    	XSSFFont font = workbook.createFont();
					    	XSSFFont seconfFont = ((XSSFCellStyle)(cell.getCellStyle())).getFont();
					    	font.setBold(seconfFont.getBold());
					    	font.setColor(seconfFont.getXSSFColor());
					    	style.setFont(font);
					    	
					    	newCell.setCellStyle(style);
						}
						else
						{							
							newCell.setCellValue(outsourceFormat.format(globals.convertDateToYYYY(content)));
							XSSFCellStyle style = workbook.createCellStyle();
							
							if(cell.getColumnIndex() == supplyDateCell.getColumnIndex())
							{
						    	style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
						    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
							}
					    	
					    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
					    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
					    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
					    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
					    	
					    	XSSFFont font = workbook.createFont();
					    	XSSFFont seconfFont = ((XSSFCellStyle)(cell.getCellStyle())).getFont();
					    	font.setBold(seconfFont.getBold());
					    	font.setColor(seconfFont.getXSSFColor());
					    	style.setFont(font);
					    	
					    	newCell.setCellStyle(style);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
			    	
			    	
				}
				else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
				{
					Cell newCell = r.createCell(column);
					double content = cell.getNumericCellValue();
			    	newCell.setCellValue(content);
			    	
			    	XSSFCellStyle style = workbook.createCellStyle();
			    	
			    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			    	
			    	XSSFFont font = workbook.createFont();
			    	XSSFFont seconfFont = ((XSSFCellStyle)(cell.getCellStyle())).getFont();
			    	font.setBold(seconfFont.getBold());
			    	font.setColor(seconfFont.getXSSFColor());
			    	style.setFont(font);
			    	
			    	newCell.setCellStyle(style);
				}
				else if(cell.getCellType() == Cell.CELL_TYPE_BLANK)
				{
					Cell newCell = r.createCell(column);
					
					newCell.setCellValue("");
					XSSFCellStyle style = workbook.createCellStyle();
					
					if(cell.getColumnIndex() == supplyDateCell.getColumnIndex())
					{
				    	style.setFillForegroundColor(IndexedColors.RED.index);
				    	style.setFillPattern(CellStyle.SOLID_FOREGROUND);
					}
			    	
			    	style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			    	style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			    	
			    	newCell.setCellStyle(style);
				}
			}
			
			newRow++;
			
		}
		
		return true;
	}
	
	public void setCC(List<String> ccList) 
	{
		this.ccList = ccList;
		
	}

	public void setPermission(boolean purchasingPermission) 
	{
		this.purchasingPermission = purchasingPermission;
		
	}
	
	public void setDatesFilter(boolean acceptOrder , boolean noDate, boolean passDate,
			boolean futureDate,boolean beyondRequestDate, List<String> untilDate) 
	{
		this.acceptOrder = acceptOrder;
		this.noDate = noDate;
		this.passDate = passDate;
		this.futureDate = futureDate;
		this.beyondRequestDate = beyondRequestDate;
		this.untilDate = untilDate;
		
	}

	public void setDaysBeyondRequestDate(int daysBeyondRequestDate) 
	{
		this.daysBeyondRequestDate = daysBeyondRequestDate;
		
	}
	
	
}
