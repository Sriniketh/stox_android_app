package lights.northern.sriniketh.stox;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by srinikethanr on 4/12/16.
 */
public class AutoCompleteTextWatcher implements TextWatcher {
    public static final String TAG = "TEXT_WATCHER";
    Context context;
    MainActivity mainActivity;
    AutoCompleteAsyncTask task;
    String autocomplete_url;

    public AutoCompleteTextWatcher(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        task = new AutoCompleteAsyncTask(context, "AUTO");
        autocomplete_url = "";
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mainActivity.company_set_flag = false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mainActivity.company_set_flag = false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        mainActivity.company_set_flag = false;
        if (!s.toString().equals("")) {
            autocomplete_url = "http://stox-007.appspot.com/?req=autocomplete&param=" + s.toString();
            task = new AutoCompleteAsyncTask(context, "AUTO");
            task.execute(autocomplete_url);
        }
    }
}
