/**
 * 
 */
package pl.app.cellpost;

/**
 * @author szpetny
 *
 */
import java.util.HashMap;

import pl.app.cellpost.CellPostInternals.Accounts;
import pl.app.cellpost.CellPostInternals.Emails;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

    private static final String DATABASE_NAME = "cellpost.db";
    private static final int DATABASE_VERSION = 3;
    private static final String ACCOUNTS_TABLE_NAME = "accounts";
    private static final String EMAILS_TABLE_NAME = "emails";

    private static HashMap<String, String> sAccountsProjectionMap;
    private static HashMap<String, String> sEmailsProjectionMap;

    private static final String ACCOUNTS = "";
    private static final String ACCOUNT_ID = "_ID";
    private static final String ACCOUNT_NAME = "address";
    private static final String EMAILS = "";
    private static final String EMAIL_ID = "_ID";
    private static final String EMAIL_TITLE = "SUBJECT";


	private static final String TAG = "DbAdapter";
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private final Context ctx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ACCOUNTS_TABLE_NAME + " ("
                    + Accounts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Accounts.ADDRESS + " TEXT,"
                    + Accounts.USER + " TEXT,"
                    + Accounts.PASS + " TEXT,"
                    + Accounts.SERVER + " TEXT,"
                    + Accounts.PORT + " INTEGER,"
                    + Accounts.ACCOUNT_TYPE + " TEXT,"
                    + Accounts.SECURITY + " TEXT,"
                    + Accounts.IMAP_PATH_PREF + " TEXT"  
                    + Accounts.DELETE_EMAILS + " TEXT"
                    + "); " 
                    + "CREATE TABLE " + EMAILS_TABLE_NAME + " ("
                    + Emails._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Emails.FROM + " TEXT,"
                    + Emails.TO + " TEXT,"
                    + Emails.CC + " TEXT,"
                    + Emails.BCC + " TEXT,"
                    + Emails.SUBJECT + " TEXT,"
                    + Emails.CONTENTS + " TEXT,"
                    + Emails.ATTACHMENT + " BLOB,"
                    + Emails.EMAIL_TYPE + " TEXT,"
                    + Emails.EMAIL_CREATED_DATE + " INTEGER,"
                    + Emails.EMAIL_MODIFIED_DATE + " INTEGER,"
                    + Emails.EMAIL_DELIVERED_DATE + " INTEGER,"
                    + Emails.WHICH_ACCOUNT + " TEXT "
                    + "CONSTRAINT FOREIGN KEY (" + Emails.WHICH_ACCOUNT + ") "
                    + "REFERENCES " + ACCOUNTS_TABLE_NAME + "(" + Accounts._ID + ") "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS accounts; "
            		  + "DROP TABLE IF EXISTS emails");
            onCreate(db);
        }
	}

	/**
	* Constructor - takes the context to allow the database to be
	* opened/created
	*
	* @param ctx the Context within which to work
	*/
	public DbAdapter(Context ctx) {
		this.ctx = ctx;
	}

	/**
	* Open the database. If it cannot be opened, try to create a new
	* instance of the database. If it cannot be created, throw an exception to
	* signal the failure
	*
	* @return this (self reference, allowing this to be chained in an
	* initialization call)
	* @throws SQLException if the database could be neither opened or created
	*/
	public DbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(ctx);
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}


	/**
	* Create a new account using the values provided in ContentValues. If the account is
	* successfully created return the new accountId for that account, otherwise return
	* a -1 to indicate failure.
	*
	* @param ContentValues containing account parameters
	* @return accountId or -1 if failed
	*/
	public long createAccount(ContentValues accountParams) {
		return db.insert(ACCOUNTS_TABLE_NAME, null, accountParams);
	}

	/**
	* Delete the account settings with the given accountId
	*
	* @param accountId id of account settings to delete
	* @return true if deleted, false otherwise
	*/
	public boolean deleteAccount(long accountId) {	
		Log.i("Delete called", "value__" + accountId);
		return db.delete(ACCOUNTS_TABLE_NAME, ACCOUNT_ID + "=" + accountId, null) > 0;
	}
	
	/**
	* Delete the all accounts settings from database. Useful during testing.
	*
	* @return true if deleted, false otherwise
	*/
	public boolean deleteAllAccounts() {	
		return db.delete(ACCOUNTS_TABLE_NAME, ACCOUNT_ID + "> 0", null) > 0;
	}

	/**
	* Return a Cursor over the list of all accounts in the database
	*
	* @return Cursor over all accounts
	*/
	public Cursor fetchAllAccounts() {
		return db.query(ACCOUNTS_TABLE_NAME, new String[] {ACCOUNT_ID, ACCOUNT_NAME}, 
				null, null, null, null, null);
	}

	/**
	* Return a Cursor positioned at the account that matches the given accountId
	*
	* @param accountId id of account to retrieve
	* @return Cursor positioned to matching account, if found
	* @throws SQLException if account could not be found/retrieved
	*/
	public Cursor fetchAccount(long accountId) throws SQLException {
	
		Cursor cursor = db.query(true, ACCOUNTS_TABLE_NAME, new String[] {ACCOUNT_ID,
				ACCOUNT_NAME}, ACCOUNT_ID + "=" + accountId, 
				null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;	
	}

	/**
	* Update the account settings using the details provided. The account to be updated is
	* specified using the accountId, and it is altered to use the values passed in
	*
	* @param accountId id of account to update
	* @param accountParams to set new account parameters 
	* @return true if the account was successfully updated, false otherwise
	*/
	public boolean updateAccount(long accountId, ContentValues accountParams) {
		return db. update(ACCOUNTS_TABLE_NAME, accountParams, ACCOUNT_ID + "=" + accountId, null) > 0;
	}
	
	/**
	* Checks uniqueness of given account
	* 
	* @param accountName the value which uniqueness needs to be checked
	* @return true if the value is unique, false otherwise
	*/
	public boolean checkUnique(String accountName) {
		
		if (db.query(ACCOUNTS_TABLE_NAME, new String[] {ACCOUNT_NAME}, ACCOUNT_NAME + "=" + accountName, 
				null, null, null, null, null)== null)
			return true;
		else
			return false;
	}
	
	
	static {
	    sAccountsProjectionMap = new HashMap<String, String>();
	    sAccountsProjectionMap.put(Accounts._ID, Accounts._ID);
	    sAccountsProjectionMap.put(Accounts.ADDRESS, Accounts.ADDRESS);
	    sAccountsProjectionMap.put(Accounts.USER, Accounts.USER);
	    sAccountsProjectionMap.put(Accounts.PASS, Accounts.PASS);
	    sAccountsProjectionMap.put(Accounts.SERVER, Accounts.SERVER);
	    sAccountsProjectionMap.put(Accounts.PORT, Accounts.PORT);
	    sAccountsProjectionMap.put(Accounts.ACCOUNT_TYPE, Accounts.ACCOUNT_TYPE);
	    sAccountsProjectionMap.put(Accounts.SECURITY, Accounts.SECURITY);
	    sAccountsProjectionMap.put(Accounts.IMAP_PATH_PREF, Accounts.IMAP_PATH_PREF);
	    sAccountsProjectionMap.put(Accounts.DELETE_EMAILS, Accounts.DELETE_EMAILS);
	    
	    sEmailsProjectionMap = new HashMap<String, String>();
	    sEmailsProjectionMap.put(Emails._ID, Emails._ID);
	    sEmailsProjectionMap.put(Emails.FROM, Emails.FROM);
	    sEmailsProjectionMap.put(Emails.TO, Emails.TO);
	    sEmailsProjectionMap.put(Emails.CC, Emails.CC);
	    sEmailsProjectionMap.put(Emails.BCC, Emails.BCC);
	    sEmailsProjectionMap.put(Emails.SUBJECT, Emails.SUBJECT);
	    sEmailsProjectionMap.put(Emails.CONTENTS, Emails.CONTENTS);
	    sEmailsProjectionMap.put(Emails.ATTACHMENT, Emails.ATTACHMENT);
	    sEmailsProjectionMap.put(Emails.EMAIL_TYPE, Emails.EMAIL_TYPE);
	    sEmailsProjectionMap.put(Emails.EMAIL_CREATED_DATE, Emails.EMAIL_CREATED_DATE);
	    sEmailsProjectionMap.put(Emails.EMAIL_MODIFIED_DATE, Emails.EMAIL_MODIFIED_DATE);
	    sEmailsProjectionMap.put(Emails.EMAIL_DELIVERED_DATE, Emails.EMAIL_DELIVERED_DATE);
	    sEmailsProjectionMap.put(Emails.WHICH_ACCOUNT, Emails.WHICH_ACCOUNT);
    }
}