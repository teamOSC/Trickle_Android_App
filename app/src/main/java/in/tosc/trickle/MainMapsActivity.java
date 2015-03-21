package in.tosc.trickle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

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

        FloatingActionButton mapActionButton = makeFAB(R.drawable.ic_places, FloatingActionButton.POSITION_TOP_CENTER, this);

        SubActionButton.Builder mapItemBuilder = new SubActionButton.Builder(this);
        // repeat many times:

        SubActionButton mapItemButton1 = makeSAB(R.drawable.ic_hospital, this, mapItemBuilder);
        SubActionButton mapItemButton2 = makeSAB(R.drawable.ic_restaurant, this, mapItemBuilder);
        SubActionButton mapItemButton3 = makeSAB(R.drawable.ic_restroom, this, mapItemBuilder);
        SubActionButton mapItemButton4 = makeSAB(R.drawable.ic_atm, this, mapItemBuilder);
        SubActionButton mapItemButton5 = makeSAB(R.drawable.ic_pump, this, mapItemBuilder);
        SubActionButton mapItemButton6 = makeSAB(R.drawable.ic_taxi, this, mapItemBuilder);

        FloatingActionMenu mapActionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(mapItemButton1)
                .addSubActionView(mapItemButton2)
                .addSubActionView(mapItemButton3)
                .addSubActionView(mapItemButton4)
                .addSubActionView(mapItemButton5)
                .addSubActionView(mapItemButton6)
                .attachTo(mapActionButton)
                .setRadius(250)
                .setStartAngle(0)
                .setEndAngle(180)
                .build();




        FloatingActionButton heatActionButton = makeFAB(R.drawable.ic_heatmap, FloatingActionButton.POSITION_BOTTOM_CENTER, this);

        SubActionButton.Builder heatItemBuilder = new SubActionButton.Builder(this);

        SubActionButton heatItemButton1 = makeSAB(R.drawable.ic_crime, this, heatItemBuilder);
        SubActionButton heatItemButton2 = makeSAB(R.drawable.ic_water, this, heatItemBuilder);
        SubActionButton heatItemButton3 = makeSAB(R.drawable.ic_disaster, this, heatItemBuilder);
        SubActionButton heatItemButton4 = makeSAB(R.drawable.ic_healthcare, this, heatItemBuilder);
        SubActionButton heatItemButton5 = makeSAB(R.drawable.ic_pollution, this, heatItemBuilder);
        SubActionButton heatItemButton6 = makeSAB(R.drawable.ic_population, this, heatItemBuilder);

        FloatingActionMenu heatActionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(heatItemButton1)
                .addSubActionView(heatItemButton2)
                .addSubActionView(heatItemButton3)
                .addSubActionView(heatItemButton4)
                .addSubActionView(heatItemButton5)
                .addSubActionView(heatItemButton6)
                .attachTo(heatActionButton)
                .setRadius(250)
                .setStartAngle(0)
                .setEndAngle(-180)
                .build();

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

    private FloatingActionButton makeFAB (int resId, int pos, Context c) {
        ImageView icon = new ImageView(c);
        icon.setImageResource(resId);
        return new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setPosition(pos)
                .build();
    }

    private SubActionButton makeSAB (int resId, Context c, SubActionButton.Builder sBuilder) {
        ImageView icon = new ImageView(c);
        icon.setImageResource(resId);
        icon.setPadding(20,20,20,20);
        int mySubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.my_sub_action_button_size);
        int mySubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.my_sub_action_button_content_margin);
        FrameLayout.LayoutParams newContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        newContentParams.setMargins(mySubActionButtonContentMargin,
                mySubActionButtonContentMargin,
                mySubActionButtonContentMargin,
                mySubActionButtonContentMargin);
        sBuilder.setLayoutParams(newContentParams);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mySubActionButtonSize, mySubActionButtonSize);
        sBuilder.setLayoutParams(newParams);
        return sBuilder.setContentView(icon).build();
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

