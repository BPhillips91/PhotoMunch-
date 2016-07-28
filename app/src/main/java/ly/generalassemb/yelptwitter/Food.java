package ly.generalassemb.yelptwitter;

/**
 * Created by aaronfields on 7/20/16.
 */
public class Food {

    protected String foodPic;
    protected String restaurantName;
    protected String foodId;

    public Food(){

    }

    public Food(String foodPic, String foodId, String restaurantName) {
        this.foodPic = foodPic;
        this.restaurantName = restaurantName;
        this.foodId = foodId;

    }

    public String getFoodPic() {
        return foodPic;
    }

    public void setFoodPic(String foodPic) {
        this.foodPic = foodPic;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
