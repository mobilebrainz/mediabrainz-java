package app.mediabrainz.viewmodel;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.Status;
import app.mediabrainz.data.datamapper.ArtistMapper;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.data.room.entity.User;
import app.mediabrainz.data.room.repository.SuggestionRepository;

import static app.mediabrainz.MediaBrainzApp.api;


public class ResultSearchVM extends CompositeDisposableViewModel {

    public final MutableLiveData<Boolean> progressld = new MutableLiveData<>();
    public final MutableLiveData<Boolean> noresultsld = new MutableLiveData<>();
    public final MutableLiveData<Boolean> errorld = new MutableLiveData<>();

    public final MutableLiveData<List<Artist>> artistsld = new MutableLiveData<>();
    public final MutableLiveData<List<ReleaseGroup>> releaseGroupsld = new MutableLiveData<>();
    public final MutableLiveData<List<Recording>> recordingsld = new MutableLiveData<>();
    public final MutableLiveData<List<String>> tagsld = new MutableLiveData<>();
    public final MutableLiveData<List<String>> usersld = new MutableLiveData<>();
    public final MutableLiveData<List<Release>> releasesld = new MutableLiveData<>();

    private final SuggestionRepository suggestionRepository = new SuggestionRepository();

    public void insertSuggestion(@Nullable String word, @NonNull Suggestion.SuggestionField suggestionField) {
        if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled() && !TextUtils.isEmpty(word)) {
            suggestionRepository.insert(new Suggestion(word, suggestionField));
        }
    }

    private void initLoading() {
        errorld.setValue(false);
        noresultsld.setValue(false);
        progressld.setValue(true);
    }

    private void setError() {
        progressld.setValue(false);
        errorld.setValue(true);
    }

    public void searchArtists(String query) {
        if (artistsld.getValue() == null) {
            initLoading();
            dispose(api.searchArtist(query,
                    result -> {
                        progressld.setValue(false);
                        /*
                        // with mapping
                        final List<Artist> artists = new ArtistMapper().convertArtistsFromApi(result);
                        if (! artists.isEmpty()) {
                            artistsld.setValue(new ArtistMapper().convertArtistsFromApi(result));
                        } else {
                            noresultsld.setValue(true);
                        }
                        */
                        // without mapping
                        if (result != null && result.getCount() != 0) {
                            artistsld.setValue(result.getArtists());
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    t -> setError()));
        }
    }

    public void searchReleaseGroups(String artistQuery, String albumQuery) {
        if (releaseGroupsld.getValue() == null) {
            initLoading();
            dispose(api.searchAlbum(artistQuery, albumQuery,
                    result -> {
                        progressld.setValue(false);
                        if (result != null && result.getCount() != 0) {
                            releaseGroupsld.setValue(result.getReleaseGroups());
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    t -> setError()));
        }
    }

    public void searchRecordigs(String artistQuery, String albumQuery, String trackQuery) {
        if (recordingsld.getValue() == null) {
            initLoading();
            dispose(api.searchRecording(artistQuery, albumQuery, trackQuery,
                    result -> {
                        progressld.setValue(false);
                        if (result != null && result.getCount() != 0) {
                            recordingsld.setValue(result.getRecordings());
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    t -> setError()));
        }
    }

    public void searchTags(String searchQuery) {
        if (tagsld.getValue() == null) {
            initLoading();
            dispose(api.searchTagFromSite(searchQuery, 1, 100,
                    result -> {
                        progressld.setValue(false);
                        if (result != null && !result.isEmpty()) {
                            tagsld.setValue(result);
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    t -> setError()));
        }
    }

    public void searchUsers(String searchQuery) {
        if (usersld.getValue() == null) {
            initLoading();
            dispose(api.searchUserFromSite(searchQuery, 1, 100,
                    result -> {
                        progressld.setValue(false);
                        if (result != null && !result.isEmpty()) {
                            usersld.setValue(result);
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    t -> setError()));
        }
    }

    public void searchReleasesByBarcode(String barcode) {
        if (releasesld.getValue() == null) {
            initLoading();
            dispose(api.searchReleasesByBarcode(barcode,
                    result -> {
                        progressld.setValue(false);
                        final List<Release> releases = new ArrayList<>();
                        if (result != null && result.getCount() != 0) {
                            releases.addAll(result.getReleases());
                        }
                        releasesld.setValue(releases);
                    },
                    t -> setError()));
        }
    }

}
