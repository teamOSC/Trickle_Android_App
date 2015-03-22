package in.tosc.trickle.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by championswimmer on 22/3/15.
 */
public class DistStatsLoader extends AsyncTask<Integer, Void, ArrayList<DistStatsObject>>{

    Context mContext;
    GoogleMap mMap;

    static ArrayList<DistStatsObject> distStatsObjects;
    static boolean loadedOnce;
    static int statChoice;

    public DistStatsLoader(Context context, GoogleMap map) {
        mContext = context;
        mMap = map;
    }

    public static final int STATS_LITERACY = 41;
    public static final int STATS_POPULATION = 42;
    public static final int STATS_SEXRATIO = 43;
    public static final int STATS_GROWTH = 44;


    @Override
    protected ArrayList<DistStatsObject> doInBackground(Integer... params) {

        if (loadedOnce && (distStatsObjects != null)) {
            return distStatsObjects;
        } else {
            try {
                JSONArray jArr = new JSONArray(loadJSONFromAsset());
                distStatsObjects = new ArrayList<>(jArr.length());
                for (int i = 0; i < jArr.length(); i++) {
                    Log.d("Trickle", jArr.getJSONObject(i).getString("district"));
                    String district = jArr.getJSONObject(i).getString("district");
                    double latitude;
                    double longitude;
                    try {
                        latitude = jArr.getJSONObject(i).getJSONObject("coords").getDouble("lat");
                        longitude = jArr.getJSONObject(i).getJSONObject("coords").getDouble("lng");
                    } catch (Exception e ) {
                        latitude = 0;
                        longitude = 0;
                    }
                    int sexratio = jArr.getJSONObject(i).getInt("sex_ratio");
                    long population = Long.valueOf(jArr.getJSONObject(i).getString("population").replace(",", ""));
                    float literacy = (float)jArr.getJSONObject(i).getDouble("literacy");
                    float growth = (float)jArr.getJSONObject(i).getDouble("growth");
                    distStatsObjects.add(new DistStatsObject(
                            district,
                            latitude,
                            longitude,
                            sexratio,
                            population,
                            literacy,
                            growth
                            ));
                }
                statChoice = params[0];
                return distStatsObjects;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String commaStripper (String cs) {
        return cs.replace(",", "");
    }

    @Override
    protected void onPostExecute(ArrayList<DistStatsObject> distStatsObjects) {
        super.onPostExecute(distStatsObjects);
        switch (statChoice) {
            case STATS_SEXRATIO: loadSexRatioOnMap(); break;
            case STATS_GROWTH:
            case STATS_LITERACY:
            case STATS_POPULATION:
        }
    }

    private void loadSexRatioOnMap () {
        if (mMap.getCameraPosition().zoom > 10) {
            for (DistStatsObject distStatsObject : distStatsObjects) {
                IconGenerator ic = new IconGenerator(mContext);
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(distStatsObject.latitude, distStatsObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(distStatsObject.latitude, distStatsObject.longitude))
                        .title(String.valueOf(distStatsObject.sexRatio)));
            }
        } else {
            // make heatmap otherwise
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = mContext.getAssets().open("district_stats.json");
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