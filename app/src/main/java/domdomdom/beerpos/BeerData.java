package domdomdom.beerpos;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by domk1_000 on 3/25/2015.
 */
public class BeerData extends Stats {
    ArrayList<String> beerName = new ArrayList<>();
    ArrayList<Boolean> beerOnTap = new ArrayList<>();

    ArrayList<Double> beerValue = new ArrayList<>();
    ArrayList<Integer> beerClicks = new ArrayList<>();
    ArrayList<Long> beerStart = new ArrayList<>();
    ArrayList<Long> beerEnd = new ArrayList<>();
    ArrayList<String> storedBeer = new ArrayList<>();



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
                    Log.d("Beer.csv", "RowData: " + RowData[0] + "," + RowData[1] + "," + RowData[2] + "," + RowData[3]);

                    beerName.add(index,RowData[0]);
                    //beerName.add("test");
                    beerValue.add(index,Double.parseDouble(RowData[1]));
                    //beerValue.add((double) 1);
                    beerClicks.add(index,Integer.parseInt(RowData[2]));
                    //beerClicks.add(0);
                    beerOnTap.add(index,Boolean.valueOf(RowData[3]));
                    beerStart.add(index, Long.valueOf(RowData[4]));
                    beerEnd.add(index, Long.valueOf(RowData[5]));
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
          //  if(beerOnTap.get(i) == false) {
                storedBeer.add(beerName.get(i) + "     " + "$" + beerValue.get(i));
                Log.d("updateBeerList_Indes", "Index: " + i);
           // }

        }
        //adapter1.notifyDataSetChanged();
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
                fw.append(',');
                fw.append((beerStart.get(i)).toString());
                fw.append(',');
                fw.append((beerEnd.get(i)).toString());
                fw.append('\n');
            }
        }
        fw.flush();
        fw.close();
    }

    public Map<String, List<String>> getInfo() throws IOException, NoSuchFieldException, IllegalAccessException {
        getBeerData();
        double kegWeight = 59.5;
        double kegPintAmount = 124;

        HashMap<String, List<String>> BarDetails = new HashMap<String, List<String>>();

        Log.d("Da Fuq", String.valueOf(getWeightValue()));
        if (beerName.size() > 0) {

        for(int i = 0; i < beerName.size(); i++) {
            List<String> beer_name = new ArrayList<String>();
            beer_name.add(String.valueOf(weightValue));

            if (beerOnTap.get(i) == true ) {

            }
            else {
                //Doesn't take into account store hours
                long kegDuration = ((beerStart.get(i) - beerEnd.get(i))*3600);
                beer_name.add("Time taken for beer to empty: " + kegDuration + " hours");
                beer_name.add("Average pints per hour: " + (weightValue/kegWeight) );
            }
            BarDetails.put(beerName.get(i), beer_name);
        }

        }

        Map<String, List<String>> treeMap = new TreeMap<String, List<String>>(BarDetails);


        return treeMap;

    }

}
