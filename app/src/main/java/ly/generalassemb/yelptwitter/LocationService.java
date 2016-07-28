package ly.generalassemb.yelptwitter;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class LocationService extends Service implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location location;
    double lat, longitude;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "Creating Service");
        mGoogleApiClient = null;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "Starting Service");
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        Intent intent = new Intent();
        intent.setAction("coordinatesLoaded");
        sendBroadcast(intent);
        Log.d("STOP", "Service stopped");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = getLocation(mGoogleApiClient);
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public Location getLocation(GoogleApiClient client) {
        location = null;
        location = LocationServices.FusedLocationApi.getLastLocation(client);

        if (location != null) {
            lat = location.getLatitude();
            longitude = location.getLongitude();
            ResultsSingleton.getInstance().setLatitude(lat);
            ResultsSingleton.getInstance().setLongitude(longitude);
            Log.d("cords", "lat = " + lat + " long = " + longitude);
        }
        return location;
    }
}