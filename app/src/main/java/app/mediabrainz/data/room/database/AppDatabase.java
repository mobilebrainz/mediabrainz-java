package app.mediabrainz.data.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import app.mediabrainz.data.room.dao.RecommendDao;
import app.mediabrainz.data.room.dao.SuggestionDao;
import app.mediabrainz.data.room.dao.UserDao;
import app.mediabrainz.data.room.entity.Recommend;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.data.room.entity.User;


@Database(entities = {Recommend.class, Suggestion.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "mediabrainzdb";

    public abstract RecommendDao recommendDao();

    public abstract SuggestionDao suggestionDao();

    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

}
