package lights.northern.sriniketh.stox;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by srinikethanr on 4/21/16.
 */
public class FavoritesAdapter extends ArrayAdapter<String> {
    private final Context context;
    private String[] company;
    private String[] symbol;
    private String[] price;
    private String[] change;
    private String[] marketcap;

    public FavoritesAdapter(Context context, String[] company, String[] symbol, String[] price, String[] change, String[] marketcap) {
        super(context, -1, company);
        this.context = context;
        this.company = company;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.marketcap = marketcap;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.favorites_list_item, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.fav_company_symbol);
        textView1.setText(symbol[pos]);
        TextView textView2 = (TextView) rowView.findViewById(R.id.fav_company_name);
        textView2.setText(company[pos]);
        TextView textView3 = (TextView) rowView.findViewById(R.id.fav_company_price);
        textView3.setText(price[pos]);
        TextView textView4 = (TextView) rowView.findViewById(R.id.fav_company_change);
        textView4.setText(change[pos]);
        LinearLayout textView4Bg = (LinearLayout) rowView.findViewById(R.id.fav_company_change_background);
        if (change[pos] != null && change[pos].indexOf("+") > -1) {
            textView4Bg.setBackgroundColor(Color.GREEN);
        } else {
            textView4Bg.setBackgroundColor(Color.RED);
        }
        TextView textView5 = (TextView) rowView.findViewById(R.id.fav_company_marketcap);
        textView5.setText(marketcap[pos]);
        return rowView;
    }
}
