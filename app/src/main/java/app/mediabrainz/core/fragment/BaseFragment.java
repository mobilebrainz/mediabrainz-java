package app.mediabrainz.core.fragment;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public abstract class BaseFragment extends Fragment {

    protected <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(getActivity()).get(modelClass);
    }

    protected View inflate(@LayoutRes int layoutRes, @Nullable ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, container, false);
    }

    @MainThread
    protected void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @MainThread
    protected void toast(@StringRes final int resId) {
        if (getContext() != null) {
            Toast.makeText(getContext(), getContext().getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

    @MainThread
    protected void snackbarNotAction(@NonNull View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}

