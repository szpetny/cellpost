package pl.app.cellpost.logic;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.util.Log;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

public class MailAuthenticator extends javax.mail.Authenticator {
	private final String TAG = "MailAuthenticator";
	private final String secureModeOn = "SSL/TSL";
	private final String secureModeOff = "Bez SSL/TSL";
	private final String POP3 = "POP3";
	private final String IMAP = "IMAP";
	
	public static final String SEND = "SEND";
	public static final String RECEIVE = "RECEIVE";
	
	private String user = null;
	private String password = null;
	private String server = null;
	private String port = null;
	private String security = null;
	private String address = null;
	private Session session = null; 
	private String provider = null; 
	private String deleteEmails = null;
	
	private static Map<String,String> receiveProviders;
	private String[] deleteEmailsOptions = {"Tak ", "Nigdy"};
	
	private Store store = null;
	private Folder folder = null, inbox = null;
	private POP3Folder pop3folder = null;
	private IMAPFolder imapFolder = null;
	
	static {   
	    Security.addProvider(new JSSEProvider());   
	    receiveProviders = new HashMap<String,String>();
	    receiveProviders.put("POP3", "pop3");
	    receiveProviders.put("IMAP", "imap");
	}  
	
	
	@SuppressWarnings("unchecked")
	public MailAuthenticator(Map accountData, String operation) {  
		user = (String)accountData.get("user");   
	    password = (String)accountData.get("password");   
	    server = (String)accountData.get("server");
	    port = (String)accountData.get("port");
	    security = (String)accountData.get("security");
	    address = (String)accountData.get("address");
	    
	    Properties props = new Properties();   
	    
		if (SEND.equals(operation)) {			
			props.setProperty("mail.transport.protocol", "smtp");   
			props.setProperty("mail.host", server);   
			props.put("mail.smtp.auth", "true");   
			props.put("mail.smtp.port", port);  
			props.put("mail.smtp.from", address);   
			props.put("mail.smtp.socketFactory.port", port);   
			if (secureModeOn.equals(security)) {
				props.put("mail.smtp.socketFactory.class",   
		          "javax.net.ssl.SSLSocketFactory");   
				props.put("mail.smtp.socketFactory.fallback", "false");
			}
			else {
				props.put("mail.smtp.socketFactory.class",   
		          "javax.net.ssl.SSLSocketFactory");   
				props.put("mail.smtp.socketFactory.fallback", "true");
			}

			props.setProperty("mail.smtp.quitwait", "false");   
		}
		else if (RECEIVE.equals(operation)) {
			provider = (String)accountData.get("provider");
			deleteEmails = (String) accountData.get("deleteEmails");
			
			if (secureModeOn.equals(security) && POP3.equals(provider) 
					&& deleteEmailsOptions[0].equals(deleteEmails)) {
				props.setProperty("mail.pop3s.rsetbeforequit","true");
			}
			else if (secureModeOn.equals(security) && POP3.equals(provider)) {
				props.setProperty("mail.pop3s.rsetbeforequit","false");
			}
			
			else if (secureModeOff.equals(security) && POP3.equals(provider) 
					&& deleteEmailsOptions[0].equals(deleteEmails)) {
				props.setProperty("mail.pop3.rsetbeforequit","true");
			}
			else if (secureModeOff.equals(security) && POP3.equals(provider)) {
				props.setProperty("mail.pop3.rsetbeforequit","false");
			}
		}

		session = Session.getDefaultInstance(props, this);   

	}   
	
	
	protected PasswordAuthentication getPasswordAuthentication() {   
	    return new PasswordAuthentication(user, password);   
	}   
	
	
	public synchronized boolean sendMail(String subject, String body, 
			String sender, String to, String cc, String bcc) {   
	    try{
		    MimeMessage message = new MimeMessage(session);   
		    DataHandler handler = new DataHandler(
		    		(DataSource) new ByteArrayDataSource(body.getBytes(), "text/plain"));   
		    message.setSender(new InternetAddress(sender));   
		    message.setSubject(subject);   
		    message.setDataHandler(handler);   
		    if ("".equals(to) == false)
		    	if (to.indexOf(',') > 0)   
			        message.setRecipients(Message.RecipientType.TO, 
			        		InternetAddress.parse(to));   
			    else  
			        message.setRecipient(Message.RecipientType.TO, 
			        		new InternetAddress(to));   
		    
		    if ("".equals(cc) == false)
			    if (cc.indexOf(',') > 0)   
			        message.setRecipients(Message.RecipientType.CC, 
			        		InternetAddress.parse(cc));   
			    else  
			        message.setRecipient(Message.RecipientType.CC, 
			        		new InternetAddress(cc));   
		    
		    if ("".equals(bcc) == false)
			    if (bcc.indexOf(',') > 0)   
			        message.setRecipients(Message.RecipientType.BCC, 
			        		InternetAddress.parse(bcc));   
			    else  
			        message.setRecipient(Message.RecipientType.BCC, 
			        		new InternetAddress(bcc));   
		    
		    Transport.send(message);   
		    return true;
	    } catch(Exception e){
	    	Log.e(TAG, "Nothing sent. Exception occured: " + e);
	    }
	    
	    return false;
	}   
	
	public synchronized Map<String, Message> getMail(String whichMessages) {
		Message[] allEmails = null;
		Map<String,Message> unreadEmails = new HashMap<String,Message>();
		try {
			if (secureModeOn.equals(security) && POP3.equals(provider)) {
				store = session.getStore(receiveProviders.get(provider) + "s");
			}
			else if (secureModeOff.equals(security) && POP3.equals(provider)) {
				store = session.getStore(receiveProviders.get(provider));
			}
			else if (secureModeOn.equals(security) && IMAP.equals(provider)) {
				store = session.getStore(receiveProviders.get(provider) + "s");
			}
			else if (secureModeOff.equals(security) && IMAP.equals(provider)) {
				store = session.getStore(receiveProviders.get(provider));
			}
			store.connect(server, user, password);
			folder = store.getDefaultFolder();
			inbox = folder.getFolder("INBOX");
			
			inbox.open(Folder.READ_WRITE);

			if (POP3.equals(provider)) {
				pop3folder = (POP3Folder) inbox;
				allEmails = pop3folder.getMessages();
			}
			if (IMAP.equals(provider)) {
				imapFolder = (IMAPFolder) inbox;
				allEmails = imapFolder.getMessages();
			}
			
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE); 
			fp.add(FetchProfile.Item.CONTENT_INFO); 
			if (allEmails != null) {
				inbox.fetch(allEmails, fp);
				if ( "-1".equals(whichMessages)) {			  
					for (Message email : allEmails) {
						if (pop3folder != null)
						   unreadEmails.put(
							 pop3folder.getUID(email), email);
						else
						   unreadEmails.put(
						     (new Long(imapFolder.getUID(email))).toString(), 
						     email);  
					  }
				}
				else {
					  String[] previousMsgUIDL = whichMessages.split(" ");
					  List<String> tmpList = new ArrayList<String>();
					  for (String UID : previousMsgUIDL) 
						  tmpList.add(UID);
					  for (Message email : allEmails) {
						if (pop3folder != null) {
						  if (!tmpList.contains(pop3folder.getUID(email)))
								unreadEmails.put(
								   pop3folder.getUID(email), 
								   email);
						}	  
						else {
							String imapUID = (
									new Long(imapFolder.getUID(email))).toString();
							if (tmpList.contains(imapUID) == false)
								unreadEmails.put(
									(new Long(imapFolder.getUID(email))).toString(), 
									email);
						}
					  }
				}
			}
					
			
			
		} catch (NoSuchProviderException e) {
			Log.e(TAG, "NoSuchProviderException " + e.getMessage());
		} catch (MessagingException e) {
			Log.e(TAG, "MessagingException " + e.getMessage());
		}
		return unreadEmails;
	}
	
	public void closeInbox() {
		if (inbox != null && inbox.isOpen()) {
			try {
				inbox.close(true);
				
			} catch (MessagingException e) {
				Log.e(TAG, "MessagingException " + e.getMessage());
			}
		}
		if (imapFolder != null && imapFolder.isOpen()) {
			try {
				imapFolder.close(true);
				
			} catch (MessagingException e) {
				Log.e(TAG, "MessagingException " + e.getMessage());
			}
		}
		if (pop3folder != null && pop3folder.isOpen()) {
			try {
				pop3folder.close(true);
				
			} catch (MessagingException e) {
				Log.e(TAG, "MessagingException " + e.getMessage());
			}
		}
		if (folder != null && folder.isOpen()) {
			try {
				folder.close(true);
				
			} catch (MessagingException e) {
				Log.e(TAG, "MessagingException " + e.getMessage());
			}
		}
		if (store != null && store.isConnected()) {
			try {
				store.close();
				
			} catch (MessagingException e) {
				Log.e(TAG, "MessagingException " + e.getMessage());
			}
		}
	}
	
	public class ByteArrayDataSource implements DataSource {   
	    private byte[] data;   
	    private String type;   
	
	
		public ByteArrayDataSource(byte[] data, String type) {   
		    super();   
		    this.data = data;   
		    this.type = type;   
		}   
		
		
		public ByteArrayDataSource(byte[] data) {   
		    super();   
		    this.data = data;   
		}   
		
		
		public void setType(String type) {   
		    this.type = type;   
		}   
		
		
		public String getContentType() {   
		    if (type == null)   
		        return "application/octet-stream";   
		    else  
		        return type;   
		}   
		
		
		public InputStream getInputStream() throws IOException {   
		    return new ByteArrayInputStream(data);   
		}   
		
		
		public String getName() {   
		    return "ByteArrayDataSource";   
		}   
		
		
		public OutputStream getOutputStream() throws IOException {   
		    throw new IOException("Not Supported");   
		}   

	}

}   