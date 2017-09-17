package lovestar.com.mcquiz_mechanicalengineering;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class TabsActivity extends AppCompatActivity {
    static ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Sample Mcqs","Normal Quiz","Medium","Hard"};
    int Numboftabs =4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        Bundle bundle = getIntent().getExtras();
        String subname=bundle.get("subjectName").toString();
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.tool_bar);
        toolbarTop.setTitle(" " + subname);
//        getSupportActionBar().setIcon(R.drawable.dontknowans);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);// To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setSelectedIndicatorColors(R.color.tab_active);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);
    }
}
