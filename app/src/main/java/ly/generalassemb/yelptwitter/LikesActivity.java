package ly.generalassemb.yelptwitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {
    LikesAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    List<String>keys;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users").child(ResultsSingleton.getInstance().getUserName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        mRecyclerView = (RecyclerView) findViewById(R.id.likes_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        keys = new ArrayList<>();
        final List<Food> fList = new ArrayList<>();
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                keys.add(key);

                // This iterates through the different children of the given user and retrieves
                // the values for the current Users likes

                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    String url = dataSnapshot.child("foodPic").getValue().toString();
                    String name = dataSnapshot.child("restaurantName").getValue().toString();
                    String id = dataSnapshot.child("foodId").getValue().toString();
                    Food m = new Food(url,id, name);
                    fList.add(m);
                    i += 2;
                    mAdapter = new LikesAdapter(fList,keys, LikesActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        };
        userRef.addChildEventListener(listener);





    }


}
