package app.mediabrainz.data.room.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import app.mediabrainz.data.room.entity.Suggestion;


@Dao
public interface SuggestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Suggestion... suggestions);

    @Query("DELETE FROM suggestions")
    void deleteAll();

    @Query("SELECT * from suggestions WHERE word LIKE :word AND field = :field")
    List<Suggestion> findSuggestionsByWordAndField(String word, String field);

}
