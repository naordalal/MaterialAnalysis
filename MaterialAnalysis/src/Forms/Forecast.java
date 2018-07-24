package Forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Authenticator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import AnalyzerTools.Analyzer;
import AnalyzerTools.MonthDate;
import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Globals;
import MapFrames.MainMapFrame;
import MapFrames.ReportViewFrame;
import Reports.ForecastAttachment;
import Reports.ProductInit;
import Reports.ProductInitHistory;

public class Forecast extends Form
{
	
	private String customer;
	private String description;
	private String notes;
	private String userName;

	public Forecast() 
	{
		super();
	}
	
	public Forecast(int id , String customer , String catalogNumber , String quantity , Date initDate , Date requireDate , String description , String userName , String notes) 
	{
		super(id , catalogNumber, quantity, initDate, requireDate);
		
		this.customer = customer;
		this.userName = (userName == null) ? "" : userName;
		this.description = description;
		this.notes = notes;
	}

	public String getCustomer() 
	{
		return customer;
	}

	public void setCustomer(String customer) 
	{
		this.customer = customer;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getNotes() 
	{
		return notes;
	}

	public void setNotes(String notes) 
	{
		this.notes = notes;
	}

	@Override
	public String[] getColumns() 
	{
		String [] columns = new String[9];
		columns[0] = "Customer";
		columns[1] = "Catalog Number";
		columns[2] = "Description";
		columns[3] = "Quantity";
		columns[4] = "Init Date";
		columns[5] = "Require Date";
		columns[6] = "User Update";
		columns[7] = "Notes";
		columns[8] = "Attachments";
		
		return columns;
	}

	@Override
	public String[] getRow() 
	{
		String [] row = new String[9];
		row[0] = this.customer;
		row[1] = super.getCatalogNumber();
		row[2] = this.description;
		row[3] = super.getQuantity();
		row[4] = Globals.dateWithoutHourToString(super.getCreateDate());
		row[5] = Globals.dateWithoutHourToString(super.getRequestDate());
		row[6] = this.userName;
		row[7] = this.notes;
		
		DataBase db = new DataBase();
		List<ForecastAttachment> attachments = db.getAllForecastAttachments(getId());
		String cellValue = (attachments.size() > 0) ? "Click for see attachments" : "Click for add attachment";
		row[8] = cellValue;
		
		return row;
	}

	@Override
	public boolean canEdit() 
	{
		return true;
	}

	@Override
	public void updateValue(int column, String newValue , String userName) throws Exception 
	{
		if(!canEdit())
			return;
		
		if(new MonthDate(super.getCreateDate()).before(new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate))))
			throw new Exception("Cannot update old forecast");
		
		switch(column)
		{
			case 0:
				this.customer = newValue;
			case 1:
				super.setCatalogNumber(newValue);
				break;
			case 2:
				this.description = newValue;
				break;
			case 3:
				if(NumberUtils.isCreatable(newValue))
					super.setQuantity(newValue);
				else
					throw new Exception("Quantity have to be a numeric value");
				break;
			case 5:
				Date requestDate;
				if((requestDate = Globals.isValidDate(newValue)) != null)
					super.setRequstDate(requestDate);
				else
					throw new Exception("Request date have to be a date format");
				break;
			case 6:
				return;
			case 7:
				this.notes = newValue;
				break;
				
			default:
				return;
		}
		
		Analyzer analyzer = new Analyzer();
		analyzer.updateFC(super.getId(), customer, super.getCatalogNumber(), super.getQuantity(), Globals.dateWithoutHourToString(super.getCreateDate())
				, Globals.dateWithoutHourToString(super.getRequestDate()), this.description, userName , this.notes);
		
	}
	
	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		List<Integer> columns = new ArrayList<>();
		if(super.getCreateDate().before(new MonthDate(Globals.addMonths(Globals.getTodayDate(), -Globals.monthsToCalculate))))
		{
			columns.add(0);
			columns.add(1);
			columns.add(2);
			columns.add(3);
			columns.add(4);
			columns.add(5);
			columns.add(6);
			columns.add(7);
		}
		else
		{
			columns.add(4);
			columns.add(6);		
		}
		
		columns.add(8);
		
		return columns;
	}

	@Override
	public boolean isNeedRequireDate() 
	{
		return true;
	}

	@Override
	public boolean isNeedInit() 
	{
		return true;
	}
	
	@Override
	public List<Integer> getFilterColumns() 
	{
		List<Integer> filterColumns = new ArrayList<>();
		filterColumns.add(0);
		filterColumns.add(1);
		filterColumns.add(2);
		
		return filterColumns;

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		return null;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame forecastFrame, Object... args) 
	{
		List<Forecast> forecasts = (List<Forecast>) args[0];
		DataBase db = new DataBase();
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			String directoryPath = null;
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int col = tcl.getColumn();
				Forecast forecast = forecasts.get(row);
				if(col != 8)
					return null;
				
				if(tcl.getTable().getValueAt(row, col).equals("Click for add attachment"))
				{
					addForecastAttachment(forecast);
					forecastFrame.updateCellValue(row, col, "Click for see attachments");
					forecastFrame.setColumnWidth();
					return null;
				}
				
				List<ForecastAttachment> forecastAttachments = db.getAllForecastAttachments(forecast.getId());
				if(forecastAttachments == null || forecastAttachments.size() == 0)
					return null;
				
				ReportViewFrame forecastAttachmentsFrame = MainMapFrame.createReportViewFrame(email , auth , userName , forecastAttachments , "Forecast Attachments View");
				CallBack<Object> valueCellChangeAction = getValueCellChangeActionOfForecastAttachment(email, auth, userName, row, forecastFrame, forecastAttachmentsFrame, forecastAttachments);
				CallBack<Object> doubleLeftClickAction = forecastAttachments.get(0).getDoubleLeftClickAction(email, auth, userName, forecastAttachmentsFrame , forecastAttachments);
				CallBack<Object> rightClickAction = forecastAttachments.get(0).getRightClickAction(email, auth, userName, forecastAttachmentsFrame , forecastAttachments);
				
				Globals globals = new Globals();
				forecastAttachmentsFrame.setCallBacks(valueCellChangeAction, doubleLeftClickAction, rightClickAction);
				JButton addForecastAttachmentButton = new JButton();
				
				addForecastAttachmentButton.addActionListener((e) ->
				{
					addForecastAttachment(forecast);
					
					List<ForecastAttachment> newForecastAttachments = db.getAllForecastAttachments(forecast.getId());
					forecastAttachments.clear();
					forecastAttachments.addAll(newForecastAttachments);
					forecastAttachmentsFrame.refresh(newForecastAttachments.stream().map(t -> t.getRow()).toArray(String[][]::new));
					forecastAttachmentsFrame.setColumnWidth();
					
					if(forecastAttachments.size() == 0)
						forecastFrame.updateCellValue(row, 8, "Click for add attachment");
					else
						forecastFrame.updateCellValue(row, 8, "Click for see attachments");
					
					forecastFrame.setColumnWidth();
				});
				
				addForecastAttachmentButton.setIcon(globals.addIcon);
				addForecastAttachmentButton.setFocusable(false);
				addForecastAttachmentButton.setContentAreaFilled(false);
				addForecastAttachmentButton.setPressedIcon(globals.clickAddIcon);
				addForecastAttachmentButton.setToolTipText("Add forecast attachment");
				
				forecastAttachmentsFrame.setCustomComponent(addForecastAttachmentButton);
				forecastAttachmentsFrame.show();
				
				return null;
			}
			
			private void addForecastAttachment(Forecast forecast) 
			{
				DataBase db = new DataBase();
				JFileChooser attachmentFileChooser = new JFileChooser();
				if(directoryPath != null)
					attachmentFileChooser.setCurrentDirectory(new File(directoryPath));
				int returnVal = attachmentFileChooser.showOpenDialog(null);
				String filePath;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File attachFile = attachmentFileChooser.getSelectedFile();
					filePath = attachFile.getAbsolutePath();
					directoryPath = attachFile.getPath();
				} else {
				    System.out.println("File access cancelled by user.");
				    return;
				}
				
				String fileName = "";
				while(true)
				{
					fileName = JOptionPane.showInputDialog(null , "Enter file name", JOptionPane.OK_OPTION);
					if(fileName == null || fileName.equals(""))
						continue;
					
					break;
				}
				
				if(!db.addForecastAttachment(forecast.getId() , fileName , filePath))
				{
					JOptionPane.showConfirmDialog(null, "You already added this attachment","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
			}
		};
		
		return doubleLeftClickAction;
	}

	@Override
	public CallBack<Object> getRightClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		return null;
	}
	
	public CallBack<Object> getValueCellChangeActionOfForecastAttachment(String email, Authenticator auth, String userName,int selectedRow
			,ReportViewFrame forecastFrame,ReportViewFrame forecastAttachmentsFrame, Object... args) {
		
		List<ForecastAttachment> forecastAttachments = (List<ForecastAttachment>) args[0];
		DataBase db = new DataBase();
		
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
				ForecastAttachment forecastAttachment = forecastAttachments.get(row);
				
				try
				{
					forecastAttachment.updateValue(userName , column, newValue);
				} 
				catch (Exception e) 
				{
					forecastAttachmentsFrame.updateCellValue(row,column,oldValue);
					JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
					return e;
				}
							
				List<ForecastAttachment> newForecastAttachments = db.getAllForecastAttachments(forecastAttachment.getForecastId());
				forecastAttachments.clear();
				forecastAttachments.addAll(newForecastAttachments);
				forecastAttachmentsFrame.refresh(newForecastAttachments.stream().map(t -> t.getRow()).toArray(String[][]::new));
				forecastAttachmentsFrame.setColumnWidth();
				
				if(forecastAttachments.size() == 0)
				{
					forecastFrame.updateCellValue(selectedRow, 8, "Click for add attachment");
					forecastFrame.setColumnWidth();
				}
				
				return null;
			}
		};
		
		return valueCellChangeAction;
	}

}
