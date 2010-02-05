package pl.app.cellpost;

import pl.app.cellpost.common.DbAdapter;
import android.test.AndroidTestCase;
import android.util.Log;

public class DeleteAccountTest extends AndroidTestCase {
	
	public void  testAndroidTestCaseSetupProperly  () {
		DbAdapter da = new DbAdapter(getContext().getApplicationContext());
		da.open();
		if (da.deleteAllAccounts())
			Log.i("TESTING", "TEST PASSED!!!!!!!!!!!!!!!!");
		da.close();
	}
	
}
