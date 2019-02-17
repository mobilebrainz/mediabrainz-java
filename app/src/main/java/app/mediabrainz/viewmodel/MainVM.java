package app.mediabrainz.viewmodel;

import android.text.TextUtils;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;
import app.mediabrainz.core.viewmodel.event.Status;

import static app.mediabrainz.MediaBrainzApp.api;


public class MainVM extends CompositeDisposableViewModel {

    private String artistMbid;
    public final MutableLiveData<Resource<List<String>>> genresResource = new MutableLiveData<>();

    public final MutableLiveData<List<String>> genresld = new MutableLiveData<>();

    public final SingleLiveEvent<Boolean> hasArtist = new SingleLiveEvent<>();

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

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        if (!TextUtils.isEmpty(artistMbid)) {
            if (this.artistMbid == null) {
                hasArtist.setValue(true);
            }
            this.artistMbid = artistMbid;
        }
    }
}
