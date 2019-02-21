package app.mediabrainz.data.model;


import androidx.annotation.NonNull;

public class Artist {

    @NonNull
    private String mbid;

    @NonNull
    private String name;

    public Artist(@NonNull String mbid, @NonNull String name) {
        this.mbid = mbid;
        this.name = name;
    }

    public String getMbid() {
        return mbid;
    }

    public String getName() {
        return name;
    }

}
