package ly.generalassemb.yelptwitter;

/**
 * Created by aaronfields on 7/20/16.
 */
public class BusinessYelpApp {
    private String mName;
    private String mRatingImageUrl;
    private String mAddress;

    public BusinessYelpApp(String mName, String mRatingImageUrl, String mAddress) {
        this.mName = mName;
        this.mRatingImageUrl = mRatingImageUrl;
        this.mAddress = mAddress;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmRatingImageUrl() {
        return mRatingImageUrl;
    }

    public void setmRatingImageUrl(String mRatingImageUrl) {
        this.mRatingImageUrl = mRatingImageUrl;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
