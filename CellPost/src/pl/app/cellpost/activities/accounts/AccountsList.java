/**
 * 
 */
package pl.app.cellpost.activities.accounts;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
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
public class AccountsList extends ListActivity {
		
		private static final String TAG = "AccountsList";
		
		private DbAdapter dbAdapter;
		
		// Ids for menus items.
	    private static final int ADD_ID = Menu.FIRST;
	    private static final int EDIT_ID = Menu.FIRST + 1;
	    private static final int DELETE_ID = Menu.FIRST + 2;
	   
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.accounts_list_look);
			listAccounts();
	
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		     super.onCreateOptionsMenu(menu);
		     menu.add(0, ADD_ID, 0, R.string.menu_add)
		         .setShortcut('0', 'a')
		         .setIcon(android.R.drawable.ic_menu_add);	     
		        return true;
		}
	
		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
		     switch(item.getItemId()) {
		        case ADD_ID:
		        	addAccount();
		            return true;
		     }
		     return super.onMenuItemSelected(featureId, item);
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
				editAccount(info.id);
		        return true;
	    	case DELETE_ID:
	    		info = (AdapterContextMenuInfo) item.getMenuInfo();
	    		deleteAccount(info.id);
		        return true;
			}
			return super.onContextItemSelected(item);
		}
	
	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        editAccount(id);
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        listAccounts();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	super.onDestroy();
	    	if (dbAdapter != null) {
	    		dbAdapter.close();
	    		dbAdapter = null;
	    	}
	    }
	    
		private void deleteAccount(long accountId) {
			DbAdapter dbAdapter = new DbAdapter(getApplication().getApplicationContext());
			if (dbAdapter.deleteAccount(accountId)) {
				listAccounts();
			}
			else {
				Log.e(TAG, "Operation of deleting account failed!");
			}
			
		}

		private void editAccount(long accountId) {
			Intent intent = new Intent(this, AccountConfig.class);
	        intent.putExtra(Accounts._ID, accountId);
	        startActivity(intent);
			
		}

		private void addAccount() {
			startActivity(new Intent(this, AccountConfig.class));
		}
		
		private void listAccounts() {
			if (dbAdapter == null)
			    dbAdapter = new DbAdapter(this);
				Cursor c = dbAdapter.fetchAllAccounts();
				startManagingCursor(c);
				ListAdapter adapter = new  SimpleCursorAdapter (getApplication().getApplicationContext(), R.layout.account_single_row, 
						c, new String[]{Accounts.ADDRESS}, new int []{R.id.accountRow});     
				setListAdapter(adapter);
				registerForContextMenu(getListView());
		}
		
}
