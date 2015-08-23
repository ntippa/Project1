package portfolio.nanodegree.android.ntippa.project1;

import android.support.v4.app.Fragment;

/**
 * Created by Nalini on 8/11/2015.
 */
public class DetailsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DetailsFragment();
    }
}
