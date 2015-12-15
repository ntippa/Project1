package portfolio.nanodegree.android.ntippa.project1.model;

import java.util.ArrayList;

/**
 * Created by Nalini on 11/7/2015.
 * do we need 2 separate model objects
 */
public class GalleryItemDetail {

    private String mItemId;// id of item.String??

    private ArrayList<String> mReviews;

    private ArrayList<String> mVideoURLs;

    //private videoUrls;


    public String getItemId() {
        return mItemId;
    }

    public void setItemId(String mItemId) {
        this.mItemId = mItemId;
    }

    public ArrayList<String> getReviews() {
        return mReviews;
    }

    public void setReviews(ArrayList<String> mReviews) {
        this.mReviews = mReviews;
    }

    public ArrayList<String> getVideoURLs() {
        return mVideoURLs;
    }

    public void setVideoURLs(ArrayList<String> mVideoURLs) {
        this.mVideoURLs = mVideoURLs;
    }
}
