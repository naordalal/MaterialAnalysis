package Reports;

import java.util.List;

import javax.mail.Authenticator;

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
	public abstract CallBack<Object> getValueCellChangeAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getDoubleLeftClickAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
	public abstract CallBack<Object> getRightClickAction(String email , Authenticator auth , String userName , ReportViewFrame frame , Object ... args);
}
