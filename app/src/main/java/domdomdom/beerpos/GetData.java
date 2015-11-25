package domdomdom.beerpos;

/**
 * Created by domk1_000 on 11/8/2015.
 */
import java.io.BufferedReader;
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


    public JSONObject getweight(String address,String client_id,String client_secret,String redirect_uri,String token,String sc, String sv) {
        // Making HTTP request
        try {
            // DefaultHttpClient
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(address + "client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri="+redirect_uri+"&access_token="+token+"&sc="+sc+"&sv="+sv);

//            params.add(new BasicNameValuePair("client_id", client_id));
//            params.add(new BasicNameValuePair("client_secret", client_secret));
//            params.add(new BasicNameValuePair("redirect_uri", redirect_uri));
//            params.add(new BasicNameValuePair("access_token", token));
//            params.add(new BasicNameValuePair("sc", sc));
//            params.add(new BasicNameValuePair("sv", sv));
//            Log.d("params", String.valueOf(params));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//          httpPost.setEntity(new UrlEncodedFormEntity(params));
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
            Log.e("JSONStr", json);
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