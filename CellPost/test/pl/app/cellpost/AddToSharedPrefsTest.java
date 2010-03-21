package pl.app.cellpost;

import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import android.util.Log;

public class AddToSharedPrefsTest extends AndroidTestCase {
	
	private static final String TAG = "AddToSharedPrefsTest";
	private static final String PREFS_NAME = "CellPostPrefsFile";
	private static final String UIDL = "_UIDL";
	private static final String MAIL_LISTENER_RUNNING = "MAIL_LISTENER_RUNNING";
	public void  testAndroidTestCaseSetupProperly  () {
		SharedPreferences settings = getContext().getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("test_cellpost@o2.pl" + UIDL, "-1");
        editor.putString("m.stellert@gmail.com" + UIDL, "-1");
        Log.i(TAG, "Test wlasnie dodal spowrotem info o poczatku kont w cellpost");
        editor.putBoolean(MAIL_LISTENER_RUNNING, false);
        Log.i(TAG, "Oraz wylacza flage service MailListener");
        editor.commit();
	}
	
}
