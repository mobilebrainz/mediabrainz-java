package app.mediabrainz.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.ReleaseGroupsPagerAdapter;
import app.mediabrainz.adapter.recycler.ReleaseGroupsAdapter;
import app.mediabrainz.core.adapter.RetryCallback;
import app.mediabrainz.core.fragment.LazyFragment;
import app.mediabrainz.core.viewmodel.ReleaseGroupsVM;
import app.mediabrainz.core.viewmodel.event.Status;
import app.mediabrainz.viewmodel.MainVM;


import static app.mediabrainz.account.Preferences.PreferenceName.RELEASE_GROUP_OFFICIAL;


public class ReleaseGroupsTabFragment extends LazyFragment implements
        RetryCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String RELEASES_TAB = "RELEASES_TAB";

    private String artistMbid;
    private ReleaseGroupsPagerAdapter.ReleaseTab releaseGroupType;
    private ReleaseGroupsVM releaseGroupsVM;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;
    private ReleaseGroupsAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkStateView;

    private MutableLiveData<Boolean> mutableIsOfficial = new MutableLiveData<>();

    public static ReleaseGroupsTabFragment newInstance(int releasesTab) {
        Bundle args = new Bundle();
        args.putInt(RELEASES_TAB, releasesTab);
        ReleaseGroupsTabFragment fragment = new ReleaseGroupsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_paged_recycler, container);

        releaseGroupType = ReleaseGroupsPagerAdapter.ReleaseTab.values()[getArguments().getInt(RELEASES_TAB)];

        pagedRecyclerView = layout.findViewById(R.id.pagedRecyclerView);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkStateView = layout.findViewById(R.id.itemNetworkStateView);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            artistMbid = getActivityViewModel(MainVM.class).getArtistMbid();
            if (!TextUtils.isEmpty(artistMbid)) {
                loadView();
            }
        }
    }

    @Override
    protected void lazyLoad() {
        adapter = new ReleaseGroupsAdapter(this);
        adapter.setHolderClickListener(releaseGroup -> {
            //if (getContext() instanceof OnReleaseGroupCommunicator) {
                //((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroup.getId());
            //}
        });

        releaseGroupsVM = ViewModelProviders.of(this).get(ReleaseGroupsVM.class);
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

    private void initSwipeToRefresh() {
        releaseGroupsVM.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkStateView.setVisibility(View.VISIBLE);

                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }

                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.ERROR ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.LOADING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecyclerView.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            releaseGroupsVM.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecyclerView.scrollToPosition(0);
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
