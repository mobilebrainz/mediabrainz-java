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
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistSearchAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupSearchAdapter;
import app.mediabrainz.adapter.recycler.TrackSearchAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.data.room.repository.SuggestionRepository;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.data.room.entity.Suggestion.SuggestionField.ALBUM;
import static app.mediabrainz.data.room.entity.Suggestion.SuggestionField.ARTIST;
import static app.mediabrainz.data.room.entity.Suggestion.SuggestionField.TRACK;
import static app.mediabrainz.data.room.entity.Suggestion.SuggestionField.USER;


public class ResultSearchFragment extends BaseFragment {

    public static final String TAG = "ResultSearchF";

    private String artistQuery;
    private String albumQuery;
    private String trackQuery;
    private String searchQuery;
    private int searchType = -1;
    private boolean isLoading;
    private boolean isError;

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

            search();
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
        noresultsView.setVisibility(View.GONE);
        viewError(false);
        viewProgressLoading(true);

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
            searchRecording();
        } else if (!TextUtils.isEmpty(albumQuery)) {
            //toolbarTopTitleView.setText(R.string.search_album_title);
            //toolbarBottomTitleView.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + albumSearch : albumSearch);
            searchAlbum();
        } else if (!TextUtils.isEmpty(artistQuery)) {
            //toolbarTopTitleView.setText(R.string.search_artist_title);
            //toolbarBottomTitleView.setText(artistSearch);
            searchArtist();
        }
    }

    private void searchRecording() {
        api.searchRecording(
                artistQuery, albumQuery, trackQuery,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<Recording> recordings = result.getRecordings();
                        TrackSearchAdapter adapter = new TrackSearchAdapter(recordings);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startRecordingActivity(this, recordings.get(position).getId());
                        });
                        saveQueryAsSuggestion();
                        if (recordings.size() == 1) {
                            //ActivityFactory.startRecordingActivity(this, recordings.get(0).getId());
                        }
                    }
                },
                this::showConnectionWarning
        );
    }

    private void searchAlbum() {
        api.searchAlbum(
                artistQuery, albumQuery,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<ReleaseGroup> releaseGroups = result.getReleaseGroups();
                        ReleaseGroupSearchAdapter adapter = new ReleaseGroupSearchAdapter(releaseGroups);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //showReleases(releaseGroups.get(position).getId());
                        });
                        saveQueryAsSuggestion();
                        if (releaseGroups.size() == 1) {
                            //showReleases(releaseGroups.get(0).getId());
                        }
                    }
                },
                this::showConnectionWarning);
    }

    private void searchArtist() {
        api.searchArtist(
                artistQuery,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresultsView.setVisibility(View.VISIBLE);
                    } else {
                        List<Artist> artists = result.getArtists();
                        ArtistSearchAdapter adapter = new ArtistSearchAdapter(artists);
                        searchRecyclerView.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> {
                            //ActivityFactory.startArtistActivity(this, artists.get(position).getId());
                        });
                        saveQueryAsSuggestion();
                        if (artists.size() == 1) {
                            //ActivityFactory.startArtistActivity(this, artists.get(0).getId());
                        }
                    }
                },
                this::showConnectionWarning);
    }

    private void saveQueryAsSuggestion() {
        if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            SuggestionRepository suggestionRepository = new SuggestionRepository();
            if (!TextUtils.isEmpty(artistQuery)) {
                suggestionRepository.insert(new Suggestion(artistQuery, ARTIST));
            }
            if (!TextUtils.isEmpty(albumQuery)) {
                suggestionRepository.insert(new Suggestion(albumQuery, ALBUM));
            }
            if (!TextUtils.isEmpty(trackQuery)) {
                suggestionRepository.insert(new Suggestion(trackQuery, TRACK));
            }
            if (!TextUtils.isEmpty(searchQuery)) {
                if (searchType == SearchType.TAG.ordinal()) {
                    suggestionRepository.insert(new Suggestion(searchQuery, Suggestion.SuggestionField.TAG));
                } else if (searchType == SearchType.USER.ordinal()) {
                    suggestionRepository.insert(new Suggestion(searchQuery, USER));
                }
            }
        }
    }

    public void viewProgressLoading(boolean isView) {
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

    public void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> search());
    }
}
