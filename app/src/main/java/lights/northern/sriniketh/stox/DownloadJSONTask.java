package lights.northern.sriniketh.stox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadJSONTask extends AsyncTask<String, Void, ArrayList<String>> {

    Context context;
    String task_tag;

    public DownloadJSONTask(Context context, String task_tag) {
        this.context = context;
        this.task_tag = task_tag;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> result = new ArrayList<>();
        for (String param : params) {
            String response = "";
            try {
                if (param.toLowerCase().contains("chart.finance.yahoo.com")) {
                    Bitmap image = null;
                    try {
                        InputStream in = new java.net.URL(param).openStream();
                        image = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String temp = Base64.encodeToString(b, Base64.DEFAULT);
                    result.add(temp);
                } else {
                    URL url = new URL(param);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                    result.add(response);
                }
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
        if (task_tag.equals("AUTOCOMPLETE")) {
            mainActivity.downloadProgressBar.setVisibility(View.VISIBLE);
        } else if (task_tag.equals("LOAD_COMPANY")) {
            mainActivity.refreshProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
        MainActivity mainActivity = (MainActivity) context;
        try {
            mainActivity.news_intent = result.get(0);
            mainActivity.chart_intent = result.get(1);
            mainActivity.current_intent = result.get(2);
            mainActivity.yahoo_chart_intent = result.get(3);
            mainActivity.yahoo_chart_expanded_intent = result.get(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (task_tag.equals("AUTOCOMPLETE")) {
            mainActivity.downloadProgressBar.setVisibility(View.GONE);
        } else if (task_tag.equals("LOAD_COMPANY")) {
            mainActivity.refreshProgress.setVisibility(View.GONE);
            mainActivity.startResultActivity();
        }
        //Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
    }
}