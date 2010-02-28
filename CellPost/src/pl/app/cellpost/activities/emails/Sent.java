/**
 * 
 */
package pl.app.cellpost.activities.emails;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Emails;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author Malgorzata Stellert
 *
 */
public class Sent extends ListActivity {
		
		private static final String TAG = "Drafts";
		
		private DbAdapter dbAdapter;
		
		// Ids for menus items.
	    private static final int EDIT_ID = Menu.FIRST;
	    private static final int DELETE_ID = Menu.FIRST + 1;
	   
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.list_look);
			listSent();
	
		}
		
	    @Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	        menu.add(1, DELETE_ID, 1, R.string.menu_delete);
		}

	    @Override
		public boolean onContextItemSelected(MenuItem item) {
	    	AdapterContextMenuInfo info; 
			switch(item.getItemId()) {
			case EDIT_ID:		
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				editSent(info.id);
		        return true;
	    	case DELETE_ID:
	    		info = (AdapterContextMenuInfo) item.getMenuInfo();
	    		deleteSent(info.id);
		        return true;
			}
			return super.onContextItemSelected(item);
		}
	
	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        editSent(id);
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        listSent();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	super.onDestroy();
	    	if (dbAdapter != null) {
	    		dbAdapter.close();
	    		dbAdapter = null;
	    	}
	    }
	    
		private void deleteSent(long accountId) {
			DbAdapter dbAdapter = new DbAdapter(getApplication().getApplicationContext());
			if (dbAdapter.deleteEmail(accountId)) {
				listSent();
			}
			else {
				Log.e(TAG, "Operation of deleting a sent email failed!");
			}
			
		}

		private void editSent(long accountId) {
			Intent intent = new Intent(this, MailSender.class);
	        intent.putExtra(Emails._ID, accountId);
	        startActivity(intent);
			
		}
		
		private void listSent() {
			if (dbAdapter == null)
			    dbAdapter = new DbAdapter(this);
				Cursor c = dbAdapter.fetchAllSent();
				startManagingCursor(c);
				if (c.moveToFirst()) {
					ListAdapter adapter = new  SimpleCursorAdapter (getApplication().getApplicationContext(), R.layout.list_single_row, 
							c, new String[]{Emails.ADDRESSEE}, new int []{R.id.singleRow});     
					setListAdapter(adapter);
					registerForContextMenu(getListView());
				}
				else {
					Log.e(TAG, "The database crash or sth...");
				}
		}
		
}
