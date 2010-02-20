package pl.app.cellpost.activities.emails;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
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
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (dbAdapter == null)
        	dbAdapter = new DbAdapter(this);
        */
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
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (emailId != null)
        	savedInstanceState.putLong(Emails._ID, emailId);
        savedInstanceState.putString(Emails.ADDRESSEE, to.getText().toString());
        savedInstanceState.putString(Emails.CC, cc.getText().toString());
        savedInstanceState.putString(Emails.BCC, bcc.getText().toString());
        savedInstanceState.putString(Emails.SUBJECT, subject.getText().toString());
        savedInstanceState.putString(Emails.CONTENTS, contents.getText().toString());
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        saveMail();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
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
            to.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.ADDRESSEE)));
            cc.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.CC)));
            bcc.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.BCC)));
            subject.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.SUBJECT)));
            contents.setText(cursor.getString(cursor.getColumnIndexOrThrow(Emails.CONTENTS)));
                       
        }
    }
    
    private void saveMail() {
    	ContentValues emailData = new ContentValues();
		emailData.put(Emails.ADDRESSEE, to.getText().toString());
		emailData.put(Emails.CC, cc.getText().toString());
		emailData.put(Emails.BCC, bcc.getText().toString());	
		emailData.put(Emails.SUBJECT, subject.getText().toString());
		emailData.put(Emails.CONTENTS, contents.getText().toString());
		
        if (emailId == null) {
            long id = dbAdapter.createEmail(emailData);
            if (id > 0) {
            	emailId = id;
            }
        } else {
            dbAdapter.updateEmail(emailId, emailData);
        }
    }

	private void sendMail() {
		try {   
        	GMailAuthenticator sender = new GMailAuthenticator("username@gmail.com", "password");
            sender.sendMail("This is Subject",   
                    "This is Body",   
                    "user@gmail.com",   
                    "user@yahoo.com");   
        } catch (Exception e) {   
            Log.e(TAG, e.getMessage(), e);   
        } 
	}
	
	private void deleteMail() {
		
	}
}
