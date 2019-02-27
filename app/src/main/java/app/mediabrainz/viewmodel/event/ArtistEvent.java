package app.mediabrainz.viewmodel.event;

import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;


public class ArtistEvent extends CompositeDisposableViewModel {

    public final SingleLiveEvent<Artist> artist = new SingleLiveEvent<>();

}
