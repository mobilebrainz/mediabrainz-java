package app.mediabrainz.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;


public class ArtistFragment extends BaseArtistFragment implements
        View.OnClickListener {

    public static final String TAG = "ArtistFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.artist_fragment, container);

        errorView = view.findViewById(R.id.errorView);
        progressView = view.findViewById(R.id.progressView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        initTopMenu(view);

        return view;
    }

    public void initTopMenu(View view) {
        view.findViewById(R.id.releasesItem).setOnClickListener(this);
        view.findViewById(R.id.relationsItem).setOnClickListener(this);
        view.findViewById(R.id.tagsItem).setOnClickListener(this);
        view.findViewById(R.id.linksItem).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void show(Artist artist) {
        Log.i(TAG, "show: ");
    }

    @Override
    public void onClick(View v) {
        if (!isLoading) {
            switch (v.getId()) {
                case R.id.releasesItem:
                    Navigation.findNavController(v).navigate(R.id.action_artistFragment_to_artistReleasesFragment);
                    break;
                case R.id.relationsItem:

                    break;
                case R.id.tagsItem:
                    Navigation.findNavController(v).navigate(R.id.action_artistFragment_to_artistTagsPagerFragment);
                    break;
                case R.id.linksItem:
                    Navigation.findNavController(v).navigate(R.id.action_artistFragment_to_artistLinksFragment);
                    break;
            }
        }
    }
}
