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
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.ReleaseGroupRatingsVM;
import app.mediabrainz.viewmodel.StartVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class ReleaseGroupRatingsFragment extends BaseFragment {

    public static final String TAG = "RGRatingsFragment";

    private final float LASTFM_ALBUM_LISTENERS_COEFF = 75;
    private final float LASTFM_ALBUM_PLAYCOUNT_COEFF = 300;

    private ReleaseGroup releaseGroup;
    private ReleaseGroupRatingsVM releaseGroupRatingsVM;
    private float mrating;

    private View contentView;
    private View progressView;
    private TextView loginWarningView;
    private TextView allRatingView;
    private RatingBar userRatingBar;

    private TableLayout ratingsTableView;
    private View lastfmPlaycountTableRow;
    private View lastfmListenersTableRow;
    private TextView lastfmListenersView;
    private TextView lastfmPlaycountView;
    private RatingBar lastfmListenersRatingBar;
    private RatingBar lastfmPlaycountRatingBar;

    private View rateyourmusicTableRow;
    private RatingBar rateyourmusicRatingBar;
    private TextView rateyourmusicNumberView;
    private View rateyourmusicProgressBar;

    private View progarchivesTableRow;
    private RatingBar progarchivesRatingBar;
    private TextView progarchivesNumberView;
    private View progarchivesProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflate(R.layout.release_group_ratings_fragment, container);

        contentView = layout.findViewById(R.id.contentView);
        progressView = layout.findViewById(R.id.progressView);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        allRatingView = layout.findViewById(R.id.allRatingView);
        userRatingBar = layout.findViewById(R.id.userRatingBar);

        ratingsTableView = layout.findViewById(R.id.ratingsTableView);
        lastfmPlaycountTableRow = layout.findViewById(R.id.lastfmPlaycountTableRow);
        lastfmListenersTableRow = layout.findViewById(R.id.lastfmListenersTableRow);
        lastfmListenersView = layout.findViewById(R.id.lastfmListenersView);
        lastfmPlaycountView = layout.findViewById(R.id.lastfmPlaycountView);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfmListenersRatingBar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfmPlaycountRatingBar);

        rateyourmusicTableRow = layout.findViewById(R.id.rateyourmusicTableRow);
        rateyourmusicRatingBar = layout.findViewById(R.id.rateyourmusicRatingBar);
        rateyourmusicNumberView = layout.findViewById(R.id.rateyourmusicNumberView);
        rateyourmusicProgressBar = layout.findViewById(R.id.rateyourmusicProgressBar);

        progarchivesTableRow = layout.findViewById(R.id.progarchivesTableRow);
        progarchivesRatingBar = layout.findViewById(R.id.progarchivesRatingBar);
        progarchivesNumberView = layout.findViewById(R.id.progarchivesNumberView);
        progarchivesProgressBar = layout.findViewById(R.id.progarchivesProgressBar);

        return layout;
    }

    public static ReleaseGroupRatingsFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseGroupRatingsFragment fragment = new ReleaseGroupRatingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setSubtitle(null);
        if (getActivity() != null) {
            releaseGroupRatingsVM = getActivityViewModel(ReleaseGroupRatingsVM.class);
            observeReleaseGroupRatingsVM();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void observeReleaseGroupRatingsVM() {
        releaseGroupRatingsVM.releaseGroupld.observe(this, rg -> {
            if (rg != null) {
                releaseGroup = rg;
                showAllRating();
                showUserRating();
                setEditListeners();
            }
        });

        releaseGroupRatingsVM.postReleaseGroupRatingEvent.observe(this, a -> {
            if (a != null) {
                showUserRating();
                showAllRating();
            }
        });

        releaseGroupRatingsVM.progressld.observe(this, this::showProgressLoading);
        releaseGroupRatingsVM.errorld.observe(this, aBoolean -> {
            if (aBoolean) {
                showErrorSnackbar(R.string.error_post_rating, R.string.connection_error_retry,
                        v -> releaseGroupRatingsVM.postRating(mrating));
            } else {
                dismissErrorSnackbar();
            }
        });
    }

    private void showAllRating() {
        Rating rating = releaseGroup.getRating();
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

    private void showUserRating() {
        Rating rating = releaseGroup.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingBar.setRating(r);
        }
    }

    private void setEditListeners() {
        userRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (progressView.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                if (fromUser) {
                    mrating = rating;
                    releaseGroupRatingsVM.postRating(rating);
                }
            } else {
                navigate(R.id.action_global_loginFragment);
            }
        });
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
