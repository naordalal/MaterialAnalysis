package Reports;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Authenticator;

import Forms.Form;
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
	public static Map<Integer, List<Integer>> getInvalidReportColumnsPerRow(List<? extends Report> reports)
	{
		return reports.stream().collect(Collectors.toMap((report) -> reports.indexOf(report), (report) -> report.getInvalidEditableColumns()));
	}
}
