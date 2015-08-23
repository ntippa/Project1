package portfolio.nanodegree.android.ntippa.project1;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/*
author:ntippa
dt:8/23/2015 last edit
Launches the P1 app
 */
public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        //return new ImageFragment();
        return new GalleryFragment();
    }


}
