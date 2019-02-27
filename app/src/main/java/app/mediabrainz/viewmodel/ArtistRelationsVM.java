package app.mediabrainz.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;


public class ArtistRelationsVM extends CompositeDisposableViewModel {

    public final MutableLiveData<List<Relation>> relationsld = new MutableLiveData<>();

}
