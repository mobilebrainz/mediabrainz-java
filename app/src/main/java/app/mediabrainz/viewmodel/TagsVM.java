package app.mediabrainz.viewmodel;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.interfaces.GetTagsInterface;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.event.SingleLiveEvent;


public class TagsVM extends CompositeDisposableViewModel {

    public static class TagVote {
        private String tag;
        private UserTagXML.VoteType voteType;

        public TagVote(String tag, UserTagXML.VoteType voteType) {
            this.tag = tag;
            this.voteType = voteType;
        }

        public String getTag() {
            return tag;
        }

        public UserTagXML.VoteType getVoteType() {
            return voteType;
        }
    }

    public SingleLiveEvent<TagVote> postTag = new SingleLiveEvent<>();

    private final List<Tag> itemTags = new ArrayList<>();
    private final List<Tag> userItemTags = new ArrayList<>();
    private final List<Tag> itemGenres = new ArrayList<>();
    private final List<Tag> userItemGenres = new ArrayList<>();

    public void setTags(GetTagsInterface getTagsInterface) {
        setTags(getTagsInterface.getTags(),
                getTagsInterface.getUserTags(),
                getTagsInterface.getGenres(),
                getTagsInterface.getUserGenres());
    }

    private void setTags(List<Tag> tags, List<Tag> userTags, List<Tag> genres, List<Tag> userGenres) {
        itemTags.clear();
        userItemTags.clear();
        itemGenres.clear();
        userItemGenres.clear();

        if (tags != null && genres != null)
        for (Tag tag : tags) {
            if (!genres.contains(tag)) {
                itemTags.add(tag);
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
            itemGenres.addAll(genres);
        }
        if (userGenres != null) {
            userItemGenres.addAll(userGenres);
        }
    }

    public List<Tag> getItemTags() {
        return itemTags;
    }

    public List<Tag> getUserItemTags() {
        return userItemTags;
    }

    public List<Tag> getItemGenres() {
        return itemGenres;
    }

    public List<Tag> getUserItemGenres() {
        return userItemGenres;
    }

}
