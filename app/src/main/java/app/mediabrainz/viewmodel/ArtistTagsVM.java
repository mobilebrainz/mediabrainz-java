package app.mediabrainz.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.NOTHING;


public class ArtistTagsVM extends CompositeDisposableViewModel {

    public final MutableLiveData<Artist> artistld = new MutableLiveData<>();

    public final SingleLiveEvent<Boolean> postArtistTagEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Artist> artistTags = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> propagateEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> errorTagld = new SingleLiveEvent<>();

    private void setTagError(Throwable t) {
        progressld.setValue(false);
        errorTagld.setValue(true);
    }

    public void postArtistTag(String tag, UserTagXML.VoteType voteType, boolean propagateTagToAlbums) {
        if (artistld.getValue() != null) {
            progressld.setValue(true);
            dispose(api.postArtistTag(artistld.getValue().getId(), tag, voteType,
                    metadata -> {
                        progressld.setValue(false);
                        boolean ok = metadata.getMessage().getText().equals("OK");
                        postArtistTagEvent.setValue(ok);
                        if (ok) {
                            loadArtistTags();
                        }
                        if (ok && propagateTagToAlbums) {
                            propagateTagToAlbums(tag, voteType);
                        }
                    },
                    this::setTagError));
        }
    }

    private void propagateTagToAlbums(String tag, UserTagXML.VoteType voteType) {
        if (artistld.getValue() != null) {
            progressld.setValue(true);
            dispose(api.searchOfficialReleaseGroups(artistld.getValue().getId(),
                    releaseGroupSearch -> {
                        progressld.setValue(false);
                        if (releaseGroupSearch.getCount() > 0) {
                            postTagToReleaseGroups(tag, voteType, releaseGroupSearch.getReleaseGroups());
                        }
                    },
                    t -> {
                        progressld.setValue(false);
                        propagateEvent.setValue(false);
                    },
                    100, 0, ALBUM, NOTHING));
        }
    }

    private void loadArtistTags() {
        if (artistld.getValue() != null) {
            progressld.setValue(true);
            dispose(api.getArtistTags(artistld.getValue().getId(),
                    artist -> {
                        progressld.setValue(false);
                        artistTags.setValue(artist);
                    },
                    this::setTagError));
        }
    }

    private void postTagToReleaseGroups(String tag, UserTagXML.VoteType voteType, List<ReleaseGroup> releaseGroups) {
        progressld.setValue(true);
        dispose(api.postTagToReleaseGroups(tag, voteType, releaseGroups,
                metadata -> {
                    progressld.setValue(false);
                    propagateEvent.setValue(metadata.getMessage().getText().equals("OK"));
                },
                t -> {
                    progressld.setValue(false);
                    propagateEvent.setValue(false);
                }));
    }

}
