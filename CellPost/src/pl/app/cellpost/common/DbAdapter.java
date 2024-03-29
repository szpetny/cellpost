package pl.app.cellpost.common;

/**
 * @author M.Stellert
 *
 */
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
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
			db.execSQL("CREATE TABLE accounts " +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							   "ADDRESS TEXT NOT NULL, " +
							   "USER TEXT, " +
							   "PASS TEXT, " +
							   "ACCOUNT_TYPE TEXT, " +
							   "INCOMING_SERVER TEXT, " +
							   "INCOMING_PORT NUMERIC, " +
							   "INCOMING_SECURITY TEXT, " +
							   "DELETE_EMAILS TEXT, " +
							   "OUTGOING_SERVER TEXT, " +
							   "OUTGOING_PORT NUMERIC, " +
							   "OUTGOING_SECURITY TEXT," +
							   "DEFAULT_ACCOUNT TEXT, " +
							   "NICK TEXT); ");		
			db.execSQL("CREATE TABLE emails " +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							 "SENDER TEXT, " +
							 "ADDRESSEE TEXT NOT NULL, " +
							 "CC TEXT, " +
							 "BCC TEXT, " +
							 "SUBJECT TEXT, " +
							 "CONTENTS TEXT, " +
							 "ATTACHMENT BLOB, " +
							 "CREATE_DATE TIMESTAMP, " +
							 "MODIFY_DATE TIMESTAMP, " +
							 "DELIVER_DATE TIMESTAMP " +
							 "RECEIVE_DATE TIMESTAMP);");
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
	* Create a new account using the values provided in ContentValues. 
	* If the account is successfully created return the new accountId 
	* for that account, otherwise return -1 to indicate failure.
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
	
		Cursor cursor = db.query(true, Accounts.TABLE_NAME, new String[] {"*"}, 
				Accounts._ID + "=" + accountId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;	
	}
	
	/**
	* Return  a Cursor over the list of all accounts in the database, 
	* which have configured incoming server. 
	*
	* @return Cursor positioned to matching account, if found
	* @throws SQLException if account could not be found/retrieved
	*/
	public Cursor fetchAccountsToGetMail() throws SQLException {	
		Cursor cursor = db.query(true, Accounts.TABLE_NAME, 
				new String[] {Accounts.ADDRESS, Accounts.INCOMING_SERVER, 
				Accounts.INCOMING_PORT, Accounts.INCOMING_SECURITY, 
				Accounts.USER, Accounts.PASS, Accounts.DELETE_EMAILS, 
				Accounts.ACCOUNT_TYPE}, 
				"(" + Accounts.INCOMING_SERVER + " != '') AND " +
				"(" + Accounts.INCOMING_PORT + " != '') AND " +
				"(" + Accounts.INCOMING_SECURITY + " != '')",
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
	
		Cursor cursor = db.query(true, Accounts.TABLE_NAME, new String[] {"*"}, 
				Accounts.DEFAULT + "= 'true' ", null, null, null, null, null);
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
		setAccountAsDefault(accountId, accountParams);
		return db.update(Accounts.TABLE_NAME, accountParams, 
				Accounts._ID + "=" + accountId, null) > 0;
	}
	
	/**
	* As cannot be two accounts set as default, 
	* to unable this situation this method checks if new configured account should be default.
	* When true, sets other accounts to NOT DEFAULT.
	* Also if there is no default account, 
	* method adds parameter DEFAULT set to true to current accountParams
	*
	* @param accountId id of new configured account 
	* @param accountParams to parameters of new configured account 
	*/
	private void setAccountAsDefault(long accountId, ContentValues accountParams) {
		Cursor c = fetchDefaultAccount();
		if (c.moveToFirst()) {
			if (accountParams.get(Accounts.DEFAULT) != null 
				&& "true".equals(accountParams.get(Accounts.DEFAULT).toString())){
				ContentValues defaultParam = new ContentValues();
				defaultParam.put(Accounts.DEFAULT, "false");
				db.update(Accounts.TABLE_NAME, defaultParam, 
						Accounts._ID + " != " + accountId, null);
			}
		}
		else {
			accountParams.put(Accounts.DEFAULT, "true");
		}
		c.close();
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
		return db.delete(Emails.TABLE_NAME, 
				Emails._ID + "=" + emailId, null) > 0;
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
				Emails.DELIVER_DATE + " is not null ", 
				null, null, null, Emails.DELIVER_DATE + " DESC");
	}
	
	/**
	* Return a Cursor over the list of all received emails from 
	* all configured accounts in the database
	*
	* @return Cursor over all emails
	*/
	public Cursor fetchAllEmailsReceived() {
		return db.query(Emails.TABLE_NAME, new String[] {"*"}, 
				Emails.RECEIVE_DATE + " is not null ", 
				null, null, null, Emails.RECEIVE_DATE + " DESC");
	}
	
	/**
	* Return a Cursor over the list of all draft emails in the database
	*
	* @return Cursor over all emails
	*/
	public Cursor fetchAllDrafts() {
		return db.query(Emails.TABLE_NAME, new String[] {"*"}, 
				Emails.MODIFY_DATE + " is not null ", 
				null, null, null, Emails.MODIFY_DATE + " DESC");
	}

	/**
	* Return a Cursor positioned at the email that matches the given emailId
	*
	* @param emailId id of email to retrieve
	* @return Cursor positioned to matching email, if found
	* @throws SQLException if email could not be found/retrieved
	*/
	public Cursor fetchEmail(long emailId) throws SQLException {
	
		Cursor cursor = db.query(true, Emails.TABLE_NAME, new String[] {"*"}, 
				Emails._ID + "=" + emailId, null, null, null, null, null);
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
		return db. update(Emails.TABLE_NAME, emailParams,
				Emails._ID + "=" + emailId, null) > 0;
	}


	
}