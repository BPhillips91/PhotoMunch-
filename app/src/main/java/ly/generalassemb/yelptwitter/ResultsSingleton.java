package ly.generalassemb.yelptwitter;

import java.util.ArrayList;

/**
 * Created by aaronfields on 7/20/16.
 */
public class ResultsSingleton {
    private String userName;
    private String userID;
    private double latitude;
    private double longitude;
    private boolean isLoggedIn = false;

    private static ResultsSingleton resultsSingleton;
    private static ArrayList<Food> foodArrayList;

    private ResultsSingleton(){
        foodArrayList = new ArrayList<>();
    }

    public static ResultsSingleton getInstance(){
        if(resultsSingleton == null)
            resultsSingleton = new ResultsSingleton();
        return resultsSingleton;
    }

    public ArrayList<Food> getFoodArrayList() {
        return foodArrayList;
    }

    public void setFoodArrayList(ArrayList<Food> foodArrayList) {
        ResultsSingleton.foodArrayList = foodArrayList;
    }

    public Food getFoodAtPosition(int position){
        return foodArrayList.get(position);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }



}
