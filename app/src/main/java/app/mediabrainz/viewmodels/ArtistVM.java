package app.mediabrainz.viewmodels;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.Status;

import static app.mediabrainz.MediaBrainzApp.api;


public class ArtistVM extends CompositeDisposableViewModel {

    public MutableLiveData<Resource<Artist>> artistResource = new MutableLiveData<>();

    public void getArtist(String mbid) {
        Resource<Artist> resource = artistResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS
            || !mbid.equals(resource.getData().getId())) {
                loadArtist(mbid);
        }
    }

    public void loadArtist(String mbid) {
        artistResource.setValue(Resource.loading());
        dispose(api.getArtist(mbid,
                artist -> artistResource.setValue(Resource.success(artist)),
                t -> artistResource.setValue(Resource.error(t))));
    }

}
