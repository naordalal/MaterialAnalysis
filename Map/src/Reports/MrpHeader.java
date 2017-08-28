package Reports;

import java.util.List;

import MainPackage.CallBack;
import MainPackage.Message;
import MapFrames.ReportViewFrame;

public class MrpHeader extends Report
{

	@Override
	public String[] getRow() 
	{
		return null;
	}

	@Override
	public String[] getColumns() 
	{
		return null;
	}

	@Override
	public Message updateValue(String userName, int column, String newValue) throws Exception 
	{
		return null;
	}

	@Override
	public List<Integer> getInvalidEditableColumns() 
	{
		return null;
	}

	@Override
	public List<Integer> getFilterColumns() 
	{
		return null;
	}

	@Override
	public CallBack<Object> getValueCellChangeAction(String userName, ReportViewFrame frame, Object... args) 
	{
		return null;
	}

	@Override
	public CallBack<Object> getDoubleLeftClickAction(String userName, ReportViewFrame frame, Object... args) 
	{
		return null;
	}

	@Override
	public CallBack<Object> getRightClickAction(String userName, ReportViewFrame frame, Object... args) 
	{
		return null;
	}

}
