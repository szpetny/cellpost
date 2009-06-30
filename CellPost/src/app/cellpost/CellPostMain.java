package app.cellpost;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class CellPostMain extends ListActivity {
	private static final String TAG = "CellPostMain";
	
	// Menu item id
    public static final int MENU_ITEM_SELECT = Menu.FIRST;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        setContentView(R.layout.main_list_look);

        ArrayList<HashMap<String, String>> lMainMenu = new ArrayList<HashMap<String, String>>();
        lMainMenu.get(0).put("0", "Inbox");
        lMainMenu.get(1).put("1", "Send e-mail");
        lMainMenu.get(2).put("2", "Drafts");
        lMainMenu.get(3).put("3", "Settings");

        ListAdapter adapter = new SimpleAdapter(this, 
        										lMainMenu,
        										R.layout.main_list_look,
        										new String[] {"0", "1", "2", "3"}, 
        										new int[] { R.id.text1});

        setListAdapter(adapter);
    }

}