package app.mediabrainz.viewmodel.event;

import java.util.List;

import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;


public class ArtistRelationsEvent extends CompositeDisposableViewModel {

    public final SingleLiveEvent<List<Relation>> relations = new SingleLiveEvent<>();

}
