package portfolio.nanodegree.android.ntippa.project1;

/*
* Aug 6:thrusday
* fix the urls
* */
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by ntippa on 7/13/2015.
 * Fragment representing the Gallery view
 */
public class GalleryFragment extends Fragment {

    public static final String TAG = GalleryFragment.class.getSimpleName();


    GridView mGridView;

    ArrayList<GalleryItem> items;

    GalleryAdapter mAdapter;

    static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE = "w185";


    public GalleryFragment() {
        //super();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_gallery,container,false);

        Toolbar actionBar = (Toolbar) v.findViewById(R.id.appBar);
        actionBar.setTitle("Gallery");

        ((AppCompatActivity)getActivity()).setSupportActionBar(actionBar);

        mGridView = (GridView) v.findViewById(R.id.gridView);


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sort_popularity:
                //todo:sort the items by popularity
                if(items.size() > 0){//chk that there r items indeed!!
                    Log.d(TAG," sorting by Popularity");

                    Collections.sort(items, new Comparator<GalleryItem>() {
                        @Override
                        public int compare(GalleryItem lhs, GalleryItem rhs) {
                            return rhs.getmPopularity().compareTo(lhs.getmPopularity());
                        }
                    });

                    Log.d(TAG,"Done sorting");
                    mGridView.setAdapter(new GalleryAdapter(getActivity(), items));
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
                            return rhs.getmRating().compareTo(lhs.getmRating());
                        }
                    });

                    Log.d(TAG, "Done sorting by rating");
                    mGridView.setAdapter(new GalleryAdapter(getActivity(),items));
                    Log.d(TAG,"set adapter after sorting by Rating");

                }else{
                    Log.d(TAG,"Unable to sort by rating");
                    Toast.makeText(getActivity(),"Unable to process request",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMoviesJSon();
    }

    private void fetchMoviesJSon() {
        Log.d(TAG,"Launching ImageFetchTask");
        ImageFetcherTask task = new ImageFetcherTask();
        task.execute();
        Log.d(TAG,"Done executing ImageFetchTask");
    }

    private class ImageFetcherTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            items = new MoviesFetcher().fetchMovies();//
            Log.d(TAG,"Done fetching movies JSon String");

            Log.d(TAG," in doInBackground");
            Log.d(TAG,"Items count::" + items.size());
            Log.d(TAG,"printing gallery items");
            Log.d(TAG,"Item 1:desc" + items.get(0).getmDescription() +"Title" + items.get(0).getmTitle());
            Log.d(TAG,"Item 2:desc" + items.get(1).getmDescription() +"Title" + items.get(1).getmTitle());
            return items;
        }


        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            items = galleryItems;
            if(galleryItems != null) {
                Log.d(TAG,"set the adapter");
                mGridView.setAdapter(new GalleryAdapter(getActivity(), items));
            }else {
                Log.d(TAG,"adapter is null");
                mGridView.setAdapter(null);
            }

        }
    }




    //custom adapter with Imageviews
    private class GalleryAdapter extends BaseAdapter {

        private final Context context;
        private final ArrayList<GalleryItem> galleryItems;

        protected GalleryAdapter(Context context, ArrayList<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           final GalleryItem item  = (GalleryItem) getItem(position);

            if(convertView == null){
               convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item,parent,false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context,item.getmTitle() + ":P:" +item.getmPopularity() + ":R:" +  item.getmRating(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"launching details");
                    Intent movieItemIntent = new Intent(getActivity(),DetailsActivity.class);
                    movieItemIntent.putExtra("MOVIE_ITEM",item);
                    startActivity(movieItemIntent);


                }
            });

            //BUILD Complete URL here

            String posterUrl = item.getmPosterUrl();

           StringBuffer string_url = new StringBuffer(POSTER_BASE_URL);
            string_url.append(POSTER_SIZE);
            string_url.append(posterUrl);

            Log.d(TAG,"Image URL");
            Log.d(TAG,string_url.toString());


           try { // Trigger the download of the URL asynchronously into the image view.
               Picasso.with(context) //
                       .load(string_url.toString()) //
                       .placeholder(R.drawable.placeholder) //
                       .error(R.drawable.error) //
                       .fit() //
                       .tag(context) //
                       .into(imageView);
           }catch(Exception e){
               Log.d(TAG,"Eception in Picasso loading image url");
               e.printStackTrace();
           }



            return imageView;
        }
    }
}
