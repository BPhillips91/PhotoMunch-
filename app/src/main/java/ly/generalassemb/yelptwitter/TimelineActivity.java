package ly.generalassemb.yelptwitter;

import android.app.ListActivity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

public class TimelineActivity extends ListActivity {
    String companyName;
    private static final String SEARCH_QUERY = "#truefoodkitchen";
    public static final String EXTRA_NAME = "restaurant_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query(SEARCH_QUERY)
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .build();

        setListAdapter(adapter);

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        for (int i = 0; i < 10; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(4, 4, 4, 4);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.hemingway));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
        }

    }
}
