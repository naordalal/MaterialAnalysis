package MainPackage;

public class Message 
{
	private String message;
	private int column;
	
	public Message(String message , int column) 
	{
		this.message = message;
		this.column = column;
	}
	
	public String getMessage() 
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}
}
