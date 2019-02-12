package app.mediabrainz.viewmodels;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.viewmodel.BaseViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.Status;

import static app.mediabrainz.MediaBrainzApp.api;


public class ResultSearchVM extends BaseViewModel {

    public final MutableLiveData<Resource<Artist.ArtistSearch>> artistSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<ReleaseGroup.ReleaseGroupSearch>> rgSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<Recording.RecordingSearch>> recordingSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<List<String>>> tagSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<List<String>>> userSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<Release.ReleaseSearch>> barcodeSearch = new MutableLiveData<>();

    public void getArtistSearch(String artistQuery) {
        Resource<Artist.ArtistSearch> resource = artistSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadArtistSearch(artistQuery);
        }
    }

    public void getReleaseGroupSearch(String artistQuery, String albumQuery) {
        Resource<ReleaseGroup.ReleaseGroupSearch> resource = rgSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadReleaseGroupSearch(artistQuery, albumQuery);
        }
    }

    public void getRecordingSearch(String artistQuery, String albumQuery, String trackQuery) {
        Resource<Recording.RecordingSearch> resource = recordingSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadRecordingSearch(artistQuery, albumQuery, trackQuery);
        }
    }

    public void getTagSearch(String searchQuery) {
        Resource<List<String>> resource = tagSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadTagSearch(searchQuery);
        }
    }

    public void getUserSearch(String searchQuery) {
        Resource<List<String>> resource = userSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadUserSearch(searchQuery);
        }
    }

    public void getBarcodeSearch(String searchQuery) {
        Resource<Release.ReleaseSearch> resource = barcodeSearch.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadBarcodeSearch(searchQuery);
        }
    }

    private void loadArtistSearch(String artistQuery) {
        artistSearch.setValue(Resource.loading());
        dispose(api.searchArtist(artistQuery,
                result -> artistSearch.setValue(Resource.success(result)),
                t -> artistSearch.setValue(Resource.error(t))));
    }

    private void loadReleaseGroupSearch(String artistQuery, String albumQuery) {
        rgSearch.setValue(Resource.loading());
        dispose(api.searchAlbum(artistQuery, albumQuery,
                result -> rgSearch.setValue(Resource.success(result)),
                t -> rgSearch.setValue(Resource.error(t))));
    }

    private void loadRecordingSearch(String artistQuery, String albumQuery, String trackQuery) {
        recordingSearch.setValue(Resource.loading());
        dispose(api.searchRecording(artistQuery, albumQuery, trackQuery,
                result -> recordingSearch.setValue(Resource.success(result)),
                t -> recordingSearch.setValue(Resource.error(t))));
    }

    private void loadTagSearch(String searchQuery) {
        tagSearch.setValue(Resource.loading());
        dispose(api.searchTagFromSite(searchQuery, 1, 100,
                result -> tagSearch.setValue(Resource.success(result)),
                t -> tagSearch.setValue(Resource.error(t))));
    }

    private void loadUserSearch(String searchQuery) {
        userSearch.setValue(Resource.loading());
        dispose(api.searchUserFromSite(searchQuery, 1, 100,
                result -> userSearch.setValue(Resource.success(result)),
                t -> userSearch.setValue(Resource.error(t))));
    }

    private void loadBarcodeSearch(String searchQuery) {
        barcodeSearch.setValue(Resource.loading());
        dispose(api.searchReleasesByBarcode(searchQuery,
                result -> barcodeSearch.setValue(Resource.success(result)),
                t -> barcodeSearch.setValue(Resource.error(t))));
    }

}
