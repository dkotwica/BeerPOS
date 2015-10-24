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
    EditText editText;
    ArrayList<String> itemList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        String[] items={"Apple","Banana","Clementine"};
        itemList=new ArrayList<String>(Arrays.asList(items));
        adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.txtview,itemList);
        ListView listV=(ListView)findViewById(R.id.list);
        listV.setAdapter(adapter);
        //editText=(EditText)findViewById(R.id.txtInput);
        Button btAdd=(Button)findViewById(R.id.openTab);
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
                alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        //Editable YouEditTextValue = editText.getText();
                        //OR
                        String newItem = input.getText().toString();
                        itemList.add(newItem);
                        adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();

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

