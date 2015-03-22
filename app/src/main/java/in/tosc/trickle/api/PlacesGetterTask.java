package in.tosc.trickle.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by championswimmer on 22/3/15.
 */
public class PlacesGetterTask extends AsyncTask<PlacesGetArgs, Void, ArrayList<PlaceObject>> {

    public PlacesGetterTask (GoogleMap map) {
        mMap = map;
    }

    public static final String TAG = "Trickle PlacesGetter";

    ArrayList<PlaceObject> pObjs;
    GoogleMap mMap;


    @Override
    protected ArrayList<PlaceObject> doInBackground(PlacesGetArgs... params) {
        PlacesGetArgs argObj = params[0];
        String reqUrl ="http://tosc.in:8087/" +
                "?lat="+ argObj.latitude +
                "&long="+ argObj.longitude +
                "&type=" + argObj.placetype +
                "&radius=" + getRadiusFromZoom(argObj.zoom);

        Log.d(TAG, "url = " + reqUrl );

        HttpClient client = new DefaultHttpClient();
        HttpResponse resp;
        String jString;
        try {
            resp = client.execute(new HttpGet(reqUrl));
            InputStream iStream = resp.getEntity().getContent();
            if (iStream != null) {
                jString = convertInputStreamToString(iStream);
                Log.d(TAG, "resp = " + jString);
                JSONObject jobj = new JSONObject(jString);
                JSONArray jArr = jobj.getJSONArray("data");
                pObjs = new ArrayList<>(jArr.length());
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject mJobj = jArr.getJSONObject(i);
                    Log.d(TAG, "mJobj = " + mJobj.toString());
                    Log.d(TAG, "mJobj.coords = " + mJobj.getJSONObject("coords").toString());
                    PlaceObject pobj = new PlaceObject();
                    pobj.latitude =
                            mJobj.getJSONObject("coords")
                            .getDouble("lat");
                    pobj.longitude =
                            mJobj.getJSONObject("coords")
                                    .getDouble("lng");
                    pobj.name =
                            mJobj.getString("name");
                    try {
                        pobj.rating =
                                (float) mJobj.getDouble("rating");
                    } catch (Exception e) {
                        pobj.rating = 3;
                    }

                    pObjs.add(i, pobj);
                }
            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return pObjs;
    }

    @Override
    protected void onPostExecute(ArrayList<PlaceObject> placeObjects) {
        super.onPostExecute(placeObjects);

        if (mMap.getCameraPosition().zoom > 11) {
            for (Iterator<PlaceObject> iterator = placeObjects.iterator(); iterator.hasNext(); ) {
                PlaceObject placeObject = iterator.next();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(placeObject.latitude, placeObject.longitude))
                        .title(placeObject.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(setMarkerColor(placeObject.rating))));
            }
        } else {
            // make heatmap otherwise
            List<LatLng> pointList = new ArrayList<LatLng>();
            for (PlaceObject placeObject : placeObjects) {
                pointList.add(new LatLng(placeObject.latitude, placeObject.longitude));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .data(pointList) //FIXME: this comes to be null sometimes
                                .build();
                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private float setMarkerColor (float rating) {
        switch ((int) rating) {
            case 1: return BitmapDescriptorFactory.HUE_RED;
            case 2: return BitmapDescriptorFactory.HUE_ROSE;
            case 3:default: return BitmapDescriptorFactory.HUE_ORANGE;
            case 4: return BitmapDescriptorFactory.HUE_YELLOW;
            case 5: return BitmapDescriptorFactory.HUE_GREEN;
        }
    }

    private int getRadiusFromZoom (float zoom) {
        switch ((int) zoom) {
            case 2:
            case 3:
            case 4: return 2000000;
            case 5: return 1200000;
            case 6: return 700000;
            case 7: return 400000;
            case 8: return 300000;
            case 9: return 200000;
            case 10:default: return 150000;
            case 11: return 50000;
            case 12: return 10000;
            case 13: return 7500;
            case 14: return 6000;
            case 15: return 5000;
            case 16: return 4000;
            case 17: return 3000;
            case 18: return 2000;
            case 19:
            case 20:
            case 21: return 1000;
        }
    }
}
