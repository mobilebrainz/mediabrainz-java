package app.mediabrainz.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import app.mediabrainz.viewmodels.event.Resource;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class LoginVM extends ViewModel {

    public final MutableLiveData<Resource<Boolean>> authorized = new MutableLiveData<>();

    public void authorize(String username, String password) {
        authorized.setValue(Resource.loading());
        oauth.authorize(username, password,
                () -> authorized.setValue(Resource.success(true)),
                t -> authorized.setValue(Resource.error(t)));
    }

}
