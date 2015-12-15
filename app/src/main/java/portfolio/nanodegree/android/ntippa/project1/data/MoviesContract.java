package portfolio.nanodegree.android.ntippa.project1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by Nalini on 10/1/2015.
 */
public class MoviesContract {

     static final String TAG =MoviesContract.class.getSimpleName() ;

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "portfolio.nanodegree.android.ntippa.project1";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://portfolio.nanodegree.android.ntippa.project1/movies/ is a valid path for
    //i chose to match the uri's to match those on theMovieDB
    ///movie/popular; /movie/{id}; /movie/{id}/review etc

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE_DETAILS = "details";

    public static final class Movies implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        // Column with the foreign key into the details table.
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //title
        public static final String COLUMN_TITLE = "title";

        //posterUrl
        public static final String COLUMN_POSTER_URL = "posterURL";

        //description
        public static final String COLUMN_DESC = "description";

        //rating
        public static final String COLUMN_RATING = "rating";

        //popularity
        public static final String COLUMN_POPULARITY = "popularity";

        //release_date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //favourite
        public static final String COLUMN_FAVOURITE = "favourite";


        //ex: content://portfolio.nan...../movie/12345
        public static Uri buildMovieUri(long id) {
            Log.d(TAG, "movieURI with id:::" + String.valueOf(ContentUris.withAppendedId(CONTENT_URI, id)));
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildMoviePreferenceUri(String preference){
            Log.d(TAG,"Movie Preference::" + CONTENT_URI.buildUpon().appendPath(preference).build());
            return CONTENT_URI.buildUpon().appendPath(preference).build();
        }

    }

    public static final class MovieDetails implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAILS).build();// todo: check this URI

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAILS;

        public static final String TABLE_NAME = "details";

        public static final String COLUMN_MOVIE_KEY= "movie_id";

        public static final String COLUMN_MOVIE_REVIEWS = "reviews";

        public static final String COLUMN_MOVIE_TRAILERS_URL = "trailersURL";


        public static Uri builDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDetailsQuerySettingFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
