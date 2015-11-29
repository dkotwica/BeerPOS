package domdomdom.beerpos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class beerMenu extends Sale {
    ArrayList<String> beerMenuList;
    ArrayList<String> storedBeer = new ArrayList<>();
    ArrayAdapter<String> beerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_menu);
        beerMenuList = new ArrayList<>();
        beerAdapter = new ArrayAdapter<String>(this, R.layout.beer_list_item, R.id.beerView, storedBeer);
        ListView beerListView = (ListView) findViewById(R.id.beerMenuList);
        beerListView.setAdapter(beerAdapter);

        beerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // setting onItemLongClickListener and passing the position to the function
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                removeBeerFromList(position);

                return true;
            }
        });
    }

    protected void removeBeerFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                beerMenu.this);

        alert.setTitle("Add to tap list");
        alert.setMessage("Do you want to add this beer?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub
                for (int i = 0; i < beerClicks.size(); i++) {
                    int onTap = -1;
                    if (beerOnTap.get(i) == false) {
                        onTap++;
                    }
                    if (onTap == deletePosition) {

                        beerClicks.set(i, 0);
                        beerOnTap.set(i, true);
                        updateBeerList();
                        break;
                    }
                }
                // main code on after clicking yes

                beerAdapter.notifyDataSetChanged();
                beerAdapter.notifyDataSetInvalidated();

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


