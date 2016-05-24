package lights.northern.sriniketh.stox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MainActivity mainActivity;

    CustomAutoCompleteView customAutoCompleteView;
    Button clearButton;
    Button quoteButton;
    AutoCompleteAdapter autoCompleteAdapter;
    ProgressBar progressBar;
    ProgressBar downloadProgressBar;

    String[] autocomplete_values = new String[]{""};
    String[] autocomplete_labels = new String[]{""};

    String company_set_symbol = "";
    String company_set_name = "";
    boolean company_set_flag = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FavoritesAdapter favoritesAdapter;
    DynamicListView favoritesListView;

    String[] main_company = new String[]{""};
    String[] main_symbol = new String[]{""};
    String[] main_price = new String[]{""};
    String[] main_change = new String[]{""};
    String[] main_marketcap = new String[]{""};

    String news_intent = "";
    String chart_intent = "";
    String current_intent = "";
    String yahoo_chart_intent = "";
    String yahoo_chart_expanded_intent = "";
    String yahoo_chart_url_intent = "";

    Switch autoRefreshSwitch;
    ImageView refreshButton;
    ProgressBar refreshProgress;
    Handler handler;
    Runnable refreshTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.bullish_48);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //initialize ui components
        clearButton = (Button) findViewById(R.id.clear);
        quoteButton = (Button) findViewById(R.id.get_quote);
        customAutoCompleteView = (CustomAutoCompleteView) findViewById(R.id.autocompleteView);
        company_set_flag = false;
        company_set_symbol = "";
        company_set_name = "";
        mainActivity = this;

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Clear", Toast.LENGTH_SHORT).show();
                customAutoCompleteView.setText("");
                progressBar.setVisibility(View.GONE);
                refreshProgress.setVisibility(View.GONE);
                downloadProgressBar.setVisibility(View.GONE);
            }
        });
        quoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (company_set_flag) {
                    //Toast.makeText(getApplicationContext(), company_set, Toast.LENGTH_SHORT).show();
                    startResultActivity();
                } else {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    if (customAutoCompleteView.getText().toString().equals("")) {
                        builder.setMessage("Please enter a Stock Name/Symbol");
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else if (customAutoCompleteView.getText().toString().contains(" ")) {
                        builder.setMessage("There should not be any space between characters");
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Log.d("VALIDATE", "entering else in quote button listener");
                        String autocomplete_url = "http://stox-007.appspot.com/?req=autocomplete&param=" + customAutoCompleteView.getText().toString();
                        AutoCompleteAsyncTask task = new AutoCompleteAsyncTask(mainActivity, "VALIDATE");
                        task.execute(autocomplete_url);
                    }

                }
            }
        });

        customAutoCompleteView.addTextChangedListener(new AutoCompleteTextWatcher(this));
        customAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), customAutoCompleteView.getText(), Toast.LENGTH_SHORT).show();

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(parent.getApplicationWindowToken(), 0);

                company_set_flag = true;
                //company_set = customAutoCompleteView.getText().toString();
                company_set_symbol = ((TextView) view.findViewById(R.id.autocomplete_symbol)).getText().toString();
                String temp = ((TextView) view.findViewById(R.id.autocomplete_company_name)).getText().toString();
                company_set_name = temp.substring(0, temp.indexOf('(') - 1);

                downloadTask("AUTOCOMPLETE");
            }
        });
        autoCompleteAdapter = new AutoCompleteAdapter(this, autocomplete_values, autocomplete_labels);
        customAutoCompleteView.setAdapter(autoCompleteAdapter);
        progressBar = (ProgressBar) findViewById(R.id.autocomplete_progress);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progress);

        favoritesListView = (DynamicListView) findViewById(R.id.favourites_list);
        favoritesAdapter = new FavoritesAdapter(this, main_company, main_symbol, main_price, main_change, main_marketcap);
        //favoritesListView.setAdapter(favoritesAdapter);

        handler = new Handler(Looper.getMainLooper());
        refreshTask = new Runnable() {
            @Override
            public void run() {
                loadSharedPrefs();
                handler.postDelayed(refreshTask, 10000);
            }
        };
        autoRefreshSwitch = (Switch) findViewById(R.id.auto_refresh_switch);
        autoRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(refreshTask, 10000);
                } else {
                    //Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(refreshTask);
                }
            }
        });
        refreshButton = (ImageView) findViewById(R.id.refresh_button);
        refreshButton.setClickable(true);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSharedPrefs();
            }
        });
        refreshProgress = (ProgressBar) findViewById(R.id.refresh_progress);
    }

    void downloadTask(String task_tag) {
        DownloadJSONTask task = new DownloadJSONTask(mainActivity, task_tag);
        String news_url = "http://stox-007.appspot.com/?req=news&param=" + company_set_symbol;
        String chart_url = "http://stox-007.appspot.com/?req=chart&param=" + company_set_symbol;
        String current_url = "http://stox-007.appspot.com/?req=quote&param=" + company_set_symbol;
        String yahoo_chart_url = "http://chart.finance.yahoo.com/t?s=" + company_set_symbol + "&lang=en-US";
        String yahoo_chart_url_expanded = "http://chart.finance.yahoo.com/t?s=" + company_set_symbol + "&lang=en-US&width=1200&height=765";
        yahoo_chart_url_intent = yahoo_chart_url;
        task.execute(new String[]{news_url, chart_url, current_url, yahoo_chart_url, yahoo_chart_url_expanded});

        //TODO current FAILURE then dialog on get quote saying no stock info available
        //TODO check if entered text is present in a the list of possible results
    }

    void startResultActivity() {
        Intent resultActivityIntent = new Intent(MainActivity.this, ResultActivity.class);

        //Check if details got are valid
        try {
            JSONArray jsonArray = new JSONArray(current_intent);
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.getJSONObject(i);
                //Log.d("JSON", obj.toString());
                if (obj.has("Status")) {
                    if (!(obj.getString("Status").equals("SUCCESS"))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                        builder.setMessage("Stock information unavailable");
                        AlertDialog alert = builder.create();
                        alert.show();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resultActivityIntent.putExtra("COMPANY_SET_SYMBOL", company_set_symbol);
        resultActivityIntent.putExtra("COMPANY_SET_NAME", company_set_name);

        resultActivityIntent.putExtra("NEWS", news_intent);
        resultActivityIntent.putExtra("CHART", chart_intent);
        resultActivityIntent.putExtra("CURRENT", current_intent);
        resultActivityIntent.putExtra("YAHOO_CHART", yahoo_chart_intent);
        resultActivityIntent.putExtra("YAHOO_CHART_EXPANDED", yahoo_chart_expanded_intent);
        resultActivityIntent.putExtra("YAHOO_CHART_URL", yahoo_chart_url_intent);

        startActivity(resultActivityIntent);
    }

    void loadSharedPrefs() {
        sharedPreferences = this.getSharedPreferences("SHARED_SYMBOLS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String temp = sharedPreferences.getString("SYMBOLS", null);
        if (temp != null) {
            Gson gson = new Gson();
            ArrayList<String> favs = gson.fromJson(temp, ArrayList.class);

            int len = favs.size();
            String[] urls = new String[len];
            int count = 0;
            for (String fav : favs) {
                Log.d("FAVS_SHARED_LOAD", fav);

                String url = "http://stox-007.appspot.com/?req=quote&param=" + fav;
                urls[count] = url;
                ++count;
            }
            LoadFavsTask task = new LoadFavsTask(mainActivity, len);
            task.execute(urls);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPrefs();
    }
}