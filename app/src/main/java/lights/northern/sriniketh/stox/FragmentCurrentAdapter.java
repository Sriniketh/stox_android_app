package lights.northern.sriniketh.stox;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by srinikethanr on 4/19/16.
 */
public class FragmentCurrentAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] stock_details;
    private final String[] stock_details_heading;

    public FragmentCurrentAdapter(Context context, String[] stock_details, String[] stock_details_heading) {
        super(context, -1, stock_details);
        this.context = context;
        this.stock_details = stock_details;
        this.stock_details_heading = stock_details_heading;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.current_list_item, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.stock_detail_title);
        TextView textView2 = (TextView) rowView.findViewById(R.id.stock_detail_value);
        textView1.setText(stock_details_heading[pos]);
        textView2.setText(stock_details[pos]);
        if (stock_details_heading[pos].equals("CHANGE") || stock_details_heading[pos].equals("CHANGEYTD")) {
            if (stock_details[pos].indexOf("+") > -1) {
                textView2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow, 0);
            } else {
                textView2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
            }
        }
        return rowView;
    }
}
