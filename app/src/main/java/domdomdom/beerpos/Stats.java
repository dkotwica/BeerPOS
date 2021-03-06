package domdomdom.beerpos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats extends MainActivity {

    private static String CLIENT_ID = "18b37f730f1847aa994b22c5a72dfa05";
    //Use your own client id
    private static String CLIENT_SECRET ="1cf2be2258084120b63d9394feb8b83f";
    //Use your own client secret
    private static String SV = "C374493A1F544AE0ACA90EA56D37C0AD";
    private static String SC = "01857ACD59484A6C9016EF727F38B84C";
    private static String REDIRECT_URI="http://www.cise.ufl.edu/~mssz/SeniorProject/senior-F15.html";
    private static String GRANT_TYPE="authorization_code";
    private static String TOKEN_URL ="https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/?";
    private static String OAUTH_URL ="https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/?";
    private static String DATA_URL = "https://api.ihealthlabs.com:8443/openapiv2/user/91b8a667ea4a433d8d85fa774d4b1392/weight.json/?";
    private static String API_NAME = "OpenApiWeight+OpenApiUserInfo";
    private static String RAPI_NAME = "OpenApiWeight+OpenApiUserInfo";
    //private static String OAUTH_SCOPE="https://www.googleapis.com/auth/urlshortener";
    //Change the Scope as you need
    private static String Token;
    public ArrayList<Double> weightValue;


    WebView web;
    Button auth;
    Button btweight;
    SharedPreferences pref;
    TextView Access;
    TextView dataList;
    Map<String, List<String>> Bar_Category;
    List<String> Bar_List;
    ExpandableListView Exp_list;
    BeerAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        weightValue = new ArrayList<>();
        final BeerData b = new BeerData();
        Exp_list = (ExpandableListView) findViewById(R.id.exp_list);
        try {
            Bar_Category = b.getInfo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Bar_List = new ArrayList<String>(Bar_Category.keySet());
        adapter = new BeerAdapter(this, Bar_Category, Bar_List);
        Exp_list.setAdapter(adapter);


        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        Access =(TextView)findViewById(R.id.Access);
        dataList = (TextView)findViewById(R.id.dataList);
        auth = (Button)findViewById(R.id.auth);
        auth.setOnClickListener(new View.OnClickListener() {
            Dialog auth_dialog;
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                auth_dialog = new Dialog(Stats.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView)auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(OAUTH_URL+"redirect_uri="+REDIRECT_URI+"&response_type=code&client_id="+CLIENT_ID+"&APIName="+API_NAME+"&RequiredAPIName="+RAPI_NAME);
                web.setWebViewClient(new WebViewClient() {

                    boolean authComplete = false;
                    Intent resultIntent = new Intent();

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);

                    }

                    String authCode;
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        if (url.contains("?code=") && authComplete != true) {
                            Uri uri = Uri.parse(url);
                            authCode = uri.getQueryParameter("code");
                            Log.i("", "CODE : " + authCode);
                            authComplete = true;
                            resultIntent.putExtra("code", authCode);
                            Stats.this.setResult(Activity.RESULT_OK, resultIntent);
                            setResult(Activity.RESULT_CANCELED, resultIntent);

                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("Code", authCode);
                            edit.commit();
                            auth_dialog.dismiss();
                            new TokenGet().execute();
                            Toast.makeText(getApplicationContext(),"Authorization Code is: " +authCode, Toast.LENGTH_SHORT).show();
                        }else if(url.contains("error=access_denied")){
                            Log.i("", "ACCESS_DENIED_HERE");
                            resultIntent.putExtra("code", authCode);
                            authComplete = true;
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                            auth_dialog.dismiss();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("BeerPOS Stats");
                auth_dialog.setCancelable(true);
            }
        });
        btweight = (Button)findViewById(R.id.weightList);
        btweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataGet().execute();
                try {
                    Bar_Category = b.getInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

            }




        });

    }
    protected void onPause() {
        super.onPause();

        try {
            // saveBeerData();
            saveWeightData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onResume() {
        super.onResume();
        try {
           // getBeerData();
            getWeightData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void saveWeightData() throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/BeerPOS");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        final String filenameWeight = folder.toString() + "/" + "BeerPOS_WEIGHT.csv";


        FileWriter fw = new FileWriter(filenameWeight);
        fw.write("");
        if (weightValue.size() > 0) {
            for (int i = 0; i < weightValue.size(); i++) {
                fw.append((weightValue.get(i)).toString());
                fw.append('\n');
            }
        }
        fw.flush();
        fw.close();
    }

    private void getWeightData() throws IOException {
        Log.d("getBeerData", "Sucessfully called the getBeerData()");
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/BeerPOS");

        weightValue.clear();
        if (folder.exists()) {
            Log.d("getBeerData", "Folder exists");

            final String filenameWeight = folder.toString() + "/" + "BeerPOS_WEIGHT.csv";

            FileInputStream is = new FileInputStream(filenameWeight);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                int index = 0;
                String line;
                while ((line = reader.readLine()) != null) {

                    String[] RowData = line.split(",");
                    // Log.d("Beer.csv","RowData: "+RowData[0]+","+RowData[1]+","+RowData[2]+","+RowData[3]);

                    weightValue.add(index, Double.valueOf(RowData[0]));
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
    }

    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String Code;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Stats.this);
            pDialog.setMessage("Contacting iHealth ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            Code = pref.getString("Code", "");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            GetAccessToken jParser = new GetAccessToken();
            JSONObject json = jParser.gettoken(TOKEN_URL,Code,CLIENT_ID,CLIENT_SECRET,REDIRECT_URI,GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null){

                try {

                    String tok = json.getString("AccessToken");
                    Token = tok;
                    String expire = json.getString("Expires");
                    String refresh = json.getString("RefreshToken");
                    String userid = json.getString("UserID");


                    auth.setText("Retrieved");
                    //Access.setText("Access Token:"+tok+'\n'+"Expires:"+expire+'\n'+"Refresh Token:"+refresh+'\n'+"User ID: "+userid);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }
    private class DataGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        //String Token;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Stats.this);
            pDialog.setMessage("Contacting iHealth ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //Token = pref.getString("access_token", "");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            GetData jParser = new GetData();
            JSONObject json = jParser.getweight(DATA_URL, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, Token, SC, SV, 0);
            Log.d("get data", CLIENT_ID);
            return json;
        }




        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            try {
                getWeightData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (json != null){

                try {
                    JSONObject jsonDataList = new JSONObject(json.toString());
                    JSONArray jsonWeight = jsonDataList.getJSONArray("WeightDataList");


                    for (int i = 0; i < jsonWeight.length(); i++) {
                        if(weightValue.size() == 0){
                            weightValue.add(jsonWeight.getJSONObject(i).getDouble("WeightValue"));
                        }
                        else {
                            weightValue.set(0, jsonWeight.getJSONObject(i).getDouble("WeightValue"));
                        }

                        dataList.setText("Weight Value:"+weightValue.get(0));
                    }

                    btweight.setText("Refresh");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    saveWeightData();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }

}