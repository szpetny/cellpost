package app.cellpost;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class CellPostMain extends ListActivity implements OnTouchListener{
	private static final String TAG = "CellPostMain";
	
	// Menu item id
    public static final int MENU_ITEM_SELECT = Menu.FIRST;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
                
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch(position) {
			case 0: System.out.println("Inbox");
					break;
			case 1: System.out.println("Send e-mail");
					break;
			case 2: System.out.println("Drafts");
					break;
			case 3: System.out.println("Settings");
					break;
		}
		
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	
}