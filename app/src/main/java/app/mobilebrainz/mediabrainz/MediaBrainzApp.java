package app.mobilebrainz.mediabrainz;


import android.accounts.AccountManager;
import android.app.Application;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.Config;
import app.mobilebrainz.mediabrainz.account.OAuth;
import app.mobilebrainz.mediabrainz.account.Preferences;
import app.mobilebrainz.mediabrainz.apihandler.Api;


public class MediaBrainzApp extends Application {

    public static final String SUPPORT_MAIL = "mobilebrainz@gmail.com";
    public static final String YOUTUBE_API_KEY = "AIzaSyBHRfvdBmjm7Fk4BE8exK7QZ85hJLiE8gU";

    public static OAuth oauth;
    public static Api api;
    //public static AppDatabase appDatabase;

    private static MediaBrainzApp instance;
    private static Preferences preferences;

    private static List<String> genres = new ArrayList<>();

    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.setUserAgentHeader(getPackage() + "/" + getVersion() + " (" + SUPPORT_MAIL + ")");

        oauth = new OAuth(AccountManager.get(this));
        api = new Api(oauth);
        preferences = new Preferences();

        //appDatabase = AppDatabase.getDatabase(this);
    }

    public static MediaBrainzApp getContext() {
        return instance;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static String getVersion() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    public static String getPackage() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    public static List<String> getGenres() {
        return genres;
    }

    public static void setGenres(List<String> genres) {
        MediaBrainzApp.genres = genres;
    }
}
