package mkruglikov.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class FavoriteMoviesProvider extends android.content.ContentProvider {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favoriteMoviesDb";
    public static final String TABLE_NAME = "movies";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_POSTER = "poster";
    public static final String KEY_VOTE_AVERAGE = "voteAverage";
    public static final String KEY_OVERVIEW = "overview";

    static final String AUTHORITY = "mkruglikov.popularmovies";
    static final String PATH = TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
    static final int URI_FAVORITE_MOVIES = 101;
    static final int URI_FAVORITE_MOVIES_ID = 102;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, URI_FAVORITE_MOVIES);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", URI_FAVORITE_MOVIES_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == URI_FAVORITE_MOVIES_ID) {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection))
                selection = KEY_ID + " = " + id;
            else
                selection = selection + " AND " + KEY_ID + " = " + id;
        } else if (uriMatcher.match(uri) != URI_FAVORITE_MOVIES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == URI_FAVORITE_MOVIES)
            return CONTENT_TYPE;
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_FAVORITE_MOVIES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (uriMatcher.match(uri) != URI_FAVORITE_MOVIES_ID)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection))
            selection = KEY_ID + " = " + id;
        else
            selection = selection + " AND " + KEY_ID + " = " + id;


        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) != URI_FAVORITE_MOVIES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + "(" +
                    KEY_ID + " integer primary key," +
                    KEY_TITLE + " text," +
                    KEY_RELEASE_DATE + " text," +
                    KEY_POSTER + " text," +
                    KEY_VOTE_AVERAGE + " float," +
                    KEY_OVERVIEW + " text" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
