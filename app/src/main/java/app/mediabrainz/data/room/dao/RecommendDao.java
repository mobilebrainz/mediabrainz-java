package app.mediabrainz.data.room.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import app.mediabrainz.data.room.entity.Recommend;


@Dao
public interface RecommendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recommend recommend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecommends(Recommend... recommends);

    @Delete
    void delete(Recommend recommend);

    @Delete
    void deleteRecommends(Recommend... recommends);

    @Query("DELETE FROM recommends")
    void deleteAll();

    @Query("SELECT * from recommends WHERE tag = :tag")
    Recommend findRecommendByTag(String tag);

    @Query("SELECT * from recommends ORDER BY number DESC")
    List<Recommend> getAllRecommends();

}
