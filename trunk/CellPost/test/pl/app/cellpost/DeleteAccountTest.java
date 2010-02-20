package pl.app.cellpost;

import pl.app.cellpost.common.DbAdapter;
import android.test.AndroidTestCase;
import android.util.Log;

public class DeleteAccountTest extends AndroidTestCase {
	
	private static final String TAG = "DeleteAccountTest";
	
	public void  testAndroidTestCaseSetupProperly  () {
		DbAdapter da = new DbAdapter(getContext().getApplicationContext());
		//da.open();
		if (da.deleteAllAccounts())
			Log.i(TAG, "TEST PASSED!!!!!!!!!!!!!!!!");
		da.close();
	}
	
}
