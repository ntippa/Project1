package portfolio.nanodegree.android.ntippa.project1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorCompletionService;
import java.util.prefs.Preferences;

import portfolio.nanodegree.android.ntippa.project1.data.MoviesContract;
import portfolio.nanodegree.android.ntippa.project1.model.GalleryItemDetail;

/**
 * Created by Nalini on 7/24/2015.
 * utility class to perform mundane tasks of fetching movies data from theMovieDB
 * //TODO: use RETROFIT next iteration
 */
public class MoviesFetcher {

    public static final String TAG = MoviesFetcher.class.getSimpleName();

    private static final String ENDPOINT = "http://api.themoviedb.org/3/discover/movie?";
    private static final String MOVIE_METADATA_ENDPOINT = "http://api.themoviedb.org/3/movie";

    private static final String API_KEY = "0f4fa69552a469f69fd2ded5676d0c4e";//empty string on purpose
    private static final String SORT_ORDER = "popularity.desc";
    private static final String NUM_OF_PAGES = "1";
    private static final int FAVOURITE_DEFAULT = 0;// means not a favourite

    private final Context mContext;


    public MoviesFetcher(Context mContext) {
        this.mContext = mContext;
    }

    public  void fetchMovies() {

        ArrayList<GalleryItem> items = new ArrayList<>();
        Log.d(TAG, " beginning fetchMovie");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        InputStream inputStream = null;
        try{


            Uri builtUri = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("sort_by",SORT_ORDER)
                    .appendQueryParameter("page",NUM_OF_PAGES)
                    .appendQueryParameter("api_key", API_KEY).build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG,"built URL" + url);


            // Will contain the raw JSON response as a string.
            String moviesJsonStr;// = null;

            // Create the request to theMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.d(TAG,"inutStream is null");//todo: what to do if inputstream null
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //return null;
                Log.d(TAG,"Buffer length is null");
            }

            moviesJsonStr = buffer.toString();

            Log.d(TAG,"movies  JSON:" + moviesJsonStr);

            Log.d(TAG,"Before parsing moviesJson String");

            try{
                //parsing the MoviesJSON,constructing Movie items
                parseMoviesJson(items, moviesJsonStr);
            }catch(JSONException e){
                Log.d(TAG,"JSON exception when parsing movies jSon");//todo: if this is correct way
                e.printStackTrace();
            }

            Log.d(TAG,"Items count:" + items.size());

        } catch (Exception e) {
            Log.d(TAG,"Exception in inputstream,");
            e.printStackTrace();
            // If the code didn't successfully get th emovie data, there's no point in attemping
            // to parse it.
            //return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch(final IOException e){
                    Log.e(TAG," Error closing input stream", e);
                }

            }
        }

//        return items;
    }

    private void parseMoviesJson(ArrayList<GalleryItem> items, String moviesJsonStr) throws JSONException {

        Log.d(TAG," entering parseJson");
        //these are the names of jSON objects that needs to be extracted
        final String RESULTS = "results";
        final String MOVIES = "movies";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Log.d(TAG,"1");
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        Log.d(TAG,"2");
        // Log.d(TAG,"moviesJSON details" + moviesJson.toString());
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        Log.d(TAG,"3");

        Log.d(TAG,"MoviesArray length/size:" + moviesArray.length());
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());//Insert new Movies list into db

        for(int i = 0; i < moviesArray.length(); i++) {

            JSONObject movieObject = moviesArray.getJSONObject(i);
            Log.d(TAG, "movieObject details::");
            Log.d(TAG, "movie" + i + "title:" + movieObject.getString("title"));

            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_ID, movieObject.getInt("id"));
            movieValues.put(MoviesContract.Movies.COLUMN_DESC, movieObject.getString("overview"));
            movieValues.put(MoviesContract.Movies.COLUMN_POSTER_URL, movieObject.getString("poster_path"));
            movieValues.put(MoviesContract.Movies.COLUMN_TITLE, movieObject.getString("title"));
            movieValues.put(MoviesContract.Movies.COLUMN_RATING, movieObject.getDouble("vote_average"));//real in db
            movieValues.put(MoviesContract.Movies.COLUMN_POPULARITY, movieObject.getDouble("popularity"));//real in db
            movieValues.put(MoviesContract.Movies.COLUMN_FAVOURITE, Utility.DEFAULT_FAVOURITE);//todo:if there is a better way to insert this value

            //time
            try {
                Date date = sdf.parse(movieObject.getString("release_date"));
                long release_date = date.getTime();
                movieValues.put(MoviesContract.Movies.COLUMN_RELEASE_DATE, MoviesContract.normalizeDate(release_date));//date stored as long/integer values

            } catch (ParseException e) {
                e.printStackTrace();
            }

            cVVector.add(movieValues);
            Log.d(TAG, " Movie added");
        }

        Log.d(TAG,"cVVVector size::" + cVVector.size());
            int inserted = 0;
            if(cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.Movies.CONTENT_URI,cvArray);
            }

           Log.d(TAG,"Movies Fecthed " + inserted + "inserted");
            Log.d(TAG,"Done parsing JSON");


    }


    private ArrayList<String> parseVideoURLsJson(String videoURLsJsonStr) throws JSONException{//todo:does it need to return the ArrayList
        Log.d(TAG," Begin parsing videoURL Json");
        ArrayList<String> videoURLs = new ArrayList<>();

        Log.d(TAG,"1");
        JSONObject videoURLsJson = new JSONObject(videoURLsJsonStr);
        Log.d(TAG,"2");
        // Log.d(TAG,"moviesJSON details" + moviesJson.toString());
        JSONArray jsonArray = videoURLsJson.getJSONArray("results");
        Log.d(TAG,"3");

        for(int i = 0; i < jsonArray.length(); i++) {

            String complete_videoURL;
            JSONObject videoURLsObject = jsonArray.getJSONObject(i);//STOPPED HERE 12:35

            Log.d(TAG, "video URL details::");
            Log.d(TAG, "movie:" + i + videoURLsObject.getString("id"));
            Log.d(TAG, "Video URL type :" + videoURLsObject.getString("type"));
            Log.d(TAG, "Video URL site:" + i + videoURLsObject.getString("site"));
            Log.d(TAG, "Video URL key:" + i + videoURLsObject.getString("key"));

            // ArrayList<String> reviews = new ArrayList<String>();
            //todo: constructing video url string:chk??
          //  String complete_videoURL = videoURLsObject.getString("id") + videoURLsObject.getString("type") + videoURLsObject.getString("site") + videoURLsObject.getString("key");
            if(videoURLsObject.getString("site").equalsIgnoreCase("YouTube")){//todo: if not Youtube,then what??Check videoJSON/API
               complete_videoURL = "https://www.youtube.com/watch?v=" + videoURLsObject.getString("key");
                Log.d(TAG,"complete videoURL::" + complete_videoURL);
                videoURLs.add(complete_videoURL);
            }
           // String complete_videoURL =  videoURLsObject.getString("site") +"," + videoURLsObject.getString("key");


           //todo: do we need to store the Video URLS to GalleryItemDetail??
//            GalleryItemDetail item = new GalleryItemDetail();
//            //todo: chk this.chk if u need to generate ne wone
//            item.setItemId(movieObject.getString("id"));
//            item.setReviews(reviews);
        }


        return videoURLs;//todo??
    }
    //utility function to parse Reviews jSon
    private ArrayList<String> parseReviewsJson(ArrayList<String> reviews,String reviewsJsonStr) throws JSONException{

        Log.d(TAG," BeGIN parsing Reviews JSon");
        //these are the names of jSON objects that needs to be extracted
        final String RESULTS = "results";


        Log.d(TAG,"1");
        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        Log.d(TAG,"2");
        // Log.d(TAG,"moviesJSON details" + moviesJson.toString());
        JSONArray jsonArray = reviewsJson.getJSONArray(RESULTS);
        Log.d(TAG,"3");

        for(int i = 0; i < jsonArray.length(); i++) {

            JSONObject movieObject = jsonArray.getJSONObject(i);//STOPPED HERE 12:35

            Log.d(TAG, "movieObject details::");
            Log.d(TAG, "movie" + i);
            Log.d(TAG, "review :" + movieObject.getString("content"));

            // ArrayList<String> reviews = new ArrayList<String>();
            reviews.add(movieObject.getString("content"));

            Log.d(TAG, "adding to reviews");

            //todo: do we need to store reviews to GalleryItemDeatil??
            //todo: if so, can we check is GalleryItemDetail object exists and create one conditionally??
//            GalleryItemDetail item = new GalleryItemDetail();
//            //todo: chk this.chk if u need to generate ne wone
//            item.setItemId(movieObject.getString("id"));
//            item.setReviews(reviews);
        }
        return reviews;//todo:
    }

    //Fetch reviews from theMovieDB
    public ArrayList<String> fetchReviews(int id){//todo:chk return type


        int MOVIE_ID = id;
        ArrayList<String> reviews = new ArrayList<>();
        Log.d(TAG, " fetching reviews now");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{

            Uri builtUri = Uri.parse(MOVIE_METADATA_ENDPOINT).buildUpon()
                    .appendPath(String.valueOf(MOVIE_ID))
                    .appendPath("reviews")     //http://api.themoviedb.org/3/movie/id/reviews
                    .appendQueryParameter("api_key", API_KEY).build();
            URL url = new URL(builtUri.toString());
            Log.d(TAG,"reviews URL" + url);

            // Will contain the raw JSON response as a string.
            String reviewsJsonStr;// = null;

            // Create the request to theMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.d(TAG,"inutStream is null");
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //return null;
                Log.d(TAG,"Buffer length is null");
            }

            reviewsJsonStr = buffer.toString();

            Log.d(TAG,"Reviews JSON:" + reviewsJsonStr);

            Log.d(TAG,"Before parsing reviewsJson");

            try{
                //parsing the MoviesJSON,constructing Movie items
                reviews = parseReviewsJson(reviews, reviewsJsonStr);
            }catch(JSONException e){
                Log.d(TAG,"JSON exception when parsing  ReviewsjSon");//todo: if this is correct way
                e.printStackTrace();
            }

            Log.d(TAG,"No. of Reviews :" + reviews.size());



        }catch(Exception e){

            Log.d(TAG,"Exception with reviews inputstream");
            e.printStackTrace();

        }finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Fetching error", "Error closing stream", e);
                }
            }
        }

        return reviews;
    }

    //Fetch videoURLS from theMovieDB endpoint
    public ArrayList<String> fetchVideoURLs(int movie_id){

        ArrayList<String> videoURLs = new ArrayList<>();//

        Log.d(TAG," beginning fetching video URLs");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse(MOVIE_METADATA_ENDPOINT).buildUpon()
                    .appendPath(String.valueOf(movie_id))
                    .appendPath("videos")     //http://api.themoviedb.org/3/movie/id/reviews
                    .appendQueryParameter("api_key", API_KEY).build();
            URL url = new URL(builtUri.toString());
            Log.d(TAG,"videos URL" + url);

            // Will contain the raw JSON response as a string.
            String videoURLsJsonStr = null;

            // Create the request to theMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.d(TAG,"inutStream is null");
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //return null;
                Log.d(TAG,"Buffer length is null");
            }

            videoURLsJsonStr = buffer.toString();

            Log.d(TAG,"videoURLs JSON:" + videoURLsJsonStr);
            Log.d(TAG,"Before parsing videoURLs Json");

            try{
                //parsing the videoURLs JSON
                videoURLs = parseVideoURLsJson(videoURLsJsonStr);
            }catch(JSONException e){
                Log.d(TAG,"JSON exception when parsing  ReviewsjSon");//todo: if this is correct way
                e.printStackTrace();
            }

        }catch(Exception e){

        }finally{

        }

        return videoURLs;
    }





}
