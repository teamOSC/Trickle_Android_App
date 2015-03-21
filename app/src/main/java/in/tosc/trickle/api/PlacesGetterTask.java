package in.tosc.trickle.api;

import android.os.AsyncTask;

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

/**
 * Created by championswimmer on 22/3/15.
 */
public class PlacesGetterTask extends AsyncTask<PlacesGetArgs, Void, PlaceObject[]> {

    PlaceObject[] pObjs;


    @Override
    protected PlaceObject[] doInBackground(PlacesGetArgs... params) {
        PlacesGetArgs argObj = params[0];
        String reqUrl = "http://tosc.in:8087/" +
                "?lat="+ argObj.latitude +
                "&long="+ argObj.latitude +
                "&type=" + argObj.placetype;

        HttpClient client = new DefaultHttpClient();
        HttpResponse resp;
        String jString;
        try {
            resp = client.execute(new HttpGet(reqUrl));
            InputStream iStream = resp.getEntity().getContent();
            if (iStream != null) {
                jString = convertInputStreamToString(iStream);
                JSONObject jobj = new JSONObject(jString);
                JSONArray jArr = jobj.getJSONArray("data");
                pObjs = new PlaceObject[jArr.length()];
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject mJobj = jArr.getJSONObject(i);
                    pObjs[i].latitude =
                            mJobj.getJSONObject("coords")
                            .getDouble("lat");
                    pObjs[i].longitude =
                            mJobj.getJSONObject("coords")
                                    .getDouble("lng");
                    pObjs[i].name =
                            mJobj.getString("name");
                    pObjs[i].rating =
                            (float) mJobj.getDouble("rating");
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
    protected void onPostExecute(PlaceObject[] placeObjects) {
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
}
