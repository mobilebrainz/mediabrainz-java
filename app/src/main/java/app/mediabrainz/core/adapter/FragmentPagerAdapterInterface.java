package app.mediabrainz.core.adapter;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;


public interface FragmentPagerAdapterInterface {

    void setupTabViews(TabLayout tabLayout);

    Fragment getFragment(int position);

    void updateFragments();
}
