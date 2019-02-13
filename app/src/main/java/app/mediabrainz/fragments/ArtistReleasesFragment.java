package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodels.ArtistVM;
import app.mediabrainz.viewmodels.MainVM;


public class ArtistReleasesFragment extends BaseFragment {

    private static final String TAG = "ArtistReleasesF";

    private MainVM mainVM;
    private ArtistVM artistVM;

    private String mbid;

    public static ArtistReleasesFragment newInstance() {
        return new ArtistReleasesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.artist_releases_fragment, container);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            artistVM = getActivityViewModel(ArtistVM.class);
            mainVM = getActivityViewModel(MainVM.class);
            mbid = mainVM.getArtistMbid();
            Log.i(TAG, "onActivityCreated: ");
            if (!TextUtils.isEmpty(mbid)) {

            }
        }
    }

}
