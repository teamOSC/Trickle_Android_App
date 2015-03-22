package in.tosc.trickle.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
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
            case STATS_GROWTH: loadGrowthOnMap(); break;
            case STATS_LITERACY: loadLiteracyOnMap(); break;
            case STATS_POPULATION: loadPopulationOnMap(); break;
        }
    }

    private void loadSexRatioOnMap () {
        if (mMap.getCameraPosition().zoom > 8) {
            for (DistStatsObject distStatsObject : distStatsObjects) {
                float distance[] = new float[3];
                Location.distanceBetween(
                        distStatsObject.latitude,
                        distStatsObject.longitude,
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        distance
                );
                if (distance[0] > 100000) continue;
                IconGenerator ig = new IconGenerator(mContext);
                if (distStatsObject.sexRatio < 800) ig.setStyle(IconGenerator.STYLE_RED);
                else if (distStatsObject.sexRatio < 900) ig.setStyle(IconGenerator.STYLE_ORANGE);
                else ig.setStyle(IconGenerator.STYLE_GREEN);

//                mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(distStatsObject.latitude, distStatsObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(distStatsObject.latitude, distStatsObject.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(ig.makeIcon(String.valueOf(distStatsObject.sexRatio)))));
            }
        } else {
            // make heatmap otherwise
            List<WeightedLatLng> pointList = new ArrayList<WeightedLatLng>();
            for (DistStatsObject distStatsObject : distStatsObjects) {
                pointList.add(
                        new WeightedLatLng(
                                new LatLng(distStatsObject.latitude,
                                        distStatsObject.longitude),
                                (-distStatsObject.sexRatio)));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .weightedData(pointList) //FIXME: this comes to be null sometimes
                                .build();
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }
        }
    }
    private void loadPopulationOnMap () {
        if (mMap.getCameraPosition().zoom > 8) {
            for (DistStatsObject distStatsObject : distStatsObjects) {
                float distance[] = new float[3];
                Location.distanceBetween(
                        distStatsObject.latitude,
                        distStatsObject.longitude,
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        distance
                );
                if (distance[0] > 100000) continue;
                IconGenerator ig = new IconGenerator(mContext);
                if (distStatsObject.population < 4000000) ig.setStyle(IconGenerator.STYLE_GREEN);
                else if (distStatsObject.population < 7000000) ig.setStyle(IconGenerator.STYLE_ORANGE);
                else ig.setStyle(IconGenerator.STYLE_RED);

//                mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(distStatsObject.latitude, distStatsObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(distStatsObject.latitude, distStatsObject.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(ig.makeIcon(String.valueOf(distStatsObject.population)))));
            }
        } else {
            // make heatmap otherwise
            List<WeightedLatLng> pointList = new ArrayList<WeightedLatLng>();
            for (DistStatsObject distStatsObject : distStatsObjects) {
                pointList.add(
                        new WeightedLatLng(
                                new LatLng(distStatsObject.latitude,
                                        distStatsObject.longitude),
                                (distStatsObject.population)));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .weightedData(pointList) //FIXME: this comes to be null sometimes
                                .build();
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }
        }
    }
    private void loadLiteracyOnMap () {
        if (mMap.getCameraPosition().zoom > 8) {
            for (DistStatsObject distStatsObject : distStatsObjects) {
                float distance[] = new float[3];
                Location.distanceBetween(
                        distStatsObject.latitude,
                        distStatsObject.longitude,
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        distance
                );
                if (distance[0] > 100000) continue;
                IconGenerator ig = new IconGenerator(mContext);
                if (distStatsObject.literacy > 85) ig.setStyle(IconGenerator.STYLE_GREEN);
                else if (distStatsObject.literacy > 75) ig.setStyle(IconGenerator.STYLE_ORANGE);
                else ig.setStyle(IconGenerator.STYLE_RED);

//                mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(distStatsObject.latitude, distStatsObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(distStatsObject.latitude, distStatsObject.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(ig.makeIcon(String.valueOf(distStatsObject.literacy) + "%"))));
            }
        } else {
            // make heatmap otherwise
            List<WeightedLatLng> pointList = new ArrayList<WeightedLatLng>();
            for (DistStatsObject distStatsObject : distStatsObjects) {
                pointList.add(
                        new WeightedLatLng(
                                new LatLng(distStatsObject.latitude,
                                        distStatsObject.longitude),
                                (-distStatsObject.literacy)));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .weightedData(pointList) //FIXME: this comes to be null sometimes
                                .build();
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }
        }
    }

    private void loadGrowthOnMap () {
        if (mMap.getCameraPosition().zoom > 8) {
            for (DistStatsObject distStatsObject : distStatsObjects) {
                float distance[] = new float[3];
                Location.distanceBetween(
                        distStatsObject.latitude,
                        distStatsObject.longitude,
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude,
                        distance
                );
                if (distance[0] > 100000) continue;
                IconGenerator ig = new IconGenerator(mContext);
                if (distStatsObject.growth > 40) ig.setStyle(IconGenerator.STYLE_GREEN);
                else if (distStatsObject.growth > 20) ig.setStyle(IconGenerator.STYLE_ORANGE);
                else ig.setStyle(IconGenerator.STYLE_RED);

//                mMap.addCircle(new CircleOptions()
//                        .center(new LatLng(distStatsObject.latitude, distStatsObject.longitude)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(distStatsObject.latitude, distStatsObject.longitude))
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(ig.makeIcon(String.valueOf(distStatsObject.growth) + "%"))));
            }
        } else {
            // make heatmap otherwise
            List<WeightedLatLng> pointList = new ArrayList<WeightedLatLng>();
            for (DistStatsObject distStatsObject : distStatsObjects) {
                pointList.add(
                        new WeightedLatLng(
                                new LatLng(distStatsObject.latitude,
                                        distStatsObject.longitude),
                                (-distStatsObject.growth)));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .weightedData(pointList) //FIXME: this comes to be null sometimes
                                .build();
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
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
