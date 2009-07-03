/**
 * 
 */
package pl.app.cellpost;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author stellmal
 *
 */
public final class CellPostInternals {
	public static final String AUTHORITY = "pl.app.cellpost.Data";
	
    /**
     * The default sort order for tables
     */
    public static final String DEFAULT_SORT_ORDER = "modified DESC";

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
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The email address
         * <P>Type: TEXT</P>
         */
        public static final String ADDRESS = "address";
        
        /**
         * The username
         * <P>Type: TEXT</P>
         */
        public static final String USER = "user";
        
        /**
         * The password
         * <P>Type: TEXT</P>
         */
        public static final String PASS = "pass";
        
        /**
         * The server
         * <P>Type: TEXT</P>
         */
        public static final String SERVER = "server";
        
        /**
         * The port number
         * <P>Type: INTEGER</P>
         */
        public static final String PORT = "port";
        
        /**
         * The type of the email account (POP3 or IMAP)
         * <P>Type: TEXT</P>
         */
        public static final String ACCOUNT_TYPE = "account_type";
        
        /**
         * The security type of the email account
         * <P>Type: TEXT</P>
         */
        public static final String SECURITY = "security";
        
        /**
         * The IMAP path prefix
         * <P>Type: TEXT</P>
         */
        public static final String IMAP_PATH_PREF = "imap_path_pref";

    }

    
    /**
     * Emails table
     */
    public static final class Emails implements BaseColumns {
        // This class cannot be instantiated
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
         * The 'From' field of the email
         * <P>Type: TEXT</P>
         */
        public static final String FROM = "from";
        
        /**
         * The 'To' field of the email
         * <P>Type: TEXT</P>
         */
        public static final String TO = "to";
        
        /**
         * The 'Cc' (carbon copy) field of the email
         * <P>Type: TEXT</P>
         */
        public static final String CC = "cc";
        
        /**
         * The 'Bcc' (blind carbon copy) field of the email
         * <P>Type: TEXT</P>
         */
        public static final String BCC = "bcc";
        
        /**
         * The subject of the email
         * <P>Type: TEXT</P>
         */
        public static final String SUBJECT = "subject";
        
        /**
         * The email contents
         * <P>Type: TEXT</P>
         */
        public static final String CONTENTS = "contents";
        
        /**
         * The attachment itself
         * <P>Type: BLOB</P>
         */
        public static final String ATTACHMENT = "attachment";
        
        /**
         * Type of the email (available: inbox, sent or draft)
         * <P>Type: TEXT</P>
         */
        public static final String EMAIL_TYPE = "email_type";

        /**
         * The timestamp for when the email was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String EMAIL_CREATED_DATE = "created";

        /**
         * The timestamp for when the email draft was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String EMAIL_MODIFIED_DATE = "modified";
        
        /**
         * The timestamp for when the email was delivered to inbox
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String EMAIL_DELIVERED_DATE = "delivered";
        
        /**
         * Indicates which of user account the email belong to
         * Uses account id (A_ID from table Accounts), FK
         * <P>Type: INTEGER</P>
         */
        public static final String WHICH_ACCOUNT = "which_account";
    }
}
