package app.mobilebrainz.mediabrainz.data.room.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import app.mobilebrainz.mediabrainz.data.room.entity.User;


@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Query("DELETE FROM users")
    void deleteAll();

    @Delete
    void deleteUser(User... users);

    @Query("SELECT * from users WHERE name = :username")
    User findUser(String username);

    @Query("SELECT * from users ORDER BY name ASC")
    List<User> getAllUsers();

}
