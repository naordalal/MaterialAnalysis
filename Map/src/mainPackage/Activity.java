package mainPackage;

import java.time.LocalDateTime;

public class Activity 
{

	private String name;
	private boolean followUp;
	private boolean acceptOrder;
	private boolean withoutDueDate; 
	private boolean pastDueDate;
	private boolean supplyOnTime;
	private boolean expediteReport;
	private boolean importExpediteReport;
	private boolean exportExpediteReport;
	private String project;
	private LocalDateTime date;
	private boolean beyondRequestDate;
	
	public Activity(String name , boolean followUp , boolean acceptOrder , boolean withoutDueDate , boolean pastDueDate , boolean supplyOnTime, boolean beyondRequestDate ,
			boolean expediteReport , boolean importExpediteReport , boolean exportExpediteReport , String project , LocalDateTime date)
	{
		this.name = name;
		this.followUp = followUp;
		this.acceptOrder = acceptOrder;
		this.withoutDueDate = withoutDueDate;
		this.pastDueDate = pastDueDate;
		this.supplyOnTime = supplyOnTime;
		this.beyondRequestDate = beyondRequestDate;
		this.expediteReport = expediteReport;
		this.importExpediteReport = importExpediteReport;
		this.exportExpediteReport = exportExpediteReport;
		this.project = project;
		this.date = date;
		
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFollowUp() {
		return followUp;
	}
	public void setFollowUp(boolean followUp) {
		this.followUp = followUp;
	}
	public boolean isAcceptOrder() {
		return acceptOrder;
	}
	public void setAcceptOrder(boolean acceptOrder) {
		this.acceptOrder = acceptOrder;
	}
	public boolean isWithoutDueDate() {
		return withoutDueDate;
	}
	public void setWithoutDueDate(boolean withoutDueDate) {
		this.withoutDueDate = withoutDueDate;
	}
	public boolean isPastDueDate() {
		return pastDueDate;
	}
	public void setPastDueDate(boolean pastDueDate) {
		this.pastDueDate = pastDueDate;
	}
	public boolean isSupplyOnTime() {
		return supplyOnTime;
	}
	public void setSupplyOnTime(boolean supplyOnTime) {
		this.supplyOnTime = supplyOnTime;
	}
	public boolean isExpediteReport() {
		return expediteReport;
	}
	public void setExpediteReport(boolean expediteReport) {
		this.expediteReport = expediteReport;
	}
	public boolean isImportExpediteReport() {
		return importExpediteReport;
	}
	public void setImportExpediteReport(boolean importExpediteReport) {
		this.importExpediteReport = importExpediteReport;
	}
	public boolean isExportExpediteReport() {
		return exportExpediteReport;
	}
	public void setExportExpediteReport(boolean exportExpediteReport) {
		this.exportExpediteReport = exportExpediteReport;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}


	public String getProject() {
		return project;
	}


	public void setProject(String project) {
		this.project = project;
	}


	public boolean isBeyondRequestDate() {
		return beyondRequestDate;
	}


	public void setBeyondRequestDate(boolean beyondRequestDate) {
		this.beyondRequestDate = beyondRequestDate;
	}
	
	
	
}
