package pl.app.cellpost.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Message;
import javax.mail.MessagingException;

import pl.app.cellpost.R;
import pl.app.cellpost.common.DbAdapter;
import pl.app.cellpost.common.CellPostInternals.Accounts;
import pl.app.cellpost.common.CellPostInternals.Emails;
import pl.app.cellpost.logic.MailAuthenticator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

public class MailListener extends Service{
	private final String TAG = "MailListener";
	
	private Timer timer = new Timer(); 
	private MailAuthenticator receiver = null;
	private DbAdapter dbAdapter = null;
	
	private final String PREFS_NAME = "CellPostPrefsFile";
	private final String UIDL = "_UIDL";
	private final String NEW_EMAILS_CHECK = "NEW_EMAILS_CHECK";
	private final String NEW_EMAILS_NOTIFICATION = "NEW_EMAILS_NOTIFICATION";
	private SharedPreferences settings;
	
	private String user = null;
	private String password = null;
	private String deleteEmails = null;;
	private String security = null;
	private String port = null;
	private String server = null;
	private String accountType = null;
	private String address = null;
	private boolean newEmailsNotification = false;
		
	private static Activity activity;
	
	public static  NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 666; 
    private InternalWatcher watcher = new InternalWatcher();
		
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public static void setMainActivity(Activity newActivity) {
		  activity = newActivity;
	}

	@Override
	public void onCreate() { 
		super.onCreate(); 
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
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
		long interval = getInterval();
		if (interval != 0) {
			timer.scheduleAtFixedRate(
					new TimerTask() { 
						public void run() {
							listen();
						}
					},0, interval);
		}
		else {
			listen();
		}
		
		
	}
	
	private void stopCheckForEmails() {
		stopSelf();
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
	}
	
	private long getInterval() {
		settings = getSharedPreferences(PREFS_NAME, 0);
		long interval = settings.getLong(NEW_EMAILS_CHECK, 15L) * 60000;
		return interval;
	}	
	
	private void listen () {
		if (dbAdapter == null)
        	dbAdapter = new DbAdapter(getApplication().getApplicationContext());
		List<Map<String,String>> accounts = new ArrayList<Map<String,String>>();
		Cursor cursor = dbAdapter.fetchAccountsToGetMail();
		
			Map<String, String> account = null;
			if (cursor.moveToFirst()) {
				 do {
					address = cursor.getString(
							cursor.getColumnIndex(Accounts.ADDRESS));
					user = cursor.getString(
							cursor.getColumnIndex(Accounts.USER));
					password = cursor.getString(
							cursor.getColumnIndex(Accounts.PASS));
					server = cursor.getString(
							cursor.getColumnIndex(Accounts.INCOMING_SERVER));
					port = cursor.getString(
							cursor.getColumnIndex(Accounts.INCOMING_PORT));
					security = cursor.getString(
							cursor.getColumnIndex(Accounts.INCOMING_SECURITY));
					deleteEmails = cursor.getString(
							cursor.getColumnIndex(Accounts.DELETE_EMAILS));
					accountType = cursor.getString(
							cursor.getColumnIndex(Accounts.ACCOUNT_TYPE));
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

			else {
				Log.e(TAG, "No accounts with incoming server configured!");
			}
			
			if (accounts != null && accounts.isEmpty() == false) {
				for (Map<String,String> accountConf : accounts) {
					try {
						String whichMessages = null;
						receiver = new MailAuthenticator(accountConf, MailAuthenticator.RECEIVE);
						Map<String, Message> emails = null;		
						settings = getSharedPreferences(PREFS_NAME, 0);
						whichMessages = settings.getString(accountConf.get("address") + UIDL, "-1");
						newEmailsNotification = settings.getBoolean(NEW_EMAILS_NOTIFICATION, true);
						emails = receiver.getMail(whichMessages);
						 
						if (emails != null && emails.isEmpty() == false) {
							StringBuffer msgsUIDL = new StringBuffer();
							for (Message email : emails.values()) {
								ContentValues emailParams = new ContentValues();
								emailParams.put(Emails.ADDRESSEE, accountConf.get("address"));
								emailParams.put(Emails.SENDER, email.getFrom()[0].toString());
								emailParams.put(Emails.SUBJECT, email.getSubject().toString());
								emailParams.put(Emails.CONTENTS, email.getContent().toString());
								if (email.getReceivedDate() == null) {
									Timestamp receiveDate = new Timestamp(new Date().getTime());
									emailParams.put(Emails.RECEIVE_DATE, receiveDate.toGMTString());
								}	
								else {
									emailParams.put(Emails.RECEIVE_DATE, email.getReceivedDate().toGMTString());
								}
								dbAdapter.saveEmail(emailParams);
																
							}	
							if ("-1".equals(whichMessages) == false)
								msgsUIDL.append(whichMessages.trim() + " ");
							for (String UID : emails.keySet()) {
								msgsUIDL.append(UID + " ");
							}
							if ("".equals(msgsUIDL.toString()) == false) {
						        SharedPreferences.Editor editor = settings.edit();
						        editor.putString(accountConf.get("address") + UIDL, msgsUIDL.toString());
						        editor.commit();
							}

							watcher.addObserver((Observer) activity);
							watcher.notifyAboutNewEmails();
							if (newEmailsNotification) {
								showNotification();  
							}
						}
						
						receiver.closeInbox();
					}
					catch (MessagingException e) {
						Log.e(TAG, "MessagingException " + e);
					} 
					catch (IOException e) {
						Log.e(TAG, "IOException " + e);
					} 
				}
			}
	}
	
	public class InternalWatcher extends Observable {
		public void notifyAboutNewEmails () {
			setChanged();
			notifyObservers();
		}
	}
	
	private void showNotification() {
        String notificationContent = (String) getText(R.string.emailsNotifier);
        mNotificationManager.notify(
                   NOTIFICATION_ID,
                   new Notification(
                	   R.drawable.icon,                
                	   notificationContent,                 
                       System.currentTimeMillis()));                 
    }
}
	
