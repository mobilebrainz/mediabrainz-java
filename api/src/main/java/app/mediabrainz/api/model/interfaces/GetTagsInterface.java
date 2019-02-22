package app.mediabrainz.api.model.interfaces;


import java.util.List;

import app.mediabrainz.api.model.Tag;


public interface GetTagsInterface {

    List<Tag> getTags();

    List<Tag> getGenres();

    List<Tag> getUserGenres();

    List<Tag> getUserTags();

}
