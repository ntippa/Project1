package portfolio.nanodegree.android.ntippa.project1;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Nalini on 8/11/2015.
 */
public class DetailsActivity extends SingleFragmentActivity {
    static final String TAG = DetailsActivity.class.getSimpleName();
    @Override
    protected Fragment createFragment() {
       // return new DetailsFragment();
        Log.d(TAG, "createFragment");
        GalleryItem movie = getIntent().getParcelableExtra(DetailsFragment.EXTRA_MOVIE_ITEM);

        return DetailsFragment.newInstance(movie);
    }
}
