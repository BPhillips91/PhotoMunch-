package ly.generalassemb.yelptwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "Uzr84VNnQjD7RJQnXneJTPifp";
    public static final String TWITTER_SECRET = "XEo4SwCTB85HSe2bw65HSJ45nbHMIObVgjQddJDILzDaYtOXWT";
    private TwitterLoginButton loginButton;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERID = "userid";


    private String twitter_username;
    private long twitter_userId;
    private String twitter_image_url;
    private String twitter_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());
        setContentView(R.layout.activity_login);


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                twitter_username = session.getUserName();
                twitter_userId = session.getUserId();
                twitter_image_url = " https://twitter.com/" + twitter_username + "/profile_image?size=original";

                ResultsSingleton.getInstance().setUserName(twitter_username);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("username", twitter_username);
                returnIntent.putExtra("twitterId", twitter_userId);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();


            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
                //updateUI(null);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


}
