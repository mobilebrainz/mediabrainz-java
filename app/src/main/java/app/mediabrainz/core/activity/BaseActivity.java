package app.mediabrainz.core.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public abstract class BaseActivity extends AppCompatActivity {

    protected  <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    @MainThread
    protected void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @MainThread
    protected void toast(@StringRes final int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @MainThread
    protected void snackbarNotAction(@NonNull View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
