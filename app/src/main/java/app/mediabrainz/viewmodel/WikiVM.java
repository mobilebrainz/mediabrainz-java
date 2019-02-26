package app.mediabrainz.viewmodel;

import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class WikiVM extends CompositeDisposableViewModel {

    public final MutableLiveData<Map<String, String>> urlMap = new MutableLiveData<>();

    public void getUrlMap(String wikidataQ, String lang) {
        if (urlMap.getValue() == null) {
            initLoading();
            api.getSitelinks(wikidataQ,
                    map -> {
                        if (map == null || map.isEmpty()) {
                            noresultsld.setValue(true);
                        } else {
                            urlMap.setValue(map);
                        }
                        progressld.setValue(false);
                    },
                    this::setError,
                    lang);
        }
    }

}
