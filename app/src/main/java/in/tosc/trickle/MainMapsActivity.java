package in.tosc.trickle;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainMapsActivity extends FragmentActivity
        implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "Trickle MMActivity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    LatLng mLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);


        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            moveMapToMyLocation();
        } catch (Exception e) {
            // Y u do dis ?
        }


    }


    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void moveMapToMyLocation () {

        mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        if (mMap.getMyLocation() != null) {
            mLatLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        }

        Log.w(TAG, mLatLng.toString());
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }




    @Override
    public void onConnected(Bundle connectionHint) {
        Log.w(TAG, "onConnected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            moveMapToMyLocation();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed");

    }
}

