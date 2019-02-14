package app.mediabrainz.viewmodels;

import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginVM extends CompositeDisposableViewModel {

    public final SingleLiveEvent<Resource<Boolean>> authorized = new SingleLiveEvent<>();

    public void authorize(String username, String password) {
        authorized.setValue(Resource.loading());
        dispose(oauth.authorize(username, password,
                () -> authorized.setValue(Resource.success(true)),
                t -> authorized.setValue(Resource.error(t))));
    }

}
