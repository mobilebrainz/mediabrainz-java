package app.mediabrainz.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import app.mediabrainz.R;
import app.mediabrainz.apihandler.Api;
import app.mediabrainz.core.activity.BaseActivity;
import app.mediabrainz.core.navigation.NavigationUIExtension;
import app.mediabrainz.core.zxing.IntentIntegrator;
import app.mediabrainz.core.zxing.IntentResult;
import app.mediabrainz.util.MbUtils;

import static app.mediabrainz.MediaBrainzApp.SUPPORT_MAIL;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private NavController navController;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;

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

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        drawer = findViewById(R.id.drawer);
        navController = Navigation.findNavController(this, R.id.navHostView);
        navigationView = findViewById(R.id.navigationView);
        NavigationUI.setupActionBarWithNavController(this, navController, drawer);
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
                    hideLogNavItems();
                }
            }
        });

        /*
        FloatingActionButton fab = findViewById(R.id.fabView);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        */
    }

    private void hideLogNavItems() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.loginFragment).setVisible(!oauth.hasAccount());
        menu.findItem(R.id.logoutAction).setVisible(oauth.hasAccount());
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean handled = true;
        switch (menuItem.getItemId()) {
            case R.id.feedbackAction:
                sendEmail();
                break;

            case R.id.scanBarcodeAction:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                break;

            case R.id.logoutAction:
                //todo: add confirm dialog?
                oauth.logOut();
                navController.navigate(R.id.startFragment);
                break;

            default:
                handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
        }
        if (handled) {
            NavigationUIExtension.handleBottomSheetBehavior(navigationView);
        }
        drawer.closeDrawer(GravityCompat.START);
        return handled;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.BARCODE_REQUEST) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (intentResult != null) {
                String barcode = intentResult.getContents();
                if (barcode != null) {
                    //start barcode search fragment
                    //ActivityFactory.startSearchActivity(this, barcode, SearchType.BARCODE);
                }
            }
        }
    }

    private void sendEmail() {
        try {
            startActivity(Intent.createChooser(
                    MbUtils.emailIntent(SUPPORT_MAIL, Api.CLIENT), getString(R.string.choose_email_client)));
        } catch (android.content.ActivityNotFoundException ex) {
            snackbarNotAction(coordinatorLayout, R.string.send_mail_error);
        }
    }

}
