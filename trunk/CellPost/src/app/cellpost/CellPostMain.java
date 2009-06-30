package app.cellpost;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class CellPostMain extends ListActivity {
	private static final String TAG = "CellPostMain";
	
	// Menu item id
    public static final int MENU_ITEM_SELECT = Menu.FIRST;
	
    public final static String[] MAIN_MENU = { "Inbox",
		   "Send e-mail",
		   "Drafts",
		   "Settings" };
    
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setListAdapter(new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, MAIN_MENU));
        getListView().setTextFilterEnabled(true);

    }

}