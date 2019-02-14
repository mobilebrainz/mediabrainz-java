package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodels.ArtistVM;
import app.mediabrainz.viewmodels.MainVM;


public abstract class BaseArtistFragment extends BaseFragment {

    protected ArtistVM artistVM;
    protected String mbid;

    protected boolean isLoading;
    protected boolean isError;

    protected View errorView;
    protected View progressView;
    protected View noresultsView;

    protected abstract void show(Artist artist);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArtistVM();
    }

    private void initArtistVM() {
        if (getActivity() != null && !TextUtils.isEmpty(mbid = getActivityViewModel(MainVM.class).getArtistMbid())) {
            artistVM = getActivityViewModel(ArtistVM.class);
            artistVM.artistResource.observe(this, resource -> {
                if (resource == null) return;
                switch (resource.getStatus()) {
                    case LOADING:
                        viewProgressLoading(true);
                        break;
                    case ERROR:
                        showConnectionWarning(resource.getThrowable());
                        break;
                    case SUCCESS:
                        viewProgressLoading(false);
                        Artist artist = resource.getData();
                        if (artist != null) {
                            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                            if (actionBar != null) {
                                actionBar.setSubtitle(artist.getName());
                            }
                            show(artist);
                        }
                        break;
                }
            });
            load();
        }
    }

    protected void load() {
        viewError(false);
        viewProgressLoading(false);
        noresultsView.setVisibility(View.GONE);

        artistVM.getArtist(mbid);

        // todo: сделать принудительный рефрешинг артиста?
        // artistVM.loadArtist(mbid);
    }

    protected void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    protected void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

    protected void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

}
