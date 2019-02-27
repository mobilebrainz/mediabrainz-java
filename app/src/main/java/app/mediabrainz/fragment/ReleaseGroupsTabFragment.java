package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupsAdapter;
import app.mediabrainz.core.adapter.RetryCallback;
import app.mediabrainz.core.fragment.LazyFragment;
import app.mediabrainz.core.viewmodel.event.Status;
import app.mediabrainz.viewmodel.ReleaseGroupsVM;

import static app.mediabrainz.account.Preferences.PreferenceName.RELEASE_GROUP_OFFICIAL;


public class ReleaseGroupsTabFragment extends LazyFragment implements
        RetryCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "ReleaseGroupsTabF";
    private static final String RELEASES_TAB = "ReleaseGroupsTabFragment.RELEASES_TAB";
    private static final String ARTIST_MBID = "ReleaseGroupsTabFragment.ARTIST_MBID";
    private static final String ARTIST_NAME = "ReleaseGroupsTabFragment.ARTIST_NAME";

    private boolean isError;
    private boolean isLoading;
    private String artistMbid;
    private ReleaseGroupsPagerAdapter.ReleaseTab releaseGroupType;
    private ReleaseGroupsVM releaseGroupsVM;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;
    private ReleaseGroupsAdapter adapter;

    private MutableLiveData<Boolean> mutableIsOfficial = new MutableLiveData<>();

    public static ReleaseGroupsTabFragment newInstance(int releasesTab, String artistMbid, String artistName) {
        Bundle args = new Bundle();
        args.putInt(RELEASES_TAB, releasesTab);
        args.putString(ARTIST_MBID, artistMbid);
        args.putString(ARTIST_NAME, artistName);
        ReleaseGroupsTabFragment fragment = new ReleaseGroupsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_paged_recycler, container);

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            artistMbid = getArguments().getString(ARTIST_MBID);
            setSubtitle(getArguments().getString(ARTIST_NAME));
            releaseGroupType = ReleaseGroupsPagerAdapter.ReleaseTab.values()[getArguments().getInt(RELEASES_TAB)];
            loadView();
        }
    }

    @Override
    protected void lazyLoad() {
        adapter = new ReleaseGroupsAdapter(this);
        adapter.setHolderClickListener(releaseGroup -> {
            if (!isLoading) {
                ArtistReleasesFragmentDirections.ActionArtistReleasesFragmentToReleasesFragment action =
                        ArtistReleasesFragmentDirections.actionArtistReleasesFragmentToReleasesFragment(releaseGroup.getId(), null);
                navigate(action);
            }
        });

        releaseGroupsVM = getViewModel(ReleaseGroupsVM.class);
        mutableIsOfficial.setValue(MediaBrainzApp.getPreferences().isReleaseGroupOfficial());
        releaseGroupsVM.load(artistMbid, releaseGroupType.getAlbumType(), mutableIsOfficial);
        releaseGroupsVM.realeseGroupLiveData.observe(this, adapter::submitList);
        releaseGroupsVM.getNetworkState().observe(this, adapter::setNetworkState);

        pagedRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        pagedRecyclerView.setNestedScrollingEnabled(true);
        pagedRecyclerView.setItemViewCacheSize(100);
        pagedRecyclerView.setHasFixedSize(true);
        pagedRecyclerView.setAdapter(adapter);

        MediaBrainzApp.getPreferences().registerOnSharedPreferenceChangeListener(this);

        initSwipeToRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        showError(isVisibleToUser);
    }

    private void showError(boolean isVisibleToUser) {
        if (isVisibleToUser && isError) {
            showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> retry());
        } else {
            dismissErrorSnackbar();
        }
    }

    private void initSwipeToRefresh() {
        releaseGroupsVM.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                isError = networkState.getStatus() == Status.ERROR;
                showError(true);
                isLoading = networkState.getStatus() == Status.LOADING;
                swipeRefreshLayout.setRefreshing(isLoading);

                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    pagedRecyclerView.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) {
                releaseGroupsVM.refresh();
                pagedRecyclerView.scrollToPosition(0);
            }
        });
    }

    @Override
    public void retry() {
        if (releaseGroupsVM != null) {
            releaseGroupsVM.retry();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(RELEASE_GROUP_OFFICIAL)) {
            if (!loadView()) {
                pagedRecyclerView.setAdapter(null);
                setLoaded(false);
            }
            /*
            if (getUserVisibleHint()) {
                mutableIsOfficial.setValue(MediaBrainzApp.getPreferences().isReleaseGroupOfficial());
                releaseGroupsVM.refresh();
                swipeRefreshLayout.setRefreshing(false);
                pagedRecycler.scrollToPosition(0);
            } else {
                pagedRecycler.setAdapter(null);
                setLoaded(false);
            }
            */
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaBrainzApp.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
