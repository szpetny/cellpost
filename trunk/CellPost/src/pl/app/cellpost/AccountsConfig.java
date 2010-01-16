/**
 * 
 */
package pl.app.cellpost;

import pl.app.cellpost.CellPostInternals.Accounts;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author stellmal
 *
 */
public class AccountsConfig extends Activity {
	
	private static final String ACTION_FIRST_USAGE = "pl.app.cellpost.FIRST_USAGE";
	private Uri mUri;
	
	// Identifiers for our menu items.
    private static final int ADD_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;

	private static final int GET_ACCOUNT_TYPE = 0;
	
	private static final String POP3_CONFIG = "POP3";
	private static final String IMAP_CONFIG = "IMAP";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Intent intent = getIntent();
		final String action = intent.getAction();

		if (ACTION_FIRST_USAGE.equals(action)) {
		Context context = getApplicationContext();
		CharSequence text = "Please press Menu > Add to configure at least one new e-mail account";
		int duration = 30000;

		Toast msg = Toast.makeText(context, text, duration);
		msg.show();
			
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
	            addAccount();
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

		private void addAccount() {
			startActivityForResult(new Intent(this, AccountChoice.class), GET_ACCOUNT_TYPE);
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == GET_ACCOUNT_TYPE) {
				String action = data.getAction();
				
				if (POP3_CONFIG.equals(action)) {
					setContentView(R.layout.pop3_account_config_look);
				}
				else if (IMAP_CONFIG.equals(action)) {
					setContentView(R.layout.imap_account_config_look);
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        	builder.setMessage("Oooops! Something wrong's just happened")
		        	       .setCancelable(false)
		        	       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	                finish();
		        	           }
		        	       });
		        	AlertDialog alert = builder.create();
		        	alert.show();
		        	return;
				}
				
				EditText address = (EditText) findViewById(R.id.address);
				final Editable addressVal = address.getText();
				
				Button okButton = (Button) findViewById(R.id.ok);
				okButton.setOnClickListener(new OnClickListener() {
					  public void onClick(View v) {
						  DataProvider dp = new DataProvider();
						  if(dp.checkUnique(Accounts.CONTENT_URI, 
								  new String[] {Accounts._ID, Accounts.ADDRESS}, addressVal.toString()))
							  insertAccount();
					  }

					private void insertAccount() {
						// TODO Auto-generated method stub
						
					}
					});
			}
		}
}
