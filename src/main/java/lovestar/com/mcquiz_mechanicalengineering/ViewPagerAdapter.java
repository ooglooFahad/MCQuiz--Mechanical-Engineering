package lovestar.com.mcquiz_mechanicalengineering;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Asif ullah on 2/8/2017.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[];
    int NumbOfTabs;
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
        {
            SimpleMcqs simpleMcqs = new SimpleMcqs();
            return simpleMcqs;
        }
        if(position == 1)
        {
            NormalQuiz normalQuiz = new NormalQuiz();
            return normalQuiz;
        }
        if(position == 2)
        {
            MediumQuiz mediumQuiz = new MediumQuiz();
            return mediumQuiz;
        }
        else
        {
            HardQuiz hardQuiz = new HardQuiz();
            return hardQuiz;
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}