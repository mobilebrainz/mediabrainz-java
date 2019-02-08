package app.mobilebrainz.mediabrainz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import app.mobilebrainz.mediabrainz.MediaBrainzApp;
import app.mobilebrainz.mediabrainz.R;
import app.mobilebrainz.mediabrainz.ui.HtmlAssetTextView;


public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        TextView appVersionView = view.findViewById(R.id.appVersionView);
        HtmlAssetTextView aboutView = view.findViewById(R.id.aboutView);
        aboutView.setAsset("about.html");
        appVersionView.setText(getResources().getString(R.string.version_text, MediaBrainzApp.getVersion()));

        return view;
    }

}
