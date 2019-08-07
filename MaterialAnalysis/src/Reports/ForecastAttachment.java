package Reports;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Authenticator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import Components.TableCellListener;
import MainPackage.CallBack;
import MainPackage.DataBase;
import MainPackage.Message;
import MapFrames.ReportViewFrame;

public class ForecastAttachment extends Report
{
	private int forecastId;
	private String fileName;
	private String filePath;

	public ForecastAttachment(int forecastId , String fileName , String filePath)
	{
		this.forecastId = forecastId;
		this.fileName = fileName;
		this.filePath = filePath;
	}

	public int getForecastId() {
		return forecastId;
	}

	public void setForecastId(int forecastId) {
		this.forecastId = forecastId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String[] getRow() {
		String [] row = new String[2];
		row[0] = this.fileName;
		row[1] = this.filePath;
		
		return row;
	}

	@Override
	public String[] getColumns() {
		String [] columns = new String[2];
		columns[0] = "File Name";
		columns[1] = "File Path";
		
		return columns;
	}

	@Override
	public Message updateValue(String userName, int column, String newValue) throws Exception 
	{
		DataBase db = new DataBase();
		String oldFilePath = filePath;
		switch(column)
		{
			case 0 :
				if(newValue.trim().equals(""))
				{
					int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this file?","",JOptionPane.YES_NO_OPTION);
					if(res == JOptionPane.YES_OPTION)
					{
						if(db.deleteForecastAttachment(forecastId , filePath))
							JOptionPane.showConfirmDialog(null, "Delete successfuly" ,"",JOptionPane.PLAIN_MESSAGE);
						else
							throw new Exception("Cannot delete");	
							
					}
					else
						throw new Exception("There is no change");
					return null;
				}
				else
					fileName = newValue;
				break;
			case 1 :
				filePath = newValue;
				break;
			default:
				return null;
		}
		
		db.updateForecastAttachment(forecastId , oldFilePath , filePath , fileName);
		return null;
		
	}

	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		List<Integer> columns = new ArrayList<>();
		columns.add(1);
		return columns;
	}

	@Override
	public List<Integer> getFilterColumns() 
	{
		List<Integer> columns = new ArrayList<>();
		columns.add(0);
		return columns;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		
		return null;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		
		List<ForecastAttachment> forecastAttachments = (List<ForecastAttachment>) args[0];
		
		CallBack<Object> doubleLeftClickAction = new CallBack<Object>()
		{
			String directoryPath = null;
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				int col = tcl.getColumn();
				ForecastAttachment forecastAttachment = forecastAttachments.get(row);
				
				if(col == 1)
				{
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
				        return null;
				    }
					
					try {
						forecastAttachment.updateValue(userName, col, filePath);
						frame.updateCellValue(row,col,filePath);
						frame.setColumnWidth();
					} catch (Exception e) {
						JOptionPane.showConfirmDialog(null, e.getMessage() ,"Error",JOptionPane.PLAIN_MESSAGE);
						return e;
					}
				}
				
				return null;
			}
		};
		
		return doubleLeftClickAction;
	}

	@Override
	public CallBack<Object> getRightClickAction(String email, Authenticator auth, String userName,
			ReportViewFrame frame, Object... args) {
		
		List<ForecastAttachment> forecastAttachments = (List<ForecastAttachment>) args[0];
		
		CallBack<Object> rightClickAction = new CallBack<Object>()
		{
			@Override
			public Object execute(Object... objects) 
			{
				TableCellListener tcl = (TableCellListener)objects[0];
				int row = tcl.getRow();
				ForecastAttachment forecastAttachment = forecastAttachments.get(row);
				
				String path = forecastAttachment.getFilePath();
				try {
					Desktop.getDesktop().open(new File(path));
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showConfirmDialog(null, "Cannot open file","",JOptionPane.PLAIN_MESSAGE);
					return null;
				}
				
				return null;
			}
		};
		
		return rightClickAction;
	}

}
