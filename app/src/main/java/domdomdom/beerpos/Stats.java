package domdomdom.beerpos;

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
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private static String DATA_URL = "http://sandboxapi.ihealthlabs.com/openapiv2/user/91b8a66ea4a433d8d85fa774d4b1392/weight.json/?";
    private static String API_NAME = "OpenApiWeight+OpenApiUserInfo";
    private static String RAPI_NAME = "OpenApiWeight+OpenApiUserInfo";
    //private static String OAUTH_SCOPE="https://www.googleapis.com/auth/urlshortener";
    //Change the Scope as you need
    private String Token;
    WebView web;
    Button auth;
    Button btweight;
    SharedPreferences pref;
    TextView Access;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        Access =(TextView)findViewById(R.id.Access);
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
            }



            });
    }

    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String Code;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Stats.this);
            pDialog.setMessage("Contacting Google ...");
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

                    Log.d("Token Access", Token);
                    Log.d("Expire", expire);
                    Log.d("Refresh", refresh);
                    auth.setText("Authenticated");
                    Access.setText("Access Token:"+tok+'\n'+"Expires:"+expire+'\n'+"Refresh Token:"+refresh+'\n'+"User ID: "+userid);
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
        //String Code;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Stats.this);
            pDialog.setMessage("Contacting Google ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            // Code = pref.getString("Code", "");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            GetData jParser = new GetData();
            JSONObject json = jParser.getweight(DATA_URL, Token, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, SC, SV);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null){

                try {

                    String weightValue = json.getString("WeightValue");

                    btweight.setText("Authenticated");
                    Access.setText("Weight Value:"+weightValue);
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

}