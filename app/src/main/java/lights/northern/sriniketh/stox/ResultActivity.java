package lights.northern.sriniketh.stox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {

    String company_set_name = "";
    String company_set_symbol = "";
    String news = "";
    String chart = "";
    String current = "";
    String yahoo_chart = "";
    String yahoo_chart_expanded = "";
    String yahoo_chart_url = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Menu menu;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intentFromMainActivity = getIntent();
        //Toast.makeText(getApplicationContext(), intentFromMainActivity.getStringExtra("COMPANY_SET_SYMBOL") + intentFromMainActivity.getStringExtra("COMPANY_SET_NAME"), Toast.LENGTH_SHORT).show();
        company_set_name = intentFromMainActivity.getStringExtra("COMPANY_SET_NAME");
        this.setTitle(company_set_name);
        company_set_symbol = intentFromMainActivity.getStringExtra("COMPANY_SET_SYMBOL");
        news = intentFromMainActivity.getStringExtra("NEWS");
        chart = intentFromMainActivity.getStringExtra("CHART");
        current = intentFromMainActivity.getStringExtra("CURRENT");
        yahoo_chart = intentFromMainActivity.getStringExtra("YAHOO_CHART");
        yahoo_chart_expanded = intentFromMainActivity.getStringExtra("YAHOO_CHART_EXPANDED");
        yahoo_chart_url = intentFromMainActivity.getStringExtra("YAHOO_CHART_URL");

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "You shared this post.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Post not shared.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error sharing the post.", Toast.LENGTH_SHORT).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("CURRENT"));
        tabLayout.addTab(tabLayout.newTab().setText("HISTORICAL"));
        tabLayout.addTab(tabLayout.newTab().setText("NEWS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        assert viewPager != null;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        sharedPreferences = this.getSharedPreferences("SHARED_SYMBOLS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String temp = sharedPreferences.getString("SYMBOLS", null);
        if (temp != null) {
            Gson gson = new Gson();
            ArrayList<String> favs = gson.fromJson(temp, ArrayList.class);
            Log.d("FAVS_SHARED", favs.toString());
            if (favs.contains(company_set_symbol)) {
                //company present
                showOption(R.id.action_favorite_added);
                hideOption(R.id.action_favorite);
            } else {
                //company not present
                showOption(R.id.action_favorite);
                hideOption(R.id.action_favorite_added);
            }
        } else {
            //company not present
            showOption(R.id.action_favorite);
            hideOption(R.id.action_favorite_added);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_facebook:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    //calculate last price
                    String price = "";
                    try {
                        JSONArray jsonArray = new JSONArray(current);
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if (obj.has("LastPrice")) {
                                double rounded = (double) Math.round(Double.parseDouble(obj.getString("LastPrice")) * 100) / 100;
                                price = rounded + "";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current Stock Price of " + company_set_name + ", " + price)
                            .setContentDescription(
                                    "Stock Information of " + company_set_name)
                            .setContentUrl(Uri.parse("http://finance.yahoo.com/q?s=" + company_set_symbol))
                            .setImageUrl(Uri.parse(yahoo_chart_url + "&width=1200&height=765"))
                            .build();

                    shareDialog.show(linkContent);
                }
                return true;
            case R.id.action_favorite:
                Toast.makeText(getApplicationContext(), "Bookmarked " + company_set_name + "!!", Toast.LENGTH_SHORT).show();

                String temp = sharedPreferences.getString("SYMBOLS", null);
                if (temp != null) {
                    Gson gson = new Gson();
                    ArrayList<String> favs = gson.fromJson(temp, ArrayList.class);
                    favs.add(company_set_symbol);
                    Log.d("FAVS_SHARED_ADD", favs.toString());
                    editor.putString("SYMBOLS", gson.toJson(favs));
                    editor.commit();
                } else {
                    Gson gson = new Gson();
                    ArrayList<String> favs = new ArrayList<String>();
                    favs.add(company_set_symbol);
                    Log.d("FAVS_SHARED_ADD", favs.toString());
                    editor.putString("SYMBOLS", gson.toJson(favs));
                    editor.commit();
                }
                //editor.putString(company_set_symbol, company_set_symbol);
                //editor.commit();

                hideOption(R.id.action_favorite);
                showOption(R.id.action_favorite_added);
                return true;
            case R.id.action_favorite_added:

                String temp2 = sharedPreferences.getString("SYMBOLS", null);
                if (temp2 != null) {
                    Gson gson = new Gson();
                    ArrayList<String> favs = gson.fromJson(temp2, ArrayList.class);
                    favs.remove(company_set_symbol);
                    Log.d("FAVS_SHARED_REMOVE", favs.toString());
                    editor.putString("SYMBOLS", gson.toJson(favs));
                    editor.commit();
                }
                //editor.remove(company_set_symbol);
                //editor.commit();

                hideOption(R.id.action_favorite_added);
                showOption(R.id.action_favorite);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
