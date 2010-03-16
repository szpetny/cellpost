package pl.app.cellpost;

import pl.app.cellpost.common.DbAdapter;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

public class FetchAllAccountsTest extends AndroidTestCase {
	private final String TAG = "TESTING FetchAllAccountsTest";
	public void  testAndroidTestCaseSetupProperly  () {
		DbAdapter da = new DbAdapter(getContext().getApplicationContext());
		Cursor c = da.fetchAllAccounts();
		if (c != null) {
			for (int i = 0; i < c.getColumnNames().length; i++)
				Log.i(TAG, c.getColumnNames()[i]);
			
		}
			Log.i(TAG, "FetchAllAccountsTest = TEST PASSED!!!!!!!!!!!!!!!!");
		c.close();
		da.close();
	}
	
}
