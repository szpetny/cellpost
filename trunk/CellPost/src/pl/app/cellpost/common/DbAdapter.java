/**
 * 
 */
package pl.app.cellpost.common;

/**
 * @author szpetny
 *
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    private static final String DATABASE_PATH = "/data/data/pl.app.cellpost/databases/";
    private static final int DATABASE_VERSION = 3;

	private static final String TAG = "DbAdapter";
	private DatabaseHelper dbHelper;
	private static SQLiteDatabase db;

	private static Context ctx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        }
        
        public void createDataBase() throws IOException{
        	 
        	boolean dbExist = checkDataBase();
     
        	if(!dbExist){
        		this.getReadableDatabase();
     
            	try {
        			copyDataBase();
     
        		} catch (IOException e) {
     
            		Log.e(TAG, "Error copying database");
     
            	}
        	}
     
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS accounts; "
            		  + "DROP TABLE IF EXISTS emails");
            try {
				copyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        private boolean checkDataBase(){       	 
        	SQLiteDatabase checkDB = null;
     
        	try{
        		String myPath = DATABASE_PATH + DATABASE_NAME;
        		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        	}catch(SQLiteException e){
        		Log.e(TAG, "Database does not exist");
     
        	}
     
        	if(checkDB != null){     
        		checkDB.close();
        	}
     
        	return checkDB != null ? true : false;
        }
        
        private void copyDataBase() throws IOException{
        	InputStream myInput = ctx.getAssets().open(DATABASE_NAME);
        	String outFileName = DATABASE_PATH + DATABASE_NAME;
        	OutputStream myOutput = new FileOutputStream(outFileName);
        	byte[] buffer = new byte[1024];
        	int length;
        	while ((length = myInput.read(buffer))>0){
        		myOutput.write(buffer, 0, length);
        	}
        	myOutput.flush();
        	myOutput.close();
        	myInput.close();
     
        }
     
        public void openDataBase() throws SQLException{
        	boolean dbExist = checkDataBase();
        	String myPath = DATABASE_PATH + DATABASE_NAME;
        	if(!dbExist){
            	this.getReadableDatabase();   
            	try {     
        			copyDataBase();
     
        		} catch (IOException e) {     
            		throw new Error("Error copying database");    
            	}
        	}
        	 db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
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
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		dbHelper.openDataBase();
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
		if (checkEmpty() == false) {
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
		else return false;
	}
	
	public boolean checkEmpty() {
		try {
			return db.rawQuery("PRAGMA table_info( accounts );", null) == null; 
			
		} catch (SQLiteException sqle) {
			throw new SQLException("CHUJ!");
		}
	}
	
}