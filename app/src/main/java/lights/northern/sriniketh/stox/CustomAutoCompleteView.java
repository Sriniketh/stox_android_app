package lights.northern.sriniketh.stox;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by srinikethanr on 4/12/16.
 */
public class CustomAutoCompleteView extends AutoCompleteTextView {
    public CustomAutoCompleteView(Context context) {
        super(context);
    }

    public CustomAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

    //after a selection we have to capture the new value and append to the existing text
    @Override
    protected void replaceText(final CharSequence text) {
        super.replaceText(text);
    }
}
