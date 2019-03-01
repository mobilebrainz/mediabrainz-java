package app.mediabrainz.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.NavGraphDirections;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.ArtistSearchAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupSearchAdapter;
import app.mediabrainz.adapter.recycler.SearchListAdapter;
import app.mediabrainz.adapter.recycler.TrackSearchAdapter;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.viewmodel.ResultSearchVM;

import static app.mediabrainz.MediaBrainzApp.oauth;
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

    private ResultSearchVM resultSearchVM;

    protected SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView searchRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.result_search_fragment, container);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        configSearchRecycler();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) search(true);
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            setSubtitle(null);
            ResultSearchFragmentArgs args = ResultSearchFragmentArgs.fromBundle(getArguments());
            artistQuery = args.getArtistQuery();
            albumQuery = args.getAlbumQuery();
            trackQuery = args.getTrackQuery();
            searchQuery = args.getSearchQuery();
            searchType = args.getSearchType();

            resultSearchVM = getViewModel(ResultSearchVM.class);
            observe();
            search(false);
        }
    }

    private void observe() {
        resultSearchVM.progressld.observe(this, aBoolean -> {
            isLoading = aBoolean;
            swipeRefreshLayout.setRefreshing(aBoolean);
        });
        resultSearchVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean) {
                showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> search(true));
            } else {
                dismissErrorSnackbar();
            }
        });
        resultSearchVM.noresultsld.observe(this, aBoolean -> {
            if (aBoolean) showInfoSnackbar(R.string.no_results);
        });
        resultSearchVM.artistsld.observe(this, this::showArtists);
        resultSearchVM.releaseGroupsld.observe(this, this::showReleaseGroups);
        resultSearchVM.recordingsld.observe(this, this::showRecordings);
        resultSearchVM.tagsld.observe(this, this::showTags);
        resultSearchVM.usersld.observe(this, this::showUsers);
        //resultSearchVM.releasesld.observe(this, this::showReleases);
    }

    private void showArtists(List<Artist> artists) {
        ArtistSearchAdapter adapter = new ArtistSearchAdapter(artists);
        searchRecyclerView.setAdapter(adapter);
        if (artists.size() == 1) {
            navigateToArtist(artists.get(0));
        } else {
            adapter.setHolderClickListener(position -> navigateToArtist(artists.get(position)));
        }
    }

    private void navigateToArtist(Artist artist) {
        if (!isLoading && !isError) {
            resultSearchVM.insertSuggestion(artist.getName(), ARTIST);
            NavGraphDirections.ActionGlobalArtistFragment action
                    = NavGraphDirections.actionGlobalArtistFragment(artist.getId());
            navigate(action);
        }
    }

    private void showReleaseGroups(List<ReleaseGroup> releaseGroups) {
        ReleaseGroupSearchAdapter adapter = new ReleaseGroupSearchAdapter(releaseGroups);
        searchRecyclerView.setAdapter(adapter);
        if (releaseGroups.size() == 1) {
            navigateToReleases(releaseGroups.get(0));
        } else {
            adapter.setHolderClickListener(position -> navigateToReleases(releaseGroups.get(position)));
        }
    }

    private void navigateToReleases(ReleaseGroup releaseGroup) {
        if (!isLoading && !isError) {
            resultSearchVM.insertSuggestion(releaseGroup.getTitle(), ALBUM);
            List<Artist.ArtistCredit> artists = releaseGroup.getArtistCredits();
            if (artists != null && !artists.isEmpty()) {
                resultSearchVM.insertSuggestion(artists.get(0).getArtist().getName(), ARTIST);
            }
            int type = ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP.ordinal();
            NavGraphDirections.ActionGlobalReleasesFragment action =
                    NavGraphDirections.actionGlobalReleasesFragment(type, releaseGroup.getId(), null);
            navigate(action);
        }
    }

    private void showRecordings(List<Recording> recordings) {
        TrackSearchAdapter adapter = new TrackSearchAdapter(recordings);
        searchRecyclerView.setAdapter(adapter);
        if (recordings.size() == 1) {
            navigateToRecording(recordings.get(0));
        } else {
            adapter.setHolderClickListener(position -> navigateToRecording(recordings.get(position)));
        }
    }

    private void navigateToRecording(Recording recording) {
        if (!isLoading && !isError) {
            resultSearchVM.insertSuggestion(recording.getTitle(), TRACK);
            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            if (artists != null && !artists.isEmpty()) {
                resultSearchVM.insertSuggestion(artists.get(0).getArtist().getName(), ARTIST);
            }

            //navigate(action);
        }
    }

    private void showTags(List<String> tags) {
        SearchListAdapter adapter = new SearchListAdapter(tags);
        searchRecyclerView.setAdapter(adapter);
        if (tags.size() == 1) {
            navigateToTag(tags.get(0));
        } else {
            adapter.setHolderClickListener(position -> navigateToTag(tags.get(position)));
        }
    }

    private void navigateToTag(String tag) {
        if (!isLoading && !isError) {
            resultSearchVM.insertSuggestion(tag, Suggestion.SuggestionField.TAG);

            //ActivityFactory.startTagActivity(this, strings.get(position), false);
            //navigate(action);
        }
    }

    private void showUsers(List<String> users) {
        SearchListAdapter adapter = new SearchListAdapter(users);
        searchRecyclerView.setAdapter(adapter);
        if (users.size() == 1) {
            navigateToUser(users.get(0));
        } else {
            adapter.setHolderClickListener(position -> navigateToUser(users.get(position)));
        }
    }

    private void navigateToUser(String user) {
        if (!isLoading && !isError) {
            resultSearchVM.insertSuggestion(user, USER);

            //navigate(action);
        }
    }

    /*
    private void showReleases(List<Release> releases) {
        if (releases.isEmpty()) {
            showAddBarcodeDialog();
        } else {
            ReleaseAdapter adapter = new ReleaseAdapter(releases, null);
            searchRecyclerView.setAdapter(adapter);
            adapter.setHolderClickListener(position -> {
                if (isLoading || isError) return;
                //onRelease(releases.get(position).getId());
            });
            if (!(isLoading || isError) && releases.size() == 1) {
                //onRelease(releases.get(0).getId());
            }
        }
    }
    */

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

    private void search(boolean refresh) {
        if (getActivity() == null) return;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) return;

        if (searchType != -1) {
            actionBar.setSubtitle(searchQuery);

            if (searchType == SearchType.TAG.ordinal()) {
                actionBar.setTitle(getString(R.string.search_tag_title));
                resultSearchVM.searchTags(searchQuery, refresh);

            } else if (searchType == SearchType.USER.ordinal()) {
                actionBar.setTitle(getString(R.string.search_user_title));
                resultSearchVM.searchUsers(searchQuery, refresh);

            } else if (searchType == SearchType.BARCODE.ordinal()) {
                actionBar.setTitle(getString(R.string.search_barcode_title));
                resultSearchVM.searchReleasesByBarcode(searchQuery, refresh);
            }

        } else if (!TextUtils.isEmpty(trackQuery)) {
            actionBar.setTitle(getString(R.string.search_track_title));
            // todo: do title without artistQuery?
            actionBar.setSubtitle(!TextUtils.isEmpty(artistQuery) ? artistQuery + " / " + trackQuery : trackQuery);
            //actionBar.setSubtitle(!TextUtils.isEmpty(trackQuery);
            resultSearchVM.searchRecordigs(artistQuery, albumQuery, trackQuery, refresh);

        } else if (!TextUtils.isEmpty(albumQuery)) {
            actionBar.setTitle(getString(R.string.search_album_title));
            // todo: do title without artistQuery?
            actionBar.setSubtitle(!TextUtils.isEmpty(artistQuery) ? artistQuery + " / " + albumQuery : albumQuery);
            //actionBar.setSubtitle(albumQuery);
            resultSearchVM.searchReleaseGroups(artistQuery, albumQuery, refresh);

        } else if (!TextUtils.isEmpty(artistQuery)) {
            actionBar.setTitle(getString(R.string.search_artist_title));
            actionBar.setSubtitle(artistQuery);
            resultSearchVM.searchArtists(artistQuery, refresh);
        }
    }

}
