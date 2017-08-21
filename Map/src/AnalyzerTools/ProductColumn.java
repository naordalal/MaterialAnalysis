package AnalyzerTools;

import MainPackage.Globals.FormType;

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
	
	@Override
	public String toString() 
	{
		String s = "";
		s += "Forecast : " + this.forecast + "\n";
		s += "Material Availability : " + this.materialAvailability + "\n";
		s += "Work Order : " + this.workOrder + "\n";
		s += "Work Order After Supplied : " + this.workOrderAfterSupplied + "\n";
		s += "Customer Orders : " + this.customerOrders + "\n";
		s += "Supplied : " + this.supplied + "\n";
		s += "Open Customer Order : " + this.openCustomerOrder + "\n";
		
		return s;
	}

	public int getCategoriesCount() 
	{
		return 7;
	}

	public double getColumnValue(int index) 
	{
		switch (index) 
		{
			case 0:
				return this.forecast;
			case 1:
				return this.materialAvailability;
			case 2:
				return this.workOrder;
			case 3:
				return this.workOrderAfterSupplied;
			case 4:
				return this.customerOrders;
			case 5:
				return this.supplied;
			case 6:
				return this.openCustomerOrder;
	
			default:
				return 0;
		}
	}

	public String getColumn(int index) 
	{
		switch (index) 
		{
			case 0:
				return "Forecast";
			case 1:
				return "Material Availability";
			case 2:
				return "Work Order";
			case 3:
				return "Work Order After Supplied";
			case 4:
				return "Customer Orders";
			case 5:
				return "Supplied";
			case 6:
				return "Open Customer Order";
	
			default:
				return "";
		}
	}

	public FormType getFormType(String category) 
	{		
		switch (category) 
		{
			case "Forecast":
				return FormType.FC;
			case "Work Order":
				return FormType.WO;
			case "Customer Orders":
				return FormType.PO;
			case "Supplied":
				return FormType.SHIPMENT;
				
			default:
				return null;
		}
	}
}
