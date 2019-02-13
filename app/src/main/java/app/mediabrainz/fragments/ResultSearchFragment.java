package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistSearchAdapter;
import app.mediabrainz.adapter.recycler.ReleaseAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupSearchAdapter;
import app.mediabrainz.adapter.recycler.SearchListAdapter;
import app.mediabrainz.adapter.recycler.TrackSearchAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.communicator.ShowToolbarTitleCommunicator;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodels.ResultSearchVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


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
            observeReleaseGroupSearch();
            observeRecordingSearch();
            observeTagSearch();
            observeUserSearch();
            observeBarcodeSearch();

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

    private void observeReleaseGroupSearch() {
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

    private void observeTagSearch() {
        resultSearchVM.tagSearch.observe(this, resource -> {
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
                    List<String> result = resource.getData();
                    if (result == null || result.isEmpty()) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        SearchListAdapter adapter = new SearchListAdapter(result);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startTagActivity(this, strings.get(position), false);
                        });
                        if (result.size() == 1) {
                            //ActivityFactory.startTagActivity(this, strings.get(0), false);
                        }
                    }
                    break;
            }
        });
    }

    private void observeUserSearch() {
        resultSearchVM.userSearch.observe(this, resource -> {
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
                    List<String> result = resource.getData();
                    if (result == null || result.isEmpty()) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        SearchListAdapter adapter = new SearchListAdapter(result);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startUserActivity(this, strings.get(position));
                        });
                        if (result.size() == 1) {
                            //ActivityFactory.startUserActivity(this, strings.get(0));
                        }
                    }
                    break;
            }
        });
    }

    private void observeBarcodeSearch() {
        resultSearchVM.barcodeSearch.observe(this, resource -> {
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
                    Release.ReleaseSearch result = resource.getData();
                    if (result == null || result.getCount() == 0) {
                        showAddBarcodeDialog();
                    } else {
                        List<Release> releases = result.getReleases();
                        ReleaseAdapter adapter = new ReleaseAdapter(releases, null);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //onRelease(releases.get(position).getId());
                        });
                        if (releases.size() == 1) {
                            //onRelease(releases.get(0).getId());
                        }
                    }
                    break;
            }
        });
    }

    private void showAddBarcodeDialog() {
        if (getContext() == null) return;
        View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
        TextView titleTextView = titleView.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.barcode_header, searchQuery));
        if (oauth.hasAccount()) {
            new AlertDialog.Builder(getContext())
                    .setCustomTitle(titleView)
                    .setMessage(getString(R.string.barcode_info_log))
                    .setPositiveButton(R.string.barcode_btn, (dialog, which) -> {
                        //getSupportFragmentManager().beginTransaction().add(R.id.contentView, BarcodeSearchFragment.newInstance(searchQuery)).commit();
                    })
                    .setNegativeButton(R.string.barcode_cancel, (dialog, which) -> {
                        dialog.cancel();
                        //ResultSearchActivity.this.finish();
                    })
                    .show();
        } else {
            new AlertDialog.Builder(getContext())
                    .setCustomTitle(titleView)
                    .setMessage(R.string.barcode_info_nolog)
                    .setPositiveButton(R.string.login, (dialog, which) -> {
                        //ActivityFactory.startLoginActivity(this);
                        //ResultSearchActivity.this.finish();
                    });
        }
    }

    private void configSearchRecycler() {
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setItemViewCacheSize(50);
        searchRecyclerView.setDrawingCacheEnabled(true);
        searchRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        searchRecyclerView.setHasFixedSize(true);
    }

    private void search() {
        if (getActivity() == null) return;
        noresultsView.setVisibility(View.GONE);
        viewError(false);

        ShowToolbarTitleCommunicator titleActivity = (ShowToolbarTitleCommunicator) getActivity();
        if (searchType != -1) {
            titleActivity.showToolbarSubTitle(searchQuery);

            if (searchType == SearchType.TAG.ordinal()) {
                titleActivity.showToolbarTitle(getString(R.string.search_tag_title));
                resultSearchVM.getTagSearch(searchQuery);

            } else if (searchType == SearchType.USER.ordinal()) {
                titleActivity.showToolbarTitle(getString(R.string.search_user_title));
                resultSearchVM.getUserSearch(searchQuery);

            } else if (searchType == SearchType.BARCODE.ordinal()) {
                titleActivity.showToolbarTitle(getString(R.string.search_barcode_title));
                resultSearchVM.getBarcodeSearch(searchQuery);
            }

        } else if (!TextUtils.isEmpty(trackQuery)) {
            titleActivity.showToolbarTitle(getString(R.string.search_track_title));
            // todo: do title without artistQuery?
            titleActivity.showToolbarSubTitle(!TextUtils.isEmpty(artistQuery) ? artistQuery + " / " + trackQuery : trackQuery);
            //titleActivity.showToolbarSubTitle(!TextUtils.isEmpty(trackQuery);
            resultSearchVM.getRecordingSearch(artistQuery, albumQuery, trackQuery);

        } else if (!TextUtils.isEmpty(albumQuery)) {
            titleActivity.showToolbarTitle(getString(R.string.search_album_title));
            // todo: do title without artistQuery?
            titleActivity.showToolbarSubTitle(!TextUtils.isEmpty(artistQuery) ? artistQuery + " / " + albumQuery : albumQuery);
            //titleActivity.showToolbarSubTitle(albumQuery);
            resultSearchVM.getReleaseGroupSearch(artistQuery, albumQuery);

        } else if (!TextUtils.isEmpty(artistQuery)) {
            titleActivity.showToolbarTitle(getString(R.string.search_artist_title));
            titleActivity.showToolbarSubTitle(artistQuery);
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
