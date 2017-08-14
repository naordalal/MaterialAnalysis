package AnalyzerTools;

public class ProductColumn 
{
	private String catalogNumber;
	private String description;
	private int forecast;
	private int materialAvailability;
	private int workOrder;
	private int workOrderAfterSupplied;
	private int customerOrders;
	private int supplied;
	private int openCustomerOrder;
	
	public ProductColumn(String catalogNumber , String description , int forecast , int materialAvailability , int workOrder 
			, int workOrderAfterSupplied , int customerOrders , int supplied , int openCustomerOrder) 
	{
		this.catalogNumber = catalogNumber;
		this.description = description;
		this.forecast = forecast;
		this.materialAvailability = materialAvailability;
		this.workOrder = workOrder;
		this.workOrderAfterSupplied = workOrderAfterSupplied;
		this.customerOrders = customerOrders;
		this.supplied = supplied;
		this.openCustomerOrder = openCustomerOrder;
	}
	
	public String getCatalogNumber() {
		return catalogNumber;
	}
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getForecast() {
		return forecast;
	}
	public void setForecast(int forecast) {
		this.forecast = forecast;
	}
	public int getMaterialAvailability() {
		return materialAvailability;
	}
	public void setMaterialAvailability(int materialAvailability) {
		this.materialAvailability = materialAvailability;
	}
	public int getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(int workOrder) {
		this.workOrder = workOrder;
	}
	public int getWorkOrderAfterSupplied() {
		return workOrderAfterSupplied;
	}
	public void setWorkOrderAfterSupplied(int workOrderAfterSupplied) {
		this.workOrderAfterSupplied = workOrderAfterSupplied;
	}
	public int getCustomerOrders() {
		return customerOrders;
	}
	public void setCustomerOrders(int customerOrders) {
		this.customerOrders = customerOrders;
	}
	public int getSupplied() {
		return supplied;
	}
	public void setSupplied(int supplied) {
		this.supplied = supplied;
	}
	public int getOpenCustomerOrder() {
		return openCustomerOrder;
	}
	public void setOpenCustomerOrder(int openCustomerOrder) {
		this.openCustomerOrder = openCustomerOrder;
	}
}
