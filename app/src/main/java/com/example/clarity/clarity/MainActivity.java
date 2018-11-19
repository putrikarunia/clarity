package com.example.clarity.clarity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import static android.support.design.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private String mainFolder = "Clarity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigationView();
    }

    private void setupFileSystem() {
        File folder = new File(getFilesDir(), mainFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void setupNavigationView() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu);
        fragmentManager = getSupportFragmentManager();

        //Set Add Menu to be selected by default
        Menu menu = bottomNavigation.getMenu();
        menu.getItem(1).setChecked(true);
        fragment = new AddFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.rootLayout, fragment).commit();

        //Removing labels and animation
        bottomNavigation.setLabelVisibilityMode(LABEL_VISIBILITY_UNLABELED);
        bottomNavigation.setItemHorizontalTranslation(false);
        bottomNavigation.setItemIconTintList(getResources().getColorStateList(R.color.bottom_nav_color));

        //Handle choosing the menu
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_gallery:
                        fragment = new GalleryFragment();
                        break;
                    case R.id.action_add:
                        fragment = new AddFragment();
                        break;
                    case R.id.action_settings:
                        fragment = new SettingsFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.rootLayout, fragment).commit();
                return true;
            }
        });
    }


}
