/**
 * 
 */
package pl.app.cellpost.activities.emails;

import java.util.Observable;
import java.util.Observer;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.services.MailListener;
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
 * @author stellmal
 *
 */
public class Inbox extends ListActivity implements Observer{
	private static final String TAG = "Inbox";
	private static final String REPLY = "REPLY";
	private static final String FORWARD = "FORWARD";
	
    private DbAdapter dbAdapter;
    private static SimpleCursorAdapter adapter = null;
    private Cursor c;
    
    // Menu item ids
    public static final int REPLY_ID = Menu.FIRST;
    public static final int FORWARD_ID = Menu.FIRST + 1;
    public static final int DELETE_ID = Menu.FIRST + 2;
    private static final int REFRESH_ID = Menu.FIRST + 3;
    
	private static final int NOTIFICATION_ID = 666;
	
	
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, REPLY_ID, 0, R.string.menu_reply);
        menu.add(1, FORWARD_ID, 1, R.string.menu_forward);
        menu.add(1, DELETE_ID, 1, R.string.menu_delete);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info; 
		switch(item.getItemId()) {
		case REPLY_ID:		
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			replyTo(info.id);
	        return true;
		case FORWARD_ID:		
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			forwardTo(info.id);
	        return true;
    	case DELETE_ID:
    		info = (AdapterContextMenuInfo) item.getMenuInfo();
    		deleteEmail(info.id);
	        return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        replyTo(id);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState); 
		 if (dbAdapter == null)
	        	dbAdapter = new DbAdapter(this);
		 MailListener.setMainActivity(this);
		 startService(new Intent(this, MailListener.class));
	     setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
	     if (MailListener.mNotificationManager != null)
	    	 MailListener.mNotificationManager.cancel(NOTIFICATION_ID); 
	     listEmails();
		 
	}
	    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	     super.onCreateOptionsMenu(menu);
	     menu.add(0, REFRESH_ID, 0, R.string.menu_refresh)
	         .setIcon(android.R.drawable.ic_menu_upload);	 
	     return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	     switch(item.getItemId()) {
	        case REFRESH_ID:
	        	refreshEmails();
	            return true;
	     }
	     return super.onMenuItemSelected(featureId, item);
	}

    private void deleteEmail(long id) {
		if (dbAdapter.deleteEmail(id)) {
			listEmails();
		}
		else {
			Log.e(TAG, "Operation of deleting account failed!");
		}
		
	}

	private void listEmails() {
		c = dbAdapter.fetchAllEmailsReceived();
		startManagingCursor(c);
		if (c.moveToFirst()) {
			adapter = new SimpleCursorAdapter(this, R.layout.list_single_3lines_row, c,
						new String[] {Emails.RECEIVE_DATE, Emails.SENDER, Emails.SUBJECT}, 
						new int[] {R.id.firstValue, R.id.secondValue, R.id.thirdValue});
			setListAdapter((ListAdapter)adapter);
			registerForContextMenu(getListView());
		}
		
	}
	
	private void refreshEmails() {
		c.requery();
		adapter.notifyDataSetChanged();
		setListAdapter((ListAdapter)adapter);
		registerForContextMenu(getListView());
	}

	private void forwardTo(long id) {
		Intent intent = new Intent(this, MailSender.class);
        intent.putExtra(Emails._ID, id);
        intent.putExtra(FORWARD, FORWARD);
        startActivity(intent);
	}

	private void replyTo(long id) {
		Intent intent = new Intent(this, MailSender.class);
        intent.putExtra(Emails._ID, id);
        intent.putExtra(REPLY, REPLY);
        startActivity(intent);	
	}

	public void update(Observable observable, Object object) {
		refreshEmails();
	}
}
