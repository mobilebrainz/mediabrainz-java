package app.mediabrainz.viewmodels;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.BaseViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.Status;

import static app.mediabrainz.MediaBrainzApp.api;


public class MainVM extends BaseViewModel {

    public final MutableLiveData<Resource<List<String>>> genresResource = new MutableLiveData<>();

    public void getGenres() {
        Resource<List<String>> resource = genresResource.getValue();
        if (resource == null || resource.getData() == null || resource.getStatus() != Status.SUCCESS) {
            loadGenres();
        }
    }

    public void loadGenres() {
        genresResource.setValue(Resource.loading());
        dispose(api.getGenres(
                genres -> genresResource.setValue(Resource.success(genres)),
                t -> genresResource.setValue(Resource.error(t))));
    }

}
