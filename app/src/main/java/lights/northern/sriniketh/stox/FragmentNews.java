package lights.northern.sriniketh.stox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by srinikethanr on 4/15/16.
 */
public class FragmentNews extends Fragment {

    static FragmentNewsAdapter fragmentNewsAdapter;
    static ListView fragmentNewsView;
    static String[] news_title = new String[]{""};
    static String[] news_content = new String[]{""};
    static String[] news_publisher = new String[]{""};
    static String[] news_date = new String[]{""};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_news, container, false);

        fragmentNewsView = (ListView) rootView.findViewById(R.id.news_list);
        fragmentNewsAdapter = new FragmentNewsAdapter(getActivity(), news_title, news_content, news_publisher, news_date);
        fragmentNewsView.setAdapter(fragmentNewsAdapter);

        ResultActivity resultActivity = (ResultActivity) getActivity();
        String result = resultActivity.news;
        try {
            JSONArray jsonArray = new JSONArray(result);
            //Log.d("RESPONSE", jsonArray.toString());
            int len = jsonArray.length();
            String[] title = new String[len];
            String[] content = new String[len];
            String[] publisher = new String[len];
            String[] date = new String[len];
            for (int i = 0; i < len; ++i) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String temp = "<a href='" + obj.getString("url") + "'>";
                title[i] = temp + obj.getString("title") + "</a>";
                //Log.d("NEWS_TITLE", title[i]);
                content[i] = obj.getString("content");
                publisher[i] = "Publisher : " + obj.getString("source");

                final String OLD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final String NEW_FORMAT = "dd MMMM yyyy, HH:mm:ss";
                String oldDateString = obj.getString("date");
                String newDateString;
                SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                Date d = sdf.parse(oldDateString);
                sdf.applyPattern(NEW_FORMAT);
                newDateString = sdf.format(d);
                date[i] = "Date : " + newDateString;
            }

            news_title = title;
            news_content = content;
            news_publisher = publisher;
            news_date = date;
            fragmentNewsAdapter.notifyDataSetChanged();
            fragmentNewsAdapter = new FragmentNewsAdapter(getActivity(), news_title, news_content, news_publisher, news_date);
            fragmentNewsView.setAdapter(fragmentNewsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }
}
