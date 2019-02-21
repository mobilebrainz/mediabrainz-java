package app.mediabrainz.data.datamapper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.data.model.Artist;
import app.mediabrainz.api.model.Artist.ArtistSearch;


public class ArtistMapper {

    @NonNull
    public Artist convertArtistFromApi(@NonNull app.mediabrainz.api.model.Artist apiArtist) {
        Artist artist = new Artist(apiArtist.getId(), apiArtist.getName());

        return artist;
    }

    @NonNull
    public List<Artist> convertArtistsFromApi(@Nullable ArtistSearch artistSearch) {
        final List<app.mediabrainz.data.model.Artist> artists = new ArrayList<>();
        if (artistSearch != null && artistSearch.getCount() != 0) {
            for (app.mediabrainz.api.model.Artist apiArtist : artistSearch.getArtists()) {
                artists.add(convertArtistFromApi(apiArtist));
            }
        }
        return artists;
    }

    /*
    public static app.mediabrainz.api.model.Artist convertArtistToApi(Artist artist) {

    }
    */
}
