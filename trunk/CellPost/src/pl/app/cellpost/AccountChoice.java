/**
 * 
 */
package pl.app.cellpost;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * @author stellmal
 *
 */
public class AccountChoice extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.account_type_choice);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			setResult(RESULT_OK, (new Intent()).setAction("POP3"));
			break;
			
		case 1:
			setResult(RESULT_OK, (new Intent()).setAction("IMAP"));
			break;

		default:
			break;
		}
		
		finish();
		
	}
}
