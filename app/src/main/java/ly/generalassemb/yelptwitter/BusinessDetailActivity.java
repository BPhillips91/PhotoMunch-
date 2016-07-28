package ly.generalassemb.yelptwitter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.entities.Business;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;
import xyz.hanks.library.SmallBang;
import xyz.hanks.library.SmallBangListener;

public class BusinessDetailActivity extends AppCompatActivity {
    LinearLayout mapsButton;
    LinearLayout addButton;
    TextView businessNameDisplay;
    TextView businessAddressDisplay;
    YelpAPI yelpAPI;
    String businessId;
    String imageURL;
    TextView textView;
    Call<Business> call;
    BusinessYelpApp businessForDisplay;
    CollapsingToolbarLayout collapsingToolbar;
    ArrayList<String> imageUrls;
    private String hashtag;
    private ListView listView;
    private String hello;
    SmallBang mSmallBang;
    ImageView saveIcon;
    ImageView mapIcon;
    private String SEARCH_QUERY;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheese);

        Intent intent = getIntent();
        businessId = intent.getStringExtra("name_id");
        imageURL = intent.getStringExtra("image_url");

        saveIcon = (ImageView)findViewById(R.id.saveIcon);
        mapIcon = (ImageView)findViewById(R.id.mapIcon);

        yelpAPI = MainActivity.yelpAPI;
        call = yelpAPI.getBusiness(businessId);

        mSmallBang = SmallBang.attach2Window(this);
        int oneColorValue = Color.parseColor("#f5aa5b");
        int twoColorValue = Color.parseColor("#ee573e");
        int threeColorValue = Color.parseColor("#f17c45");
        int[] ints = new int[] {twoColorValue, oneColorValue, threeColorValue};

        mSmallBang.setColors(ints);

        new BusinessInfoTask().execute();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        businessNameDisplay = (TextView)findViewById(R.id.businessName);
        businessAddressDisplay = (TextView)findViewById(R.id.businessAddress);
        businessNameDisplay.setText("");
        businessAddressDisplay.setText("");

        mapsButton = (LinearLayout) findViewById(R.id.mapsButton);
        addButton = (LinearLayout) findViewById(R.id.saveButton);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setTitle("");

        listView = (ListView) findViewById(R.id.twitter_list);

    }

    private class BusinessInfoTask extends AsyncTask<Void, Void, BusinessYelpApp> {
        @Override
        protected BusinessYelpApp doInBackground(Void... voids) {

            Response<Business> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = "";
            for (int i = 0; i< response.body().location().displayAddress().size(); i++) {
                address += response.body().location().displayAddress().get(i) +", ";
            }

            address = address.substring(0, (address.length() - 2));
            BusinessYelpApp business = new BusinessYelpApp(response.body().name(), response.body().ratingImgUrl(), address);

            Document doc = null;
            try {
                doc = Jsoup.connect("http://www.yelp.com/biz_photos/" + businessId + "?tab=food").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements all = doc.getAllElements();
            Pattern p = Pattern.compile("(?is)\"src_high_res\": \"(.+?)\"");
            Matcher m = p.matcher(all.toString());
            imageUrls = new ArrayList<>();
            int i = 0;
            while (m.find() && (i < 10)) {
                imageUrls.add("http:"+m.group(1));
                i++;
            }

            return business;
        }

        @Override
        protected void onPostExecute(BusinessYelpApp business) {
            super.onPostExecute(business);

            businessForDisplay = business;
            collapsingToolbar.setTitle(businessForDisplay.getmName());
            businessNameDisplay.setText(businessForDisplay.getmName());
            businessAddressDisplay.setText(businessForDisplay.getmAddress());

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
            for (int i = 0; i < imageUrls.size(); i++) {
                ImageView imageView = new ImageView(BusinessDetailActivity.this);
                imageView.setId(i);
//
                int height= layout.getMeasuredHeight();
                layout.addView(imageView, height, height);
                Picasso.with(BusinessDetailActivity.this)
                        .load(imageUrls.get(i))
                        .resize(height, height)
                        .centerCrop()
                        .into(imageView);
            }

            hashtag = businessForDisplay.getmName()
                    .replace(" ", "")
                    .replace("!", "")
                    .replace("$", "")
                    .replace("&", "");
            Log.d("HASHTAG", hashtag);

            if (!Fabric.isInitialized()) {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(LoginActivity.TWITTER_KEY,LoginActivity.TWITTER_SECRET);
                Fabric.with(BusinessDetailActivity.this, new Twitter(authConfig));
            }

            final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                    .query("#"+hashtag)
                    .build();
            final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(BusinessDetailActivity.this)
                    .setTimeline(searchTimeline)
                    .build();

            listView.setAdapter(adapter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listView.setNestedScrollingEnabled(true);
            }

            mapsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSmallBang.bang(view.findViewById(R.id.mapIcon), 20, new SmallBangListener() {
                        @Override
                        public void onAnimationStart() {
                        }

                        @Override
                        public void onAnimationEnd() {

                        }

                    });
                    goToMaps();
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSmallBang.bang(view.findViewById(R.id.saveIcon), 40, new SmallBangListener() {
                        @Override
                        public void onAnimationStart() {
                            saveIcon.setImageResource(R.drawable.ic_check_black_24dp);
                        }

                        @Override
                        public void onAnimationEnd() {

                        }
                    });
                    addToLikes();
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_likes:
                Intent intent = new Intent(BusinessDetailActivity.this, LikesActivity.class);
                startActivity(intent);
                break;
            case R.id.action_tweet:
                TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("#"+hashtag);

                builder.show();

                final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                        .query("#"+hashtag)
                        .build();
                final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(BusinessDetailActivity.this)
                        .setTimeline(searchTimeline)
                        .build();

                listView.setAdapter(adapter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    listView.setNestedScrollingEnabled(true);
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void goToMaps(){

        double mLatitude = ResultsSingleton.getInstance().getLatitude();
        double mLongitude = ResultsSingleton.getInstance().getLongitude();

        String uri = String.format(Locale.ENGLISH, "geo:0,0?q="+businessForDisplay.getmAddress());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        BusinessDetailActivity.this.startActivity(mapIntent);

    }

    public void addToLikes(){
        String userName = ResultsSingleton.getInstance().getUserName();
        String businessName = businessForDisplay.getmName();
        String businessId = this.businessId;
        String businessUrl = imageURL;
        Toast.makeText(BusinessDetailActivity.this, "Added!", Toast.LENGTH_SHORT).show();

        DatabaseReference userRef = database.getReference("users").child(userName);
        Food likedObject = new Food(businessUrl,businessId,businessName);
        userRef.push().setValue(likedObject);


    }
}

