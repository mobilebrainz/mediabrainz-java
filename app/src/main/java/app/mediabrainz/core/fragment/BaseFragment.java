package app.mediabrainz.core.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


public abstract class BaseFragment extends Fragment {

    private Snackbar errorSnackbar;
    private Snackbar infoSnackbar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(null);
            }
        }
    }

    protected void navigate(@NonNull NavDirections directions) {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(directions);
        }
    }

    protected void navigate(@IdRes int resId) {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(resId);
        }
    }

    protected <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(modelClass);
    }

    protected View inflate(@LayoutRes int layoutRes, @Nullable ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, container, false);
    }

    @MainThread
    protected void showInfoSnackbar(@StringRes int resId) {
        if (getView() != null) {
            infoSnackbar = Snackbar.make(getView(), resId, Snackbar.LENGTH_LONG);
            infoSnackbar.show();
        }
    }

    @MainThread
    protected void showErrorSnackbar(@StringRes int messageResId, @StringRes int actionResId, View.OnClickListener action) {
        if (getView() != null) {
            errorSnackbar = Snackbar.make(getView(), messageResId, Snackbar.LENGTH_INDEFINITE);
            errorSnackbar.setAction(actionResId, action).show();
        }
    }

    protected Snackbar getErrorSnackbar() {
        return errorSnackbar;
    }

    protected void dismissErrorSnackbar() {
        if (errorSnackbar != null && errorSnackbar.isShown()) {
            errorSnackbar.dismiss();
        }
    }

    protected Snackbar getInfoSnackbar() {
        return infoSnackbar;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (errorSnackbar != null && errorSnackbar.isShown()) {
            errorSnackbar.dismiss();
        }
        if (infoSnackbar != null && infoSnackbar.isShown()) {
            infoSnackbar.dismiss();
        }
    }
}

