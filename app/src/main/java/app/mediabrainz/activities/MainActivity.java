package app.mediabrainz.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewParent;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.core.activity.BaseActivity;
import app.mediabrainz.core.navigation.NavigationUIExtension;


public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

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
        navigationView.setNavigationItemSelectedListener(this);

        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                NavigationView view = weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                } else {
                    NavigationUIExtension.checkNavViewMenuItem(view, destination);
                }
            }
        });

        /*
        FloatingActionButton fab = findViewById(R.id.fabView);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        */
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

    //Custom NavigationUI.setupWithNavController(navigationView, navController);
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean handled = true;
        switch (menuItem.getItemId()) {
            case R.id.feedbackAction:

                break;

            case R.id.scanBarcodeAction:

                break;

            default:
                handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
        }
        if (handled) {
            NavigationUIExtension.handleBottomSheetBehavior(navigationView);
        }
        return handled;
    }

}
