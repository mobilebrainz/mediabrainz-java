package app.mediabrainz.viewmodel;

import app.mediabrainz.api.oauth.OAuthException;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginVM extends CompositeDisposableViewModel {

    public final SingleLiveEvent<Boolean> authEvent = new SingleLiveEvent<>();

    public void authorize(String username, String password) {
        initLoading();
        dispose(oauth.authorize(username, password,
                () -> {
                    progressld.setValue(false);
                    authEvent.setValue(true);
                },
                t -> {
                    progressld.setValue(false);
                    if (t != null && t.equals(OAuthException.INVALID_AUTENTICATION_ERROR)) {
                        throwableld.setValue(t);
                    } else {
                        errorld.setValue(true);
                    }
                }));
    }

}
