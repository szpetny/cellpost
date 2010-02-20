package pl.app.cellpost.activities.accounts;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AccountConfig extends Activity {

	private static final String TAG = "AccountConfig";
	
    private EditText addressText;
    private EditText userText;
    private EditText passText;
    private Spinner accountTypeOption;
    private EditText incomingServerText;
    private EditText incomingPortText;
    private Spinner incomingSecurityOption;
    private EditText outgoingServerText;
    private EditText outgoingPortText;
    private Spinner outgoingSecurityOption;
    private Spinner deleteEmailsOption;
    private Long accountId;
    private DbAdapter dbAdapter;
    private ArrayAdapter<CharSequence> inSecurityAdapter;
    private ArrayAdapter<CharSequence> outSecurityAdapter;
    private ArrayAdapter<CharSequence> deleteEmailsAdapter;
    private ArrayAdapter<CharSequence> accountTypeAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dbAdapter == null)
        	dbAdapter = new DbAdapter(this);

        setContentView(R.layout.account_config_look);
        
        addressText = (EditText) findViewById(R.id.address);
        userText = (EditText) findViewById(R.id.user);
        passText = (EditText) findViewById(R.id.pass);
        
        accountTypeOption = (Spinner) findViewById(R.id.accountType);
        accountTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.account_conf_account_types, android.R.layout.simple_spinner_item);
        accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeOption.setAdapter(accountTypeAdapter);
        
        incomingServerText = (EditText) findViewById(R.id.incomingServer);
        incomingPortText = (EditText) findViewById(R.id.incomingPort);
        
        incomingSecurityOption = (Spinner) findViewById(R.id.incomingSecurity);
        inSecurityAdapter = ArrayAdapter.createFromResource(
                this, R.array.account_conf_security_options, android.R.layout.simple_spinner_item);
        inSecurityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomingSecurityOption.setAdapter(inSecurityAdapter);
        
        outgoingServerText = (EditText) findViewById(R.id.outgoingServer);
        outgoingPortText = (EditText) findViewById(R.id.outgoingPort);
        
        outgoingSecurityOption = (Spinner) findViewById(R.id.outgoingSecurity);
        outSecurityAdapter = ArrayAdapter.createFromResource(
                this, R.array.account_conf_security_options, android.R.layout.simple_spinner_item);
        outSecurityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outgoingSecurityOption.setAdapter(outSecurityAdapter);
        
        deleteEmailsOption = (Spinner) findViewById(R.id.deleteEmails);
        deleteEmailsAdapter = ArrayAdapter.createFromResource(
                this, R.array.delete_from_serv_options, android.R.layout.simple_spinner_item);
        deleteEmailsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteEmailsOption.setAdapter(deleteEmailsAdapter);
      
        Button okButton = (Button) findViewById(R.id.ok);
        Button cancelButton = (Button) findViewById(R.id.cancel);   
       
        accountId = savedInstanceState != null ? savedInstanceState.getLong(Accounts._ID) : null;
		if (accountId == null) {
			Bundle extras = getIntent().getExtras();            
			accountId = extras != null ? extras.getLong(Accounts._ID) : null;
		}

        populateFields();
       				
        okButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	    setResult(RESULT_OK);
        	    finish();
        	}
          
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        	    finish();
        	}
          
        });
    }
    
    private void populateFields() {
        if (accountId != null) {
            Cursor cursor = dbAdapter.fetchAccount(accountId);
            startManagingCursor(cursor);
            addressText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.ADDRESS)));
            userText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.USER)));
            //passText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.PASS)));
            accountTypeOption.setSelection(accountTypeAdapter.getPosition(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.ACCOUNT_TYPE))));
            incomingServerText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.INCOMING_SERVER)));
            incomingPortText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.INCOMING_PORT)));
            incomingSecurityOption.setSelection(inSecurityAdapter.getPosition(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.INCOMING_SECURITY))));
            outgoingServerText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.OUTGOING_SERVER)));
            outgoingPortText.setText(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.OUTGOING_PORT)));
            outgoingSecurityOption.setSelection(outSecurityAdapter.getPosition(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.OUTGOING_SECURITY))));
            deleteEmailsOption.setSelection(deleteEmailsAdapter.getPosition(cursor.getString(cursor.getColumnIndexOrThrow(Accounts.DELETE_EMAILS))));
            
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (accountId != null)
        	savedInstanceState.putLong(Accounts._ID, accountId);
        savedInstanceState.putString(Accounts.ADDRESS, addressText.getText().toString());
        savedInstanceState.putString(Accounts.USER, userText.getText().toString());
        savedInstanceState.putString(Accounts.INCOMING_SERVER, incomingServerText.getText().toString());
        savedInstanceState.putString(Accounts.INCOMING_PORT, incomingPortText.getText().toString());
        savedInstanceState.putInt(Accounts.INCOMING_SECURITY, incomingSecurityOption.getSelectedItemPosition());
        savedInstanceState.putString(Accounts.OUTGOING_SERVER, outgoingServerText.getText().toString());
        savedInstanceState.putString(Accounts.OUTGOING_PORT, outgoingPortText.getText().toString());
        savedInstanceState.putInt(Accounts.OUTGOING_SECURITY, outgoingSecurityOption.getSelectedItemPosition());
        savedInstanceState.putInt(Accounts.DELETE_EMAILS, deleteEmailsOption.getSelectedItemPosition());
        String password = null;
		if (passText.getText() != null && "".equals(passText.getText()) == false)
			password = passText.getText().toString();	
        savedInstanceState.putString(Accounts.PASS, szyfrPassword(password));
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
    	ContentValues accountData = new ContentValues();
		accountData.put(Accounts.ADDRESS, addressText.getText().toString());
		accountData.put(Accounts.USER, userText.getText().toString());
		String password = null;
		if (passText.getText() != null && "".equals(passText.getText()) == false)
			password = passText.getText().toString();	
		accountData.put(Accounts.PASS, szyfrPassword(password));
		accountData.put(Accounts.INCOMING_SERVER, incomingServerText.getText().toString());
		accountData.put(Accounts.INCOMING_PORT, incomingPortText.getText().toString());
		accountData.put(Accounts.INCOMING_SECURITY, inSecurityAdapter.getItem(incomingSecurityOption.getSelectedItemPosition()).toString());
		accountData.put(Accounts.OUTGOING_SERVER, outgoingServerText.getText().toString());
		accountData.put(Accounts.OUTGOING_PORT, outgoingPortText.getText().toString());
		accountData.put(Accounts.OUTGOING_SECURITY, outSecurityAdapter.getItem(outgoingSecurityOption.getSelectedItemPosition()).toString());
		accountData.put(Accounts.ACCOUNT_TYPE, accountTypeAdapter.getItem(accountTypeOption.getSelectedItemPosition()).toString());
		accountData.put(Accounts.DELETE_EMAILS, deleteEmailsAdapter.getItem(deleteEmailsOption.getSelectedItemPosition()).toString());

        if (accountId == null) {
            long id = dbAdapter.createAccount(accountData);
            if (id > 0) {
            	accountId = id;
            }
        } else {
            dbAdapter.updateAccount(accountId, accountData);
        }
    }
    
    private String szyfrPassword (String passwordToSzyfr) {
    	String password = passwordToSzyfr;
    	
		MessageDigest m;
		String shyfrpassword = null;
		try {
			m = MessageDigest.getInstance( "MD5" );
			m.update( password.getBytes(), 0, password.length() );
			shyfrpassword = new BigInteger( 1, m.digest() ).toString( 16 );
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "Error during szyfrowanie hasla! " + e);
		}	
		return shyfrpassword;
    }
    
}
