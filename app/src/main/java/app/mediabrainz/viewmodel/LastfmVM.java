package app.mediabrainz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class LastfmVM extends CompositeDisposableViewModel {

    private String artistName;

    public final MutableLiveData<LastfmResult> lastfmInfold = new MutableLiveData<>();

    public LastfmResult getLastfmInfo(String artistName) {
        if (lastfmInfold.getValue() == null || !artistName.equals(this.artistName)) {
            initLoading();
            dispose(api.getArtistFromLastfm(artistName,
                    result -> {
                        this.artistName = artistName;
                        lastfmInfold.setValue(result);
                        progressld.setValue(false);
                    },
                    this::setError));
            return null;
        }
        return lastfmInfold.getValue();
    }
}
