package in.tosc.trickle.api;

import android.os.AsyncTask;
import android.util.Log;

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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by championswimmer on 22/3/15.
 */
public class PlacesGetterTask extends AsyncTask<PlacesGetArgs, Void, ArrayList<PlaceObject>> {

    public static final String TAG = "Trickle PlacesGetter";

    ArrayList<PlaceObject> pObjs;


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
                    if (mJobj.getString("rating") != null) {
                        pobj.rating =
                                (float) mJobj.getDouble("rating");
                    } else pobj.rating = 3;

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

    private int getRadiusFromZoom (float zoom) {
        switch ((int) zoom) {
            case 2:
            case 3:
            case 4: return 75000;
            case 5: return 50000;
            case 6: return 30000;
            case 7: return 25000;
            case 8: return 22000;
            case 9: return 20000;
            case 10:default: return 18000;
            case 11: return 15000;
            case 12: return 12000;
            case 13: return 9000;
            case 14: return 7000;
            case 15: return 6000;
            case 16: return 5000;
            case 17: return 4000;
            case 18: return 3000;
            case 19:
            case 20:
            case 21: return 2000;
        }
    }
}
