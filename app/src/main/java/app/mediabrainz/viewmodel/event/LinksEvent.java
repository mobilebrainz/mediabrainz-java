package app.mediabrainz.viewmodel.event;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;


public class LinksEvent extends CompositeDisposableViewModel {

    public final SingleLiveEvent<List<Url>> urls = new SingleLiveEvent<>();

}
