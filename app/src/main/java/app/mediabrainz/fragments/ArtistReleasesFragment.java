package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.oauth.OAuthException;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodels.ArtistVM;
import app.mediabrainz.viewmodels.MainVM;


public class ArtistReleasesFragment extends BaseFragment {

    private static final String TAG = "ArtistReleasesF";

    private ArtistVM artistVM;
    private MainVM mainVM;

    private String mbid;
    private boolean isLoading;
    private boolean isError;

    private ViewPager pagerView;
    private TabLayout tabsView;
    private View errorView;
    private View progressView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_pager_without_icons, container);

        pagerView = view.findViewById(R.id.pagerView);
        tabsView = view.findViewById(R.id.tabsView);
        errorView = view.findViewById(R.id.errorView);
        progressView = view.findViewById(R.id.progressView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            mainVM = getActivityViewModel(MainVM.class);
            if (!TextUtils.isEmpty(mbid = mainVM.getArtistMbid())) {
                artistVM = getActivityViewModel(ArtistVM.class);

                artistVM.artistResource.observe(this,resource -> {
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
                                show();
                            }
                            break;
                    }
                });
                load();
            }
        }
    }

    private void load() {
        viewError(false);
        viewProgressLoading(false);

        artistVM.getArtist(mbid);

        // todo: сделать принудительный рефрешинг артиста?
        // artistVM.loadArtist(mbid);
    }

    private void show() {
        ReleaseGroupsPagerAdapter pagerAdapter = new ReleaseGroupsPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        pagerAdapter.setupTabViews(tabsView);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            progressView.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

}
