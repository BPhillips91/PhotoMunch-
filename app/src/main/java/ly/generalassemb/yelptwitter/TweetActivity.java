package ly.generalassemb.yelptwitter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class TweetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        File myImageFile = new File("/path/to/image");
        Uri myImageUri = Uri.fromFile(myImageFile);

//        TweetComposer.Builder builder = new TweetComposer.Builder(this);
//                .text("#"+hashtag);
//                //.image(myImageUri);
//        builder.show();
    }
}
