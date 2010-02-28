package pl.app.cellpost.activities.emails;

import java.sql.Timestamp;
import java.util.Date;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.logic.GMailAuthenticator;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MailSender extends Activity {
	private final String TAG = "MAIL_SENDER";

    private DbAdapter dbAdapter;
	
    private static final int SEND_ID = Menu.FIRST;
    private static final int SAVE_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    
    private Long emailId;
    private EditText to;
    private EditText cc;
    private EditText bcc;
    private EditText subject;
    private EditText contents;
    private String attachment = null;
    private Timestamp creationDate = null;
    private Timestamp modificationDate = null;
    private Timestamp deliverDate = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
	    setContentView(R.layout.mail_send_look);

	    if (dbAdapter == null)
        	dbAdapter = new DbAdapter(getApplication().getApplicationContext());
	    
	    to = (EditText) findViewById(R.id.to);
	    cc = (EditText) findViewById(R.id.cc);
	    bcc = (EditText) findViewById(R.id.bcc);
	    subject = (EditText) findViewById(R.id.subject);
	    contents = (EditText) findViewById(R.id.contents);
	    creationDate = new Timestamp(new Date().getTime());
	    
	    emailId = savedInstanceState != null ? savedInstanceState.getLong(Emails._ID) : null;
		if (emailId == null) {
			Bundle extras = getIntent().getExtras();            
			emailId = extras != null ? extras.getLong(Emails._ID) : null;
		}
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
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(Emails._ID, emailId);
    }  
	
	@Override
    protected void onPause() {
        super.onPause();
        saveMail();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (dbAdapter == null)
        	dbAdapter = new DbAdapter(getApplication().getApplicationContext());
        populateFields();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	saveMail();
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }
    
    private void populateFields() {
        if (emailId != null) {
            Cursor cursor = dbAdapter.fetchEmail(emailId);
            startManagingCursor(cursor);
            if (cursor.moveToFirst()) {
            	to.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.ADDRESSEE)));
                cc.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.CC)));
                bcc.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.BCC)));
                subject.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.SUBJECT)));
                contents.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.CONTENTS)));
            }
            else {
            	Log.e(TAG, "The database crash or sth...");
            }
                       
        }
    }
    
    private void saveMail() {
    	ContentValues emailData = new ContentValues();
	    if (to != null && "".equals(to.getText().toString()) == false) {
	    	emailData.put(Emails.ADDRESSEE, to.getText().toString());
			emailData.put(Emails.CC, cc.getText().toString());
			emailData.put(Emails.BCC, bcc.getText().toString());	
			emailData.put(Emails.SUBJECT, subject.getText().toString());
			emailData.put(Emails.CONTENTS, contents.getText().toString());

			if(creationDate != null) 
	    		emailData.put(Emails.CREATE_DATE, creationDate.toGMTString());
			if(modificationDate != null) 
	    		emailData.put(Emails.MODIFY_DATE, modificationDate.toGMTString());
			if(deliverDate != null) 
	    		emailData.put(Emails.DELIVER_DATE, deliverDate.toGMTString());
	    }
	    if (emailData != null && emailData.containsKey(Emails.ADDRESSEE)) {
	    	if (emailId == null) {
	            long id = dbAdapter.saveEmail(emailData);
	            if (id > 0) {
	            	emailId = id;
	            }
	        } else {
	            dbAdapter.updateSavedEmail(emailId, emailData);
	        }
	    }
        
    }

	private void sendMail() {  
		Cursor cursor = dbAdapter.fetchDefaultAccount();
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			String defaultAddress = cursor.getString(cursor.getColumnIndex(Accounts.ADDRESS));
			String password = cursor.getString(cursor.getColumnIndex(Accounts.PASS));
			String subject = this.subject.getText().toString();
			String contents = this.contents.getText().toString();
			String to = this.to.getText().toString();
			String cc = this.cc.getText().toString();
			String bcc = this.bcc.getText().toString();
			
			try { 
				GMailAuthenticator sender = new GMailAuthenticator(defaultAddress, password);
	            sender.sendMail(subject, contents, defaultAddress, to);   
	            deliverDate = new Timestamp(new Date().getTime());
	            
	        } catch (Exception e) {   
	            Log.e(TAG, "Error during trying to send e-mail! " + e.getMessage(), e);   
	        } 
	        
		}

		else {
			Log.e(TAG, "Something wrong happened..");
		}
        	
		saveMail();
	}
	
	private void deleteMail() {
		if (emailId != null) {
			dbAdapter.deleteEmail(emailId);
		}
		else {
			to.setText("");
		    cc.setText("");
		    bcc.setText("");
		    subject.setText("");
		    contents.setText("");
		    attachment = null;
		    creationDate = null;
		    modificationDate = null;
		    deliverDate = null;

		}
	}
}
