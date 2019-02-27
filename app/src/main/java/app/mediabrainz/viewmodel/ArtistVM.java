package app.mediabrainz.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class ArtistVM extends CompositeDisposableViewModel {

    private String artistMbid;
    public final MutableLiveData<Artist> artistld = new MutableLiveData<>();

    public void getArtist(@NonNull String mbid) {
        //??? убрать проверку, чтобы рефрешился артист после логгирования, изменения данных и т.д.
        if (artistld.getValue() == null || !mbid.equals(artistMbid)) {
            artistMbid = mbid;
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

}
