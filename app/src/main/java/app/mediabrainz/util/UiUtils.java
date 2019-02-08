package app.mediabrainz.util;


import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;


public class UiUtils {

    public static void hideKeyboard (@NonNull Activity activity) {
        activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_HIDDEN);
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

}
