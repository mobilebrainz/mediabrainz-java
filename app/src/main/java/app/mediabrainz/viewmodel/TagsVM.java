package app.mediabrainz.viewmodel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.NOTHING;


public class TagsVM extends CompositeDisposableViewModel {

    private List<Tag> Itemtags = new ArrayList<>();
    private List<Tag> userItemTags = new ArrayList<>();
    private List<Tag> ItemGenres = new ArrayList<>();
    private List<Tag> userItemGenres = new ArrayList<>();

    public final SingleLiveEvent<Boolean> postArtistTagEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Artist> artistTags = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> propagateEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> errorTagld = new SingleLiveEvent<>();

    private void setTagError(Throwable t) {
        progressld.setValue(false);
        errorTagld.setValue(true);
    }

    public void postArtistTag(String artistId, String tag, UserTagXML.VoteType voteType, boolean propagateTagToAlbums) {
        progressld.setValue(true);
        dispose(api.postArtistTag(artistId, tag, voteType,
                metadata -> {
                    progressld.setValue(false);
                    boolean ok = metadata.getMessage().getText().equals("OK");
                    postArtistTagEvent.setValue(ok);
                    if (ok) {
                        loadArtistTags(artistId);
                    }
                    if (ok && propagateTagToAlbums) {
                        propagateTagToAlbums(artistId, tag, voteType);
                    }
                },
                this::setTagError));
    }

    private void propagateTagToAlbums(String artistId, String tag, UserTagXML.VoteType voteType) {
        progressld.setValue(true);
        dispose(api.searchOfficialReleaseGroups(artistId,
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

    private void loadArtistTags(String artistId) {
        progressld.setValue(true);
        dispose(api.getArtistTags(artistId,
                artist -> {
                    progressld.setValue(false);
                    artistTags.setValue(artist);
                },
                this::setTagError));
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

    public List<Tag> getItemtags() {
        return Itemtags;
    }

    public void setItemtags(List<Tag> itemtags) {
        this.Itemtags = itemtags;
    }

    public List<Tag> getUserItemTags() {
        return userItemTags;
    }

    public void setUserItemTags(List<Tag> userItemTags) {
        this.userItemTags = userItemTags;
    }

    public List<Tag> getItemGenres() {
        return ItemGenres;
    }

    public void setItemGenres(List<Tag> itemGenres) {
        this.ItemGenres = itemGenres;
    }

    public List<Tag> getUserItemGenres() {
        return userItemGenres;
    }

    public void setUserItemGenres(List<Tag> userItemGenres) {
        this.userItemGenres = userItemGenres;
    }
}
