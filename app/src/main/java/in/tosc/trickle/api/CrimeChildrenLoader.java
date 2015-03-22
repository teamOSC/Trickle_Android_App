package in.tosc.trickle.api;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by championswimmer on 22/3/15.
 */
public class CrimeChildrenLoader extends AsyncTask<Integer, Void, ArrayList<CrimeChildrenObject>>{

    Context mContext;
    GoogleMap mMap;

    static ArrayList<CrimeChildrenObject> crimeChildrenObjects;
    static boolean loadedOnce;

    public CrimeChildrenLoader(Context context, GoogleMap map) {
        mContext = context;
        mMap = map;
    }


    @Override
    protected ArrayList<CrimeChildrenObject> doInBackground(Integer... params) {

        if (loadedOnce && (crimeChildrenObjects != null)) {
            return crimeChildrenObjects;
        } else {
            try {
                JSONArray jArr = new JSONArray(loadJSONFromAsset());
                crimeChildrenObjects = new ArrayList<>(jArr.length());
                for (int i = 0; i < jArr.length(); i++) {
                    Log.d("Trickle", jArr.getJSONObject(i).getString("address"));
                    String address = jArr.getJSONObject(i).getString("address");
                    double latitude;
                    double longitude;
                    try {
                        latitude = jArr.getJSONObject(i).getJSONObject("coords").getDouble("lat");
                        longitude = jArr.getJSONObject(i).getJSONObject("coords").getDouble("lng");
                    } catch (Exception e ) {
                        latitude = 0;
                        longitude = 0;
                    }
                    int crimes;
                    try {
                        crimes = jArr.getJSONObject(i).getInt("crimes");
                    } catch (Exception e) {
                        crimes = 50;
                    }
                    crimeChildrenObjects.add(new CrimeChildrenObject(
                            address,
                            latitude,
                            longitude,
                            crimes
                            ));
                }
                loadedOnce = true;
                return crimeChildrenObjects;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    @Override
    protected void onPostExecute(ArrayList<CrimeChildrenObject> crimeChildrenObjects) {
        super.onPostExecute(crimeChildrenObjects);
        loadCrimeChildrenMap();

    }

    private void loadCrimeChildrenMap() {
        if (mMap.getCameraPosition().zoom > 9) {
            for (CrimeChildrenObject crimeChildrenObject : crimeChildrenObjects) {
                float distance[] = new float[3];
                Location.distanceBetween(
                        crimeChildrenObject.latitude,
                        crimeChildrenObject.longitude,
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        distance
                );
                if (distance[0] > 200000) continue;
                IconGenerator ig = new IconGenerator(mContext);
                if (crimeChildrenObject.crimes > 500) ig.setStyle(IconGenerator.STYLE_RED);
                else if (crimeChildrenObject.crimes > 100) ig.setStyle(IconGenerator.STYLE_ORANGE);
                else ig.setStyle(IconGenerator.STYLE_GREEN);

//                mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(crimeChildrenObject.latitude, crimeChildrenObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(crimeChildrenObject.latitude, crimeChildrenObject.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(ig.makeIcon(String.valueOf(crimeChildrenObject.crimes)))));
            }
        } else {
            // make circlemap otherwise
            for (CrimeChildrenObject crimeChildrenObject : crimeChildrenObjects) {
                if (crimeChildrenObject.crimes < 200) continue;
                mMap.addCircle(new CircleOptions()
                        .fillColor(Color.RED)
                        .strokeColor(Color.RED)
                        .radius(crimeChildrenObject.crimes * 10)
                        .center(new LatLng(crimeChildrenObject.latitude,
                                crimeChildrenObject.longitude)));
            }

        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = mContext.getAssets().open("crime_children.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

}
