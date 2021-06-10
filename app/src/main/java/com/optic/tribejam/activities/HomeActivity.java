package com.optic.tribejam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.optic.tribejam.R;
import com.optic.tribejam.fragments.ChatFragment;
import com.optic.tribejam.fragments.FiltersFragment;
import com.optic.tribejam.fragments.HomeFragment;
import com.optic.tribejam.fragments.ProfileFragment;
import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.TokenProvider;
import com.optic.tribejam.providers.UserProvider;
import com.optic.tribejam.utils.ViewedMessageHelper;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UserProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UserProvider();

        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.itemHome){
                        //Fragment--> Home
                        openFragment(new HomeFragment());

                    }
                    else if(item.getItemId() == R.id.itemChat){
                        //Fragment--> Chat
                        openFragment(new ChatFragment());

                    }
                    else if(item.getItemId() == R.id.itemFilters){
                        //Fragment--> Filters
                        openFragment(new FiltersFragment());

                    }
                    else if(item.getItemId() == R.id.itemProfile){
                        //Fragment--> Profile
                        openFragment(new ProfileFragment());

                    }
                    return true;
                }
            };
    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }
}