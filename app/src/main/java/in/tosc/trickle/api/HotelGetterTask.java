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
public class HotelGetterTask extends AsyncTask<HotelGetArgs, Void, ArrayList<HotelObject>> {

    public HotelGetterTask (GoogleMap map) {
        mMap = map;
    }

    public static final String TAG = "Trickle HotelGetter";

    ArrayList<HotelObject> hObjs;
    GoogleMap mMap;


    @Override
    protected ArrayList<HotelObject> doInBackground(HotelGetArgs... params) {
        HotelGetArgs argObj = params[0];
        String reqUrl ="http://tosc.in:8087/" +
                "?lat="+ argObj.latitude +
                "&long="+ argObj.longitude +
                "&type=" + "hotels";

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
                JSONArray jArr = jobj.getJSONArray("hotels");
                hObjs = new ArrayList<>(jArr.length());
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject mJobj = jArr.getJSONObject(i);
                    Log.d(TAG, "mJobj = " + mJobj.toString());
                    Log.d(TAG, "mJobj.coords = " + mJobj.getJSONObject("coords").toString());
                    HotelObject hobj = new HotelObject();
                    hobj.latitude =
                            mJobj.getJSONObject("coords")
                                    .getDouble("lat");
                    hobj.longitude =
                            mJobj.getJSONObject("coords")
                                    .getDouble("lng");
                    hobj.name =
                            mJobj.getString("name");
                    hobj.meta =
                            mJobj.getString("meta");
                    

                    hObjs.add(i, hobj);
                }
            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return hObjs;
    }

    @Override
    protected void onPostExecute(ArrayList<HotelObject> hotelObjects) {
        super.onPostExecute(hotelObjects);

        if (mMap.getCameraPosition().zoom > 11) {
            for (HotelObject hotelObject : hotelObjects) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(hotelObject.latitude, hotelObject.longitude))
                        .snippet(hotelObject.meta)
                        .title(hotelObject.name));
            }
        } else {
            // make heatmap otherwise
            List<LatLng> pointList = new ArrayList<LatLng>();
            for (HotelObject hotelObject : hotelObjects) {
                pointList.add(new LatLng(hotelObject.latitude, hotelObject.longitude));
            }
            if (!pointList.isEmpty()) {
                HeatmapTileProvider provider =
                        new HeatmapTileProvider.Builder()
                                .data(pointList) //FIXME: this comes to be null sometimes
                                .opacity(1)
                                .radius(40)
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

}
