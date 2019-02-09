package app.mediabrainz.core.util;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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

    public static void showError(Context context, Throwable t) {
        if (t != null) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void showMessage(Activity activity, final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, final int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

}
