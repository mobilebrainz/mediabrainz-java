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

    protected View errorView;
    protected View progressView;
    protected View noresultsView;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected abstract void show(Artist artist);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArtistVM();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!isLoading) artistVM.refreshArtist();
            });
        }
    }

    private void initArtistVM() {
        if (getActivity() != null) {
            artistVM = getActivityViewModel(ArtistVM.class);
            artistVM.progressld.observe(this, this::showProgressLoading);
            artistVM.errorld.observe(this, this::showError);
            artistVM.artistld.observe(this, artist -> {
                if (artist != null) {
                    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setSubtitle(artist.getName());
                    }
                    show(artist);
                }
            });
            artistVM.loadArtist();
        }
    }

    protected void showProgressLoading(boolean show) {
        if (show) {
            isLoading = true;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            } else if (progressView != null) {
                progressView.setVisibility(View.VISIBLE);
            }
        } else {
            isLoading = false;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            } else if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }
        }
    }

    protected void showError(boolean show) {
        if (show) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
            errorView.findViewById(R.id.retryButton).setOnClickListener(v -> artistVM.refreshArtist());
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

}
