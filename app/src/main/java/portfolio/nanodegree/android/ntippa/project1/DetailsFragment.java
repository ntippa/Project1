package portfolio.nanodegree.android.ntippa.project1;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Nalini on 8/11/2015.
 * Fragment projecting details of each Gallery item
 */
public class DetailsFragment extends Fragment {

    public static final String TAG = DetailsFragment.class.getSimpleName();
    static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    static final String POSTER_SIZE = "w185";

    GalleryItem mItem;
    TextView mTitle_View, mOverview_View, mDeatil_View;
    ImageView mPoster_View;

    public DetailsFragment() {
       // super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment,container,false);




        Toolbar actionBar = (Toolbar) view.findViewById(R.id.appBar);
        actionBar.setTitle("Movie Details");

        ((AppCompatActivity)getActivity()).setSupportActionBar(actionBar);





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

        mDeatil_View = (TextView) view.findViewById(R.id.year);
        String[] s = mItem.getmReleaseDate().split("-");
        mDeatil_View.setText(s[0]);
       /* Calendar c = Calendar.getInstance();
        try{
           // c.setTime(new SimpleDateFormat("yyyy").parse(mItem.getmReleaseDate()));
            mDeatil_View.setText(new SimpleDateFormat("yyyy").parse(mItem.getmReleaseDate()).toString());
        }catch(ParseException e){
            e.printStackTrace();
        }*/
       // mDeatil_View.setText(c.get(Calendar.YEAR));
       // mDeatil_View.setText(mItem.getmReleaseDate());

        mOverview_View = (TextView) view.findViewById(R.id.detail2);
        mOverview_View.setText(mItem.getmDescription());


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItem = getActivity().getIntent().getParcelableExtra("MOVIE_ITEM");

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
}
