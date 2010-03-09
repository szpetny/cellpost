/**
 * 
 */
package pl.app.cellpost.activities.emails;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.services.MailListener;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class Inbox extends ListActivity {
	private static final String TAG = "Inbox";
	private static final String PREFS_NAME = "CellPostPrefsFile";
	private static final String MAIL_LISTENER_RUNNING = "MAIL_LISTENER_RUNNING";
	private static final String REPLY = "REPLY";
	private static final String FORWARD = "FORWARD";
	
    private DbAdapter dbAdapter;
    
    // Menu item ids
    public static final int REPLY_ID = Menu.FIRST;
    public static final int FORWARD_ID = Menu.FIRST + 1;
    public static final int DELETE_ID = Menu.FIRST + 2;
    
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
		 if (isServiceRunning() == false) 
			 setServiceRunning();
		 
		 if (dbAdapter == null)
	        	dbAdapter = new DbAdapter(this);		
	     setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
	     listEmails();
		 
	}
	
	@Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceRunning() == false) 
			 setServiceRunning();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (isServiceRunning()) 
			 setServiceNotRunning();
    	if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
    }
    
    private boolean isServiceRunning() {
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	boolean isServiceRunning = settings.getBoolean(MAIL_LISTENER_RUNNING, false);
    	return isServiceRunning;
    }
    
    private void setServiceRunning() {
    	startService(new Intent(this, MailListener.class));
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MAIL_LISTENER_RUNNING, true);
        editor.commit();
    }
    
    private void setServiceNotRunning() {
    	stopService(new Intent(this, MailListener.class));
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MAIL_LISTENER_RUNNING, false);
        editor.commit();
        Log.i(TAG, "SERVICE MAILLISTENER STOP");
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
		Cursor c = dbAdapter.fetchAllEmailsReceived();
		startManagingCursor(c);
		if (c.moveToFirst()) {
		    ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_single_2lines_row, c,
		                new String[] {Emails.SENDER, Emails.SUBJECT}, new int[] {R.id.firstValue, R.id.secondValue});
			setListAdapter(adapter);
			registerForContextMenu(getListView());
		}
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
}
