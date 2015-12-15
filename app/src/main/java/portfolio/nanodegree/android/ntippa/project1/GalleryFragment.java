package portfolio.nanodegree.android.ntippa.project1;

/*
* Aug 6:thrusday
* fix the urls
* 11/13: @168
* 11/20: @ImageFecthTask fixed to insert rows to db. Need to test that.
* todo: move fetching movies to onActivityCreated
* */
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import portfolio.nanodegree.android.ntippa.project1.data.MoviesContract;

/**
 * Created by ntippa on 7/13/2015.
 * Fragment representing the Gallery view
 */
public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = GalleryFragment.class.getSimpleName();


    private Callbacks mCallbacks;

    public interface Callbacks {

        void onGalleryItemSelected(GalleryItem item);
    }
    GridView mGridView;

    ArrayList<GalleryItem> items;

    private GalleryAdapter mAdapter;

    static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE = "w185";

    private static final int GALLERY_ITEMS_LOADER = 1;//

    private static final String[] MOVIES_PROJECTION = {
            "rowid _id",
         MoviesContract.Movies.COLUMN_MOVIE_ID,
        MoviesContract.Movies.COLUMN_TITLE,
        MoviesContract.Movies.COLUMN_DESC,//todo: do we need all these columns.Does it matter if we get them n dnt use them
        MoviesContract.Movies.COLUMN_POSTER_URL,
        MoviesContract.Movies.COLUMN_POPULARITY,
        MoviesContract.Movies.COLUMN_RATING,
        MoviesContract.Movies.COLUMN_RELEASE_DATE,
            MoviesContract.Movies.COLUMN_FAVOURITE
    };

    //these are the indices tied to MOVIES_PROJECTION. If projection changes
    // then these must also change
    //static final int COL_MOVIE_TABLE_ID = 0;//todo: clarify how the _ID is fetched
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_DESC = 3;
    static final int COL_MOVIE_POSTER_URL = 4;
    static final int COL_MOVIE_POPULARITY = 5;
    static final int COL_MOVIE_RATING = 6;
    static final int COL_MOVIE_RELEASE_DATE = 7;
    static final int COL_MOVIE_FAVOURITE = 8;


    public GalleryFragment() {
        //super();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.d(TAG," fetching items from savedInstanceState");
            items = savedInstanceState.getParcelableArrayList("GALLERY_ITEMS"); // avoid redo while config changes
        }else{
            items = new ArrayList<GalleryItem>();//can this object be null??
        }
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");

        //the cursor adapter will take data from cursor and populate in views
        mAdapter = new GalleryAdapter(getActivity(),null,0);

       View v = inflater.inflate(R.layout.fragment_gallery,container,false);

        Toolbar actionBar = (Toolbar) v.findViewById(R.id.appBar);
        actionBar.setTitle("Gallery");

        ((AppCompatActivity)getActivity()).setSupportActionBar(actionBar);

        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null){
                    Toast.makeText(getActivity(),"Clicked Movie ID:" + cursor.getInt(GalleryFragment.COL_MOVIE_ID) + "Movie Title" + cursor.getString(GalleryFragment.COL_MOVIE_TITLE), Toast.LENGTH_SHORT).show();
                    GalleryItem item = new GalleryItem();
                Log.d(TAG,"_ID" + cursor.getInt(GalleryFragment.COL_ID));
                item.setId(cursor.getInt(GalleryFragment.COL_MOVIE_ID));
                item.setmTitle(cursor.getString(GalleryFragment.COL_MOVIE_TITLE));
                item.setmDescription(cursor.getString(GalleryFragment.COL_MOVIE_DESC));
                item.setmPosterUrl(cursor.getString(GalleryFragment.COL_MOVIE_POSTER_URL));
                item.setmPopularity(cursor.getDouble(GalleryFragment.COL_MOVIE_POPULARITY));
                item.setmRating(cursor.getDouble(GalleryFragment.COL_MOVIE_RATING));

                String release_date = new Date(cursor.getLong(GalleryFragment.COL_MOVIE_RELEASE_DATE)).toString();
                item.setmReleaseDate(release_date);

//               Intent movieDetailsIntent = new Intent(getActivity(),DetailsActivity.class);
//               movieDetailsIntent.putExtra("MOVIE_ITEM",item);
//               getActivity().startActivity(movieDetailsIntent);

                    mCallbacks.onGalleryItemSelected(item);
                }

                cursor.close();
            }
        });



        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG,"onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected");
        switch(item.getItemId()){
            case R.id.action_refresh:
                fetchMoviesJSon();
                getLoaderManager().initLoader(GALLERY_ITEMS_LOADER, null, this);
                Log.d(TAG,"Cursor Loader intialised");
                return true;
            case R.id.sort_popularity:
                //todo:sort the items by popularity
                if(items.size() > 0){//chk that there r items indeed!!
                    Log.d(TAG," sorting by Popularity");

                    Collections.sort(items, new Comparator<GalleryItem>() {
                        @Override
                        public int compare(GalleryItem lhs, GalleryItem rhs) {
                            if(rhs.getmPopularity() <  lhs.getmPopularity()) return -1 ;
                            if(rhs.getmPopularity() == lhs.getmPopularity()) return 0 ;
                            return 1;
                        }
                    });

                    Log.d(TAG,"Done sorting");
//          todo:          mGridView.setAdapter(new GalleryAdapter(getActivity(), items,0));//todo: do we need new instance??
                    Log.d(TAG,"Set Adapter after sorting");

                }else{
                    Log.d(TAG,"Unable to sort by popularity");
                    Toast.makeText(getActivity(),"Unable to process request",Toast.LENGTH_SHORT).show();//maybe throw exception or better way of handling this
                }
                //todo: pass the items to the adapter??
                return true;

            case R.id.sort_rating:
                //todo
                if(items.size() > 0) {
                    Log.d(TAG, "sorting by rating");
                    Collections.sort(items, new Comparator<GalleryItem>() {
                        @Override
                        public int compare(GalleryItem lhs, GalleryItem rhs) {
                            if(rhs.getmRating() < lhs.getmRating()) return -1;
                            if(rhs.getmRating() < lhs.getmRating()) return 0;
                            return 1;
                        }
                    });

                    Log.d(TAG, "Done sorting by rating");
//          todo:          mGridView.setAdapter(new GalleryAdapter(getActivity(),items));//todo: do we need new instance
                    Log.d(TAG,"set adapter after sorting by Rating");

                }else{
                    Log.d(TAG,"Unable to sort by rating");
                    Toast.makeText(getActivity(),"Unable to process request",Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.favourite:
                //obtain favourite movie id's from shared pref
//                String id_string = Utility.getFavouritePreference(getActivity());
//                String[] favourite_movie_ids = id_string.split(",");
////            todo:    multipe id's exist. How to bulk fetch them using picasso??
//// int favourite_count = favourite_movie_ids.length;
//                while(favourite_count > 0){
//                    Log.d(TAG,"Fav id:" + favourite_movie_ids[favourite_count]);
//                    --favourite_count;
//                }
//
//                  //query db to obtain movie details of those movies
                Log.d(TAG,"Displaying favourites");
                Cursor favourite_cursor = getActivity().getContentResolver().query(
                        MoviesContract.Movies.CONTENT_URI,
                        MOVIES_PROJECTION,
                        MoviesContract.Movies.COLUMN_FAVOURITE + "= ?",
                        new String[]{Integer.toString(Utility.SET_AS_FAVOURITE)},//todo:
                        null
                ) ;

                mAdapter.swapCursor(favourite_cursor);//todo,is this the way
               Log.d(TAG,"Done displaying Favourites");

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void fetchMoviesJSon() {
        Log.d(TAG,"fetchMoviesJSOn");
        Log.d(TAG,"Launching ImageFetchTask");
        ImageFetcherTask task = new ImageFetcherTask();
        task.execute();
        Log.d(TAG,"Done executing ImageFetchTask");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onActivityCreated");
        //check if network available
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
                boolean isNetworkAvailable = cm.getBackgroundDataSetting() && (cm.getActiveNetworkInfo() != null);

        //if items present, no need to fetch them from internet
        if( isNetworkAvailable && items == null){
            Log.d(TAG," Network available.Loading from network");
           fetchMoviesJSon();
            Log.d(TAG,"Done fetching movies from network");
            getLoaderManager().initLoader(GALLERY_ITEMS_LOADER, null, this);
            Log.d(TAG,"Cursor Loader intialised");

        }
        else{//do we need to check savedInstanceState, it would mean the same to get from savedInstanceState or the db, at this point in time
                if(!isNetworkAvailable){
                    Log.d(TAG,"No network available");
                    Toast.makeText(getActivity(),"Opps.!TRY REFRESH setting Option!!", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG,"Fetching from DB");
                Cursor db_cursor =  getActivity().getContentResolver().query(
                        MoviesContract.Movies.CONTENT_URI,
                        MOVIES_PROJECTION,// todo: have to create new cursor _id,column_title
                        null,
                        null,//todo:
                        null
                ) ;

                mAdapter.swapCursor(db_cursor);//todo: in Adapter, check if stringUrl is present or not, then display only titles instead of images
            }

//        if(!isNetworkAvailable){//no network
//            Log.d(TAG,"No Network");
//            getLoaderManager().initLoader(GALLERY_ITEMS_LOADER, null, this);
//            Log.d(TAG,"Cursor Loader intialised");
//        }else{
//            Log.d(TAG," Network available.Loading from network");
//            fetchMoviesJSon();
//            Log.d(TAG,"Done fetching movies from network");
//            mAdapter.swapCursor()
//
//        }
        // Fetch movies over net if available
        //if not available, fetch movies from db

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"onSaveInstanceState");
        outState.putParcelableArrayList("GALLERY_ITEMS", items);
        Log.d(TAG,"saving the Arraylist to the Bundle");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG,"onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {//called when a new loader needs to be created
        Log.d(TAG,"onCreateLoader");

        String sortOrder = MoviesContract.Movies.COLUMN_POPULARITY + " ASC";//todo: check this one

        return new CursorLoader(getActivity(),
                MoviesContract.Movies.CONTENT_URI,
                MOVIES_PROJECTION,
                null,
                null,
                sortOrder
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.d(TAG,"onLoadFinished: Cursor count::" + cursor.getCount());

        mAdapter.swapCursor(cursor);
        Log.d(TAG," Cursor loaded to Adapter");

        cursor.moveToFirst();

        //new GalleryItem
        while(!cursor.isAfterLast()){

            GalleryItem item = new GalleryItem();
            Log.d(TAG,"_ID" + cursor.getInt(COL_ID));
            item.setId(cursor.getInt(COL_MOVIE_ID));
            item.setmTitle(cursor.getString(COL_MOVIE_TITLE));
            item.setmDescription(cursor.getString(COL_MOVIE_DESC));
            item.setmPosterUrl(cursor.getString(COL_MOVIE_POSTER_URL));
            item.setmPopularity(cursor.getDouble(COL_MOVIE_POPULARITY));
            item.setmRating(cursor.getDouble(COL_MOVIE_RATING));

            String release_date = new Date(cursor.getLong(COL_MOVIE_RELEASE_DATE)).toString();
            item.setmReleaseDate(release_date);

            items.add(item);
            Log.d(TAG," cursor item added to GalleryItems:item ID" + item.getId());

            cursor.moveToNext();
        }

        Log.d(TAG,"no.of movies loaded" + items.size());



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.d(TAG,"onLoaderReset");
        mAdapter.swapCursor(null);

    }

    //    private class ImageFetcherTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>> {
private class ImageFetcherTask extends AsyncTask<Void,Void,Void> {

        @Override
//        protected ArrayList<GalleryItem> doInBackground(Void... params) {
        protected Void doInBackground(Void... params) {
           // items = new MoviesFetcher().fetchMovies();//
            new MoviesFetcher(getActivity()).fetchMovies();
//
            return null;
        }


    }





}
