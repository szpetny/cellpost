package pl.app.cellpost;

import android.test.AndroidTestCase;
import android.util.Log;

public class DeleteAccountTest extends AndroidTestCase {
	DbAdapter da = new DbAdapter(getContext().getApplicationContext());
	
	public void  testAndroidTestCaseSetupProperly  () {
		da.open();
		if (da.deleteAllAccounts())
			Log.i("TESTING", "TEST PASSED!!!!!!!!!!!!!!!!");
		da.close();
	}
	
}
