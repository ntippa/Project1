package portfolio.nanodegree.android.ntippa.project1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import portfolio.nanodegree.android.ntippa.project1.data.MoviesContract;

/**
 * Created by Nalini on 8/11/2015.
 * Fragment projecting details of each Gallery item
 * 11/9: stopped at addToDB();
 */
public class DetailsFragment extends Fragment {

    public static final String TAG = DetailsFragment.class.getSimpleName();
    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE = "w185";
    public static final String EXTRA_MOVIE_ID = "";//todo: does this need to be empty
    public static final String EXTRA_MOVIE_ITEM = "movie";

    ShareActionProvider mShareActionProvider;
    private String mShareVideoURL;
    ArrayList<String> trailer_text = new ArrayList<>();
    final HashMap<Integer,String> URLMap = new HashMap<>();

    ArrayAdapter<String> mReviewsAdapter, mVideoUrlAdapter;

    GalleryItem mItem;
    TextView mTitle_View, mOverview_View, mDetail_View, mRating;
    ImageView mPoster_View;
    Button favourite_button;
    ImageButton trailer_button;
    ListView mReviewsListView,mVideoUrlListView;
    Cursor reviews_cursor;

    //Reviews as  variable
    //saved to InstanceState to handle config changes
    ArrayList<String> mReviews, mVideoURLs;

    //todo:Video Urls as variable
    //saved to instanceState to handle config changes

    //projection columns
    private static final String[] REVIEWS_PROJECTION = {
            "rowid _id",//_id needed for content provider,
            MoviesContract.MovieDetails.COLUMN_MOVIE_KEY,
            MoviesContract.MovieDetails.COLUMN_MOVIE_REVIEWS,
            MoviesContract.MovieDetails.COLUMN_MOVIE_TRAILERS_URL
    };
//projection column indexes used to query cursor
    static final int COL_ID = 0;
    static final int COL_MOVIE_KEY = 1;
    static final int COL_MOVIE_REVIEWS = 2;
    static final int COL_MOVIE_TRAILERS_URL = 3;


    public DetailsFragment() {
        Log.d(TAG,"DetailsFragment default constructor");
       // super();
    }

    //instance of fragment with args
    public static DetailsFragment newInstance(GalleryItem item){
        Log.d(TAG,"newInstance");
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MOVIE_ITEM,item);
       // args.putStringArrayList();

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.d(TAG,"Obtaining from savedInstanceState");
            mItem = savedInstanceState.getParcelable("MOVIE_ITEM");
            Log.d(TAG,"Movie item from savedInstanceState");
            mReviews = savedInstanceState.getStringArrayList("REVIEWS");
            Log.d(TAG,"Reviews from savedInstanceState");
            mVideoURLs = savedInstanceState.getStringArrayList("VideoURLS");
            Log.d(TAG,"VideoURLs from savedInstanceState");
        }

        // mItem = getActivity().getIntent().getParcelableExtra(EXTRA_MOVIE_ITEM);
        mItem = (GalleryItem)getArguments().getParcelable(EXTRA_MOVIE_ITEM);

        if(mItem != null){
            Log.d(TAG," Fetching reviews for movie" + mItem.getmTitle() + " ID::" + mItem.getId());
        }else{
            Log.d(TAG,"Movie details not received as arguemnt ");
        }


        //check db for reviews, if not found, then fetch them from internet
        reviews_cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieDetails.CONTENT_URI,
                REVIEWS_PROJECTION,
                MoviesContract.MovieDetails.COLUMN_MOVIE_KEY + "= ?",
                new String[]{Integer.toString(mItem.getId())},//todo:
                null
        );

        //if reviews for movie exist in db, iterate thru the db cursor and populate the reviews
        if(reviews_cursor.getCount() > 0){
            Log.d(TAG," Reviews exist for this movie in db:::" + reviews_cursor.getCount());

            mReviews = new ArrayList<>(reviews_cursor.getCount());


            reviews_cursor.moveToFirst();

            //reading reviews from cursor
            while(!reviews_cursor.isAfterLast()){

                mReviews.add(reviews_cursor.getString(COL_MOVIE_REVIEWS));
                reviews_cursor.moveToNext();
                Log.d(TAG,"Review added");
            }

            mReviewsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mReviews);
            //mReviewsListView.setAdapter(reviewsAdapter);

        }else{
            Log.d(TAG," No reviews exist for this movie");
            Log.d(TAG," Fetching reviews from Internet");
            new ReviewsFetcher().execute(mItem.getId());//todo: try/catch??
        }


        Log.d(TAG, "Initiating video URL fetching");
        new VideoURLFetcher().execute(mItem.getId());//todo: should i save URLs also to the db and retrieve from db like reviews

        setHasOptionsMenu(true);
        setRetainInstance(true);

        //  reviews_cursor.close();//
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        mReviews = new ArrayList<>();

        View view = inflater.inflate(R.layout.details_fragment,container,false);

        Toolbar actionBar = (Toolbar) view.findViewById(R.id.appBar);
        actionBar.setTitle("Movie Details");

        ((AppCompatActivity)getActivity()).setSupportActionBar(actionBar);

        assert ((AppCompatActivity) getActivity()).getSupportActionBar() != null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if(NavUtils.getParentActivityName(getActivity()) != null){
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }



        mPoster_View = (ImageView) view.findViewById(R.id.poster);
        String posterUrl = mItem.getmPosterUrl();

        StringBuffer string_url = new StringBuffer(POSTER_BASE_URL);
        string_url.append(POSTER_SIZE);
        string_url.append(posterUrl);

        Log.d(TAG, "Image URL");
        Log.d(TAG,string_url.toString());


        try { // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(getActivity()) //
                    .load(string_url.toString()) //
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.error) //
                    .fit() //
                    .tag(getActivity()) //
                    .into(mPoster_View);
        }catch(Exception e){
            Log.d(TAG,"Eception in Picasso loading image url");
            e.printStackTrace();
        }

        mTitle_View = (TextView) view.findViewById(R.id.title);
        mTitle_View.setText(mItem.getmTitle());

        mDetail_View = (TextView) view.findViewById(R.id.year);
        String[] s = mItem.getmReleaseDate().split("-");

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd hh:mm:ss z yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM yyyy");
        try{
            Date releaseDt = sdf.parse(s[0]);
            Log.d(TAG,"Formatted release date" + releaseDt.toString());
            Log.d(TAG,"New Format::" + newFormat.format(releaseDt).toString());
            mDetail_View.setText(newFormat.format(releaseDt).toString());
            Log.d(TAG,"Release Date set");
        }catch(ParseException e){
            Log.e(TAG," Release date parse exception");
            e.printStackTrace();
        }

        String rating = mItem.getmRating() + " / 10 ";
        Log.d(TAG,"Rating::" + rating);
      mRating = (TextView) view.findViewById(R.id.rating);
        mRating.setText(rating);

        mOverview_View = (TextView) view.findViewById(R.id.detail2);
        mOverview_View.setText(mItem.getmDescription());

        //mReview_textView = (TextView)view.findViewById(R.id.reviews);

        mReviewsListView = (ListView) view.findViewById(R.id.reviewsListView);
        //ArrayAdapter<String> reviewsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mReviews);
        mReviewsListView.setAdapter(mReviewsAdapter);


       // mVideoURLs_textView = (TextView) view.findViewById(R.id.trailersText);
        mVideoUrlListView = (ListView) view.findViewById(R.id.trailersListView);

       // ArrayAdapter<String> videoURLAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_view_item,R.id.trailer,trailer_text);
        mVideoUrlAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_view_item,R.id.trailer,trailer_text);
        Log.d(TAG," constructed videoAdapter");
        mVideoUrlListView.setAdapter(mVideoUrlAdapter);
        Log.d(TAG,"VideoUrlListView adapter set");


        favourite_button = (Button) view.findViewById(R.id.favourite_button);// movie_id stored to shared preferences.
        favourite_button.setOnClickListener(new View.OnClickListener(){//query the moviedb for movie details of movies stored as favs

            @Override
            public void onClick(View v) {

                String favourites = Utility.getFavouritePreference(getActivity());
                Log.d(TAG, "Favourites from Shared Prefs::" + favourites);
                String selected_as_fav = String.valueOf(mItem.getId());


                if (favourites.contains(selected_as_fav)) {
                    Toast.makeText(getActivity(), "Its already Ur Favourite!!", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getActivity(), "Saving to Ur Favourites!!", Toast.LENGTH_SHORT).show();
                    addToFavourite(getActivity(), mItem);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onActivityCreated");
        super.onActivityCreated(savedInstanceState);

//        if(savedInstanceState != null){
//            Log.d(TAG,"Obtaining from savedInstanceState");
//            mItem = savedInstanceState.getParcelable("MOVIE_ITEM");
//            Log.d(TAG,"Movie item from savedInstanceState");
//            mReviews = savedInstanceState.getStringArrayList("REVIEWS");
//            Log.d(TAG,"Reviews from savedInstanceState");
//            mVideoURLs = savedInstanceState.getStringArrayList("VideoURLS");
//            Log.d(TAG,"VideoURLs from savedInstanceState");
//        }
    }

    //creates intent with videoURL and shared as Action bar item
    private Intent createShareIntent(String videoURL_to_share){
        Log.d(TAG,"createShareIntent");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,videoURL_to_share);
        return shareIntent;

    }


    //store the info to db
    //stored the movie id to shared  preferences
    //todo: how about to db??
    private void addToFavourite(Context context,GalleryItem item) {
        //step 1: check if this is already a favourite
//        String favourites = Utility.getFavouritePreference(context);
//        Log.d(TAG,"Favourites from Shared Prefs::" + favourites);
//        String selected_as_fav = String.valueOf(item.getId());
//
//
//        if(favourites.contains(selected_as_fav)){
//            Toast.makeText(context,"Its already Ur Favourite!!",Toast.LENGTH_SHORT).show();
//        }else{
            //step2:
            Log.d(TAG," Setting favourite value to true ");
            ContentValues updateValues = new ContentValues();
            updateValues.put(MoviesContract.Movies.COLUMN_FAVOURITE,Utility.SET_AS_FAVOURITE);
            int count = context.getContentResolver().update(
                    MoviesContract.Movies.CONTENT_URI,
                    updateValues,
                    MoviesContract.Movies.COLUMN_MOVIE_ID + "= ?",
                    new String[]{Integer.toString(item.getId())}
            );
            Log.d(TAG,"Marked as favourite in DB");

            //Step3: add selected movie to shared pref
            if(Utility.setFavouritePreference(context,Integer.toString(item.getId()))){
                Log.d(TAG,"Stored movie as favourite::" + item.getId());
                Toast.makeText(context,"Added to favourites",Toast.LENGTH_SHORT);
            }

            //step4:add reviews of Favourite movies to db
            //todo: if reviews present, then proceed to add.
            //      else show a msg that reviews are unavaibale, when user clicks a favourite movie for details
            //todo: I am not sure how to distinguish between if a user clicked a favourite movie for its details vs
            //todo: if user clicked a regular movie for its details.Maybe, check if the movie_id exists in reviews table and fetch it from there if present.
            //todo: if not fetch over net.
            if(mReviews.size() > 0){
                Log.d(TAG,"Reviews present");

                Vector<ContentValues> cVVector = new Vector<ContentValues>(mReviews.size());// one row for every review

                for(String review : mReviews){
                    ContentValues reviewValues = new ContentValues();
                    reviewValues.put(MoviesContract.MovieDetails.COLUMN_MOVIE_KEY,item.getId());
                    reviewValues.put(MoviesContract.MovieDetails.COLUMN_MOVIE_REVIEWS,review);
                    cVVector.add(reviewValues);
                    Log.d(TAG,"reviews added");
                }

                Log.d(TAG,"cVVVector size::" + cVVector.size());
                int inserted = 0;
                if(cVVector.size() > 0){
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getActivity().getContentResolver().bulkInsert(MoviesContract.MovieDetails.CONTENT_URI,cvArray);
                }

                Log.d(TAG,"Favourite movie reviews :" + inserted + "inserted");

            }

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreateOptionsMenu");

        inflater.inflate(R.menu.menu_detail,menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null ){// check if videoURL exists at this time.todo: can we set this shareIntent at any other POE??
            mShareActionProvider.setShareIntent(createShareIntent(mShareVideoURL));
            Log.d(TAG,"shareIntent created");
        }else{
            Log.d(TAG,"Share Action provider is null");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected");

        switch(item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //free resources like bitmaps, cursors s
    @Override
    public void onDestroyView() {
        Log.d(TAG,"onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        Log.d(TAG,"OnStop");
        Log.d(TAG,"Closing cursors here");
        reviews_cursor.close();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"onSaveInstanceState");
        outState.putParcelable("MOVIE_ITEM", mItem);
        outState.putStringArrayList("REVIEWS", mReviews);
        outState.putStringArrayList("VideoURLS",mVideoURLs);
        outState.putStringArrayList("TrailerText",trailer_text);
        Log.d(TAG,"reviews and videoURLs saved to instance state");
        super.onSaveInstanceState(outState);
    }

    private class ReviewsFetcher extends AsyncTask<Integer,Void,ArrayList<String>>{//todo: do we return Arraylist<String> or Void

     final String TAG = ReviewsFetcher.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            int movie_id = Integer.valueOf(params[0]);
            Log.d(TAG," ReviewsFetcher: queried Id:" + movie_id);
            mReviews = new MoviesFetcher(getActivity()).fetchReviews(movie_id);
            return mReviews;//todo:
        }

        @Override
    protected void onPostExecute(ArrayList<String> reviews) {

        if(reviews != null && reviews.size() > 0) {
            mReviews = reviews;
            Log.d(TAG,"REVIEWS received:");

        }else {
            mReviews = new ArrayList<String>();
            mReviews.add("No Reviews available");
            Toast.makeText(getActivity(),"Oops!!No reviews here!!",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Reviews not available");

        }
            mReviewsAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mReviews);
            mReviewsListView.setAdapter(mReviewsAdapter);

    }


}

    private class VideoURLFetcher extends AsyncTask<Integer,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            ArrayList<String> videoURLs = new ArrayList<>();
            int movie_id = Integer.valueOf(params[0]);
            Log.d(TAG,"Asynchronously fetching video URLs");
            Log.d(TAG,"Movie id::" + movie_id);

            mVideoURLs = new MoviesFetcher(getActivity()).fetchVideoURLs(movie_id);
            Log.d(TAG,"Done fetcing video URLS");

            return videoURLs;
        }

        @Override
        protected void onPostExecute(ArrayList<String> videoURLs) {
            Log.d(TAG,"No.Of Video URLs" + mVideoURLs.size());

//            ArrayList<String> trailer_text = new ArrayList<>(mVideoURLs.size());
            //final HashMap<String,String> URLMap = new HashMap<>(mVideoURLs.size());
            final HashMap<Integer,String> URLMap = new HashMap<>(mVideoURLs.size());

            if(mVideoURLs != null && mVideoURLs.size() > 0){


                mShareVideoURL = mVideoURLs.get(0);//share first Video URL
                if(mShareActionProvider != null){// check if videoURL exists at this time.todo: can we set this shareIntent at any other POE??
                    mShareActionProvider.setShareIntent(createShareIntent(mShareVideoURL));
                    Log.d(TAG,"shareIntent created");
                }else{
                    Log.d(TAG,"Share Action provider is null");
                }


                //Effort to display URLS
                int i = 0;
                for(String s : mVideoURLs){
                    //URLMap.put(s,"Trailer" + i);
                    URLMap.put(i,s);
                    trailer_text.add("Trailer " + (i + 1));
                   // Log.d(TAG,"i:")

                    i++;
                }
//                ArrayAdapter<String> videoURLAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_view_item,R.id.trailer,trailer_text);
//                Log.d(TAG," constructed videoAdapter");
//                mVideoUrlListView.setAdapter(videoURLAdapter);
//                Log.d(TAG,"VideoUrlListView adapter set");
                mVideoUrlAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_view_item,R.id.trailer,trailer_text);
                Log.d(TAG," constructed videoAdapter");
                mVideoUrlListView.setAdapter(mVideoUrlAdapter);
                Log.d(TAG,"VideoUrlListView adapter set");

                mVideoUrlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String)parent.getItemAtPosition(position);
                        Log.d(TAG,"list item::" + item);
                        String videoUrl = URLMap.get(position);//URLMap.get(item);
                        Log.d(TAG,"videoURL retrieved from hashmap:::" + videoUrl);
                        //Log.d(TAG,"videoURL" + videoUrl);

                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                    }
                });

                Log.d(TAG,"Done displaying VideoURLs");

            }else{
                Log.d(TAG,"Video URLS not available");
            }

        }
    }

}
