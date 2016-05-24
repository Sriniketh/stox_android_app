package lights.northern.sriniketh.stox;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String[] labels;

    public AutoCompleteAdapter(Context context, String[] values, String[] labels) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.labels = labels;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.autocomplete_item, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.autocomplete_symbol);
        TextView textView2 = (TextView) rowView.findViewById(R.id.autocomplete_company_name);
        Log.d("VALUES[POS]", values[pos] + " : " + labels[pos]);
        textView1.setText(values[pos]);
        textView2.setText(labels[pos]);
        return rowView;
    }
}
