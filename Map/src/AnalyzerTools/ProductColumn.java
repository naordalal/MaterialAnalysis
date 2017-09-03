package AnalyzerTools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import MainPackage.DataBase;
import MainPackage.Globals.FormType;
import MainPackage.Pair;

public class ProductColumn 
{
	private static final String ForecastString = "Forecast";
	private static final String MaterialAvailabilityString = "Material Availability";
	private static final String WorkOrderString = "Work Order";
	private static final String WorkOrderAfterSuppliedString = "Work Order After Supplied";
	private static final String WorkOrderAfterPOAndParentsWOString = "Work Order After PO And Parents WO";
	private static final String CustomerOrdersString = "Customer Orders";
	private static final String ParentsWorkOrderString = "Parents Work Order";
	private static final String ParentsWorkOrderSuppliedString = "Parents Work Order Supplied";
	private static final String SuppliedString = "Supplied";
	private static final String OpenCustomerOrderString = "Open Customer Order";
	
	private static final int ForecastColumn = 0;
	private static final int MaterialAvailabilityColumn = 1;
	private static final int WorkOrderColumn = 2;
	private static final int WorkOrderAfterSuppliedColumn = 3;
	private static final int WorkOrderAfterPOAndParentsWOColumn = 4;
	private static final int CustomerOrdersColumn = 5;
	private static final int ParentsWorkOrderColumn = 6;
	private static final int ParentsWorkOrderSuppliedColumn = 7;
	private static final int SuppliedColumn = 8;
	private static final int OpenCustomerOrderColumn = 9;
	
	public static final int CategoriesCount = 10;
	
	private String catalogNumber;
	private String description;
	private double forecast;
	private double materialAvailability;
	private double workOrder;
	private double workOrderAfterSupplied;
	private double workOrderAfterCustomerOrderAndParentsWorkOrder;
	private double customerOrders;
	private double parentsWorkOrder;
	private double parentsWorkOrderSupplied;
	private double supplied;
	private double openCustomerOrder;
	
	public ProductColumn(String catalogNumber , String description , double forecast , double materialAvailability , double workOrder 
			, double workOrderAfterSupplied , double workOrderAfterCustomerOrderAndParentsWorkOrder , double customerOrders , 
			double parentsWorkOrder , double parentsWorkOrderSupplied , double supplied , double openCustomerOrder) 
	{
		this.catalogNumber = catalogNumber;
		this.description = description;
		this.forecast = forecast;
		this.materialAvailability = materialAvailability;
		this.workOrder = workOrder;
		this.workOrderAfterSupplied = workOrderAfterSupplied;
		this.workOrderAfterCustomerOrderAndParentsWorkOrder = workOrderAfterCustomerOrderAndParentsWorkOrder;
		this.customerOrders = customerOrders;
		this.parentsWorkOrder = parentsWorkOrder;
		this.parentsWorkOrderSupplied = parentsWorkOrderSupplied;
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
	
	public double getWorkOrderAfterCustomerOrderAndParentWorkOrder() {
		return workOrderAfterCustomerOrderAndParentsWorkOrder;
	}

	public void setWorkOrderAfterCustomerOrderAndParentWorkOrder(double workOrderAfterCustomerOrderAndParentWorkOrder) {
		this.workOrderAfterCustomerOrderAndParentsWorkOrder = workOrderAfterCustomerOrderAndParentWorkOrder;
	}

	public double getParentWorkOrder() {
		return parentsWorkOrder;
	}

	public void setParentWorkOrder(double parentWorkOrder) {
		this.parentsWorkOrder = parentWorkOrder;
	}

	public double getParentWorkOrderSupplied() {
		return parentsWorkOrderSupplied;
	}

	public void setParentWorkOrderSupplied(double parentWorkOrderSupplied) {
		this.parentsWorkOrderSupplied = parentWorkOrderSupplied;
	}

	public void addProductColumn(ProductColumn productColumn) 
	{
		this.forecast += productColumn.forecast;
		this.materialAvailability += productColumn.materialAvailability;
		this.workOrder += productColumn.workOrder;
		this.workOrderAfterCustomerOrderAndParentsWorkOrder += productColumn.workOrderAfterCustomerOrderAndParentsWorkOrder;
		this.workOrderAfterSupplied += productColumn.workOrderAfterSupplied;
		this.customerOrders += productColumn.customerOrders;
		this.parentsWorkOrder += productColumn.parentsWorkOrder;
		this.parentsWorkOrderSupplied += productColumn.parentsWorkOrderSupplied;
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
		s += "Work Order After PO And Parents WO : " + this.workOrderAfterCustomerOrderAndParentsWorkOrder + "\n";
		s += "Work Order After Supplied : " + this.workOrderAfterSupplied + "\n";
		s += "Customer Orders : " + this.customerOrders + "\n";
		s += "Parents Work Order : " + this.parentsWorkOrder + "\n";
		s += "Parents Work Order Supplied : " + this.parentsWorkOrderSupplied + "\n";
		s += "Supplied : " + this.supplied + "\n";
		s += "Open Customer Order : " + this.openCustomerOrder + "\n";
		
		return s;
	}

	public double getColumnValue(int index) 
	{
		switch (index) 
		{
			case ForecastColumn:
				return this.forecast;
			case MaterialAvailabilityColumn:
				return this.materialAvailability;
			case WorkOrderColumn:
				return this.workOrder;
			case WorkOrderAfterSuppliedColumn:
				return this.workOrderAfterSupplied;
			case WorkOrderAfterPOAndParentsWOColumn:
				return this.workOrderAfterCustomerOrderAndParentsWorkOrder;
			case CustomerOrdersColumn:
				return this.customerOrders;
			case ParentsWorkOrderColumn:
				return this.parentsWorkOrder;
			case ParentsWorkOrderSuppliedColumn:
				return this.parentsWorkOrderSupplied;
			case SuppliedColumn:
				return this.supplied;
			case OpenCustomerOrderColumn:
				return this.openCustomerOrder;
	
			default:
				return 0;
		}
	}

	public String getColumn(int index) 
	{
		switch (index) 
		{
			case ForecastColumn:
				return ForecastString;
			case MaterialAvailabilityColumn:
				return MaterialAvailabilityString;
			case WorkOrderColumn:
				return WorkOrderString;
			case WorkOrderAfterSuppliedColumn:
				return WorkOrderAfterSuppliedString;
			case WorkOrderAfterPOAndParentsWOColumn:
				return WorkOrderAfterPOAndParentsWOString;
			case CustomerOrdersColumn:
				return CustomerOrdersString;
			case ParentsWorkOrderColumn:
				return ParentsWorkOrderString;
			case ParentsWorkOrderSuppliedColumn:
				return ParentsWorkOrderSuppliedString;
			case SuppliedColumn:
				return SuppliedString;
			case OpenCustomerOrderColumn:
				return OpenCustomerOrderString;
	
			default:
				return "";
		}
	}

	public FormType getFormType(String category) 
	{		
		switch (category) 
		{
			case ForecastString:
				return FormType.FC;
			case WorkOrderString:
				return FormType.WO;
			case CustomerOrdersString:
				return FormType.PO;
			case SuppliedString:
				return FormType.SHIPMENT;
			case ParentsWorkOrderString:
				return FormType.WO; 
			default:
				return null;
		}
	}
	
	public List<String> getCatalogNumbersOfForms(String catalogNumber , String category)
	{
		List<String> catalogNumbers = new ArrayList<>();
		DataBase db = new DataBase();
		switch (category)
		{
		case ForecastString:
			catalogNumbers.add(catalogNumber);
			break;
		case MaterialAvailabilityString:
			break;
		case WorkOrderString:
			catalogNumbers.add(catalogNumber);
			break;
		case WorkOrderAfterSuppliedString:
			break;
		case CustomerOrdersString:
			catalogNumbers.add(catalogNumber);
			break;
		case SuppliedString:
			break;
		case OpenCustomerOrderString:
			break;	
		case WorkOrderAfterPOAndParentsWOString:
			break;	
		case ParentsWorkOrderString:
			List<String> parents = db.getFathers(catalogNumber).stream().map(pair -> pair.getLeft()).collect(Collectors.toList());
			catalogNumbers = parents;
			break;
		case ParentsWorkOrderSuppliedString:
			break;
		default:
			break;
		}
		return catalogNumbers;
	}

	public static String getDescriptionOfCategory(String catalogNumber , String category) 
	{
		StringBuilder description = new StringBuilder();
		List<Pair<String,Integer>> fathers;
		DataBase db = new DataBase();
		switch (category) 
		{
			case ForecastString:
				description.append("Forecast");
				break;
			case MaterialAvailabilityString:
				description.append("Previous Material Availability + Forecast + Work Order ");
				fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					description.append("+ Work Order Of ");
					description.append(father.getLeft() + "(father catalog number) * ");
					description.append(father.getRight());
					description.append("\n");
				}
				break;
			case WorkOrderString:
				description.append("Work Order");
				break;
			case WorkOrderAfterSuppliedString:
				description.append("Previous Work Order After Supplied + Work Order - Supplied");
				break;
			case CustomerOrdersString:
				description.append("Customer Orders ");
				/*fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					description.append("+ Work Order Of ");
					description.append(father.getLeft() + "(father catalog number) * ");
					description.append(father.getRight());
					description.append("\n");
				}*/
				break;
			case SuppliedString:
				description.append("Supplied ");
				/*fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					description.append("+ Supplied Of ");
					description.append(father.getLeft() + "(father catalog number) * ");
					description.append(father.getRight());
					description.append("\n");
				}*/
				break;
			case OpenCustomerOrderString:
				description.append("Previous Open Customer Order + Customer Orders - Supplied");
				break;	
			case WorkOrderAfterPOAndParentsWOString:
				description.append("Previous Work Order After PO And Parents WO + Work Order - Customer Order ");
				fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					description.append("- Work Order Of ");
					description.append(father.getLeft() + "(father catalog number)");
					description.append("\n");
				}
				break;	
			case ParentsWorkOrderString:
				fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					if(fathers.indexOf(father) != 0)
						description.append("+ ");
					description.append("Work Order Of ");
					description.append(father.getLeft() + "(father catalog number) * ");
					description.append(father.getRight());
					description.append("\n");
				}
				break;
			case ParentsWorkOrderSuppliedString:
				fathers = db.getFathers(catalogNumber);
				for (Pair<String, Integer> father : fathers) 
				{
					if(fathers.indexOf(father) != 0)
						description.append("+ ");
					description.append("Supplied Of ");
					description.append(father.getLeft() + "(father catalog number) * ");
					description.append(father.getRight());
					description.append("\n");
				}
				break;
			default:
				break;
		}
		
		return description.toString();
	}
}
