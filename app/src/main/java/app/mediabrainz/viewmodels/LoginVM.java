package app.mediabrainz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.BaseViewModel;
import app.mediabrainz.core.viewmodel.event.Resource;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginVM extends BaseViewModel {

    public final MutableLiveData<Resource<Boolean>> authorized = new MutableLiveData<>();

    public void authorize(String username, String password) {
        authorized.setValue(Resource.loading());
        addDisposable(oauth.authorize(username, password,
                () -> authorized.setValue(Resource.success(true)),
                t -> authorized.setValue(Resource.error(t))));
    }

}
