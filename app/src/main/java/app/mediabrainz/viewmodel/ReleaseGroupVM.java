package app.mediabrainz.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;

import static app.mediabrainz.MediaBrainzApp.api;


public class ReleaseGroupVM extends CompositeDisposableViewModel {

    private String releaseMbid;
    private String releaseGroupMbid;
    public final MutableLiveData<Release> releaseld = new MutableLiveData<>();
    public final MutableLiveData<ReleaseGroup> releaseGroupld = new MutableLiveData<>();

    public void getRelease(@NonNull String mbid) {
        //??? убрать проверку, чтобы рефрешился релиз после логгирования, изменения данных и т.д.
        if (releaseld.getValue() == null || !mbid.equals(releaseMbid)) {
            releaseMbid = mbid;
            refreshRelease();
        }
    }

    public void refreshRelease() {
        if (!TextUtils.isEmpty(releaseMbid)) {
            initLoading();
            dispose(api.getRelease(releaseMbid,
                    release -> {
                        progressld.setValue(false);
                        if (release != null) {
                            releaseld.setValue(release);
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    this::setError));
        }
    }

    public void getReleaseGroup(String mbid) {
        //??? убрать проверку, чтобы рефрешился релиз после логгирования, изменения данных и т.д.
        if (releaseGroupld.getValue() == null || !mbid.equals(releaseGroupMbid)) {
            releaseGroupMbid = mbid;
            refreshReleaseGroup();
        }
    }

    public void refreshReleaseGroup() {
        if (!TextUtils.isEmpty(releaseGroupMbid)) {
            initLoading();
            dispose(api.getReleaseGroup(releaseGroupMbid,
                    releaseGroup -> {
                        progressld.setValue(false);
                        if (releaseGroup != null) {
                            releaseGroupld.setValue(releaseGroup);
                        } else {
                            noresultsld.setValue(true);
                        }
                    },
                    this::setError));
        }
    }

    public String getReleaseMbid() {
        return releaseMbid;
    }
}
