package pl.app.cellpost;

import pl.app.cellpost.CellPostInternals.Emails;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class CellPostMain extends ListActivity implements OnTouchListener{
	private static final String TAG = "CellPostMain";

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
        
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            intent.setAction(Intent.ACTION_PICK);
        }
        if (intent.getData() == null) {
            intent.setData(Emails.CONTENT_URI);
        }
                
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		switch(position) {
			case 0: System.out.println("Inbox");
					String action = getIntent().getAction();
	        		if (Intent.ACTION_PICK.equals(action))
	        			startActivity(new Intent(this, Inbox.class));
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