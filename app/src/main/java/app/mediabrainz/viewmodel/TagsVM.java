package app.mediabrainz.viewmodel;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.interfaces.GetTagsInterface;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.api.model.ReleaseGroup.PrimaryType.ALBUM;
import static app.mediabrainz.api.model.ReleaseGroup.SecondaryType.NOTHING;


public class TagsVM extends CompositeDisposableViewModel {

    private List<Tag> itemtags = new ArrayList<>();
    private List<Tag> userItemTags = new ArrayList<>();
    private List<Tag> itemGenres = new ArrayList<>();
    private List<Tag> userItemGenres = new ArrayList<>();

    public final SingleLiveEvent<Boolean> postArtistTagEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Artist> artistTags = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> propagateEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> errorTagld = new SingleLiveEvent<>();


    public void setTags(GetTagsInterface getTagsInterface) {
        setTags(getTagsInterface.getTags(),
                getTagsInterface.getUserTags(),
                getTagsInterface.getGenres(),
                getTagsInterface.getUserGenres());
    }

    private void setTags(List<Tag> tags, List<Tag> userTags, List<Tag> genres, List<Tag> userGenres) {
        if (tags != null && genres != null)
        for (Tag tag : tags) {
            if (!genres.contains(tag)) {
                itemtags.add(tag);
            }
        }
        if (userTags != null && userGenres != null) {
            for (Tag tag : userTags) {
                if (!userGenres.contains(tag)) {
                    userItemTags.add(tag);
                }
            }
        }
        if (genres != null) {
            itemGenres = genres;
        }
        if (userGenres != null) {
            userItemGenres = userGenres;
        }
    }

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
        return itemtags;
    }

    public void setItemtags(List<Tag> itemtags) {
        this.itemtags = itemtags;
    }

    public List<Tag> getUserItemTags() {
        return userItemTags;
    }

    public void setUserItemTags(List<Tag> userItemTags) {
        this.userItemTags = userItemTags;
    }

    public List<Tag> getItemGenres() {
        return itemGenres;
    }

    public void setItemGenres(List<Tag> itemGenres) {
        this.itemGenres = itemGenres;
    }

    public List<Tag> getUserItemGenres() {
        return userItemGenres;
    }

    public void setUserItemGenres(List<Tag> userItemGenres) {
        this.userItemGenres = userItemGenres;
    }
}
