package portfolio.nanodegree.android.ntippa.project1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nalini on 10/1/2015.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 10;//todo: how/where to update database, database version if altering table.

    static final String DATABASE_NAME = "movies.db";
    private static final String TAG = MoviesDBHelper.class.getSimpleName() ;

    public MoviesDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "DB: onCreate");

        //todo: delete tables  for now. Change later
       // db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieDetails.TABLE_NAME);
       // db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.Movies.TABLE_NAME);

        // Create a table to hold movies.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.Movies.TABLE_NAME + " (" +
                MoviesContract.Movies.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
               // MoviesContract.Movies.COLUMN_MOVIE_ID + " INTEGER  NOT NULL, " +
                MoviesContract.Movies.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.Movies.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MoviesContract.Movies.COLUMN_DESC + " TEXT NOT NULL, " +
                MoviesContract.Movies.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesContract.Movies.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesContract.Movies.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MoviesContract.Movies.COLUMN_FAVOURITE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_MOVIE_DETAILS_TABLE = "CREATE TABLE " + MoviesContract.MovieDetails.TABLE_NAME + " (" +
               // MoviesContract.MovieDetails.COLUMN_MOVIE_KEY + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieDetails.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
               // MoviesContract.MovieDetails.COLUMN_MOVIE_REVIEWS + " TEXT NOT NULL, " +
                MoviesContract.MovieDetails.COLUMN_MOVIE_REVIEWS + " TEXT, " +
               // MoviesContract.MovieDetails.COLUMN_MOVIE_TRAILERS_URL + " TEXT NOT NULL, " +
                MoviesContract.MovieDetails.COLUMN_MOVIE_TRAILERS_URL + " TEXT, " +

                // Set up the movie_id column as a foreign key to movies table.
                " FOREIGN KEY (" + MoviesContract.MovieDetails.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MoviesContract.Movies.TABLE_NAME + " (" + MoviesContract.Movies.COLUMN_MOVIE_ID + ")) ";
               // " )";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        Log.d(TAG,"created movies table");
        db.execSQL(SQL_CREATE_MOVIE_DETAILS_TABLE);
        Log.d(TAG,"created movie_details table");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieDetails.TABLE_NAME);
        Log.d(TAG," dropped MovieDetails table");
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.Movies.TABLE_NAME);
        Log.d(TAG,"dropped Movie table");
        onCreate(db);

    }
}
