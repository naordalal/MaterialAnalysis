package AnalyzerTools;

public class MapPrice 
{
	public static final int CategoriesCount = 4;
	
	private static final String MaterialAvailabilityPriceString = "Material Availability";
	private static final String WorkOrderAfterSuppliedPriceString = "Work Order After Supplied";
	private static final String OpenCustomerOrderPriceString = "Open Customer Order";
	private static final String BudgetExceededString = "Deviation From Obligo";
	
	private static final int MaterialAvailabilityPriceColumn = 0;
	private static final int WorkOrderAfterSuppliedPriceColumn = 1;
	private static final int OpenCustomerOrderPriceColumn = 2;
	private static final int BudgetExceededColumn = 3;
	
	private String catalogNumber;
	private double materialAvailabilityPrice;
	private double workOrderAfterSuppliedPrice;
	private double openCustomerOrderPrice;
	private double budgetExceeded;

	public MapPrice(String catalogNumber , double materialAvailabilityPrice , double WorkOrderAfterSuppliedPrice , double OpenCustomerOrderPrice,
			double budgetExceeded) 
	{
		this.catalogNumber = catalogNumber;
		this.materialAvailabilityPrice = materialAvailabilityPrice;
		this.workOrderAfterSuppliedPrice = WorkOrderAfterSuppliedPrice;
		this.openCustomerOrderPrice = OpenCustomerOrderPrice;
		this.budgetExceeded = budgetExceeded;
	}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public double getMaterialAvailabilityPrice() {
		return materialAvailabilityPrice;
	}

	public void setMaterialAvailabilityPrice(double materialAvailabilityPrice) {
		this.materialAvailabilityPrice = materialAvailabilityPrice;
	}

	public double getWorkOrderAfterSuppliedPrice() {
		return workOrderAfterSuppliedPrice;
	}

	public void setWorkOrderAfterSuppliedPrice(double workOrderAfterSuppliedPrice) {
		this.workOrderAfterSuppliedPrice = workOrderAfterSuppliedPrice;
	}

	public double getOpenCustomerOrderPrice() {
		return openCustomerOrderPrice;
	}

	public void setOpenCustomerOrderPrice(double openCustomerOrderPrice) {
		this.openCustomerOrderPrice = openCustomerOrderPrice;
	}
	
	public int getColumnValue(int index) 
	{
		switch (index) 
		{
			case MaterialAvailabilityPriceColumn:
				return (int) this.materialAvailabilityPrice;
			case WorkOrderAfterSuppliedPriceColumn:
				return (int) this.workOrderAfterSuppliedPrice;
			case OpenCustomerOrderPriceColumn:
				return (int) this.openCustomerOrderPrice;
			case BudgetExceededColumn:
				return (int) this.budgetExceeded;
			default:
				return 0;
		}
	}

	public String getColumn(int index) 
	{
		switch (index) 
		{
			case MaterialAvailabilityPriceColumn:
				return MaterialAvailabilityPriceString;
			case WorkOrderAfterSuppliedPriceColumn:
				return WorkOrderAfterSuppliedPriceString;
			case OpenCustomerOrderPriceColumn:
				return OpenCustomerOrderPriceString;
			case BudgetExceededColumn:
				return BudgetExceededString;
			default:
				return "";
		}
	}

	public double getBudgetExceeded() {
		return budgetExceeded;
	}

	public void setBudgetExceeded(double budgetExceeded) {
		this.budgetExceeded = budgetExceeded;
	}
}
