/**
 * 
 */
package pl.app.cellpost;

import pl.app.cellpost.CellPostInternals.Emails;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
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
	        
	        // Perform a managed query. The Activity will handle closing and requerying the cursor
	        // when needed.
	        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null,
	                Emails.DEFAULT_SORT_ORDER);

	        // Used to map notes entries from the database to views
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.main_list_look, cursor,
	                new String[] { Emails.SUBJECT }, new int[] { android.R.id.text1 });
	        setListAdapter(adapter);
		 
	}
}
