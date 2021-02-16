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

import static com.aleksandar69.PMSU2020Tim16.database.provider.MessagesContentProvider.MESSAGES;
import static com.aleksandar69.PMSU2020Tim16.database.provider.MessagesContentProvider.MESSAGE_ID;

public class ContactsContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.aleksandar69.PMSU2020Tim16.database.provider.ContactsContentProvider";
    private static final String CONTACTS_TABLE = "CONTACT";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACTS_TABLE);

    public static final int CONTACTS = 1;
    public static final int CONTACT_ID = 2;

    private MessagesDBHandler myDB;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, CONTACTS_TABLE, CONTACTS);
        URI_MATCHER.addURI(AUTHORITY, CONTACTS_TABLE +"/#", CONTACT_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.

        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case CONTACTS:
                rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_CONTACTS,
                        selection,
                        selectionArgs);
                break;

            case CONTACT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_CONTACTS ,
                            MessagesDBHandler.COLUMN_ID_CONTACTS + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_CONTACTS,
                            MessagesDBHandler.COLUMN_ID_CONTACTS + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    public ContactsContentProvider() {

    }


    @Override
    public String getType(Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = URI_MATCHER.match(uri);

        SQLiteDatabase sqlDB = myDB.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case CONTACTS:
                id = sqlDB.insert(MessagesDBHandler.TABLE_CONTACTS,
                        null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTACTS_TABLE + "/" + id);
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
        queryBuilder.setTables(MessagesDBHandler.TABLE_CONTACTS);

        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case CONTACT_ID:
                queryBuilder.appendWhere(MessagesDBHandler.COLUMN_ID_CONTACTS + "="
                        + uri.getLastPathSegment());
                break;
            case CONTACTS:
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
            case CONTACTS:
                rowsUpdated =
                        sqlDB.update(MessagesDBHandler.TABLE_CONTACTS,
                                values,
                                selection,
                                selectionArgs);
                break;
            case CONTACT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_CONTACTS,
                                    values,
                                    MessagesDBHandler.COLUMN_ID_CONTACTS + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_CONTACTS, values,
                                    MessagesDBHandler.COLUMN_ID_CONTACTS + "=" + id +
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
