package domdomdom.beerpos;

/**
 * Created by domk1_000 on 11/8/2015.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;
public class GetData {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    public GetData() {
    }
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    Map<String, String> mapn;
    DefaultHttpClient httpClient;
    HttpPost httpPost;


    public JSONObject getweight(String address,String client_id,String client_secret,String redirect_uri,String token,String sc, String sv,int beerIndex) {
        // Making HTTP request
        try {
            ArrayList<String> beerName = new ArrayList<>();
            ArrayList<Boolean> beerOnTap = new ArrayList<>();
            ArrayList<Double> beerValue = new ArrayList<>();
            ArrayList<Long> beerStart = new ArrayList<>();
            ArrayList<Long> beerEnd = new ArrayList<>();

            ArrayList<Integer> beerClicks = new ArrayList<>();

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/BeerPOS");


            if (folder.exists()) {
                Log.d("getBeerData", "Folder exists");

                final String filenameBeers = folder.toString() + "/" + "BeerPOS_BEER.csv";

                FileInputStream is = new FileInputStream(filenameBeers);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    int index = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {

                        String[] RowData = line.split(",");
                        // Log.d("Beer.csv","RowData: "+RowData[0]+","+RowData[1]+","+RowData[2]+","+RowData[3]);

                        beerName.add(index, RowData[0]);
                        beerValue.add(index, Double.parseDouble(RowData[1]));
                        beerClicks.add(index, Integer.parseInt(RowData[2]));
                        beerOnTap.add(index, Boolean.valueOf(RowData[3]));
                        beerStart.add(index, Long.valueOf((RowData[4])));
                        beerEnd.add(index, Long.valueOf((RowData[5])));

                        //beerItem.add("beer");

                        index++;

                    }
                } catch (IOException ex) {
                    // handle exception
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // handle exception
                    }
                }
            }


            float startTime;
            float endTime;
            if(beerEnd.get(beerIndex) <= 0){
                endTime = System.currentTimeMillis() / 1000;
            }
            else{
                endTime = beerEnd.get(beerIndex);
            }

            if( beerStart.get(beerIndex) <= 0){
                startTime = beerStart.get(beerIndex);
            }
            else{
                startTime = System.currentTimeMillis() / 1000;
            }
                // DefaultHttpClient
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(address + "client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri="+redirect_uri+"&access_token="+token+"&sc="+sc+"&sv="+sv+"&start_time=" + startTime + "&end_time=" + endTime);

            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            Log.d("url", String.valueOf(httpPost.getURI()));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            Log.d("reader", String.valueOf(reader));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            json = sb.toString();
            Log.d("wtf", String.valueOf(sb));
            Log.d("JSONStr", json);
        } catch (Exception e) {
            e.getMessage();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // Parse the String to a JSON Object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return JSON String
        return jObj;
    }

}
