package com.example.donategood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.donategood.fragments.ComposeFragment;
import com.example.donategood.fragments.HomeFragment;
import com.example.donategood.fragments.ProfileFragment;
import com.example.donategood.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        Log.i(TAG, "profile button clicked");
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_compose:
                        Log.i(TAG, "compose button clicked");
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_search:
                        Log.i(TAG, "search button clicked");
                        fragment = new SearchFragment();
                        break;
                    default:
                        //home button clicked
                        Log.i(TAG, "home button clicked");
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        //set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}