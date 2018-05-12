package mkruglikov.popularmovies.utilites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favoriteMoviesDb";
    public static final String TABLE_NAME = "movies";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_POSTER = "poster";
    public static final String KEY_VOTE_AVERAGE = "voteAverage";
    public static final String KEY_OVERVIEW = "overview";

    public DBHelper(Context context) {
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
