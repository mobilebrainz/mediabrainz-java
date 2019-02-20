package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.ArtistVM;


public abstract class BaseArtistFragment extends BaseFragment {

    protected ArtistVM artistVM;
    protected boolean isLoading;
    protected boolean isError;

    protected View progressView;
    protected View noresultsView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected abstract void show(Artist artist);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            artistVM = getActivityViewModel(ArtistVM.class);
            observeProgress();
            observeError();
            observeArtist();
            observeNoResult();
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!isLoading) artistVM.refreshArtist();
            });
        }
    }

    private void observeProgress() {
        artistVM.progressld.observe(this, aBoolean -> {
            isLoading = aBoolean;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(aBoolean);
            } else if (progressView != null) {
                progressView.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void observeError() {
        artistVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean && swipeRefreshLayout != null) {
                snackbarWithAction(swipeRefreshLayout, R.string.connection_error, R.string.connection_error_retry,
                        v -> artistVM.refreshArtist());
            } else if (getErrorSnackbar() != null && getErrorSnackbar().isShown()) {
                getErrorSnackbar().dismiss();
            }
        });
    }

    private void observeArtist() {
        artistVM.artistld.observe(this, artist -> {
            if (artist != null && getActivity() != null && getActivity() instanceof AppCompatActivity) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(artist.getName());
                }
                show(artist);
            }
        });
        artistVM.loadArtist();
    }

    private void observeNoResult() {
        artistVM.noresultsld.observe(this, aBoolean -> {
            if (aBoolean && swipeRefreshLayout != null) {
                snackbarNotAction(swipeRefreshLayout, R.string.no_results);
            }
        });
    }

}
