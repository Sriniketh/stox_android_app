package lights.northern.sriniketh.stox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by srinikethanr on 4/15/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentCurrent tab1;
    FragmentHistorical tab2;
    FragmentNews tab3;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tab1 = new FragmentCurrent();
        tab2 = new FragmentHistorical();
        tab3 = new FragmentNews();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}