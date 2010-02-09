package pl.app.cellpost.activities.mails;

import pl.app.cellpost.R;
import pl.app.cellpost.logic.GMailAuthenticator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MailSender extends Activity {
	private final String MAIL_SENDER = "MAIL_SENDER";

    private static final int SEND_ID = Menu.FIRST;
    private static final int SAVE_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    
    private EditText to;
    private EditText cc;
    private EditText bcc;
    private EditText subject;
    private EditText contents;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.mail_send_look);

	    to = (EditText) findViewById(R.id.to);
	    cc = (EditText) findViewById(R.id.cc);
	    bcc = (EditText) findViewById(R.id.bcc);
	    subject = (EditText) findViewById(R.id.subject);
	    contents = (EditText) findViewById(R.id.contents);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	     super.onCreateOptionsMenu(menu);
	     menu.add(0, SEND_ID, 0, R.string.menu_send)
	         //.setShortcut('0', 's')
	         .setIcon(android.R.drawable.ic_menu_send);	 
	     
	     menu.add(0, SAVE_ID, 0, R.string.menu_save)
	         //.setShortcut('0', 's')
	         .setIcon(android.R.drawable.ic_menu_save);	 
	     
	     menu.add(0, DELETE_ID, 0, R.string.menu_delete)
	         //.setShortcut('0', 's')
	         .setIcon(android.R.drawable.ic_menu_delete);	 
	        return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	     switch(item.getItemId()) {
	        case SEND_ID:
	        	sendMail();
	            return true;
	            
	        case SAVE_ID:
	        	saveMail();
	            return true;
	            
	        case DELETE_ID:
	        	deleteMail();
	            return true;
	     }
	     return super.onMenuItemSelected(featureId, item);
	}
	
	private void sendMail() {
		try {   
        	GMailAuthenticator sender = new GMailAuthenticator("username@gmail.com", "password");
            sender.sendMail("This is Subject",   
                    "This is Body",   
                    "user@gmail.com",   
                    "user@yahoo.com");   
        } catch (Exception e) {   
            Log.e(MAIL_SENDER, e.getMessage(), e);   
        } 
	}
	
	private void saveMail() {
		
	}
	
	private void deleteMail() {
		
	}
}
