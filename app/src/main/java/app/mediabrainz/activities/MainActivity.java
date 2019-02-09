package app.mediabrainz.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.core.activity.BaseActivity;


public class MainActivity extends BaseActivity {

    private NavController navController;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            //optionsNavId = savedInstanceState.getInt(OPTIONS_NAV_ID, OPTIONS_NAV_DEFAULT);
        } else {
            //optionsNavId = OPTIONS_NAV_DEFAULT;
        }

        drawer = findViewById(R.id.drawer);
        navController = Navigation.findNavController(this, R.id.navHostView);
        navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupActionBarWithNavController(this, navController, drawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Menu drawerMenu = navigationView.getMenu();
            MenuItem drawerMenuItem = drawerMenu.findItem(destination.getId());
            if (drawerMenuItem != null && !drawerMenuItem.isChecked()) {
                unCheckAllMenuItems(drawerMenu);
                drawerMenuItem.setChecked(true);
                scrollDrawerToPosition(drawerMenuItem.getOrder());
            }
        });

        /*
        FloatingActionButton fab = findViewById(R.id.fabView);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        */
    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    private void scrollDrawerToPosition(int order) {
        final int limit = 4;
        if (order > limit) {
            RecyclerView recyclerView = (RecyclerView) navigationView.getChildAt(0);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(order - limit, 0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(OPTIONS_NAV_ID, optionsNavId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //optionsNavId = savedInstanceState.getInt(OPTIONS_NAV_ID, OPTIONS_NAV_DEFAULT);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options_menu, menu);
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean handled = true;
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
            default:
                handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
        }
        return handled;
    }

}
