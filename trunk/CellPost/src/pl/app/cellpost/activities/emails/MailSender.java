package pl.app.cellpost.activities.emails;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.app.cellpost.R;
import pl.app.cellpost.activities.main.CellPostMain;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.logic.MailAuthenticator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MailSender extends Activity {
	private final String TAG = "MAIL_SENDER";

    private DbAdapter dbAdapter = null;
	
    private static final int SEND_ID = Menu.FIRST;
    private static final int SAVE_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    
	private static final String REPLY = "REPLY";
	private static final String FORWARD = "FORWARD";
    
    private Long emailId = null;
    private EditText to = null;
    private EditText cc = null;
    private EditText bcc = null;
    private EditText subject = null;
    private EditText contents = null;
    private String attachment = null;
    private Timestamp creationDate = null;
    private Timestamp modificationDate = null;
    private Timestamp deliverDate = null;
    private TextView ifSent = null;
    private String sendingType = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
	    setContentView(R.layout.mail_send_look);

	    if (dbAdapter == null)
        	dbAdapter = new DbAdapter(getApplication().getApplicationContext());
	    
	    ifSent = (TextView) findViewById(R.id.sentLabel);
	    ifSent.setVisibility(View.INVISIBLE);
	    
	    to = (EditText) findViewById(R.id.to);
	    cc = (EditText) findViewById(R.id.cc);
	    bcc = (EditText) findViewById(R.id.bcc);
	    subject = (EditText) findViewById(R.id.subject);
	    contents = (EditText) findViewById(R.id.contents);
	    creationDate = new Timestamp(new Date().getTime());
	    
	    emailId = savedInstanceState != null ? savedInstanceState.getLong(Emails._ID) : null;
		if (emailId == null) {
			Bundle extras = getIntent().getExtras();  
			if (extras != null) {
				emailId =  extras.getLong(Emails._ID);
				sendingType = extras.getString(FORWARD);
				if (sendingType == null)
					sendingType = extras.getString(REPLY);
			}
			
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
	        	startActivity(new Intent(this, CellPostMain.class));
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
        	dbAdapter = new DbAdapter(this);
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
            	to.setText(cursor.getString(
            			cursor.getColumnIndexOrThrow(Emails.ADDRESSEE)));
                cc.setText(cursor.getString(
                		cursor.getColumnIndexOrThrow(Emails.CC)));
                bcc.setText(cursor.getString(
                		cursor.getColumnIndexOrThrow(Emails.BCC)));
                String deliverDate = cursor.getString(
                		cursor.getColumnIndexOrThrow(Emails.DELIVER_DATE));
                if (deliverDate != null || FORWARD.equals(sendingType)) {
                	subject.setText("FWD: " + cursor.getString(
                			cursor.getColumnIndexOrThrow(Emails.SUBJECT)));
                	to.setText("");
                }
                else if (REPLY.equals(sendingType)) {
                	subject.setText("RE: " + cursor.getString(
                			cursor.getColumnIndexOrThrow(Emails.SUBJECT)));
                	to.setText(cursor.getString(
                			cursor.getColumnIndexOrThrow(Emails.SENDER)));
                }              	
                else
                	subject.setText(cursor.getString(
                			cursor.getColumnIndexOrThrow(Emails.SUBJECT)));
                contents.setText(cursor.getString(
                		cursor.getColumnIndexOrThrow(Emails.CONTENTS)));
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
	    	if (emailId == null || sendingType != null) {
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
			String defaultAddress = cursor.getString(
					cursor.getColumnIndex(Accounts.ADDRESS));
			String user = cursor.getString(
					cursor.getColumnIndex(Accounts.USER));
			String password = cursor.getString(
					cursor.getColumnIndex(Accounts.PASS));
			String server = cursor.getString(
					cursor.getColumnIndex(Accounts.OUTGOING_SERVER));
			String port = cursor.getString(
					cursor.getColumnIndex(Accounts.OUTGOING_PORT));
			String security = cursor.getString(
					cursor.getColumnIndex(Accounts.OUTGOING_SECURITY));
			Map<String,String> accountData = new HashMap<String,String>();
			accountData.put("address", defaultAddress);
			accountData.put("user", user);
			accountData.put("password", password);
			accountData.put("server", server);
			accountData.put("port", port);
			accountData.put("security", security);
			
			String subject = this.subject.getText().toString();
			String contents = this.contents.getText().toString();
			String to = this.to.getText().toString();
			String cc = this.cc.getText().toString();
			String bcc = this.bcc.getText().toString();
			
			
			MailAuthenticator sender = 
				new MailAuthenticator(accountData, MailAuthenticator.SEND);
	        if (sender.sendMail(subject, contents, defaultAddress, to, cc, bcc)) {
	            deliverDate = new Timestamp(new Date().getTime());            	
	            ifSent.setVisibility(View.VISIBLE);
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
