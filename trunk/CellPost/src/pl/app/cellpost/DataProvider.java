/**
 * 
 */
package pl.app.cellpost;

import java.util.HashMap;

import pl.app.cellpost.CellPostInternals.Accounts;
import pl.app.cellpost.CellPostInternals.Emails;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author stellmal
 *
 */
public class DataProvider extends ContentProvider {
	
	private static final String TAG = "DataProvider";

    private static final String DATABASE_NAME = "cell_post.db";
    private static final int DATABASE_VERSION = 2;
    private static final String ACCOUNTS_TABLE_NAME = "accounts";
    private static final String EMAILS_TABLE_NAME = "emails";

    private static HashMap<String, String> sAccountsProjectionMap;
    private static HashMap<String, String> sEmailsProjectionMap;

    private static final int ACCOUNTS = 1;
    private static final int ACCOUNT_ID = 2;
    private static final int EMAILS = 3;
    private static final int EMAIL_ID = 4;

    private static final UriMatcher sUriMatcher;
    
    /**
     * This class helps open, create, and upgrade the database file.
     */
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

    private DatabaseHelper mOpenHelper;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            count = db.delete(ACCOUNTS_TABLE_NAME, where, whereArgs);
            break;

        case ACCOUNT_ID:
            String accountId = uri.getPathSegments().get(1);
            count = db.delete(ACCOUNTS_TABLE_NAME, Accounts._ID + "=" + accountId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
            
        case EMAILS:
            count = db.delete(EMAILS_TABLE_NAME, where, whereArgs);
            break;

        case EMAIL_ID:
            String emailId = uri.getPathSegments().get(1);
            count = db.delete(EMAILS_TABLE_NAME, Accounts._ID + "=" + emailId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            return Accounts.CONTENT_TYPE;

        case ACCOUNT_ID:
            return Accounts.CONTENT_ITEM_TYPE;
            
        case EMAILS:
            return Emails.CONTENT_TYPE;

        case EMAIL_ID:
            return Emails.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            qb.setTables(ACCOUNTS_TABLE_NAME);
            qb.setProjectionMap(sAccountsProjectionMap);
            break;

        case ACCOUNT_ID:
            qb.setTables(ACCOUNTS_TABLE_NAME);
            qb.setProjectionMap(sAccountsProjectionMap);
            qb.appendWhere(Accounts._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        case EMAILS:
            qb.setTables(EMAILS_TABLE_NAME);
            qb.setProjectionMap(sEmailsProjectionMap);
            break;

        case EMAIL_ID:
            qb.setTables(EMAILS_TABLE_NAME);
            qb.setProjectionMap(sEmailsProjectionMap);
            qb.appendWhere(Emails._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CellPostInternals.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean checkUnique(Uri uri, String[] projection, String selection) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        
        case ACCOUNT_ID:
            qb.setTables(ACCOUNTS_TABLE_NAME);
            qb.setProjectionMap(sAccountsProjectionMap);
            qb.appendWhere(Accounts._ID + "=" + uri.getPathSegments().get(1));
            break;

        case EMAIL_ID:
            qb.setTables(EMAILS_TABLE_NAME);
            qb.setProjectionMap(sEmailsProjectionMap);
            qb.appendWhere(Emails._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, null, null, null, null);

        if (c == null)
        	return false;	
        else
        	return true;
	}
	
	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CellPostInternals.AUTHORITY, "accounts", ACCOUNTS);
        sUriMatcher.addURI(CellPostInternals.AUTHORITY, "accounts/#", ACCOUNT_ID);
        sUriMatcher.addURI(CellPostInternals.AUTHORITY, "emails", EMAILS);
        sUriMatcher.addURI(CellPostInternals.AUTHORITY, "emails/#", EMAIL_ID);

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
