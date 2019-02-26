package app.mediabrainz.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;


public class LinksVM extends CompositeDisposableViewModel {

    public final MutableLiveData<List<Url>> urlsld = new MutableLiveData<>();

}
