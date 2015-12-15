package portfolio.nanodegree.android.ntippa.project1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.PublicKey;

/**
 * Created by Nalini on 11/13/2015.
 */
public class Utility {

    public static final String TAG = Utility.class.getSimpleName();

    public static final int SET_AS_FAVOURITE = 1;
    public static final int DEFAULT_FAVOURITE = 0;
    public static final String MOVIE_FAVOURITE = "favourite";// favourite preference

    // adding movie to favourite shared pref
    static boolean setFavouritePreference(Context context,String movieId){

        String id_list = getFavouritePreference(context);
        id_list = id_list + "," + movieId;
       return addPreference(context,MOVIE_FAVOURITE,id_list);
    }


    static String getFavouritePreference(Context context){
        String defValue = ","; // todo: better idea?
        return getPreference(context,MOVIE_FAVOURITE,defValue);

    }
//

    private static String getPreference(Context context, String Key, String defValue){
      String value = PreferenceManager.getDefaultSharedPreferences(context).getString(Key,defValue);

        return value;
    }

    private static boolean addPreference(Context context,String Key, String Value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(Key,Value)
                .commit();
        return true;
    }

    private static String[] convertToArray(String input){
        return input.split(",");
    }
}
