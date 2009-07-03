/**
 * 
 */
package pl.app.cellpost;

import pl.app.cellpost.CellPostInternals.Accounts;
import pl.app.cellpost.CellPostInternals.Emails;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;

/**
 * @author stellmal
 *
 */
public class Inbox extends ListActivity {
	private static final String TAG = "Inbox";
	
    // Menu item ids
    public static final int MENU_ITEM_OPEN = Menu.FIRST;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

    /**
     * The columns we are interested in from the database for accounts
     */
    private static final String[] PROJECTION_ACCOUNTS = new String[] {
            Accounts._ID, // 0
            Accounts.ADDRESS, // 1
    };
    
    /**
     * The columns we are interested in from the database for emails
     */
    private static final String[] PROJECTION_EMAILS = new String[] {
            Emails._ID, // 0
            Emails.FROM, // 1
            Emails.SUBJECT, // 2
    };

    /** The index of the title column */
    private static final int COLUMN_INDEX_FROM = 1;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
	        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

	        // If no data was given in the intent (because we were started
	        // as a MAIN activity), then use our default content provider.
	        Intent intent = getIntent();
	        if (intent.getData() == null) {
	            intent.setData(Emails.CONTENT_URI);
	        }

	        // Inform the list we provide context menus for items
	        getListView().setOnCreateContextMenuListener(this);
	        
	        Cursor cursor;
	        try {
	        	cursor = managedQuery(Accounts.CONTENT_URI, PROJECTION_ACCOUNTS, null, null,
	        			Accounts.DEFAULT_SORT_ORDER);
	        } catch (SQLiteException e) {
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("There is no configured e-mail accounts. " +
	        			" Do you want to configure your e-mail now?")
	        	       .setCancelable(false)
	        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	                startActivity(new Intent(getApplicationContext(), CellPostMain.class));
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
	        		
	        
	        
	        // Perform a managed query. The Activity will handle closing and requerying the cursor
	        // when needed.
	        cursor = managedQuery(getIntent().getData(), PROJECTION_EMAILS, null, null,
	                Emails.DEFAULT_SORT_ORDER);

	        // Used to map notes entries from the database to views
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.main_list_look, cursor,
	                new String[] { Emails.SUBJECT }, new int[] { android.R.id.text1 });
	        setListAdapter(adapter);
		 
	}
}
