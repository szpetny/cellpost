package pl.app.cellpost;

import pl.app.cellpost.CellPostInternals.Accounts;
import pl.app.cellpost.CellPostInternals.Emails;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class CellPostMain extends ListActivity implements OnTouchListener{
	private static final String TAG = "CellPostMain";
	
	/**
     * The columns we are interested in from the database for accounts
     */
    private static final String[] PROJECTION_ACCOUNTS = new String[] {
            Accounts._ID, // 0
            Accounts.ADDRESS, // 1
    };
    
    private static final String ACTION_FIRST_USAGE = "pl.app.cellpost.FIRST_USAGE";

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
        
        try {
        	Cursor	cursor = managedQuery(Accounts.CONTENT_URI, PROJECTION_ACCOUNTS, null, null,
        			Accounts.DEFAULT_SORT_ORDER);
        } catch (SQLiteException e) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("There is no configured e-mail accounts. " +
        			" Do you want to configure your e-mail now?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	                startActivity(new Intent(ACTION_FIRST_USAGE));
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                finish();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        	return;
		}
        
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            intent.setAction(Intent.ACTION_PICK);
        }
        if (intent.getData() == null) {
            intent.setData(Emails.CONTENT_URI);
        }
                
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch(position) {
			case 0: System.out.println("Inbox");
					String action = getIntent().getAction();
	        		if (Intent.ACTION_PICK.equals(action))
	        			startActivity(new Intent(this, Inbox.class));
					break;
			case 1: System.out.println("Send e-mail");
					break;
			case 2: System.out.println("Drafts");
					break;
			case 3: System.out.println("Sent");
					break;
			case 4: System.out.println("Settings");
					break;
		}
		
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	
}