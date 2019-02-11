package app.mediabrainz.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.core.fragment.BaseFragment;


public class ResultSearchFragment extends BaseFragment {

    public static final String TAG = "ResultSearchF";

    private String artistQuery;
    private String albumQuery;
    private String trackQuery;
    private String searchQuery;
    private int searchType = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.result_search_fragment, container);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            ResultSearchFragmentArgs args = ResultSearchFragmentArgs.fromBundle(getArguments());
            artistQuery = args.getArtistQuery();
            albumQuery = args.getAlbumQuery();
            trackQuery = args.getTrackQuery();
            searchQuery = args.getSearchQuery();
            searchType = args.getSearchType();

            Log.i(TAG, "onActivityCreated: ");
        }
    }
}
