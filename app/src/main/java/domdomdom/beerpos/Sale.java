package domdomdom.beerpos;

/*
http://sandboxapi.ihealthlabs.com/OpenApiV2/OAuthv2/userauthorization/
*/
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;

import android.widget.Toast;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sale extends MainActivity {

    final static String tokenizationKey = "sandbox_qkxpfc74_6z4ry4bf4dsyhfmk";

    int REQUEST_CODE = 001;

    int currentPaymentTab = 0;


    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> historyNameAdapter;
    ArrayAdapter<String> historyClicksAdapter;
    EditText editText;
    ArrayList<String> itemList;
    ArrayList<Double> tabAmount;
    ArrayList<String> beerName;
    ArrayList<Boolean> beerOnTap;
    ArrayList<String> historyDisplay;
    ArrayList<ArrayList<Integer>> beerHistoryName;
    ArrayList<ArrayList<Integer>> beerHistoryClick;
    ArrayList<Double> beerValue;
    ArrayList<Integer> beerClicks;

    ArrayList<String> beerItem = new ArrayList<String>();
    ArrayList<String> openTabs = new ArrayList<String>();
    // String[] beerStepValues;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        //String[] items={"Open Tabs"};
        beerHistoryName = new ArrayList<>();
        beerHistoryClick = new ArrayList<>();

        historyDisplay = new ArrayList<>();
        itemList=new ArrayList<String>();
        tabAmount=new ArrayList<Double>();
        beerName=new ArrayList<String>();
        beerValue=new ArrayList<>();
        beerClicks = new ArrayList<>();
        beerOnTap = new ArrayList<>();
        final String[] beerStepValues = new String[32];
        for(int i = 0; i < beerStepValues.length; i++){
            String number = Double.toString((double)i/2 + 1);
            beerStepValues[i] = number;
        }
        //tabAmount.add(0);
        //openTabs.add("test");
        adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview,openTabs);
        adapter1=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview1,beerItem);
        historyNameAdapter = new ArrayAdapter<String>(this,R.layout.list_item, R.id.txtview, historyDisplay);

        //updateTabList();
        final ListView historyList = new ListView(Sale.this);
        historyList.setAdapter(historyNameAdapter);
        ListView listV=(ListView)findViewById(R.id.list);
        listV.setAdapter(adapter);
        ListView listV2 = (ListView)findViewById(R.id.beerList);
        listV2.setAdapter(adapter1);
        Button btEdit=(Button)findViewById(R.id.editBeer);
        //editText=(EditText)findViewById(R.id.txtInput);
        Button btAdd=(Button)findViewById(R.id.openTab);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder alertHistory = new AlertDialog.Builder(
                        Sale.this);


                final LinearLayout historyLayout = new LinearLayout(Sale.this);
                historyLayout.setOrientation(LinearLayout.VERTICAL);
                historyLayout.addView(historyList);



                alertHistory.setView(historyLayout);

                alertHistory.setTitle("Tab History");
                historyDisplay.clear();
                for (int i = 0; i < beerHistoryClick.get(position).size(); i++) {
                    historyDisplay.add(beerName.get(beerHistoryName.get(position).get(i)) + " x " + beerHistoryClick.get(position).get(i));
                }
                alertHistory.setMessage(itemList.get(position));
                alertHistory.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        historyLayout.removeView(historyList);
                    }

                });

                alertHistory.show();

            }


        });



        listV2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                int onTap = -1;
                for (int i =0; i<beerClicks.size(); i++) {
                    if(beerOnTap.get(i) == true ) {
                        onTap++;
                    }
                    if (onTap == position){
                        beerClicks.set(i, (beerClicks.get(i) + 1));
                        updateBeerList();
                        break;
                    }
                }
            }
        });



        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Sale.this);
                final EditText input = new EditText(Sale.this);
                final NumberPicker np = new NumberPicker(Sale.this);
                np.setWrapSelectorWheel(false);
                np.setMinValue(1);
                np.setMaxValue(28);
                np.setDisplayedValues(beerStepValues);
                np.setValue(5);

                LinearLayout ll = new LinearLayout(Sale.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(input);
                ll.addView(np);

                alert.setView(ll);

                alert.setTitle("Add a beer");
                alert.setMessage("Beer name:");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newBeer = input.getText().toString();
                        double bv = np.getValue();
                        beerName.add(newBeer);
                        beerValue.add(bv / 2 + 0.5);
                        beerClicks.add(0);
                        beerItem.add("test");
                        beerOnTap.add(true);
                        updateBeerList();
                    }

                });
                alert.show();
            }
        });


        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String newItem = editText.getText().toString();
                // final EditText edittext = new EditText();
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        Sale.this);

                final EditText input = new EditText(Sale.this);
                alert.setView(input);
                alert.setTitle("Name of Tab");
                alert.setMessage("Name:");
                //alert.setView(editText);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        //Editable YouEditTextValue = editText.getText();
                        //OR
                        String newItem = input.getText().toString();
                        itemList.add(newItem);
                        tabAmount.add((double) 0);
                        beerHistoryClick.add(tabAmount.size()-1, new ArrayList<Integer>());
                        beerHistoryName.add(tabAmount.size()-1, new ArrayList<Integer>());
                        openTabs.add("test");

                        updateTabList();
                        //adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
                // add new item to arraylist
                //itemList.add(newItem);
                // notify listview of data changed
                //adapter.notifyDataSetChanged();
            }

        });
        listV2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // setting onItemLongClickListener and passing the position to the function
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                removeItemFromList2(position);

                return true;
            }
        });


        listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // setting onItemLongClickListener and passing the position to the function
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                removeItemFromList(position);

                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            saveSaleData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getBeerData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getBeerData()throws IOException {
        Log.d("getBeerData", "Sucessfully called the getBeerData()");
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/BeerPOS");

        boolean var = false;
        beerName.clear();
        beerValue.clear();
        beerClicks.clear();
        beerOnTap.clear();

        itemList.clear();
        tabAmount.clear();

        beerItem.clear();
        openTabs.clear();
        if (folder.exists()) {
        Log.d("getBeerData","Folder exists");

            final String filenameBeers = folder.toString() + "/" + "BeerPOS_BEER.csv";

            FileInputStream is = new FileInputStream(filenameBeers);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                int index = 0;
                String line;
                while ((line = reader.readLine()) != null) {

                    String[] RowData = line.split(",");
                   // Log.d("Beer.csv","RowData: "+RowData[0]+","+RowData[1]+","+RowData[2]+","+RowData[3]);

                    beerName.add(index,RowData[0]);
                    //beerName.add("test");
                    beerValue.add(index,Double.parseDouble(RowData[1]));
                    //beerValue.add((double) 1);
                    beerClicks.add(index,Integer.parseInt(RowData[2]));
                    //beerClicks.add(0);
                    beerOnTap.add(index,Boolean.valueOf(RowData[3]));
                    beerItem.add("beer");

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


            final String filenameTabs = folder.toString() + "/" + "BeerPOS_TABS.csv";
            FileInputStream is2 = new FileInputStream(filenameTabs);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2));
            try {
                String line;
                while ((line = reader2.readLine()) != null) {
                    String[] RowData = line.split(",");
                    itemList.add(RowData[0]);
                    //itemList.add("Dan");
                    tabAmount.add(Double.parseDouble(RowData[1]));
                    beerHistoryClick.add(tabAmount.size()-1, new ArrayList<Integer>());
                    beerHistoryName.add(tabAmount.size()-1, new ArrayList<Integer>());
                    //tabAmount.add((double) 0);
                    openTabs.add("tab");
                }
            } catch (IOException ex) {
                // handle exception
            } finally {
                try {
                    is2.close();
                } catch (IOException e) {
                    // handle exception
                }
            }

        }
        updateBeerList();
        updateTabList();
    }

    private void updateBeerList(){
                beerItem.clear();
                for (int i = 0; i < beerName.size(); i++) {
                    if(beerOnTap.get(i) == true) {
                        beerItem.add(beerName.get(i) + "     " + "$" + beerValue.get(i) + "  Amt: " + beerClicks.get(i));
                        //Log.d("updateBeerList_Indes", "Index: " + i);
                    }

                 }
        adapter1.notifyDataSetChanged();
    }

    //public void updateHistoryList(){
      //  for (int i = 0; i < beerHistoryName.size(); i++) {
        //}
    //}

    private void updateTabList(){

        for(int i =0; i< itemList.size(); i++) {
            openTabs.set(i, itemList.get(i) + "     " + "$" + tabAmount.get(i));
        }
        adapter.notifyDataSetChanged();
    }
    protected void removeItemFromList2(int position) {
        final int deletePosition2 = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                Sale.this);

        alert.setTitle("Remove from Tap");
        alert.setMessage("Do you want remove this item from Tap?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub
                int onTap = -1;
                for (int i = 0; i < beerClicks.size(); i++) {

                    if (beerOnTap.get(i) == true) {
                        onTap++;
                    }
                    if (onTap == deletePosition2) {

                        beerClicks.set(i, 0);
                        beerOnTap.set(i, false);
                        updateBeerList();
                        break;
                    }
                }
                // main code on after clicking yes

                adapter1.notifyDataSetChanged();
                adapter1.notifyDataSetInvalidated();

            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });


        alert.show();

    }

    protected void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                Sale.this);

        alert.setTitle("Tab Options");
        //alert.setMessage("Edit the Tab");
        alert.setPositiveButton("Close Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentPaymentTab = deletePosition;
                PaymentRequest paymentRequest = new PaymentRequest()
                        .clientToken(tokenizationKey);
                paymentRequest.primaryDescription(itemList.get(deletePosition));
                paymentRequest.amount("$" + String.valueOf(tabAmount.get(deletePosition)));
                startActivityForResult(paymentRequest.getIntent(Sale.this), REQUEST_CODE);

                currentPaymentTab = deletePosition;
//                itemList.remove(deletePosition);
//                tabAmount.remove(deletePosition);
//                openTabs.remove(deletePosition);
//                adapter.notifyDataSetChanged();
//                adapter.notifyDataSetInvalidated();

            }
        });
        alert.setNeutralButton("Add to Tab", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double amountToAdd = 0;

                for (int i = 0; i < beerClicks.size(); i++) {
                    amountToAdd = amountToAdd + (beerValue.get(i) * beerClicks.get(i));
                    if (beerClicks.get(i) != 0) {
                        beerHistoryName.get(deletePosition).add(i);
                        beerHistoryClick.get(deletePosition).add(beerClicks.get(i));
                    }
                    //historyClicksDisplay = String.valueOf(beerHistoryClick);
                    beerClicks.set(i, 0);

                }


                Log.d("Display", String.valueOf(historyDisplay));
                Log.d("History Name", String.valueOf(beerHistoryName));
                Log.d("History Click", String.valueOf(beerHistoryClick));
                tabAmount.set(deletePosition, (tabAmount.get(deletePosition) + amountToAdd));
                updateTabList();
                updateBeerList();
            }
        });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });

        alert.show();

    }




    private void saveSaleData() throws IOException {



            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/BeerPOS");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

           final String filenameBeers = folder.toString() + "/" + "BeerPOS_BEER.csv";



        FileWriter fw = new FileWriter(filenameBeers);
        fw.write("");
        if(beerClicks.size()>0) {
            for (int i = 0; i < beerClicks.size(); i++) {
                fw.append(beerName.get(i));
                fw.append(',');
                fw.append(beerValue.get(i).toString());
                fw.append(',');
                fw.append(beerClicks.get(i).toString());
                fw.append(',');
                fw.append((beerOnTap.get(i)).toString());
                fw.append('\n');
            }
        }
        fw.flush();
        fw.close();

        final String filenameTabs = folder.toString() + "/" + "BeerPOS_TABS.csv";



        fw = new FileWriter(filenameTabs);
        fw.write("");

        if(itemList.size()>0) {
            for (int i = 0; i < itemList.size(); i++) {

                fw.append(itemList.get(i));
                fw.append(',');
                fw.append(tabAmount.get(i).toString());
                fw.append('\n');
            }
        }
        fw.flush();
        fw.close();

    }
    public void sendBeerMenu(View view) {
        Intent intent = new Intent(this, beerMenu.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        Log.d("data", String.valueOf(data));

        if (requestCode == REQUEST_CODE) {
            Log.d("requestCode==REQUEST_CODE", String.valueOf(requestCode));
            Log.d("index to be removed", String.valueOf(currentPaymentTab));

            if(resultCode == -1) {
                itemList.remove(currentPaymentTab);
                tabAmount.remove(currentPaymentTab);
                openTabs.remove(currentPaymentTab);
                beerHistoryClick.remove(currentPaymentTab);
                beerHistoryName.remove(currentPaymentTab);
            }
            try {
                saveSaleData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();

            switch (resultCode) {
                case Activity.RESULT_OK:
                    PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                            BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                    );
                    String nonce = paymentMethodNonce.getNonce();


                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }
        }
    }

}




