package app.mediabrainz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Recording;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.viewmodel.BaseViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.Status;

import static app.mediabrainz.MediaBrainzApp.api;


public class ResultSearchVM extends BaseViewModel {

    public final MutableLiveData<Resource<Artist.ArtistSearch>> artistSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<ReleaseGroup.ReleaseGroupSearch>> rgSearch = new MutableLiveData<>();
    public final MutableLiveData<Resource<Recording.RecordingSearch>> recordingSearch = new MutableLiveData<>();

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
            loadSearchRecording(artistQuery, albumQuery, trackQuery);
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

    private void loadSearchRecording(String artistQuery, String albumQuery, String trackQuery) {
        recordingSearch.setValue(Resource.loading());
        dispose(api.searchRecording(artistQuery, albumQuery, trackQuery,
                result -> recordingSearch.setValue(Resource.success(result)),
                t -> recordingSearch.setValue(Resource.error(t))));
    }

}
