package Reports;

import java.util.List;

import MainPackage.CallBack;
import MainPackage.Message;
import MapFrames.ReportViewFrame;

public abstract class Report 
{
	public abstract String[] getRow();
	public abstract String[] getColumns();
	public abstract Message updateValue(String userName , int column, String newValue) throws Exception;
	public abstract List<Integer> getInvalidEditableColumns();
	public abstract List<Integer> getFilterColumns();
	public abstract CallBack<Object> getValueCellChangeAction(String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getDoubleLeftClickAction(String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getRightClickAction(String userName , ReportViewFrame frame , Object ... args);
}
