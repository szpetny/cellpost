/**
 * 
 */
package pl.app.cellpost.common;

/**
 * @author szpetny
 *
 */
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

    private static final String DATABASE_NAME = "cellpost.db";
    private static final int DATABASE_VERSION = 3;

	private static final String TAG = "DbAdapter";
	private DatabaseHelper dbHelper;
	private static SQLiteDatabase db;

	@SuppressWarnings("unused")
	private static Context ctx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
											   "ADDRESS TEXT NOT NULL, " +
											   "USER TEXT, " +
											   "PASS TEXT, " +
											   "ACCOUNT_TYPE TEXT, " +
											   "INCOMING_SERVER TEXT, " +
											   "INCOMING_PORT NUMERIC, " +
											   "INCOMING_SECURITY TEXT, " +
											   "DELETE_EMAILS NUMERIC, " +
											   "OUTGOING_SERVER TEXT, " +
											   "OUTGOING_PORT NUMERIC, " +
											   "OUTGOING_SECURITY TEXT," +
											   "DEFAULT_ACCOUNT TEXT, " +
											   "NICK TEXT); ");
					
			db.execSQL("CREATE TABLE emails (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
											 "SENDER TEXT, " +
											 "ADDRESSEE TEXT NOT NULL, " +
											 "CC TEXT, " +
											 "BCC TEXT, " +
											 "SUBJECT TEXT, " +
											 "CONTENTS TEXT, " +
											 "ATTACHMENT BLOB, " +
											 "CREATE_DATE TIMESTAMP, " +
											 "MODIFY_DATE TIMESTAMP, " +
											 "DELIVER_DATE TIMESTAMP);");
					
		}
		
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    Log.w(TAG, "Upgrading database, this will drop tables and recreate.");
		    db.execSQL("DROP TABLE IF EXISTS " + Accounts.TABLE_NAME);
		    db.execSQL("DROP TABLE IF EXISTS " + Emails.TABLE_NAME);
		    onCreate(db);
	    }
        
     
        @Override
    	public synchronized void close() { 
        	    if(db != null)
        	    	db.close();   
        	    super.close();
    	}

	}

	/**
	* Constructor - takes the context to allow the database to be
	* opened/created
	*
	* @param ctx the Context within which to work
	*/
	public DbAdapter(Context ctx) {
		DbAdapter.ctx = ctx;
		if (dbHelper == null) {
			dbHelper = new DatabaseHelper(ctx);
			db = dbHelper.getWritableDatabase();
		}
		
	}


	public void close() {
		dbHelper.close();
		dbHelper = null;
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
		return db.insert(Accounts.TABLE_NAME, null, accountParams);
	}
	
	/**
	* Delete the account settings with the given accountId
	*
	* @param accountId id of account settings to delete
	* @return true if deleted, false otherwise
	*/
	public boolean deleteAccount(long accountId) {	
		Log.i(TAG, "value__" + accountId);
		return db.delete(Accounts.TABLE_NAME, Accounts._ID + "=" + accountId, null) > 0;
	}
	
	/**
	* Delete the all accounts settings from database. Useful during testing.
	*
	* @return true if deleted, false otherwise
	*/
	public boolean deleteAllAccounts() {	
		return db.delete(Accounts.TABLE_NAME, Accounts._ID + "> 0", null) > 0;
	}

	/**
	* Return a Cursor over the list of all accounts in the database
	*
	* @return Cursor over all accounts
	*/
	public Cursor fetchAllAccounts() {
		return db.query(Accounts.TABLE_NAME, new String[] {"*"}, 
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
	
		Cursor cursor = db.query(true, Accounts.TABLE_NAME, new String[] {"*"}, Accounts._ID + "=" + accountId, 
				null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;	
	}
	
	/**
	* Return a Cursor positioned at the account that is the default one to send messages
	*
	* @return Cursor positioned to matching account, if found
	* @throws SQLException if account could not be found/retrieved
	*/
	public Cursor fetchDefaultAccount() throws SQLException {
	
		Cursor cursor = db.query(true, Accounts.TABLE_NAME, new String[] {"*"}, Accounts.DEFAULT + "= 'true'", 
				null, null, null, null, null);
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
		return db. update(Accounts.TABLE_NAME, accountParams, Accounts._ID + "=" + accountId, null) > 0;
	}
	
	/**
	* Checks uniqueness of given account
	* 
	* @param accountName the value which uniqueness needs to be checked
	* @return true if the value is unique, false otherwise
	*/
	public boolean checkUnique(String accountName) {
			Cursor c = null;
			try {
				c = db.rawQuery("SELECT _ID, ADDRESS FROM accounts WHERE ADDRESS = %s", new String[] {accountName}); 				
			} catch (SQLiteException sqle) {
				throw new SQLException("CHUJ!");
			}
			if (c == null) {
				return true;
			}				
			else {
				c.close();
				return false;
			}			
		
	}
	
	/**
	* Create a new email using the values provided in ContentValues. If the email is
	* successfully created return the new emailId for that email, otherwise return
	* a -1 to indicate failure.
	*
	* @param ContentValues containing email parameters
	* @return emailId or -1 if failed
	*/
	public long saveEmail(ContentValues emailParams) {
		return db.insert(Emails.TABLE_NAME, null, emailParams);
	}
	
	/**
	* Delete the email settings with the given emailId
	*
	* @param emailId id of email settings to delete
	* @return true if deleted, false otherwise
	*/
	public boolean deleteEmail(long emailId) {	
		Log.i(TAG, "value__" + emailId);
		return db.delete(Emails.TABLE_NAME, Emails._ID + "=" + emailId, null) > 0;
	}
	
	/**
	* Delete the all emails settings from database. Useful during testing.
	*
	* @return true if deleted, false otherwise
	*/
	public boolean deleteAllEmails() {	
		return db.delete(Emails.TABLE_NAME, Emails._ID + "> 0", null) > 0;
	}

	/**
	* Return a Cursor over the list of all emails in the database
	*
	* @return Cursor over all emails
	*/
	public Cursor fetchAllEmails() {
		return db.query(Emails.TABLE_NAME, new String[] {"*"}, 
				null, null, null, null, null);
	}
	
	/**
	* Return a Cursor over the list of all sent emails in the database
	*
	* @return Cursor over all emails
	*/
	public Cursor fetchAllSent() {
		return db.query(Emails.TABLE_NAME, new String[] {"*"}, 
				Emails.DELIVER_DATE + " is not null ", null, null, null, null);
	}
	
	/**
	* Return a Cursor over the list of all draft emails in the database
	*
	* @return Cursor over all emails
	*/
	public Cursor fetchAllDrafts() {
		return db.query(Emails.TABLE_NAME, new String[] {"*"}, 
				Emails.DELIVER_DATE + " is null OR " + Emails.MODIFY_DATE + " is not null ", null, null, null, null);
	}

	/**
	* Return a Cursor positioned at the email that matches the given emailId
	*
	* @param emailId id of email to retrieve
	* @return Cursor positioned to matching email, if found
	* @throws SQLException if email could not be found/retrieved
	*/
	public Cursor fetchEmail(long emailId) throws SQLException {
	
		Cursor cursor = db.query(true, Emails.TABLE_NAME, new String[] {"*"}, Emails._ID + "=" + emailId, 
				null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;	
	}

	/**
	* Update the email settings using the details provided. The email to be updated is
	* specified using the emailId, and it is altered to use the values passed in
	*
	* @param emailId id of email to update
	* @param emailParams to set new email parameters 
	* @return true if the email was successfully updated, false otherwise
	*/
	public boolean updateSavedEmail(long emailId, ContentValues emailParams) {
		return db. update(Emails.TABLE_NAME, emailParams, Emails._ID + "=" + emailId, null) > 0;
	}


	
}