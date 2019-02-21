package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.preference.AndroidResources;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import app.mediabrainz.R;
import app.mediabrainz.data.room.repository.RecommendRepository;
import app.mediabrainz.data.room.repository.SuggestionRepository;


public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String CLEAR_SUGGESTIONS = "clear_suggestions";
    private static final String CLEAR_RECOMMENDS = "clear_recommends";

    private SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        findPreference(CLEAR_SUGGESTIONS).setOnPreferenceClickListener(preference -> {
            if (preference.getKey().equals(CLEAR_SUGGESTIONS)) {
                clearSuggestionHistory();
                return true;
            }
            return false;
        });

        findPreference(CLEAR_RECOMMENDS).setOnPreferenceClickListener(preference -> {
            if (preference.getKey().equals(CLEAR_RECOMMENDS)) {
                clearRecommends();
                return true;
            }
            return false;
        });
    }

    private void clearSuggestionHistory() {
        //todo: make progress?
        //todo: remove to ViewModel?
        new SuggestionRepository().deleteAll(() -> showSnackbar(R.string.toast_search_cleared));
    }

    private void clearRecommends() {
        //todo: make progress?
        //todo: remove to ViewModel?
        new RecommendRepository().deleteAll(() -> showSnackbar(R.string.toast_recommends_cleared));
    }

    private void showSnackbar(int res) {
        if (getView() != null) {
            View view = getView().findViewById(AndroidResources.ANDROID_R_LIST_CONTAINER);
            Snackbar.make(view, res, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

}
