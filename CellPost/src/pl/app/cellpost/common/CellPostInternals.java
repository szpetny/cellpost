/**
 * 
 */
package pl.app.cellpost.common;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Malgorzata Stellert
 *
 */
public final class CellPostInternals {
	public static final String AUTHORITY = "pl.app.cellpost.Data";

    // This class cannot be instantiated
    private CellPostInternals() {}
    
    /**
     * Accounts table
     */
    public static final class Accounts implements BaseColumns {
        // This class cannot be instantiated
        private Accounts() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/accounts");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of accounts.
         */
        public static final String CONTENT_TYPE = "text/plain";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single account.
         */
        public static final String CONTENT_ITEM_TYPE = "text/plain";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "address ASC";

        /**
         * Table name
         */
        public static final String TABLE_NAME = "accounts";
        
        /**
         * The account id
         * <P>Type: TEXT</P>
         */
       public static final String _ID = "_id";
        
        /**
         * The email address
         * <P>Type: TEXT</P>
         */
        public static final String ADDRESS = "ADDRESS";
        
        /**
         * The username
         * <P>Type: TEXT</P>
         */
        public static final String USER = "USER";
        
        /**
         * The password
         * <P>Type: TEXT</P>
         */
        public static final String PASS = "PASS";
        
        /**
         * The server
         * <P>Type: TEXT</P>
         */
        public static final String INCOMING_SERVER = "INCOMING_SERVER";
        
        /**
         * The port number
         * <P>Type: INTEGER</P>
         */
        public static final String INCOMING_PORT = "INCOMING_PORT";
        
        /**
         * The security type of the email account
         * <P>Type: TEXT</P>
         */
        public static final String INCOMING_SECURITY = "INCOMING_SECURITY";
        
        /**
         * The server
         * <P>Type: TEXT</P>
         */
        public static final String OUTGOING_SERVER = "OUTGOING_SERVER";
        
        /**
         * The port number
         * <P>Type: INTEGER</P>
         */
        public static final String OUTGOING_PORT = "OUTGOING_PORT";
               
        /**
         * The security type of the email account
         * <P>Type: TEXT</P>
         */
        public static final String OUTGOING_SECURITY = "OUTGOING_SECURITY";
        
        /**
         * The type of the email account (POP3 or IMAP)
         * <P>Type: TEXT</P>
         */
        public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
        
        /**
         * Determines whether to delete e-mails from server
         * <P>Type: TEXT</P>
         */
        public static final String DELETE_EMAILS = "DELETE_EMAILS";
        
        /**
         * Determines whether this account is default one to send e-mails
         * from.
         * <P>Type: TEXT</P>
         */
        public static final String DEFAULT = "DEFAULT_ACCOUNT";
        
        /**
         * Determines whether this account is default one to send e-mails
         * from.
         * <P>Type: TEXT</P>
         */
        public static final String NICK = "NICK";

    }

    
    /**
     * Emails table
     */
    public static final class Emails implements BaseColumns {
        private Emails() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/emails");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of emails.
         */
        public static final String CONTENT_TYPE = "text/plain";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single email.
         */
        public static final String CONTENT_ITEM_TYPE = "text/plain";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        
        /**
         * Table name
         */
        public static final String TABLE_NAME = "emails";
        
        /**
         * The message id
         * <P>Type: TEXT</P>
         */
        public static final String _ID = "_id";

        /**
         * The 'From' field of the email
         * <P>Type: TEXT</P>
         */
        public static final String SENDER = "SENDER";
        
        /**
         * The 'To' field of the email
         * <P>Type: TEXT</P>
         */
        public static final String ADDRESSEE = "ADDRESSEE";
        
        /**
         * The 'Cc' (carbon copy) field of the email
         * <P>Type: TEXT</P>
         */
        public static final String CC = "CC";
        
        /**
         * The 'Bcc' (blind carbon copy) field of the email
         * <P>Type: TEXT</P>
         */
        public static final String BCC = "BCC";
        
        /**
         * The subject of the email
         * <P>Type: TEXT</P>
         */
        public static final String SUBJECT = "SUBJECT";
        
        /**
         * The email contents
         * <P>Type: TEXT</P>
         */
        public static final String CONTENTS = "CONTENTS";
        
        /**
         * The attachment itself
         * <P>Type: BLOB</P>
         */
        public static final String ATTACHMENT = "ATTACHMENT";
        
        /**
         * Type of the email (available: inbox, sent or draft)
         * <P>Type: TEXT</P>
         *//*
        public static final String EMAIL_TYPE = "email_type";*/

        /**
         * The timestamp for when the email was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATE_DATE = "CREATE_DATE";

        /**
         * The timestamp for when the email draft was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFY_DATE = "MODIFY_DATE";
        
        /**
         * The timestamp for when the email was delivered to inbox
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String DELIVER_DATE = "DELIVER_DATE";
        
        /**
         * Indicates which of user account the email belong to
         * Uses account id (A_ID from table Accounts), FK
         * <P>Type: INTEGER</P>
         */
        /*public static final String WHICH_ACCOUNT = "which_account";*/
    }
}
