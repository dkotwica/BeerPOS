package domdomdom.beerpos;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class beerMenu extends Sale {
    ArrayList<String> beerName;
    ArrayList<Boolean> beerOnTap;

    ArrayList<Double> beerValue;
    ArrayList<Integer> beerClicks;

    ArrayList<String> beerMenuList;
    ArrayList<String> storedBeer = new ArrayList<>();
    ArrayAdapter<String> beerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_menu);

        beerName = new ArrayList<>();
        beerOnTap = new ArrayList<>();
        beerValue = new ArrayList<>();
        beerClicks = new ArrayList<>();

        beerMenuList = new ArrayList<>();
        beerAdapter = new ArrayAdapter<String>(this, R.layout.beer_list_item, R.id.beerView, storedBeer);
        ListView beerListView=(ListView)findViewById(R.id.beerMenuList);
        beerListView.setAdapter(beerAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            saveBeerData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

        beerName.clear();
        beerValue.clear();
        beerClicks.clear();
        beerOnTap.clear();


        storedBeer.clear();
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
                    Log.d("Beer.csv","RowData: "+RowData[0]+","+RowData[1]+","+RowData[2]+","+RowData[3]);

                    beerName.add(index,RowData[0]);
                    //beerName.add("test");
                    beerValue.add(index,Double.parseDouble(RowData[1]));
                    //beerValue.add((double) 1);
                    beerClicks.add(index,Integer.parseInt(RowData[2]));
                    //beerClicks.add(0);
                    beerOnTap.add(index,Boolean.valueOf(RowData[3]));
                    storedBeer.add("beer");

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
        updateBeerList();
    }
    private void updateBeerList(){
        storedBeer.clear();
        for (int i = 0; i < beerName.size(); i++) {
            if(beerOnTap.get(i) == false) {
                storedBeer.add(beerName.get(i) + "     " + "$" + beerValue.get(i));
                Log.d("updateBeerList_Indes", "Index: " + i);
            }

        }
        adapter1.notifyDataSetChanged();
    }

    private void saveBeerData() throws IOException {



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
    }
}
