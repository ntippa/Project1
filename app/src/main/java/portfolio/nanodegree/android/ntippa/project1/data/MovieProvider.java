package portfolio.nanodegree.android.ntippa.project1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Nalini on 10/2/2015.
 */
public class MovieProvider extends ContentProvider {

    public static final String TAG = MovieProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDBHelper mHelper;

    static final int MOVIES = 100;
    static final int MOVIE_DETAILS = 101;


//    static final int VIDEOS_FOR_MOVIE = 300;

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        //* matches string, # matches number
        sUriMatcher.addURI(authority, MoviesContract.PATH_MOVIES,MOVIES);
        sUriMatcher.addURI(authority,MoviesContract.PATH_MOVIES + "/*" ,MOVIES);// movie/popular gives list of popular movies
       sUriMatcher.addURI(authority,MoviesContract.PATH_MOVIE_DETAILS,MOVIE_DETAILS);
      //  sUriMatcher.addURI(authority,MoviesContract.PATH_MOVIE_DETAILS + "/#/*",MOVIE_DETAILS); // movie/12345/details

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new MoviesDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        boolean useDistinct = true;
        switch (sUriMatcher.match(uri)) {
            // "movies/*/*"
            case MOVIES: {

                retCursor = mHelper.getReadableDatabase().query(useDistinct,
                        MoviesContract.Movies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        "100");

//                retCursor = mHelper.getReadableDatabase().query(useDistinct,MoviesContract.Movies.TABLE_NAME,
//                        projection
//                        , selection
//                        , selectionArgs
//                        , null
//                        , null
//                        , sortOrder);
//
                break;
            }
            case MOVIE_DETAILS:{
                retCursor = mHelper.getReadableDatabase().query(MoviesContract.MovieDetails.TABLE_NAME,
                        projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIES:
                return MoviesContract.Movies.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MoviesContract.MovieDetails.CONTENT_TYPE;
                //return MoviesContract.Movies.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
               // normalizeDate(values);
                long _id = db.insert(MoviesContract.Movies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.Movies.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case MOVIE_DETAILS: {
                long _id = db.insert(MoviesContract.MovieDetails.TABLE_NAME,null,values);
                if(_id > 0){
                    returnUri = MoviesContract.MovieDetails.builDetailsUri(_id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Start by getting a writable database
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null)
            selection = "1";// returns the count of rows.In this case all rows since the no selection means all rows

        switch (match) {

            case MOVIES: {
                rowsDeleted = db.delete(MoviesContract.Movies.TABLE_NAME, selection, selectionArgs);
                /*if (rowsDeleted == 0) {
                    throw new android.database.SQLException("Failed to delete rows " + uri);
                }*/
                break;
            }

            case MOVIE_DETAILS: {
                rowsDeleted = db.delete(MoviesContract.MovieDetails.TABLE_NAME, selection, selectionArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);

        }

        // A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match){
            case MOVIES:{
                normalizeDate(values);
                rowsUpdated = db.update(MoviesContract.Movies.TABLE_NAME,values,selection,selectionArgs);
                /*if(rowsUpdated == 0 ){
                    throw new android.database.SQLException("Failed to update rows" + uri);
                }*/
                break;

            }
            case MOVIE_DETAILS:{

                rowsUpdated = db.update(MoviesContract.MovieDetails.TABLE_NAME,values,selection,selectionArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);//notify when rows changed or when there are items to be updated
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);


        switch(match){
            case MOVIES:
                Log.d(TAG,"BulkInser");
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value: values){
                        long _id = db.insert(MoviesContract.Movies.TABLE_NAME,null,value);
                        if(_id != 0){
                            returnCount++;
                        }

                    }
                    db.setTransactionSuccessful();

                }finally{
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;


            default:
                return super.bulkInsert(uri, values);
        }

    }

    //    private Cursor getWeatherByLocationSettingAndDate(
//            Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
//        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                sLocationSettingAndDaySelection,
//                new String[]{locationSetting, Long.toString(date)},
//                null,
//                null,
//                sortOrder
//        );
//    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MoviesContract.Movies.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MoviesContract.Movies.COLUMN_RELEASE_DATE);
            values.put(MoviesContract.Movies.COLUMN_RELEASE_DATE,MoviesContract.normalizeDate(dateValue));
        }
    }
}
