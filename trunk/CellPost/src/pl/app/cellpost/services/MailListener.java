package pl.app.cellpost.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.logic.MailAuthenticator;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

public class MailListener extends Service {
	private final String TAG = "MailListener";
	
	private Timer timer = new Timer(); 
	private MailAuthenticator receiver = null;
	private DbAdapter dbAdapter = null;
	
	private static final String PREFS_NAME = "CellPostPrefsFile";
	private static final String POP3_UIDL = "_POP3_UIDL";
	private static final String NEW_IMAP_ACCOUNT = "_NEW_IMAP_ACCOUNT";
	private static final String NEW_EMAILS_CHECK = "NEW_EMAILS_CHECK";
	private static final String MAIL_LISTENER_RUNNING = "MAIL_LISTENER_RUNNING";
	private static final String RECENT = "RECENT";
	private static final String[] accountTypes = {"POP3", "IMAP"};
	
	private final List<HashMap<String, String>> accounts = new ArrayList<HashMap<String, String>>();
	
	private String user = null;
	private String password = null;
	private String deleteEmails = null;;
	private String security = null;
	private String port = null;
	private String server = null;
	private String accountType = null;
	private String address = null;
		
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() { 
		super.onCreate(); 
		Log.i(TAG,"Start check for emails in the moment");
		startCheckForEmails();

	}
	
	@Override
    public void onStart( Intent intent, int startId ) {
	  super.onStart( intent, startId );
	  startCheckForEmails();
    }
	
	@Override
	public void onDestroy() { 
		super.onDestroy(); 
		stopCheckForEmails();

	}
	
	@Override
	public void onLowMemory() { 
		super.onLowMemory(); 
		stopCheckForEmails();

	}
	
	private void startCheckForEmails() { 
		if (dbAdapter == null)
        	dbAdapter = new DbAdapter(getApplication().getApplicationContext());
		Cursor cursor = dbAdapter.fetchAccountsToGetMail();
		
		HashMap<String, String> account = null;
		if (cursor.moveToFirst()) {
			 do {
				address = cursor.getString(cursor.getColumnIndex(Accounts.ADDRESS));
				user = cursor.getString(cursor.getColumnIndex(Accounts.USER));
				password = cursor.getString(cursor.getColumnIndex(Accounts.PASS));
				server = cursor.getString(cursor.getColumnIndex(Accounts.INCOMING_SERVER));
				port = cursor.getString(cursor.getColumnIndex(Accounts.INCOMING_PORT));
				security = cursor.getString(cursor.getColumnIndex(Accounts.INCOMING_SECURITY));
				deleteEmails = cursor.getString(cursor.getColumnIndex(Accounts.DELETE_EMAILS));
				accountType = cursor.getString(cursor.getColumnIndex(Accounts.ACCOUNT_TYPE));
				account = new HashMap<String, String>();
				account.put("address", address);
				account.put("user", user);
				account.put("password", password);
				account.put("deleteEmails", deleteEmails);
				account.put("provider", accountType);
				account.put("server", server);
				account.put("port", port);
				account.put("security", security);
				accounts.add(account);
			} while (cursor.moveToNext());
			
		}

		if (accounts.isEmpty() == false) {
			timer.scheduleAtFixedRate(
					new TimerTask() { 
						public void run() {
							Log.i(TAG, "Watek odpytuje wszystkie konta po kolei.");						
							for (final HashMap<String, String> accountData : accounts) {
								Log.i(TAG, "konto " + accountData.get("address"));
								SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
								long period = settings.getLong(NEW_EMAILS_CHECK, 30L) * 60000;
								Log.i(TAG, "Co ile odpytuje konta: " + period);
								timer.scheduleAtFixedRate(
								new TimerTask() { 
									public void run() {
										try {
											HashMap<String, String> account = accountData;
											Log.i(TAG,"user " + account.get("user"));
											Log.i(TAG,"pass " + account.get("password"));
											String whichMessages = null;
											receiver = new MailAuthenticator(account, MailAuthenticator.RECEIVE);
											Map<String, Message> emails = null;
											SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
											if (accountTypes[1].equals(account.get("provider"))) {
												whichMessages = settings.getString(account.get("address") + NEW_IMAP_ACCOUNT, null);
												Log.i(TAG, "co siedzi w " + account.get("address") + NEW_IMAP_ACCOUNT + ": " + whichMessages);
												if (whichMessages != null && "true".equals(whichMessages)) {
													whichMessages = null;
													SharedPreferences.Editor editor = settings.edit();
													editor.remove(account.get("address") + NEW_IMAP_ACCOUNT);
													editor.commit();
													emails = receiver.getMail(null);
												}
												else {
													emails = receiver.getMail(RECENT);
												}	
											}
											else {
												whichMessages = settings.getString(account.get("address") + POP3_UIDL, "-1");	
												Log.i(TAG, "co siedzi w " +account.get("address") + POP3_UIDL + ": " + whichMessages);
												emails = receiver.getMail(whichMessages);
											}
											 
											if (emails != null) {
												StringBuffer pop3MsgUIDL = new StringBuffer();
												for (Message email : emails.values()) {
													ContentValues emailParams = new ContentValues();
													emailParams.put(Emails.ADDRESSEE, accountData.get("address"));
													emailParams.put(Emails.SENDER, email.getFrom()[0].toString());
													emailParams.put(Emails.SUBJECT, email.getSubject().toString());
													emailParams.put(Emails.CONTENTS, email.getContent().toString());
													emailParams.put(Emails.RECEIVE_DATE, email.getReceivedDate().toGMTString());
													
													dbAdapter.saveEmail(emailParams);
													if ("IMAP".equals(account.get("provider"))) {
														email.setFlag(Flags.Flag.SEEN, true);
													}
													
												}	
												if ("POP3".equals(account.get("provider"))) {
													for (String UID : emails.keySet()) {
														pop3MsgUIDL.append(UID + " ");
														Log.i(TAG, "co siedzi w pop3MsgUIDL " + pop3MsgUIDL);
													}
													
												}
												if ("".equals(pop3MsgUIDL.toString()) == false) {
											        SharedPreferences.Editor editor = settings.edit();
											        editor.putString(account.get("provider") + POP3_UIDL, pop3MsgUIDL.toString());
											        editor.commit();
												}
												
											}
											receiver.closeInbox();
											
										}
										catch (MessagingException e) {
											Log.e(TAG, "MessagingException " + e.getMessage());
										} 
										catch (IOException e) {
											Log.e(TAG, "IOException " + e.getMessage());
										}
									}
								}
								,0, 1000L);
							}
						}
					}
					
			,0, 5000L);		
			
		}
		else {
			Log.e(TAG, "No accounts with incoming server configured!");
		}
		
	}
	
	private void stopCheckForEmails() {
		stopSelf();
		Log.i(TAG, "SERVICE MAILLISTENER STOP");
		if (receiver != null) {
			receiver.closeInbox();
		}
		if (timer != null) { 
			timer.cancel();
		} 
		if (dbAdapter != null) {
    		dbAdapter.close();
    		dbAdapter = null;
    	}
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MAIL_LISTENER_RUNNING, false);
        editor.commit();
	}
}
