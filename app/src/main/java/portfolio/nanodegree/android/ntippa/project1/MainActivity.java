package portfolio.nanodegree.android.ntippa.project1;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;


/*
author:ntippa
dt:8/23/2015 last edit
Launches the P1 app
 */
public class MainActivity extends SingleFragmentActivity implements GalleryFragment.Callbacks{

    private static final String TAG = MainActivity.class.getSimpleName() ;

    @Override
    protected Fragment createFragment() {
        //return new ImageFragment();
        return new GalleryFragment();
    }

    @Override
    protected int getLayoutResId() {
        return super.getLayoutResId();
    }

    @Override
    public void onGalleryItemSelected(GalleryItem item) {

        if(findViewById(R.id.detailfragmentContainer) == null){
            //phone: start an instance of DetailActivity
            Intent i = new Intent(this,DetailsActivity.class);
           // i.putExtra("MOVIE_ITEM",item);
            i.putExtra(DetailsFragment.EXTRA_MOVIE_ITEM,item);
            Log.d(TAG, "set movie item as extra");
            startActivity(i);
        }else{
           //tablet
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment existingFragment = fm.findFragmentById(R.id.detailfragmentContainer);
            Fragment newFragment = DetailsFragment.newInstance(item);

            if(existingFragment != null){
                ft.remove(existingFragment);
            }

            ft.add(R.id.detailfragmentContainer,newFragment);
            ft.commit();


        }
    }
}
