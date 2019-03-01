package app.mediabrainz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class LastfmReleaseGroupVM extends CompositeDisposableViewModel {

    private String artistName;
    private String releseGroupName;

    public final MutableLiveData<LastfmResult> lastfmInfold = new MutableLiveData<>();

    public LastfmResult getReleaseGroupInfo(String artistName, String releseGroupName) {
        if (lastfmInfold.getValue() == null ||
                !(artistName.equals(this.artistName) && releseGroupName.equals(this.releseGroupName))) {
            initLoading();
            dispose(api.getAlbumFromLastfm(artistName, releseGroupName,
                    result -> {
                        this.artistName = artistName;
                        this.releseGroupName = releseGroupName;
                        lastfmInfold.setValue(result);
                        progressld.setValue(false);
                    },
                    this::setError));
            return null;
        }
        return lastfmInfold.getValue();
    }

}
