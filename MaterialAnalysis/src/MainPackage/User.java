package MainPackage;

import java.util.List;

public class User 
{
	private String nickName;
	private String email;
	private boolean adminPermission;
	private boolean purchasingPermission;
	private String signature;
	private List<String> customers;
	
	public User(String nickName , String email , boolean adminPermission , boolean purchasingPermission , String signature , List<String> customers) 
	{
		this.nickName = nickName;
		this.email = email;
		this.adminPermission = adminPermission;
		this.purchasingPermission = purchasingPermission;
		this.signature = signature;
		this.customers = customers;
	}
	
	public String getNickName() 
	{
		return nickName;
	}
	public void setNickName(String nickName) 
	{
		this.nickName = nickName;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) 
	{
		this.email = email;
	}
	
	public boolean isAdminPermission() 
	{
		return adminPermission;
	}
	public void setAdminPermission(boolean adminPermission) 
	{
		this.adminPermission = adminPermission;
	}
	
	public boolean isPurchasingPermission() 
	{
		return purchasingPermission;
	}
	public void setPurchasingPermission(boolean purchasingPermission) 
	{
		this.purchasingPermission = purchasingPermission;
	}
	
	public List<String> getCustomers() 
	{
		return customers;
	}
	public void setCustomers(List<String> customers) 
	{
		this.customers = customers;
	}

	public String getSignature() 
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}
}
