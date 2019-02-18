package app.mediabrainz.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;

import static app.mediabrainz.MediaBrainzApp.api;


public class MainVM extends CompositeDisposableViewModel {

    public final MutableLiveData<Resource<List<String>>> genresResource = new MutableLiveData<>();

    public final MutableLiveData<List<String>> genresld = new MutableLiveData<>();

    public void loadGenres() {
        if (genresld.getValue() == null) {
            initLoading();
            dispose(api.getGenres(
                    genres -> {
                        progressld.setValue(false);
                        genresld.setValue(genres);
                    },
                    this::setError));
        }
    }

}
