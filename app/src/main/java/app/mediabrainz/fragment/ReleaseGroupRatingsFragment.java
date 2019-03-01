package app.mediabrainz.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.api.externalResources.lastfm.model.Album;
import app.mediabrainz.api.externalResources.lastfm.model.LastfmResult;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Rating;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.util.StringFormat;
import app.mediabrainz.viewmodel.LastfmReleaseGroupVM;
import app.mediabrainz.viewmodel.ReleaseGroupRatingsVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class ReleaseGroupRatingsFragment extends BaseFragment {

    public static final String TAG = "RGRatingsFragment";

    private final float LASTFM_ALBUM_LISTENERS_COEFF = 75;
    private final float LASTFM_ALBUM_PLAYCOUNT_COEFF = 300;

    private String artistName;
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
                List<Artist.ArtistCredit> artistCredit = releaseGroup.getArtistCredits();
                if (artistCredit != null && !artistCredit.isEmpty()) {
                    artistName = artistCredit.get(0).getName();
                }
                showAllRating();
                showUserRating();
                observeLastfmInfo();
                setEditListeners();
            }
        });

        releaseGroupRatingsVM.postReleaseGroupRatingEvent.observe(this, a -> {
            if (a != null) {
                showUserRating();
                showAllRating();
            }
        });

        //releaseGroupRatingsVM.progressld.observe(this, this::showProgressLoading);
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

    private void observeLastfmInfo() {
        LastfmReleaseGroupVM vm = getActivityViewModel(LastfmReleaseGroupVM.class);
        vm.lastfmInfold.observe(this, this::showLastfmInfo);
        vm.progressld.observe(this, this::showProgressLoading);
        // todo: тестировать!
        vm.errorld.observe(this, aBoolean -> {
            if (aBoolean) {
                showErrorSnackbar(R.string.lastfm_connection_error, R.string.connection_error_retry,
                        v -> {
                            if (!TextUtils.isEmpty(artistName)) {
                                vm.getReleaseGroupInfo(artistName, releaseGroup.getTitle());
                            }
                        });
            } else {
                dismissErrorSnackbar();
            }
        });
        if (!TextUtils.isEmpty(artistName)) {
            showLastfmInfo(vm.getReleaseGroupInfo(artistName, releaseGroup.getTitle()));
        }
    }

    private void showLastfmInfo(LastfmResult info) {
        if (info != null) {
            Album album = info.getAlbum();
            if (album != null) {
                lastfmPlaycountTableRow.setVisibility(View.VISIBLE);
                lastfmListenersTableRow.setVisibility(View.VISIBLE);

                int listeners = album.getListeners();
                int playCount = album.getPlaycount();

                lastfmListenersView.setText(StringFormat.decimalFormat(listeners));
                lastfmPlaycountView.setText(StringFormat.decimalFormat(playCount));

                lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_ALBUM_LISTENERS_COEFF);
                lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_ALBUM_PLAYCOUNT_COEFF);
            } else {
                lastfmPlaycountTableRow.setVisibility(View.GONE);
                lastfmListenersTableRow.setVisibility(View.GONE);
            }
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
