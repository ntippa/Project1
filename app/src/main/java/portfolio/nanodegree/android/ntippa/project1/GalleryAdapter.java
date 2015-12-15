package portfolio.nanodegree.android.ntippa.project1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by Nalini on 11/16/2015.
 * i am leaving this as is since i dnt know if i need to wirein Content provider
 */
public class GalleryAdapter extends CursorAdapter {

    public static final String TAG = GalleryAdapter.class.getSimpleName();

    static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE = "w185";
    final Context mContext;
    final Cursor mCursor;

    public GalleryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mCursor = c;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false);
        return view;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
//    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {


        ImageView imageView = (ImageView) view.findViewById(R.id.gallery_item_imageView);

        String posterUrl = cursor.getString(GalleryFragment.COL_MOVIE_POSTER_URL);

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



    }
}
