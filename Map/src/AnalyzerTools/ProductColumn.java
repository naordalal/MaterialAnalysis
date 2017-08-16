package AnalyzerTools;

public class ProductColumn 
{
	private String catalogNumber;
	private String description;
	private double forecast;
	private double materialAvailability;
	private double workOrder;
	private double workOrderAfterSupplied;
	private double customerOrders;
	private double supplied;
	private double openCustomerOrder;
	
	public ProductColumn(String catalogNumber , String description , double forecast , double materialAvailability , double workOrder 
			, double workOrderAfterSupplied , double customerOrders , double supplied , double openCustomerOrder) 
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
	public double getForecast() {
		return forecast;
	}
	public void setForecast(double forecast) {
		this.forecast = forecast;
	}
	public double getMaterialAvailability() {
		return materialAvailability;
	}
	public void setMaterialAvailability(double materialAvailability) {
		this.materialAvailability = materialAvailability;
	}
	public double getWorkOrder() {
		return workOrder;
	}
	public void setWorkOrder(double workOrder) {
		this.workOrder = workOrder;
	}
	public double getWorkOrderAfterSupplied() {
		return workOrderAfterSupplied;
	}
	public void setWorkOrderAfterSupplied(int workOrderAfterSupplied) {
		this.workOrderAfterSupplied = workOrderAfterSupplied;
	}
	public double getCustomerOrders() {
		return customerOrders;
	}
	public void setCustomerOrders(double customerOrders) {
		this.customerOrders = customerOrders;
	}
	public double getSupplied() {
		return supplied;
	}
	public void setSupplied(double supplied) {
		this.supplied = supplied;
	}
	public double getOpenCustomerOrder() {
		return openCustomerOrder;
	}
	public void setOpenCustomerOrder(double openCustomerOrder) {
		this.openCustomerOrder = openCustomerOrder;
	}

	public void addProductColumn(ProductColumn productColumn) 
	{
		this.forecast += productColumn.forecast;
		this.materialAvailability += productColumn.materialAvailability;
		this.workOrder += productColumn.workOrder;
		this.workOrderAfterSupplied += productColumn.workOrderAfterSupplied;
		this.customerOrders += productColumn.customerOrders;
		this.supplied += productColumn.supplied;
		this.openCustomerOrder += productColumn.openCustomerOrder;
	}
}
