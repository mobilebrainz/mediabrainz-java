package app.mediabrainz.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;


public class ArtistVM extends CompositeDisposableViewModel {

    private String artistMbid;
    public final SingleLiveEvent<Boolean> hasArtist = new SingleLiveEvent<>();
    public final MutableLiveData<Artist> artistld = new MutableLiveData<>();

    public void loadArtist() {
        if (artistld.getValue() == null) {
            refreshArtist();
        }
    }

    public void refreshArtist() {
        if (!TextUtils.isEmpty(artistMbid)) {
            initLoading();
            dispose(api.getArtist(artistMbid,
                    artist -> {
                        progressld.setValue(false);
                        if (artist != null) {
                            artistld.setValue(artist);
                        } else {
                            noresultsld.setValue(true);
                        }
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
