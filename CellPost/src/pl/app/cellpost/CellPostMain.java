package pl.app.cellpost;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class CellPostMain extends ListActivity implements OnTouchListener{
	private static final String TAG = "CellPostMain";
    
    private static final String ACTION_FIRST_USAGE = "pl.app.cellpost.FIRST_USAGE";

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        setContentView(R.layout.main_list_look);
        DbAdapter da = new DbAdapter(this);      
        da.open();
        Cursor	cursor = da.fetchAllAccounts();
        if (cursor != null && cursor.getCount() == 0) {
        	new AlertDialog.Builder(this).setMessage("There is no configured e-mail account. " +
        			" Do you want to configure your e-mail now?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", 
        	    	   new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   startActivity(new Intent(ACTION_FIRST_USAGE));
	        	                //dialog.cancel();
	        	                
	        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                finish();
        	           }
        	       }).show();

        }
        
        Intent intent = getIntent();
        if (intent.getAction() == null) {
        	Log.i("Intent", "wow intent " + intent);
            intent.setAction(Intent.ACTION_PICK);
        }
        if (intent.getData() == null) {
   //         intent.setData(Emails.CONTENT_URI);
        }
                
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
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
			case 3: System.out.println("Sent");
					break;
			case 4: System.out.println("Settings");
					break;
		}
		
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}