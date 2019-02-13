package app.mediabrainz.core.navigation;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavDestination;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NavigationUIExtension {

    /**
     * From NavigationUI.setupWithNavController(@NonNull final NavigationView navigationView, @NonNull final NavController navController)
     */
    public static void handleBottomSheetBehavior(@NonNull NavigationView navigationView) {
        ViewParent parent = navigationView.getParent();
        if (parent instanceof DrawerLayout) {
            ((DrawerLayout) parent).closeDrawer(navigationView);
        } else {
            BottomSheetBehavior bottomSheetBehavior = NavigationUIExtension.findBottomSheetBehavior(navigationView);
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    /**
     * From NavigationUI.findBottomSheetBehavior(@NonNull View view)
     * Walks up the view hierarchy, trying to determine if the given View is contained within a bottom sheet.
     */
    @SuppressWarnings("WeakerAccess")
    private static BottomSheetBehavior findBottomSheetBehavior(@NonNull View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            ViewParent parent = view.getParent();
            return parent instanceof View ? findBottomSheetBehavior((View) parent) : null;
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        return behavior instanceof BottomSheetBehavior ? (BottomSheetBehavior) behavior : null;
    }

    public static void unCheckAllMenuItems(@NonNull final Menu menu) {
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

    private static void scrollDrawerToPosition(@NonNull NavigationView navigationView, int order) {
        final int limit = 4;
        if (order > limit) {
            RecyclerView recyclerView = (RecyclerView) navigationView.getChildAt(0);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(order - limit, 0);
            }
        }
    }

    public static void checkNavViewMenuItem(@NonNull NavigationView navigationView, @NonNull NavDestination destination) {
        Menu drawerMenu = navigationView.getMenu();
        MenuItem drawerMenuItem = drawerMenu.findItem(destination.getId());
        if (drawerMenuItem != null && !drawerMenuItem.isChecked()) {
            unCheckAllMenuItems(drawerMenu);
            drawerMenuItem.setChecked(true);
            //scrollDrawerToPosition(navigationView, drawerMenuItem.getOrder());
        }
    }

}
