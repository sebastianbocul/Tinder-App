package com.pinder.app;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pinder.app.adapters.MainFragmentManagerPagerAdapter;
import com.pinder.app.util.HideSoftKeyboard;

public class MainFragmentManager extends AppCompatActivity {
    private static final String TAG = "MainFragmentManager";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
        final ViewPager viewPager = findViewById(R.id.pager);
        Log.d(TAG, "  bottomNavigationView.getMaxItemCount(): " + bottomNavigationView.getMaxItemCount());
        MainFragmentManagerPagerAdapter adapter = new MainFragmentManagerPagerAdapter(getSupportFragmentManager(), bottomNavigationView.getMaxItemCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                HideSoftKeyboard.hideKeyboard(MainFragmentManager.this);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                        break;
                    case 1:
                        Log.d("MainFragment", "onPageSelected:  tags");
                        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                        break;
                    case 2:
                        Log.d("MainFragment", "onPageSelected:  main");
                        bottomNavigationView.getMenu().findItem(R.id.nav_main).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_matches).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.nav_tags).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_profile:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_main:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_matches:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.nav_tags:
                        viewPager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
}

   

