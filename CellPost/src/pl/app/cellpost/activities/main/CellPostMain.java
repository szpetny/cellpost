package pl.app.cellpost.activities.main;

import pl.app.cellpost.R;
import pl.app.cellpost.activities.accounts.AccountsList;
import pl.app.cellpost.activities.mails.MailSender;
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

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
        DbAdapter da = new DbAdapter(this);      
        da.open();
        Cursor	cursor = da.fetchAllAccounts();
        if (cursor != null) {
        	startManagingCursor(cursor);
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
			case 0: System.out.println("Inbox");
					break;
			case 1: startActivity(new Intent(this, MailSender.class));
					break;
			case 2: System.out.println("Drafts");
					break;
			case 3: System.out.println("Sent");
					break;
			case 4: startActivity(new Intent(this, AccountsList.class));
					break;
		}
		
	}

	
}