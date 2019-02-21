package app.mediabrainz.fragment;

import android.os.Bundle;
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

    private ViewPager pagerView;
    private TabLayout tabsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_pager_without_icons, container);

        pagerView = view.findViewById(R.id.pagerView);
        tabsView = view.findViewById(R.id.tabsView);
        progressView = view.findViewById(R.id.progressView);

        return view;
    }

    @Override
    protected void show(Artist artist) {
        if (artist.getReleaseGroups() != null && !artist.getReleaseGroups().isEmpty()) {
            ReleaseGroupsPagerAdapter pagerAdapter = new ReleaseGroupsPagerAdapter(getChildFragmentManager(), getResources());
            pagerView.setAdapter(pagerAdapter);
            pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
            tabsView.setupWithViewPager(pagerView);
            pagerAdapter.setupTabViews(tabsView);
        } else {
            snackbarNotAction(pagerView, R.string.no_results);
        }
    }

}
