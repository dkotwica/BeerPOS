package domdomdom.beerpos;

import android.app.Activity;
import android.os.Bundle;
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
        ListView beerListView=(ListView)findViewById(R.id.beerMenuList);
        beerListView.setAdapter(beerAdapter);
    }
}
