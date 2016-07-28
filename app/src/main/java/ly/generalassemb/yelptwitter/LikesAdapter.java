package ly.generalassemb.yelptwitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brendan on 7/19/16.
 */
public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.likesViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users").child(ResultsSingleton.getInstance().getUserName());
    LayoutInflater inflater;
    List<Food> foodList;
    List<String> mKeys;
    Context context;
    public LikesAdapter(List<Food> list,List<String> keys, Context context) {
        this.foodList = list;
        this.mKeys = keys;
        this.context = context;
    }

    @Override
    public likesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.from(parent.getContext()).inflate(R.layout.likes_card_item,
                parent, false);
        likesViewHolder holder = new likesViewHolder(v, context, foodList);
        return holder;
    }

    @Override
    public void onBindViewHolder(likesViewHolder holder, int position) {

        //TODO: setup layout and figure out how we want to display our images
        String imgURL = foodList.get(position).getFoodPic();
        Picasso.with(this.context)
                .load(imgURL)
                .into(holder.mImage);
        holder.mName.setText(foodList.get(position).getRestaurantName());
    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // setting the ViewHolder for my recyclerview with a clickListener

    public class likesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView mImage;
        public TextView mName;
        List<Food> foodList = new ArrayList<>();
        Context context;

        public likesViewHolder(View itemView, Context context, List<Food> food) {
            super(itemView);
            this.foodList = food;
            this.context = context;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.food_image);
            mName = (TextView) itemView.findViewById(R.id.business_name);

        }

        // telling my ClickListener what to do
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            Food food = foodList.get(position);
            Intent intent = new Intent(this.context, BusinessDetailActivity.class);
            intent.putExtra("name_id", food.getFoodId());
            intent.putExtra("image_url", food.getFoodPic());
            this.context.startActivity(intent);
            }

        @Override
        public boolean onLongClick(View view) {
            int positionLiked = getAdapterPosition();
            // TODO: set up removal on clicks
            createAndShowAlertDialog(positionLiked);



            return false;
        }
    }
    private void createAndShowAlertDialog(Integer mPosition) {
        final int position = mPosition;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("Remove");
        builder.setMessage("Are you sure you want to remove this dish?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String getKey = mKeys.get(position);
                foodList.remove(position);
                mKeys.remove(position);
                userRef.child(getKey).removeValue();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    }


