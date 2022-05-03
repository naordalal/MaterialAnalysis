package Senders;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import javax.mail.*;
import javax.mail.internet.*;
import com.google.common.base.CharMatcher;

import MainPackage.noValidEmailException;

import javax.activation.*;

public class SendEmail {

	private String source;
	private List<String> dest;
	private Authenticator auth;
	public SendEmail(String source , List<String> dest , Authenticator auth)
	{
		this.source = source;
		this.dest = dest;
		this.auth = auth;
	}
	public boolean send(String subject , String body , File AttachmentFile , String fileName) throws noValidEmailException
	{
		  body = body.replaceAll("\n", "<br>");
		  StringBuilder htmlBuilder = new StringBuilder();
		  htmlBuilder = new StringBuilder(); 
		  htmlBuilder.append("<html>");
		  htmlBuilder.append("<body><font face = \"Arial\">" + body + "</font></body>");
		  htmlBuilder.append("</html>");
		  body = htmlBuilder.toString();
		  
		// Recipient's email ID needs to be mentioned.
	      List<String> to = dest;
	      to = to.stream().filter(s -> !((String)s).equals("")).collect(Collectors.toList());
	      to = to.stream().map(s -> CharMatcher.whitespace().removeFrom(s.trim())).collect(Collectors.toList());
	      to = to.stream().filter(s -> isValidEmailAddress(s)).collect(Collectors.toList());
	      
	      if(to.size() == 0)
	    	  throw new noValidEmailException("No Valid Email");

	      // Sender's email ID needs to be mentioned
	      String from = source;

	      String SMTP_HOST = "al-co-il0c.mail.eo.outlook.com";
	      //String SMTP_HOST = "smtp.gmail.com";
	      
	      //IP = 10.10.4.16
	      //Port = 25

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", SMTP_HOST);
	      properties.setProperty("mail.smtp.auth", "false");
	      properties.setProperty("mail.smtp.port", "25");
	      //properties.setProperty("mail.smtp.starttls.enable", "true");
	      properties.setProperty("mail.debug", "false");
	      //properties.setProperty("mail.smtp.ssl.enable", "false");
	      //properties.setProperty("mail.smtp.ssl.enable", "true");
	      //properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
	      
	      
	      // Get the default Session object.
	      //While connect to computer with user name and password there is no need for authenticator
	      Session session = Session.getDefaultInstance(properties);
	      

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         InternetAddress [] internetAddresses = new InternetAddress[to.size()];
	         
	         int i =0;
	         for (String destintaion : to) 
	         {
	        	 internetAddresses[i] = new InternetAddress(destintaion);
	        	 i++;
	         }
	         message.addRecipients(Message.RecipientType.TO,
	        		 internetAddresses);

	         // Set Subject: header field
	         message.setSubject(subject,  "UTF-8");

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setContent(body ,  "text/html; charset=utf-8");
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);
	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
	         DataSource Source = new FileDataSource(AttachmentFile);
	         messageBodyPart.setDataHandler(new DataHandler(Source));
	         messageBodyPart.setFileName(fileName);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	         
	         return true;
	      }catch (Exception mex) {
	         mex.printStackTrace();
	         return false;
	      }
	}
	
	public static boolean isValidEmailAddress(String email) 
	{
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}
}
