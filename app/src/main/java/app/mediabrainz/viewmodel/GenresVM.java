package app.mediabrainz.viewmodel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class GenresVM extends CompositeDisposableViewModel {

    public final MutableLiveData<List<String>> genresld = new MutableLiveData<>();

    public List<String> getGenres() {
        if (genresld.getValue() == null) {
            initLoading();
            dispose(api.getGenres(
                    genres -> {
                        genresld.setValue(genres != null ? genres : new ArrayList<>());
                        progressld.setValue(false);
                    },
                    this::setError));
            return null;
        }
        return genresld.getValue();
    }

}
