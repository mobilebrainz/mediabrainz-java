package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.StartVM;


public class ArtistRatingsFragment extends BaseFragment {

    private StartVM startVM;

    public static ArtistRatingsFragment newInstance() {
        return new ArtistRatingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.artist_ratings_fragment, container);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startVM = getViewModel(StartVM.class);
    }

}
