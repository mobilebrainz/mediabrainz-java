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
import app.mediabrainz.NavGraphDirections;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.PagedReleaseAdapter;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.core.adapter.RetryCallback;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.viewmodel.event.Status;
import app.mediabrainz.viewmodel.ReleasesVM;


public class ReleasesFragment extends BaseFragment implements
        RetryCallback {

    private boolean isLoading;
    private String mbid;
    private String releaseMbid;
    private ReleaseBrowseService.ReleaseBrowseEntityType entityType;

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
            entityType = ReleaseBrowseService.ReleaseBrowseEntityType.values()[args.getType()];
            releaseMbid = args.getReleaseMbid();
            load();
        }
    }

    public void load() {
        adapter = new PagedReleaseAdapter(this, releaseMbid);
        adapter.setHolderClickListener(r -> {
            ReleasesFragmentDirections.ActionReleasesFragmenttToReleaseGroupFragment action =
                    ReleasesFragmentDirections.actionReleasesFragmenttToReleaseGroupFragment(r.getId());
            navigate(action);
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
