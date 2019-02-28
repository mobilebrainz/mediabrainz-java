package app.mediabrainz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;


public class ArtistRatingsVM extends CompositeDisposableViewModel {

    public final MutableLiveData<Artist> artist = new MutableLiveData<>();
    public final SingleLiveEvent<Artist> postArtistRatingEvent = new SingleLiveEvent<>();

    public void postRating(float rating) {
        if (artist.getValue() != null) {
            initLoading();
            dispose(api.postArtistRating(artist.getValue().getId(), rating,
                    metadata -> {
                        progressld.setValue(false);
                        if (metadata.getMessage().getText().equals("OK")) {
                            loadArtistRatings();
                        } else {
                            errorld.setValue(true);
                        }
                    },
                    this::setError));
        }
    }

    private void loadArtistRatings() {
        if (artist.getValue() != null) {
            initLoading();
            dispose(api.getArtistRatings(artist.getValue().getId(),
                    a -> {
                        progressld.setValue(false);
                        artist.getValue().setRating(a.getRating());
                        artist.getValue().setUserRating(a.getUserRating());
                        postArtistRatingEvent.setValue(a);
                    },
                    this::setError));
        }
    }

}
