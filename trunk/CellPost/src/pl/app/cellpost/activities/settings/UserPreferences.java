package pl.app.cellpost.activities.settings;

import java.util.HashMap;
import java.util.Map;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class UserPreferences extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = "UserPreferences";
	private static final String PREFS_NAME = "CellPostPrefsFile";
	
    private Spinner defaultAccount;
    private Spinner newEmailsCheck;
    private CheckBox newEmailsNotif;
    private DbAdapter dbAdapter;
    private ArrayAdapter<String> defaultAccountAdapter;
    private ArrayAdapter<CharSequence> newEmailsCheckAdapter;
    private Map<String,Long> idsAccountsPairs = new HashMap<String,Long>();
    
    private static final String DEFAULT_ACCOUNT = "DEFAULT_ACCOUNT";
    private static final String NEW_EMAILS_CHECK = "NEW_EMAILS_CHECK";
    private static final String NEW_EMAILS_NOTIFICATION = "NEW_EMAILS_NOTIFICATION";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dbAdapter == null)
        	dbAdapter = new DbAdapter(this);
        
        setContentView(R.layout.user_prefs_config_look);
   
		Cursor c = dbAdapter.fetchAllAccounts();
		startManagingCursor(c);
		
        defaultAccount = (Spinner) findViewById(R.id.defaultAccount);
        defaultAccountAdapter = new  ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        defaultAccountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (c.moveToFirst()) {
            do {
            	defaultAccountAdapter. add(c.getString(c.getColumnIndexOrThrow(Accounts.ADDRESS)));
            	
            	idsAccountsPairs.put(c.getString(c.getColumnIndexOrThrow(Accounts.ADDRESS)),
            				c.getLong(c.getColumnIndexOrThrow(Accounts._ID)));
	        }
	        while (c.moveToNext());
        }
        defaultAccount.setAdapter(defaultAccountAdapter);
 
        newEmailsCheck = (Spinner) findViewById(R.id.newEmailsCheck);
        newEmailsCheckAdapter = ArrayAdapter.createFromResource(
                this, R.array.new_emails_check, android.R.layout.simple_spinner_item);
        newEmailsCheckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEmailsCheck.setAdapter(newEmailsCheckAdapter);
        
        newEmailsNotif = (CheckBox) findViewById(R.id.newEmailsNotif);
      
        Button okButton = (Button) findViewById(R.id.ok);

        populateFields();
       				
        okButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	    saveState();
        	    finish();
        	}
          
        });
        
    }
    
    private void populateFields() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String defaultAccountVal = settings.getString(DEFAULT_ACCOUNT, "BLE");
        Long newEmailsCheckLongVal = settings.getLong(NEW_EMAILS_CHECK, 30L);
        String newEmailsCheckVal;
        if (newEmailsCheckLongVal.equals(60L)) {
        	newEmailsCheckLongVal /= 60;
        	newEmailsCheckVal = newEmailsCheckLongVal + " godzinê";
        }
        else {
        	newEmailsCheckVal = newEmailsCheckLongVal + " minut";
        }
        boolean newEmailsNotifVal = settings.getBoolean(NEW_EMAILS_NOTIFICATION, true);
        defaultAccount.setSelection(defaultAccountAdapter.getPosition(defaultAccountVal));   
        newEmailsCheck.setSelection(newEmailsCheckAdapter.getPosition(newEmailsCheckVal));
        newEmailsNotif.setChecked(newEmailsNotifVal);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveState();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	saveState();
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }
    
    private void saveState() {
    	String newDefaultAccount = defaultAccountAdapter.getItem(
       		 defaultAccount.getSelectedItemPosition()).toString();
    	ContentValues trueValue = new ContentValues();
    	trueValue.put(Accounts.DEFAULT, "true");
    	if (dbAdapter.updateAccount(idsAccountsPairs.get(newDefaultAccount), trueValue)) {
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(DEFAULT_ACCOUNT, newDefaultAccount);
            String tmpVal = newEmailsCheckAdapter.getItem(
           		 newEmailsCheck.getSelectedItemPosition()).toString();
            if ("Nigdy".equals(tmpVal) == false) {
           	 tmpVal = tmpVal.substring(0, tmpVal.indexOf(" "));
                Long checkEmailsPeriod = new Long(tmpVal);
                if (checkEmailsPeriod.equals(1L)) {
               	 checkEmailsPeriod *= 60;
                }
                editor.putLong(NEW_EMAILS_CHECK, checkEmailsPeriod);
            }
            else {
           	 editor.putLong(NEW_EMAILS_CHECK, 0L);
            }
            editor.putBoolean(NEW_EMAILS_NOTIFICATION, 
           		 newEmailsNotif.isChecked() ? true : false);
            editor.commit();
    	}
    	 

    }
    

}
