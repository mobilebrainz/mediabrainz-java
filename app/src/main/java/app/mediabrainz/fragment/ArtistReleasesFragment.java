package app.mediabrainz.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.api.model.Artist;


public class ArtistReleasesFragment extends BaseArtistFragment {

    private static final String TAG = "ArtistReleasesF";
    private static final String RELESES_TAB = "ArtistReleasesFragment.RELESES_TAB";

    private int releaseTab = 0;
    private ViewPager pagerView;
    private TabLayout tabsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_pager_without_icons, container);

        if (savedInstanceState != null) {
            releaseTab = savedInstanceState.getInt(RELESES_TAB, 0);
        }

        pagerView = view.findViewById(R.id.pagerView);
        tabsView = view.findViewById(R.id.tabsView);
        progressView = view.findViewById(R.id.progressView);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RELESES_TAB, releaseTab);
    }


    @Override
    public void onPause() {
        super.onPause();
        releaseTab = pagerView.getCurrentItem();
    }

    @Override
    protected void show(Artist artist) {
        if (artist.getReleaseGroups() != null && !artist.getReleaseGroups().isEmpty()) {
            ReleaseGroupsPagerAdapter pagerAdapter = new ReleaseGroupsPagerAdapter(getChildFragmentManager(), getResources());
            pagerView.setAdapter(pagerAdapter);
            pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
            pagerView.setCurrentItem(releaseTab);
            tabsView.setupWithViewPager(pagerView);
            pagerAdapter.setupTabViews(tabsView);
        } else {
            snackbarNotAction(pagerView, R.string.no_results);
        }
    }

}
