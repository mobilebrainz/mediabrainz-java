package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.ui.HtmlAssetTextView;


public class AboutFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.about_fragment, container);

        TextView appVersionView = view.findViewById(R.id.appVersionView);
        HtmlAssetTextView aboutView = view.findViewById(R.id.aboutView);
        aboutView.setAsset("about.html");
        appVersionView.setText(getResources().getString(R.string.version_text, MediaBrainzApp.getVersion()));

        return view;
    }

}
