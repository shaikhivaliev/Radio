package com.elegion.radio.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.elegion.radio.R;
import com.elegion.radio.ui.main.MainFragment;
import com.elegion.radio.ui.settings.SettingsFragment;
import com.elegion.radio.ui.stationsList.StationListFragment;

public class ContainerActivity extends AppCompatActivity implements
        DrawerLocker {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private View mSearchViewMock;
    private View mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);
        initUI();

        if (savedInstanceState == null) {
            changeFragment(MainFragment.newInstance());
        }
    }

    public void changeFragment(Fragment fragment) {

        boolean addToBackStack = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) != null;

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    public void initUI() {
        mDrawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.navigation_view);
        mToolbar = findViewById(R.id.toolbar);
        mSearchViewMock = findViewById(R.id.search_mock);
        mFragmentContainer = findViewById(R.id.fragmentContainer);
        setSupportActionBar(mToolbar);

        mToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_item_settings:
                        changeFragment(new SettingsFragment());
                        break;

                    case R.id.menu_item_logout:
                        //todo logout
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_item).getActionView();

        //менняем цвета виджета
        SearchView.SearchAutoComplete searchAutoComplete =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.WHITE);

        ImageView searchCleanQueryIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchCleanQueryIcon.setImageResource(R.drawable.ic_close_white);


        //ставим заглушку
        MenuItem sView = menu.findItem(R.id.search_menu_item);
        sView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchViewMock.setVisibility(View.VISIBLE);
                mFragmentContainer.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchViewMock.setVisibility(View.GONE);
                mFragmentContainer.setVisibility(View.VISIBLE);
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                return true;
            }
        });

        //отрабатываем запрос
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStation(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }


    private void searchStation(String query) {

        Bundle args = new Bundle();
        args.putString(StationListFragment.SEARCH_QUERY, query);
        Fragment fragment = StationListFragment.newInstance(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

        mSearchViewMock.setVisibility(View.GONE);
        mFragmentContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public void setDrawerLocked(boolean shouldLock) {
        if (shouldLock) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
