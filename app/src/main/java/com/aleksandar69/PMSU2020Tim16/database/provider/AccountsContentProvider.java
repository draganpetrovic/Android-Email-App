package com.aleksandar69.PMSU2020Tim16.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Account;

public class AccountsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.aleksandar69.PMSU2020Tim16.database.provider.AccountsContentProvider";
    private static final String ACCOUNTS_TABLE = "ACCOUNTS";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ACCOUNTS_TABLE);

    public static final int ACCOUNTS = 1;
    public static final int ACCOUNT_ID = 2;

    private MessagesDBHandler myDB;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, ACCOUNTS_TABLE, ACCOUNTS);
        URI_MATCHER.addURI(AUTHORITY, ACCOUNTS_TABLE + "/#", ACCOUNT_ID);
    }


    public AccountsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ACCOUNTS:
                rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ACCOUNTS,
                        selection,
                        selectionArgs);
                break;
            case ACCOUNT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ACCOUNTS, MessagesDBHandler.COLUMN_ID_ACCOUNTS + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ACCOUNTS, MessagesDBHandler.COLUMN_ID_ACCOUNTS + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);

        SQLiteDatabase sqlDB = myDB.getWritableDatabase();

        long id = 0;

        switch (uriType) {
            case ACCOUNTS:
                id = sqlDB.insert(MessagesDBHandler.TABLE_ACCOUNTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ACCOUNTS_TABLE + "/" + id);
    }

    @Override
    public boolean onCreate() {
        myDB = new MessagesDBHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MessagesDBHandler.TABLE_ACCOUNTS);

        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case ACCOUNT_ID:
                queryBuilder.appendWhere(MessagesDBHandler.COLUMN_ID_ACCOUNTS + "=" + uri.getLastPathSegment());
                break;
            case ACCOUNTS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(myDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsUpdated = 0;


        switch (uriType) {
            case ACCOUNTS:
                rowsUpdated =
                        sqlDB.update(MessagesDBHandler.TABLE_ACCOUNTS,
                                values,
                                selection,
                                selectionArgs);
                break;
            case ACCOUNT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_ACCOUNTS,
                                    values,
                                    MessagesDBHandler.COLUMN_ID_ACCOUNTS + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_ACCOUNTS, values,
                                    MessagesDBHandler.COLUMN_ID_ACCOUNTS + "=" + id +
                                            " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
