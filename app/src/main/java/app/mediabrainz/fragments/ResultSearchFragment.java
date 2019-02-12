package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistSearchAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupSearchAdapter;
import app.mediabrainz.adapter.recycler.TrackSearchAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodels.ResultSearchVM;


public class ResultSearchFragment extends BaseFragment {

    public static final String TAG = "ResultSearchF";

    private String artistQuery;
    private String albumQuery;
    private String trackQuery;
    private String searchQuery;
    private int searchType = -1;
    private boolean isLoading;
    private boolean isError;
    private ResultSearchVM resultSearchVM;

    private View contentView;
    private RecyclerView searchRecyclerView;
    private View errorView;
    private View progressView;
    private View noresultsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.result_search_fragment, container);

        contentView = view.findViewById(R.id.contentView);
        errorView = view.findViewById(R.id.errorView);
        progressView = view.findViewById(R.id.progressView);
        noresultsView = view.findViewById(R.id.noresultsView);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        configSearchRecycler();

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

            resultSearchVM = getViewModel(ResultSearchVM.class);
            observeArtistSearch();
            observeRgSearch();
            observeRecordingSearch();

            search();
        }
    }

    private void observeArtistSearch() {
        resultSearchVM.artistSearch.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    Artist.ArtistSearch result = resource.getData();
                    if (result == null || result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<Artist> artists = result.getArtists();
                        ArtistSearchAdapter adapter = new ArtistSearchAdapter(artists);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startArtistActivity(this, artists.get(position).getId());
                        });
                        if (artists.size() == 1) {
                            //ActivityFactory.startArtistActivity(this, artists.get(0).getId());
                        }
                    }
                    break;
            }
        });
    }

    private void observeRgSearch() {
        resultSearchVM.rgSearch.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    ReleaseGroup.ReleaseGroupSearch result = resource.getData();
                    if (result == null || result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<ReleaseGroup> releaseGroups = result.getReleaseGroups();
                        ReleaseGroupSearchAdapter adapter = new ReleaseGroupSearchAdapter(releaseGroups);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //showReleases(releaseGroups.get(position).getId());
                        });
                        if (releaseGroups.size() == 1) {
                            //showReleases(releaseGroups.get(0).getId());
                        }
                    }
                    break;
            }
        });
    }

    private void observeRecordingSearch() {
        resultSearchVM.recordingSearch.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    Recording.RecordingSearch result = resource.getData();
                    if (result == null || result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<Recording> recordings = result.getRecordings();
                        TrackSearchAdapter adapter = new TrackSearchAdapter(recordings);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startRecordingActivity(this, recordings.get(position).getId());
                        });
                        if (recordings.size() == 1) {
                            //ActivityFactory.startRecordingActivity(this, recordings.get(0).getId());
                        }
                    }
                    break;
            }
        });
    }

    private void configSearchRecycler() {
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setItemViewCacheSize(50);
        searchRecyclerView.setDrawingCacheEnabled(true);
        searchRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        searchRecyclerView.setHasFixedSize(true);
    }

    private void search() {
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        //viewProgressLoading(true);

        if (searchType != -1) {
            //toolbarBottomTitleView.setText(searchQuery);
            if (searchType == SearchType.TAG.ordinal()) {
                //toolbarTopTitleView.setText(R.string.search_tag_title);
                //searchTag();
            } else if (searchType == SearchType.USER.ordinal()) {
                //toolbarTopTitleView.setText(R.string.search_user_title);
                //searchUser();
            } else if (searchType == SearchType.BARCODE.ordinal()) {
                //toolbarTopTitleView.setText(R.string.search_barcode_title);
                //searchBarcode();
            }
        } else if (!TextUtils.isEmpty(trackQuery)) {
            //toolbarTopTitleView.setText(R.string.search_track_title);
            //toolbarBottomTitleView.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + trackSearch : trackSearch);
            resultSearchVM.getRecordingSearch(artistQuery, albumQuery, trackQuery);
        } else if (!TextUtils.isEmpty(albumQuery)) {
            //toolbarTopTitleView.setText(R.string.search_album_title);
            //toolbarBottomTitleView.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + albumSearch : albumSearch);
            resultSearchVM.getReleaseGroupSearch(artistQuery, albumQuery);
        } else if (!TextUtils.isEmpty(artistQuery)) {
            //toolbarTopTitleView.setText(R.string.search_artist_title);
            //toolbarBottomTitleView.setText(artistSearch);
            resultSearchVM.getArtistSearch(artistQuery);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentView.setAlpha(0.3F);
            searchRecyclerView.setAlpha(0.3F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            searchRecyclerView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            searchRecyclerView.setVisibility(View.INVISIBLE);
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> search());
    }
}
