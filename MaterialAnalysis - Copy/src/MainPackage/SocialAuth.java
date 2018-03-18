package MainPackage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SocialAuth extends Authenticator {
	
	private String userName , password;
	public SocialAuth(String from , String password) 
	{
		super();
		this.userName = from;
		this.password = password;
	}
    @Override
    protected PasswordAuthentication getPasswordAuthentication() 
    {

        return new PasswordAuthentication(this.userName, this.password);

    }
    
    public String getUser()
    {
    	return this.userName;
    }
    
    public String getPassword()
    {
    	return this.password;
    }
}
