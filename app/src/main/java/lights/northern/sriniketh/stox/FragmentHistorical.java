package lights.northern.sriniketh.stox;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by srinikethanr on 4/15/16.
 */
public class FragmentHistorical extends Fragment {

    private String mUrl;
    WebView mWebView;

    public FragmentHistorical() {
        mUrl = Uri.parse("file:///android_asset/chart.html").toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_historical, container, false);
        mWebView = (WebView) rootview.findViewById(R.id.webview_chart);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebViewClient(new MWebViewClient());

        ResultActivity resultActivity = (ResultActivity) getActivity();
        mWebView.loadUrl(mUrl + "?data=" + resultActivity.company_set_symbol + "&d=" + resultActivity.chart);
        return rootview;
    }

    private class MWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mUrl = url;
            view.loadUrl(url);
            return true;
        }
    }
}
