package lights.northern.sriniketh.stox;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by srinikethanr on 4/15/16.
 */
public class FragmentCurrent extends Fragment {

    static FragmentCurrentAdapter fragmentCurrentAdapter;
    static NonScrollableListView fragmentCurrentView;
    static String[] stock_details = new String[]{""};
    static String[] stock_details_heading = new String[]{""};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.fragment_current, container, false);

        fragmentCurrentView = (NonScrollableListView) rootView.findViewById(R.id.stock_details_list);
        fragmentCurrentAdapter = new FragmentCurrentAdapter(getActivity(), stock_details, stock_details_heading);
        fragmentCurrentView.setAdapter(fragmentCurrentAdapter);

        ResultActivity resultActivity = (ResultActivity) getActivity();
        //Toast.makeText(getActivity(), resultActivity.current, Toast.LENGTH_SHORT).show();
        String result = resultActivity.current;
        try {
            JSONArray jsonArray = new JSONArray(result);
            String[] details = new String[11];
            String[] headings = new String[11];
            //details[0] = "";
            //headings[0] = "Stock Details";
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject obj = jsonArray.getJSONObject(i);
                //Log.d("JSON", obj.toString());
                if (obj.has("Name")) {
                    headings[0] = "NAME";
                    details[0] = obj.getString("Name");
                } else if (obj.has("Symbol")) {
                    headings[1] = "SYMBOL";
                    details[1] = obj.getString("Symbol");
                } else if (obj.has("LastPrice")) {
                    headings[2] = "LASTPRICE";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("LastPrice")) * 100) / 100;
                    details[2] = rounded + "";
                } else if (obj.has("Change")) {
                    headings[3] = "CHANGE";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("Change")) * 100) / 100;
                    details[3] = rounded + "";
                } else if (obj.has("ChangePercent")) {
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("ChangePercent")) * 100) / 100;
                    details[3] += "(" + rounded + "%)";
                } else if (obj.has("Timestamp")) {
                    headings[4] = "TIMESTAMP";
                    String time = obj.getString("Timestamp");
                    String[] time_details = time.split(" ");
                    details[4] = time_details[2] + " " + time_details[1] + " " + time_details[5] + ", " + time_details[3] + " EST";
                } else if (obj.has("MarketCap")) {
                    headings[5] = "MARKETCAP";
                    double rounded = Double.parseDouble(obj.getString("MarketCap"));
                    if (rounded >= 1000000000) {
                        rounded = rounded / 1000000000;
                        double value = (double) Math.round(rounded * 100) / 100;
                        details[5] = value + " Billion";
                    } else if (rounded >= 1000000) {
                        rounded = rounded / 1000000;
                        double value = (double) Math.round(rounded * 100) / 100;
                        details[5] = value + " Million";
                    } else {
                        details[5] = rounded + "";
                    }
                } else if (obj.has("Volume")) {
                    headings[6] = "VOLUME";
                    details[6] = obj.getString("Volume");
                } else if (obj.has("ChangeYTD")) {
                    headings[7] = "CHANGEYTD";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("ChangeYTD")) * 100) / 100;
                    details[7] = rounded + "";
                } else if (obj.has("ChangePercentYTD")) {
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("ChangePercentYTD")) * 100) / 100;
                    details[7] += "(" + rounded + "%)";
                } else if (obj.has("High")) {
                    headings[8] = "HIGH";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("High")) * 100) / 100;
                    details[8] = rounded + "";
                } else if (obj.has("Low")) {
                    headings[9] = "LOW";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("Low")) * 100) / 100;
                    details[9] = rounded + "";
                } else if (obj.has("Open")) {
                    headings[10] = "OPEN";
                    double rounded = (double) Math.round(Double.parseDouble(obj.getString("Open")) * 100) / 100;
                    details[10] = rounded + "";
                }
            }

            stock_details_heading = headings;
            stock_details = details;
            Log.d("STOCK_DETAILS", details + "\n" + headings);
            fragmentCurrentAdapter.notifyDataSetChanged();
            fragmentCurrentAdapter = new FragmentCurrentAdapter(getActivity(), stock_details, stock_details_heading);
            fragmentCurrentView.setAdapter(fragmentCurrentAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.stock_chart_image);
        String image = resultActivity.yahoo_chart;
        final String image_expanded = resultActivity.yahoo_chart_expanded;
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            imageButton.setImageBitmap(bitmap);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //zoomImageFromThumb(imageButton, bitmap_expanded, rootView);
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.stock_image_dialog);
                    ImageView imageView = (ImageView) dialog.findViewById(R.id.expanded_stock_chart_image);
                    byte[] encodeByte_expanded = Base64.decode(image_expanded, Base64.DEFAULT);
                    Bitmap bitmap_expanded = BitmapFactory.decodeByteArray(encodeByte_expanded, 0, encodeByte_expanded.length);
                    imageView.setImageBitmap(bitmap_expanded);
                    PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
                    photoViewAttacher.update();
                    dialog.show();
                }
            });
        } catch (Exception e) {
            e.getMessage();
            return null;
        }

        return rootView;
    }
}
