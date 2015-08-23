package portfolio.nanodegree.android.ntippa.project1;

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
import java.util.prefs.Preferences;

/**
 * Created by Nalini on 7/24/2015.
 * utility class to perform mundane tasks of fetching movies data from theMovieDB
 */
public class MoviesFetcher {

    public static final String TAG = MoviesFetcher.class.getSimpleName();

    private static final String ENDPOINT = "http://api.themoviedb.org/3/discover/movie?";
    private static final String API_KEY = "";//empty string on purpose
    private static final String SORT_ORDER = "popularity.desc";
    private static final String NUM_OF_PAGES = "1";

    public  ArrayList<GalleryItem> fetchMovies() {

        ArrayList<GalleryItem> items = new ArrayList<>();
        Log.d(TAG, " beginning fetchMovie");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try{


            Uri builtUri = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("sort_by",SORT_ORDER)
                    .appendQueryParameter("page",NUM_OF_PAGES)
                    .appendQueryParameter("api_key", API_KEY).build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG,"built URL" + url);


            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

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

            moviesJsonStr = buffer.toString();

            Log.d(TAG,"movies  JSON:" + moviesJsonStr);

            Log.d(TAG,"Before parsing moviesJson String");

            try{
                //parsing the MoviesJSON,constructing Movie items
                items = parseJson(items, moviesJsonStr);
            }catch(JSONException e){
                Log.d(TAG,"JSON exception when parsing movies jSon");//todo: if this is correct way
                e.printStackTrace();
            }

            Log.d(TAG,"Items count:" + items.size());

        } catch (IOException e) {
            Log.d(TAG,"Exception in Placehilder fragment");
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
        }

        return items;
    }

    //constructing Movie items from the JSOn string
    private ArrayList<GalleryItem> parseJson(ArrayList<GalleryItem> items, String moviesJsonStr) throws JSONException {

        Log.d(TAG," entering parseJson");
        //these are the names of jSON objects that needs to be extracted
        final String RESULTS = "results";
        final String MOVIES = "movies";

        Log.d(TAG,"1");
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        Log.d(TAG,"2");
       // Log.d(TAG,"moviesJSON details" + moviesJson.toString());
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
        Log.d(TAG,"3");

        for(int i = 0; i < moviesArray.length(); i++){

            JSONObject movieObject = moviesArray.getJSONObject(i);
            Log.d(TAG,"movieObject details::");
            Log.d(TAG,"movie" + i + "title:" + movieObject.getString("title"));

            Log.d(TAG,"adding to items");
            GalleryItem item = new GalleryItem();
           //todo: chk this.chk if u need to generate ne wone
            item.setmId(movieObject.getString("id"));
            item.setmDescription(movieObject.getString("overview"));
            item.setmPosterUrl(movieObject.getString("poster_path"));
            item.setmTitle(movieObject.getString("title"));
            item.setmRating(movieObject.getString("vote_average"));//chk this
            item.setmPopularity(movieObject.getString("popularity"));
            item.setmReleaseDate(movieObject.getString("release_date"));
           /* try{
                item.setmReleaseDate(new SimpleDateFormat("yyyy-dd-MM").parse(movieObject.getString("release_date")).toString());
            }catch(ParseException e){
                e.printStackTrace();
            }
*/



            items.add(item);
            Log.d(TAG," item added");
        }


         Log.d(TAG,"Done parsing JSON");

        return items;
    }

}
