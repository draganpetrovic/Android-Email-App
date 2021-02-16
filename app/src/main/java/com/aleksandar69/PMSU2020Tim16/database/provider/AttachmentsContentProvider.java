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

public class AttachmentsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.aleksandar69.PMSU2020Tim16.database.provider.AttachmentsContentProvider";
    private static final String ATTACHMENTS_TABLE = "ATTACHMENTS";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ATTACHMENTS_TABLE);

    public static final int ATTACHMENTS = 1;
    public static final int ATTACHMENTS_ID = 2;

    private MessagesDBHandler myDB;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        URI_MATCHER.addURI(AUTHORITY, ATTACHMENTS_TABLE, ATTACHMENTS);
        URI_MATCHER.addURI(AUTHORITY, ATTACHMENTS_TABLE + "/#", ATTACHMENTS_ID);
    }

    public AttachmentsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ATTACHMENTS:
                rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ATTACHMENTS,
                        selection,
                        selectionArgs);
                break;

            case ATTACHMENTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ATTACHMENTS, MessagesDBHandler.COLUMN_ID_ATTACHMENTS + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_ATTACHMENTS, MessagesDBHandler.COLUMN_ID_ATTACHMENTS + "=" + id + " and " + selection,
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
            case ATTACHMENTS:
                id = sqlDB.insert(MessagesDBHandler.TABLE_ATTACHMENTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(ATTACHMENTS_TABLE + "/" + id);
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
        queryBuilder.setTables(MessagesDBHandler.TABLE_ATTACHMENTS);

        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case ATTACHMENTS_ID:
                queryBuilder.appendWhere(MessagesDBHandler.COLUMN_ID_ATTACHMENTS + "="
                        + uri.getLastPathSegment());
                break;
            case ATTACHMENTS:
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
            case ATTACHMENTS:
                rowsUpdated =
                        sqlDB.update(MessagesDBHandler.TABLE_ATTACHMENTS,
                                values,
                                selection,
                                selectionArgs);
                break;
            case ATTACHMENTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_ATTACHMENTS,
                                    values,
                                    MessagesDBHandler.COLUMN_ID_ATTACHMENTS + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_ATTACHMENTS, values,
                                    MessagesDBHandler.COLUMN_ID_ATTACHMENTS + "=" + id +
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
