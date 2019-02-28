package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.PagedReleaseAdapter;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.core.adapter.RetryCallback;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.viewmodel.event.Status;
import app.mediabrainz.viewmodel.ReleasesVM;


public class ReleasesFragment extends BaseFragment implements
        RetryCallback {

    public static final int ALBUM_TYPE = 1;
    public static final int RECORDING_TYPE = 2;

    private boolean isLoading;
    private int type = 0;
    private String mbid;
    private String releaseMbid;

    private ReleasesVM releasesVM;
    private PagedReleaseAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_paged_recycler, container);

        pagedRecyclerView = view.findViewById(R.id.pagedRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            setSubtitle(null);
            ReleasesFragmentArgs args = ReleasesFragmentArgs.fromBundle(getArguments());
            mbid = args.getMbid();
            type = args.getType();
            releaseMbid = args.getReleaseMbid();
            load();
        }
    }

    public void load() {
        ReleaseBrowseService.ReleaseBrowseEntityType entityType = null;
        switch (type) {
            case ALBUM_TYPE:
                entityType = ReleaseBrowseService.ReleaseBrowseEntityType.RELEASE_GROUP;
                break;

            case RECORDING_TYPE:
                entityType = ReleaseBrowseService.ReleaseBrowseEntityType.RECORDING;
                break;
        }

        adapter = new PagedReleaseAdapter(this, releaseMbid);
        adapter.setHolderClickListener(r -> {
            //((OnReleaseCommunicator) getContext()).onRelease(r.getId());
        });

        releasesVM = getViewModel(ReleasesVM.class);
        releasesVM.load(mbid, entityType);
        releasesVM.realesesLiveData.observe(this, adapter::submitList);
        releasesVM.getNetworkState().observe(this, adapter::setNetworkState);

        pagedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pagedRecyclerView.setNestedScrollingEnabled(true);
        pagedRecyclerView.setItemViewCacheSize(100);
        pagedRecyclerView.setHasFixedSize(true);
        pagedRecyclerView.setAdapter(adapter);

        initSwipeToRefresh();
    }

    private void initSwipeToRefresh() {
        releasesVM.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {
                showError(networkState.getStatus() == Status.ERROR);
                isLoading = networkState.getStatus() == Status.LOADING;
                swipeRefreshLayout.setRefreshing(isLoading);

                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    pagedRecyclerView.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) {
                releasesVM.refresh();
                pagedRecyclerView.scrollToPosition(0);
            }
        });
    }

    private void showError(boolean show) {
        if (show) {
            showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> retry());
        } else {
            dismissErrorSnackbar();
        }
    }

    @Override
    public void retry() {
        if (releasesVM != null) {
            releasesVM.retry();
        }
    }

}
