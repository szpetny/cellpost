package pl.app.cellpost.activities.main;

import pl.app.cellpost.R;
import pl.app.cellpost.activities.accounts.AccountsList;
import pl.app.cellpost.activities.emails.Drafts;
import pl.app.cellpost.activities.emails.Inbox;
import pl.app.cellpost.activities.emails.MailSender;
import pl.app.cellpost.activities.emails.Sent;
import pl.app.cellpost.activities.settings.Settings;
import pl.app.cellpost.common.DbAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class CellPostMain extends ListActivity {
	private static final String ACTION_ADD_ACCOUNT = "pl.app.cellpost.ADD_ACCOUNT";
	private DbAdapter dbAdapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
        if (dbAdapter == null)
        	dbAdapter = new DbAdapter(this);      
        Cursor	cursor = dbAdapter.fetchAllAccounts();
        startManagingCursor(cursor);
        if (!cursor.moveToFirst()) {
        	new AlertDialog.Builder(this).setMessage("There is no configured e-mail account. " +
        			" Do you want to configure your e-mail now?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", 
        	    	   new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   startActivity(new Intent(ACTION_ADD_ACCOUNT));
	        	               dialog.cancel();
	        	                
	        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                finish();
        	           }
        	       }).show();

        }        
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch(position) {
			case 0: startActivity(new Intent(this,Inbox.class));
					break;
			case 1: startActivity(new Intent(this, MailSender.class));
					break;
			case 2: startActivity(new Intent(this, Drafts.class));
					break;
			case 3: startActivity(new Intent(this, Sent.class));
					break;
			case 4: startActivity(new Intent(this, Settings.class));
					break;
		}
		
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }

}