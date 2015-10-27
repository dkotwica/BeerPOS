package domdomdom.beerpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
public class Sale extends MainActivity {
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    EditText editText;
    ArrayList<String> itemList;
    ArrayList<Integer> tabAmount;
    ArrayList<String> beerName;
    ArrayList<Integer> beerValue;
    ArrayList<String> beerItem = new ArrayList<String>();
    ArrayList<String> openTabs = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        //String[] items={"Open Tabs"};
        itemList=new ArrayList<String>();
        tabAmount=new ArrayList<Integer>();
        beerName=new ArrayList<String>();
        beerValue=new ArrayList<Integer>();
        //tabAmount.add(0);
        //openTabs.add("test");
        adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview,openTabs);
        adapter1=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview1,beerItem);
        //updateTabList();
        ListView listV=(ListView)findViewById(R.id.list);
        listV.setAdapter(adapter);
        ListView listV2 = (ListView)findViewById(R.id.beerList);
        listV2.setAdapter(adapter1);
        Button btEdit=(Button)findViewById(R.id.editBeer);
        //editText=(EditText)findViewById(R.id.txtInput);
        Button btAdd=(Button)findViewById(R.id.openTab);
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Sale.this);
                final EditText input = new EditText(Sale.this);
                alert.setView(input);
                alert.setTitle("Add a beer");
                alert.setMessage("Beer name:");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newBeer = input.getText().toString();
                        beerName.add(newBeer);
                        beerValue.add(0);
                        beerItem.add("test");
                        updateBeerList();
                    }

                });
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
                        tabAmount.add(0);
                        openTabs.add("test");

                        updateTabList();
                        //adapter.notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
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

    private void updateBeerList(){

        for(int i =0; i< beerName.size(); i++) {
            beerItem.set(i, beerName.get(i) + "     " +"$" + beerValue.get(i));
        }
        adapter1.notifyDataSetChanged();



    }

    private void updateTabList(){

        for(int i =0; i< itemList.size(); i++) {
            openTabs.set(i, itemList.get(i) + "     " +"$" + tabAmount.get(i));
        }
        adapter.notifyDataSetChanged();



    }
    protected void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                Sale.this);

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this item?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                itemList.remove(deletePosition);
                tabAmount.remove(deletePosition);
                openTabs.remove(deletePosition);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();

            }
        });
        alert.setNeutralButton("Add to Tab", new DialogInterface.OnClickListener() {
                    @Override
                public void onClick(DialogInterface dialog, int which){

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

}

