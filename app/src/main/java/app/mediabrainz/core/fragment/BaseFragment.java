package app.mediabrainz.core.fragment;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import app.mediabrainz.R;

import static androidx.core.content.ContextCompat.getSystemService;


public abstract class BaseFragment extends Fragment {

    protected <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(modelClass);
    }

    protected View inflate(@LayoutRes int layoutRes, @Nullable ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(layoutRes, container, false);
    }

    // todo: remove to Util class??
    @MainThread
    protected void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // todo: remove to Util class??
    @MainThread
    protected void toast(@StringRes final int resId) {
        if (getContext() != null) {
            Toast.makeText(getContext(), getContext().getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

    // todo: remove to Util class??
    @MainThread
    protected void snackbarNotAction(@NonNull View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    // todo: remove to Util class??
    protected boolean checkNetworkConnection() {
        boolean isNetworkConnected = false;
        if (getContext() != null) {
            ConnectivityManager connectivityManager = getSystemService(getContext(), ConnectivityManager.class);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = (networkInfo != null && networkInfo.isConnected());
                Log.i("checkNetworkConnect", "checkNetworkConnection: ");
                if (!isNetworkConnected) {
                    Log.i("checkNetworkConnect", "checkNetworkConnection: ");
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error_connect_title)
                            .setMessage(R.string.error_connect_message)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }
        }
        return isNetworkConnected;
    }

}

