package app.mediabrainz.core.activity;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public abstract class BaseActivity extends AppCompatActivity {

    protected <T extends ViewModel> T getViewModel(@NonNull Class<T> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    // todo: remove to Util class??
    @MainThread
    protected void snackbarNotAction(@NonNull View view, @StringRes int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
