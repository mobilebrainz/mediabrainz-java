package app.mediabrainz.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.ReleaseGroupVM;


public class ReleaseGroupFragment extends BaseFragment implements
        View.OnClickListener {

    public static final String TAG = "ReleaseGroupF";

    private Release release;
    private ReleaseGroupVM releaseGroupVM;
    private boolean isError;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.release_group_fragment, container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setSubtitle(null);
        if (getActivity() != null && getArguments() != null) {
            releaseGroupVM = getViewModel(ReleaseGroupVM.class);
            observeReleaseGroupVM();

            if (swipeRefreshLayout != null) {
                //swipeRefreshLayout.setOnRefreshListener(() -> artistVM.refreshArtist());
            }
            ReleaseGroupFragmentArgs args = ReleaseGroupFragmentArgs.fromBundle(getArguments());
            releaseGroupVM.getRelease(args.getMbid());
            initMenu();
        }
    }

    public void observeReleaseGroupVM() {
        releaseGroupVM.progressld.observe(this, swipeRefreshLayout::setRefreshing);
        releaseGroupVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean) {
                showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> releaseGroupVM.refreshRelease());
            } else {
                dismissErrorSnackbar();
            }
        });
        releaseGroupVM.releaseld.observe(this, r -> {
            release = r;
            releaseGroupVM.getReleaseGroup(r.getReleaseGroup().getId());
        });
        releaseGroupVM.releaseGroupld.observe(this, rg -> {
            release.setReleaseGroup(rg);
            if (!rg.getArtistCredits().isEmpty()) {
                Artist.ArtistCredit artistCredit = rg.getArtistCredits().get(0);
                setTitle(artistCredit.getArtist().getName());
                //toolbarTopTitleView.setOnClickListener(v -> onArtist(artistCredit.getArtist().getId()));
            }
            if (!TextUtils.isEmpty(rg.getTitle())) {
                setSubtitle(rg.getTitle());
            }
            show();
            insertNestedFragments();
        });

    }

    private void initMenu() {
        if (getView() != null) {
            getView().findViewById(R.id.tracksItem).setOnClickListener(this);
            getView().findViewById(R.id.releasesItem).setOnClickListener(this);
            getView().findViewById(R.id.creditsItem).setOnClickListener(this);
            getView().findViewById(R.id.tagsItem).setOnClickListener(this);
            getView().findViewById(R.id.linksItem).setOnClickListener(this);
            getView().findViewById(R.id.wikiItem).setOnClickListener(this);
            getView().findViewById(R.id.coverArtsItem).setOnClickListener(this);
            getView().findViewById(R.id.addToCollectionItem).setOnClickListener(this);
            getView().findViewById(R.id.shareItem).setOnClickListener(this);
        }
    }

    private void show() {
        if (getView() != null) {

        }
    }

    private void insertNestedFragments() {
        /*
        getActivityViewModel(ReleaseGroupRatingsVM.class).artist.setValue(artist);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.artistRatingsFragment, ArtistRatingsFragment.newInstance())
                .commit();
        */
    }

    @Override
    public void onClick(View v) {
        if (!swipeRefreshLayout.isRefreshing() && !isError) {
            switch (v.getId()) {
                case R.id.tracksItem:

                    break;

                case R.id.releasesItem:

                    break;

                case R.id.creditsItem:

                    break;

                case R.id.tagsItem:

                    break;

                case R.id.linksItem:

                    break;

                case R.id.wikiItem:

                    break;

                case R.id.coverArtsItem:

                    break;

                case R.id.addToCollectionItem:

                    break;

                case R.id.shareItem:

                    break;
            }
        }
    }

}
