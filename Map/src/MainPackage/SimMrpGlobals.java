package MainPackage;

public class SimMrpGlobals extends Globals
{

	public int maxOrderIndex = 6;
	public int maxRoundsNumber = 7;

	public int openOrdersIndex = 3;
	
	public int supplierOffset = 0;
	public int orderOffset = 1;
	public int dateOffset = 4;
	public int shippingDaysOffset = 5;
	public int quantityOffset = 6;
	

	public String itemNumberColumn = "מקטב";
	public String descriptionColumn = "תיאור פריט";
	public String descriptionColumn2 = "תאור";
	public String aviableInventoryColumn = "מלאי זמין לאחר פק\"י";
	public String allocationOfStock = "הקצאת מלאי תפי מ MRP";
	public String shortageColumn = "חוסר נוכחי 1";
	public String dateColumn = "מועד אספקה ";
	public String requiredAmountCloumn = "נדרש חודש ";
	public String balanceColumn = "כמות פתוחה ";
	public String balanceAfterOrdersColumn = "יתרת הזמנה מעבר ל 6";
	public String priceColumn = " מחיר תקן מ Data MRP";
	public String priceColumn2 = "4-מחיר תקן שיווק";
	
}
