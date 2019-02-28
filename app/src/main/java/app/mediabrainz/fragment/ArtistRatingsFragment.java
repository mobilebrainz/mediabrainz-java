package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.util.StringFormat;
import app.mediabrainz.viewmodel.ArtistRatingsVM;
import app.mediabrainz.viewmodel.LastfmVM;

import static app.mediabrainz.MediaBrainzApp.api;
import static app.mediabrainz.MediaBrainzApp.oauth;


public class ArtistRatingsFragment extends BaseFragment {

    private final float LASTFM_ARTIST_LISTENERS_COEFF = 185;
    private final float LASTFM_ARTIST_PLAYCOUNT_COEFF = 950;

    private Artist artist;
    private ArtistRatingsVM artistRatingsVM;
    private LastfmVM lastfmVM;
    private float mrating;

    private View contentView;
    private View progressView;
    private TextView loginWarningView;
    private TextView allRatingView;
    private RatingBar userRatingBar;
    private TableLayout ratingsTableView;
    private TextView lastfmListenersView;
    private TextView lastfmPlaycountView;
    private RatingBar lastfmListenersRatingBar;
    private RatingBar lastfmPlaycountRatingBar;

    public static ArtistRatingsFragment newInstance() {
        return new ArtistRatingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflate(R.layout.artist_ratings_fragment, container);

        contentView = layout.findViewById(R.id.contentView);
        progressView = layout.findViewById(R.id.progressView);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        allRatingView = layout.findViewById(R.id.allRatingView);
        userRatingBar = layout.findViewById(R.id.userRatingBar);
        ratingsTableView = layout.findViewById(R.id.ratingsTableView);
        lastfmListenersView = layout.findViewById(R.id.lastfmListenersView);
        lastfmPlaycountView = layout.findViewById(R.id.lastfmPlaycountView);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfmListenersRatingBar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfmPlaycountRatingBar);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            artistRatingsVM = getActivityViewModel(ArtistRatingsVM.class);
            observeArtistRatingsVM();
        }
    }

    private void observeArtistRatingsVM() {
        artistRatingsVM.artist.observe(this, artist -> {
            if (artist != null) {
                this.artist = artist;
                showUserRating();
                showAllRating();
                observeLastfmInfo();
                setEditListeners();
            }
        });

        artistRatingsVM.postArtistRatingEvent.observe(this, a -> {
            if (a != null) {
                showUserRating();
                showAllRating();
            }
        });

        artistRatingsVM.progressld.observe(this, this::showProgressLoading);
        artistRatingsVM.errorld.observe(this, aBoolean -> {
            if (aBoolean) {
                showErrorSnackbar(R.string.error_post_rating, R.string.connection_error_retry,
                        v -> artistRatingsVM.postRating(mrating));
            } else {
                dismissErrorSnackbar();
            }
        });
    }

    private void setEditListeners() {
        userRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (progressView.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                if (fromUser) {
                    mrating = rating;
                    artistRatingsVM.postRating(rating);
                }
            } else {
                navigate(R.id.action_global_loginFragment);
            }
        });
    }

    private void showUserRating() {
        Rating rating = artist.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingBar.setRating(r);
        }
    }

    private void showAllRating() {
        Rating rating = artist.getRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r != null) {
                Integer votesCount = rating.getVotesCount();
                allRatingView.setText(getString(R.string.rating_text, r, votesCount));
            } else {
                allRatingView.setText(getString(R.string.rating_text, 0.0, 0));
            }
        }
    }

    private void observeLastfmInfo() {
        lastfmVM = getActivityViewModel(LastfmVM.class);
        lastfmVM.lastfmInfold.observe(this, this::showLastfmInfo);
        lastfmVM.progressld.observe(this, this::showProgressLoading);
        // todo: тестировать!
        lastfmVM.errorld.observe(this, aBoolean -> {
            ratingsTableView.setVisibility(View.GONE);
            if (aBoolean) {
                showErrorSnackbar(R.string.lastfm_connection_error, R.string.connection_error_retry,
                        v -> lastfmVM.getLastfmInfo(artist.getName()));
            } else {
                dismissErrorSnackbar();
            }
        });
        showLastfmInfo(lastfmVM.getLastfmInfo(artist.getName()));
    }

    private void showLastfmInfo(LastfmResult info) {
        if (info != null) {
            ratingsTableView.setVisibility(View.VISIBLE);
            int listeners = info.getArtist().getStats().getListeners();
            int playCount = info.getArtist().getStats().getPlaycount();

            lastfmListenersView.setText(StringFormat.decimalFormat(listeners));
            lastfmPlaycountView.setText(StringFormat.decimalFormat(playCount));

            lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_ARTIST_LISTENERS_COEFF);
            lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_ARTIST_PLAYCOUNT_COEFF);
        }
    }

    private void showProgressLoading(boolean show) {
        if (show) {
            contentView.setAlpha(0.3F);
            userRatingBar.setIsIndicator(true);
            progressView.setVisibility(View.VISIBLE);
        } else {
            contentView.setAlpha(1.0F);
            userRatingBar.setIsIndicator(false);
            progressView.setVisibility(View.GONE);
        }
    }

}
