package pl.app.cellpost.activities.settings;

import pl.app.cellpost.R;
import pl.app.cellpost.activities.accounts.AccountsList;
import pl.app.cellpost.common.DbAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class Settings extends ListActivity {
	private DbAdapter dbAdapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.settings_list_look);
        if (dbAdapter == null)
        	dbAdapter = new DbAdapter(this);      
    
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch(position) {
			case 0: startActivity(new Intent(this,AccountsList.class));
					break;
			case 1: startActivity(new Intent(this, UserPreferences.class));
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