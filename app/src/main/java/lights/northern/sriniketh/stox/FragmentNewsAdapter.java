package lights.northern.sriniketh.stox;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by srinikethanr on 4/15/16.
 */
public class FragmentNewsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] news_title;
    private final String[] news_content;
    private final String[] news_publisher;
    private final String[] news_date;

    public FragmentNewsAdapter(Context context, String[] news_title, String[] news_content, String[] news_publisher, String[] news_date) {
        super(context, -1, news_title);
        this.context = context;
        this.news_title = news_title;
        this.news_content = news_content;
        this.news_publisher = news_publisher;
        this.news_date = news_date;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.news_list_item, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.news_title);
        TextView textView2 = (TextView) rowView.findViewById(R.id.news_content);
        TextView textView3 = (TextView) rowView.findViewById(R.id.news_publisher);
        TextView textView4 = (TextView) rowView.findViewById(R.id.news_date);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
        textView1.setText(Html.fromHtml(news_title[pos]));
        textView2.setText(news_content[pos]);
        textView3.setText(news_publisher[pos]);
        textView4.setText(news_date[pos]);
        return rowView;
    }
}
