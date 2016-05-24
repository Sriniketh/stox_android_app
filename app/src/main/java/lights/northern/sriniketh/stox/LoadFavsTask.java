package lights.northern.sriniketh.stox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by srinikethanr on 5/1/16.
 */
public class LoadFavsTask extends AsyncTask<String, Void, ArrayList<String>> {

    Context context;
    String[] company;
    String[] symbol;
    String[] price;
    String[] change;
    String[] marketcap;

    public LoadFavsTask(Context context, int length) {
        this.context = context;
        this.company = new String[length];
        this.symbol = new String[length];
        this.price = new String[length];
        this.change = new String[length];
        this.marketcap = new String[length];
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> result = new ArrayList<>();
        for (String param : params) {
            String response = "";
            try {
                URL url = new URL(param);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
                result.add(response);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.refreshProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        try {
            int count = 0;
            for (String result : strings) {
                //Log.d("MAP_TAG_RESULT", result);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    //Log.d("MAP_TAG_OBJ", obj.toString());
                    if (obj.has("Name")) {
                        company[count] = obj.getString("Name");
                    } else if (obj.has("Symbol")) {
                        symbol[count] = obj.getString("Symbol");
                    } else if (obj.has("LastPrice")) {
                        double rounded = (double) Math.round(Double.parseDouble(obj.getString("LastPrice")) * 100) / 100;
                        price[count] = "$ " + rounded;
                    } else if (obj.has("ChangePercent")) {
                        double rounded = (double) Math.round(Double.parseDouble(obj.getString("ChangePercent")) * 100) / 100;
                        if (rounded > 0) {
                            change[count] = "+" + rounded + "%";
                        } else {
                            change[count] = rounded + "%";
                        }
                    } else if (obj.has("MarketCap")) {
                        double rounded = Double.parseDouble(obj.getString("MarketCap"));
                        if (rounded >= 1000000000) {
                            rounded = rounded / 1000000000;
                            double value = (double) Math.round(rounded * 100) / 100;
                            marketcap[count] = "Market Cap: " + value + " Billion";
                        } else if (rounded >= 1000000) {
                            rounded = rounded / 1000000;
                            double value = (double) Math.round(rounded * 100) / 100;
                            marketcap[count] = "Market Cap: " + value + " Million";
                        } else {
                            marketcap[count] = "Market Cap: " + rounded + "";
                        }
                    }
                }
                ++count;
            }

            final MainActivity mainActivity = (MainActivity) context;
            mainActivity.main_company = company;
            mainActivity.main_symbol = symbol;
            mainActivity.main_price = price;
            mainActivity.main_change = change;
            mainActivity.main_marketcap = marketcap;
            //Log.d("MAP_TAG_RESULT", company + "\n" + symbol);
            mainActivity.favoritesAdapter.notifyDataSetChanged();
            mainActivity.favoritesAdapter = new FavoritesAdapter(context, mainActivity.main_company, mainActivity.main_symbol, mainActivity.main_price, mainActivity.main_change, mainActivity.main_marketcap);
            mainActivity.favoritesListView.setAdapter(mainActivity.favoritesAdapter);
            mainActivity.favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textview = (TextView) view.findViewById(R.id.fav_company_symbol);
                    TextView textview2 = (TextView) view.findViewById(R.id.fav_company_name);
                    //Toast.makeText(context, textview.getText(), Toast.LENGTH_SHORT).show();

                    //company_set_flag = true;
                    //company_set = customAutoCompleteView.getText().toString();
                    mainActivity.company_set_symbol = textview.getText().toString();
                    mainActivity.company_set_name = textview2.getText().toString();
                    mainActivity.downloadTask("LOAD_COMPANY");
                }
            });
            mainActivity.favoritesListView.enableSwipeToDismiss(
                    new OnDismissCallback() {
                        @Override
                        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                            for (final int position : reverseSortedPositions) {
                                final String company_name = mainActivity.favoritesAdapter.getItem(position);
                                final int pos = position;
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                //favoritesAdapter.remove(company_name);
                                                //favoritesAdapter.notifyDataSetChanged();
                                                String company_to_be_removed = mainActivity.main_symbol[pos];
                                                mainActivity.main_company = removeItem(mainActivity.main_company, pos);
                                                mainActivity.main_symbol = removeItem(mainActivity.main_symbol, pos);
                                                mainActivity.main_price = removeItem(mainActivity.main_price, pos);
                                                mainActivity.main_change = removeItem(mainActivity.main_change, pos);
                                                mainActivity.main_marketcap = removeItem(mainActivity.main_marketcap, pos);
                                                mainActivity.favoritesAdapter = new FavoritesAdapter(context,
                                                        mainActivity.main_company,
                                                        mainActivity.main_symbol,
                                                        mainActivity.main_price,
                                                        mainActivity.main_change,
                                                        mainActivity.main_marketcap);
                                                mainActivity.favoritesListView.setAdapter(mainActivity.favoritesAdapter);
                                                String temp2 = mainActivity.sharedPreferences.getString("SYMBOLS", null);
                                                if (temp2 != null) {
                                                    Gson gson = new Gson();
                                                    ArrayList<String> favs = gson.fromJson(temp2, ArrayList.class);
                                                    favs.remove(company_to_be_removed);
                                                    Log.d("FAVS_SHARED_REMOVE", favs.toString());
                                                    mainActivity.editor.putString("SYMBOLS", gson.toJson(favs));
                                                    mainActivity.editor.commit();
                                                }
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                            }
                                        });
                                builder.setMessage("Want to delete " + company_name + " from favorites?");
                                builder.show();

                            }
                        }

                        private String[] removeItem(String[] temp, int pos) {
                            String[] n = temp;
                            final List<String> list = new ArrayList<String>();
                            Collections.addAll(list, n);
                            list.remove(pos);
                            n = list.toArray(new String[list.size()]);
                            return n;
                        }
                    }
            );

            mainActivity.refreshProgress.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
