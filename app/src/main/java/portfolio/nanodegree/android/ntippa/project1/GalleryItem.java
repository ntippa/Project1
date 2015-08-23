package portfolio.nanodegree.android.ntippa.project1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: ntippa
 * dt: 7/14/2015.
 * last edit:08/23/15
 * represents each Gallery item
 */
public class GalleryItem implements Parcelable{

    private String mTitle;
    private String mId;
    private String mPosterUrl;
    private String mDescription;
    private String mRating;
    private String mPopularity;
    private String mReleaseDate;

    public GalleryItem(){
        //todo:maybe not public
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmPosterUrl() {
        return mPosterUrl;
    }

    public void setmPosterUrl(String mPosterUrl) {
        this.mPosterUrl = mPosterUrl;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(String mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String toString(){
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mId);
        dest.writeString(mPosterUrl);
        dest.writeString(mDescription);
        dest.writeString(mRating);
        dest.writeString(mPopularity);
        dest.writeString(mReleaseDate);

    }

    protected GalleryItem(Parcel in){
        mTitle = in.readString();
        mId = in.readString();
        mPosterUrl = in.readString();
        mDescription= in.readString();
        mRating = in.readString();
        mPopularity = in.readString();
        mReleaseDate = in.readString();

    }

    public static final Parcelable.Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel source) {
            return new GalleryItem(source);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };

   /* @Override
    public int compareTo(GalleryItem another) {
        return ()
    }*/
}
