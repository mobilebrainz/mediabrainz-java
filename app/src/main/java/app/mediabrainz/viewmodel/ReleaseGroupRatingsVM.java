package app.mediabrainz.viewmodel;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseGroupRatingsVM extends CompositeDisposableViewModel {

    public final MutableLiveData<ReleaseGroup> releaseGroupld = new MutableLiveData<>();
    public final SingleLiveEvent<ReleaseGroup> postReleaseGroupRatingEvent = new SingleLiveEvent<>();

    public void postRating(float rating) {
        if (releaseGroupld.getValue() != null) {
            initLoading();
            dispose(api.postAlbumRating(releaseGroupld.getValue().getId(), rating,
                    metadata -> {
                        progressld.setValue(false);
                        if (metadata.getMessage().getText().equals("OK")) {
                            loadReleaseGroupRatings();
                        } else {
                            errorld.setValue(true);
                        }
                    },
                    this::setError));
        }
    }

    private void loadReleaseGroupRatings() {
        if (releaseGroupld.getValue() != null) {
            initLoading();
            dispose(api.getAlbumRatings(releaseGroupld.getValue().getId(),
                    a -> {
                        progressld.setValue(false);
                        releaseGroupld.getValue().setRating(a.getRating());
                        releaseGroupld.getValue().setUserRating(a.getUserRating());
                        postReleaseGroupRatingEvent.setValue(a);
                    },
                    this::setError));
        }
    }

}
