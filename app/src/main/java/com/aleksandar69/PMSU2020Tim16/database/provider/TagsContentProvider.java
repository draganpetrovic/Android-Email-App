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

public class TagsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.aleksandar69.PMSU2020Tim16.database.provider.TagsContentProvider";
    private static final String TAGS_TABLE = "TAGS";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TAGS_TABLE);

    public static final int TAGS = 1;
    public static final int TAG_ID = 2;

    private MessagesDBHandler myDB;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, TAGS_TABLE, TAGS);
        URI_MATCHER.addURI(AUTHORITY, TAGS_TABLE + "/#", TAG_ID);
    }

    public TagsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case TAGS:
                rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_TAGS,
                        selection,
                        selectionArgs);
                break;
            case TAG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_TAGS, MessagesDBHandler.COLUMN_ID_TAG + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MessagesDBHandler.TABLE_TAGS, MessagesDBHandler.COLUMN_ID_TAG + "=" + id + " and " + selection,
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
            case TAGS:
                id = sqlDB.insert(MessagesDBHandler.TABLE_TAGS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(TAGS_TABLE + "/" + id);
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
        queryBuilder.setTables(MessagesDBHandler.TABLE_TAGS);

        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case TAG_ID:
                queryBuilder.appendWhere(MessagesDBHandler.COLUMN_ID_TAG + "=" + uri.getLastPathSegment());
                break;
            case TAGS:
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
            case TAGS:
                rowsUpdated =
                        sqlDB.update(MessagesDBHandler.TABLE_TAGS,
                                values,
                                selection,
                                selectionArgs);
                break;
            case TAG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_TAGS,
                                    values,
                                    MessagesDBHandler.COLUMN_ID_TAG + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(MessagesDBHandler.TABLE_TAGS, values,
                                    MessagesDBHandler.COLUMN_ID_TAG + "=" + id +
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
