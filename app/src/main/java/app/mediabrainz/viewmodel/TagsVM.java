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

    private List<Tag> tags = new ArrayList<>();
    private List<Tag> userTags = new ArrayList<>();
    private List<Tag> genres = new ArrayList<>();
    private List<Tag> userGenres = new ArrayList<>();

    public final MutableLiveData<List<String>> genresld = new MutableLiveData<>();
    public final SingleLiveEvent<Boolean> postArtistTagEvent = new SingleLiveEvent<>();
    public final SingleLiveEvent<Artist> artistTags = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> propagateEvent = new SingleLiveEvent<>();

    public void postArtistTag(String artistId, String tag, UserTagXML.VoteType voteType, boolean propagateTagToAlbums) {
        initLoading();
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
                this::setError));
    }

    private void propagateTagToAlbums(String artistId, String tag, UserTagXML.VoteType voteType) {
        initLoading();
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
                    //setError(t);
                },
                100, 0, ALBUM, NOTHING));
    }

    private void loadArtistTags(String artistId) {
        initLoading();
        dispose(api.getArtistTags(artistId,
                artist -> {
                    progressld.setValue(false);
                    artistTags.setValue(artist);
                },
                this::setError));
    }

    private void postTagToReleaseGroups(String tag, UserTagXML.VoteType voteType, List<ReleaseGroup> releaseGroups) {
        initLoading();
        dispose(api.postTagToReleaseGroups(tag, voteType, releaseGroups,
                metadata -> {
                    progressld.setValue(false);
                    propagateEvent.setValue(metadata.getMessage().getText().equals("OK"));
                },
                t -> {
                    progressld.setValue(false);
                    propagateEvent.setValue(false);
                    //setError(t);
                }));
    }

    public void loadGenres() {
        if (genresld.getValue() == null) {
            initLoading();
            dispose(api.getGenres(
                    genres -> {
                        genresld.setValue(genres != null ? genres : new ArrayList<>());
                        progressld.setValue(false);
                    },
                    this::setError));
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public List<Tag> getGenres() {
        return genres;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    public List<Tag> getUserGenres() {
        return userGenres;
    }

    public void setUserGenres(List<Tag> userGenres) {
        this.userGenres = userGenres;
    }
}
