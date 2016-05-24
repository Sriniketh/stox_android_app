package lights.northern.sriniketh.stox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by srinikethanr on 4/14/16.
 */
public class AutoCompleteAsyncTask extends AsyncTask<String, Void, String> {

    Context context;
    String task;

    public AutoCompleteAsyncTask(Context context, String task) {
        this.context = context;
        this.task = task;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        for (String param : params) {
            try {
                URL url = new URL(param);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String result) {
        MainActivity mainActivity = (MainActivity) context;
        if (task.equals("AUTO")) {
            try {

                JSONArray jsonArray = new JSONArray(result);
                //Log.d("RESPONSE", jsonArray.toString());
                int len = jsonArray.length();
                if (len > 0) {
                    String[] symbol = new String[len];
                    String[] name = new String[len];
                    String temp = "";
                    for (int i = 0; i < len; ++i) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        //Log.d("RESPONSE", obj.getString("value").toString());
                        symbol[i] = obj.getString("value");
                        temp = obj.getString("label");
                        name[i] = temp.substring(temp.indexOf('-') + 2);
                    }

                    mainActivity.autocomplete_values = symbol;
                    mainActivity.autocomplete_labels = name;
                    mainActivity.autoCompleteAdapter.notifyDataSetChanged();
                    mainActivity.autoCompleteAdapter = new AutoCompleteAdapter(mainActivity, mainActivity.autocomplete_values, mainActivity.autocomplete_labels);
                    mainActivity.customAutoCompleteView.setAdapter(mainActivity.autoCompleteAdapter);
                }
                mainActivity.progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (task.equals("VALIDATE")) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.d("VALIDATE_RESPONSE", jsonArray.toString());
                int len = jsonArray.length();
                if (len > 0) {
                    for (int i = 0; i < len; ++i) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        //Log.d("RESPONSE", obj.getString("value").toString());
                        String symbol = obj.getString("value");
                        String temp = obj.getString("label");
                        String name = temp.substring(temp.indexOf('-') + 2);
                        if (symbol.equals(mainActivity.customAutoCompleteView.getText().toString().toUpperCase())) {
                            mainActivity.company_set_symbol = symbol;
                            mainActivity.company_set_name = name;
                            mainActivity.company_set_flag = true;
                            mainActivity.progressBar.setVisibility(View.GONE);
                            mainActivity.downloadTask("LOAD_COMPANY");
                            return;
                        }
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                builder.setMessage("Invalid Symbol");
                AlertDialog alert = builder.create();
                mainActivity.progressBar.setVisibility(View.GONE);
                alert.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}