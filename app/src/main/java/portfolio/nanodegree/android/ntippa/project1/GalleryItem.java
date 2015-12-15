package portfolio.nanodegree.android.ntippa.project1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: ntippa
 * dt: 7/14/2015.
 * last edit:08/23/15
 * represents each Gallery item
 *
 * 11/22: changed rating, popularity to double
 *      :change Id to int;
 */
public class GalleryItem implements Parcelable{

    private String mTitle;
    private int mId;//todo: is this movie_id??
    private String mPosterUrl;
    private String mDescription;
    private double mRating;
    private double mPopularity;
    private String mReleaseDate;//todo: do we leave date as string??
    private int mFavourite;

    public GalleryItem(){
        //todo:maybe not public
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public double getmRating() {
        return mRating;
    }

    public void setmRating(double mRating) {
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

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public int getFavourite() {
        return mFavourite;
    }

    public void setFavourite(int mFavourite) {
        this.mFavourite = mFavourite;
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
        dest.writeInt(mId);
        dest.writeString(mPosterUrl);
        dest.writeString(mDescription);
        dest.writeDouble(mRating);//.writeString(mRating);
        dest.writeDouble(mPopularity);//.writeString(mPopularity);
        dest.writeString(mReleaseDate);
        dest.writeInt(mFavourite);

    }

    protected GalleryItem(Parcel in){
        mTitle = in.readString();
        mId = in.readInt();//.readString();
        mPosterUrl = in.readString();
        mDescription= in.readString();
        mRating = in.readDouble();//.readString();
        mPopularity = in.readDouble();//.readString();
        mReleaseDate = in.readString();
        mFavourite = in.readInt();

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
