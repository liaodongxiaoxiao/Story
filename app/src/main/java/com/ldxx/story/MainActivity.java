package com.ldxx.story;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ldxx.story.fragment.ReadFragment;
import com.ldxx.story.fragment.UnReadFragment;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MainActivity";


    //private StorySwipeAdapter adapter;

    //private boolean isDesc;

    //private SharedPreferencesUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //utils = new SharedPreferencesUtils(this);
        //isDesc = utils.isDesc();
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout1 = (TabLayout) findViewById(R.id.tab);
        ViewPager viewPager1 = (ViewPager) findViewById(R.id.viewpager);
        viewPager1.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        tabLayout1.setupWithViewPager(viewPager1);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDesc = !isDesc;
                utils.saveIsDesc(isDesc);
                pageNum = 0;
                loadStory();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, CollectionActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class TabPagerAdapter extends FragmentPagerAdapter {
        final String[] TITLES = new String[]{"未读", "已读"};

        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new UnReadFragment();
            }else{
                return new ReadFragment();
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }


}
