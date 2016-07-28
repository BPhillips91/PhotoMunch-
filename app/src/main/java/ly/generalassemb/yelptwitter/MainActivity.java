package ly.generalassemb.yelptwitter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    Toolbar tToolbar;
    PhotoGridAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    static ArrayList<Food> foodList;
    Call<SearchResponse> call;
    private static final String consumerKey = "4r7d1XOhHp9RI7xbyl3fBw";
    private static final String consumerSecret = "lZSxJRqOFRiHOPoUS2q_qo3CavU";
    private static final String token = "c8lFr2mAjc_qbj5UiAacTXKHyErSel5N";
    private static final String tokenSecret = "0pbBMj01CB51_wXbnqphQCUFRNA";
    Map<String, String> parameters;
    public static YelpAPI yelpAPI;
    CoordinateOptions coordinate;
    int revolver;
    double mLatitude;
    double mLongitude;
    SwipeRefreshLayout swipeLayout;
    ArrayList<String> ids;
    private static final int TOOLBAR_ELEVATION = 4;
    static final int REQUEST_LOCATION = 0;
    boolean isConnected;
    static boolean loadedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction("coordinatesLoaded");

        checkConnection();
        checkPermissions();

        YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        yelpAPI = apiFactory.createAPI();

        parameters = new HashMap<>();
        parameters.put("term", "food");
        parameters.put("limit", "20");

        if(!ResultsSingleton.getInstance().isLoggedIn()) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, 1);
        }

        tToolbar = (Toolbar) findViewById(R.id.toolbar);
        revolver = 0;
        tToolbar.setTitle("Munchies Nearby!");
        tToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(tToolbar);

        ids = new ArrayList<>();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        swipeLayout.setRefreshing(false);
                        revolver ++;
                        if(isConnected) {
                            getLocation();
                        }
                    }
                }, 4000);
            }
        });
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        registerReceiver(mReceiver,filter);

        if(!loadedImages){
            foodList = new ArrayList<>();
            if(isConnected) {
                getLocation();
            }
        }else{
            mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            mLayoutManager = new GridLayoutManager(MainActivity.this, 3);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new PhotoGridAdapter(foodList,MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class DownloadUrlTask extends AsyncTask<ArrayList<String>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<String>... ids) {
            int start= 0;
            int finish= 5;
            if(revolver > 3){
                revolver = 0;
            }
                switch (revolver) {
                    case 0:
                        if(ids[0].size() > 5) {
                            start = 0;
                            finish = 5;
                            break;
                        } else {
                            start = 0;
                            finish = ids[0].size();
                            break;
                        }
                    case 1:
                        if(ids[0].size() > 10) {
                            start = 5;
                            finish = 10;
                            break;
                        } else {
                            start = 5;
                            finish = ids[0].size();
                            break;
                        }
                    case 2:
                        if(ids[0].size() > 15) {
                            start = 10;
                            finish = 15;
                            break;
                        } else {
                            start = 10;
                            finish = ids[0].size();
                            break;
                        }
                    case 3:
                        if(ids[0].size() == 20) {
                            start = 15;
                            finish = 20;
                            break;
                        } else {
                            start = 15;
                            finish = ids[0].size();
                            break;
                        }
                }

            foodList = new ArrayList<>();

            try {
                for(int i = start; i < finish; i++) {
                    String id = ids[0].get(i);
                    Document doc = Jsoup.connect("http://www.yelp.com/biz_photos/" + id + "?tab=food").get();

                    Elements all = doc.getAllElements();
                    Pattern p = Pattern.compile("(?is)\"src_high_res\": \"(.+?)\"");
                    Matcher m = p.matcher(all.toString());

                    Call <Business> call2 = yelpAPI.getBusiness(id);
                    Response<Business> response2 = null;
                    try {
                        response2 = call2.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String restaurantName = "";

                    restaurantName = response2.body().name();

                    int i2 = 0;
                    while (m.find() && (i2 < 16)) {
                        Food itemUrl = new Food("http:" + m.group(1), id, restaurantName);
                        foodList.add(itemUrl);
                        i2++;
                    }
                }

                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Collections.shuffle(foodList);
            loadedImages=true;
            ResultsSingleton.getInstance().setFoodArrayList(foodList);
            mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            mLayoutManager = new GridLayoutManager(MainActivity.this, 3);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new PhotoGridAdapter(foodList,MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);

            // We need to detect scrolling changes in the RecyclerView
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                // Keeps track of the overall vertical offset in the list
                int verticalOffset;

                // Determines the scroll UP/DOWN direction
                boolean scrollingUp;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (scrollingUp) {
                            if (verticalOffset > tToolbar.getHeight()) {
                                toolbarAnimateHide();
                            } else {
                                toolbarAnimateShow(verticalOffset);
                            }
                        } else {
                            if (tToolbar.getTranslationY() < tToolbar.getHeight() * -0.6 && verticalOffset > tToolbar.getHeight()) {
                                toolbarAnimateHide();
                            } else {
                                toolbarAnimateShow(verticalOffset);
                            }
                        }
                    }
                }



                @Override
                public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    verticalOffset += dy;
                    scrollingUp = dy > 0;
                    int toolbarYOffset = (int) (dy - tToolbar.getTranslationY());
                    tToolbar.animate().cancel();



                    if (scrollingUp) {
                        if (toolbarYOffset < tToolbar.getHeight()) {
                            if (verticalOffset > tToolbar.getHeight()) {
                                toolbarSetElevation(TOOLBAR_ELEVATION);
                            }
                            tToolbar.setTranslationY(-toolbarYOffset);
                        } else {
                            toolbarSetElevation(0);
                            tToolbar.setTranslationY(-tToolbar.getHeight());
                        }
                    } else {
                        if (toolbarYOffset < 0) {
                            if (verticalOffset <= 0) {
                                toolbarSetElevation(0);
                            }
                            tToolbar.setTranslationY(0);
                        } else {
                            if (verticalOffset > tToolbar.getHeight()) {
                                toolbarSetElevation(TOOLBAR_ELEVATION);
                            }
                            tToolbar.setTranslationY(-toolbarYOffset);
                        }
                    }
                }
            });
        }
    }

    private class SearchBusinessesTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            Response<SearchResponse> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> businessId = new ArrayList<>();

            for(int i = 0; i<response.body().businesses().size(); i++){
                String id = response.body().businesses().get(i).id();
                businessId.add(id);
            }

            return businessId;
        }

        @Override
        protected void onPostExecute(ArrayList<String> ids) {
            super.onPostExecute(ids);
            MainActivity.this.ids = ids;
            new DownloadUrlTask().execute(ids);


        }
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void toolbarSetElevation(float elevation) {
        // setElevation() only works on Lollipop
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            tToolbar.setElevation(elevation);
        }
    }

    private void toolbarAnimateShow(final int verticalOffset) {
        tToolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                    }
                });
    }

    private void toolbarAnimateHide() {
        tToolbar.animate()
                .translationY(-tToolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarSetElevation(0);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String username=data.getStringExtra("username");
                //String twitterId=data.getStringExtra("twitterId");

                ResultsSingleton.getInstance().setUserName(username);
                //ResultsSingleton.getInstance().setUserID(twitterId);
                ResultsSingleton.getInstance().setLoggedIn(true);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void getCoordinates() {
        mLatitude = ResultsSingleton.getInstance().getLatitude();
        mLongitude = ResultsSingleton.getInstance().getLongitude();
        coordinate = CoordinateOptions.builder()
                .latitude(mLatitude)
                .longitude(mLongitude).build();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCoordinates();
            call = yelpAPI.search(coordinate, parameters);
            if(isConnected) {
                new SearchBusinessesTask().execute();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("SEARCH", "onCreate: You are connected");
            isConnected = true;

        } else {
            Log.d("SEARCH", "onCreate: You are not connected");
            isConnected = false;
        }

    }
    public void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }


    }

    public void getLocation(){
        Intent getLocation = new Intent(MainActivity.this, LocationService.class);
        getLocation.putExtra("location", "location");
        startService(getLocation);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_likes:
                Intent intent = new Intent(MainActivity.this, LikesActivity.class);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}