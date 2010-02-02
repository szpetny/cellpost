/**
 * 
 */
package pl.app.cellpost.activities.accounts;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Malgorzata Stellert
 *
 */
public class AccountsConfig extends Activity {
	private DbAdapter dbAdapter = new DbAdapter(getApplication().getApplicationContext());
	
	
	private static final String ACTION_FIRST_USAGE = "pl.app.cellpost.FIRST_USAGE";
	private static final String ACTION_MAIN_SCREEN = "pl.app.cellpost.MAIN_SCREEN";
	
	// Identifiers for our menu items.
    private static final int ADD_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
		final String action = intent.getAction();
		dbAdapter.open();
		
		if (ACTION_FIRST_USAGE.equals(action)) {	
			addAccount(true);
			/*CharSequence text = "Please press 'Menu > Add' to configure at least one new e-mail account";
			int duration = 1000000;
	
			Toast msg = Toast.makeText(context, text, duration);
			msg.show();*/			
		} 
		

	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);

	        menu.add(0, ADD_ID, 0, R.string.menu_add)
	                    .setShortcut('0', 'a')
	                    .setIcon(android.R.drawable.ic_menu_add);
	        
	        menu.add(0, EDIT_ID, 0, R.string.menu_edit)
            			.setShortcut('1', 'e')
            			.setIcon(android.R.drawable.ic_menu_edit);
	       
	        menu.add(0, DELETE_ID, 0, R.string.menu_delete)
	                        .setShortcut('2', 'd')
	                        .setIcon(android.R.drawable.ic_menu_delete);
	     
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle all of the possible menu actions.
	        switch (item.getItemId()) {
	        case ADD_ID:
	            addAccount(false);
	            break;
	        case EDIT_ID:
	            editAccount();
	            break;
	        case DELETE_ID:
	            deleteAccount();
	            break;
	        }
	        return super.onOptionsItemSelected(item);
	    }

		private void deleteAccount() {
			// TODO Auto-generated method stub
			
		}

		private void editAccount() {
			// TODO Auto-generated method stub
			
		}

		private void addAccount(final boolean firstTimeFlag) {
			
			setContentView(R.layout.account_config_look);
			EditText address = (EditText) findViewById(R.id.address);
			final Editable addressVal = address.getText();
			
			Button okButton = (Button) findViewById(R.id.ok);
			Button cancelButton = (Button) findViewById(R.id.cancel);
			okButton.setOnClickListener(new OnClickListener() {
				  public void onClick(View v) {
					ContentValues accountData = new ContentValues();
					accountData.put(Accounts.ADDRESS, addressVal.toString());
					if (firstTimeFlag == false) {
						dbAdapter.checkUnique(addressVal.toString());
					}
					if (dbAdapter.createAccount(accountData) != -1) {
						startActivity(new Intent(ACTION_MAIN_SCREEN));
					}
					else {
					  Log.i("Failure", "Failed to save data!");
					}
				  }
									  
			});
			cancelButton.setOnClickListener(new OnClickListener() {
				  public void onClick(View v) {
					finish();
						  
				  }
			});
		}
}
