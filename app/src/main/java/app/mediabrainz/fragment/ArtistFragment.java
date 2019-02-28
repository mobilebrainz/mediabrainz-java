package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.NavGraphDirections;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.ArtistVM;
import app.mediabrainz.viewmodel.event.ArtistEvent;
import app.mediabrainz.viewmodel.event.ArtistRelationsEvent;
import app.mediabrainz.viewmodel.event.LinksEvent;


public class ArtistFragment extends BaseFragment implements
        View.OnClickListener {

    public static final String TAG = "ArtistFragment";

    private Artist artist;
    private ArtistVM artistVM;
    private boolean isError;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.artist_fragment, container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        return view;
    }

    private void initMenu() {
        if (getView() != null) {
            getView().findViewById(R.id.releasesItem).setOnClickListener(this);
            getView().findViewById(R.id.relationsItem).setOnClickListener(this);
            getView().findViewById(R.id.tagsItem).setOnClickListener(this);
            getView().findViewById(R.id.linksItem).setOnClickListener(this);
            getView().findViewById(R.id.wikiItem).setOnClickListener(this);
            getView().findViewById(R.id.addToCollectionItem).setOnClickListener(this);
            getView().findViewById(R.id.shareItem).setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            setSubtitle(null);
            artistVM = getViewModel(ArtistVM.class);
            observeArtistVM();

            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setOnRefreshListener(() -> artistVM.refreshArtist());
            }

            ArtistFragmentArgs args = ArtistFragmentArgs.fromBundle(getArguments());
            artistVM.getArtist(args.getMbid());
            initMenu();
            insertNestedFragments();
        }
    }

    private void insertNestedFragments() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.artistRatingsFragment, ArtistRatingsFragment.newInstance())
                .commit();
    }

    private void show(Artist artist) {
        if (getView() != null) {
            TextView artistNameView = getView().findViewById(R.id.artistNameView);
            artistNameView.setText(artist.getName());
        }
    }

    private void observeArtistVM() {
        artistVM.progressld.observe(this, swipeRefreshLayout::setRefreshing);

        artistVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean) {
                showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> artistVM.refreshArtist());
            } else {
                dismissErrorSnackbar();
            }
        });
        artistVM.artistld.observe(this, artist -> {
            if (artist != null && getActivity() != null && getActivity() instanceof AppCompatActivity) {
                this.artist = artist;
                setSubtitle(artist.getName());
                show(artist);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!swipeRefreshLayout.isRefreshing() && !isError) {
            switch (v.getId()) {
                case R.id.releasesItem:
                    if (artist.getReleaseGroups() != null && !artist.getReleaseGroups().isEmpty()) {
                        ArtistFragmentDirections.ActionArtistFragmentToArtistReleasesFragment releasesAction =
                                ArtistFragmentDirections.actionArtistFragmentToArtistReleasesFragment(artist.getId(), artist.getName());
                        navigate(releasesAction);
                    } else {
                        showInfoSnackbar(R.string.no_results);
                    }
                    break;

                case R.id.relationsItem:
                    List<Relation> relations = new RelationExtractor(artist).getArtistRelations();
                    if (!relations.isEmpty()) {
                        getActivityViewModel(ArtistRelationsEvent.class).relations.setValue(relations);
                        ArtistFragmentDirections.ActionArtistFragmentToArtistRelationsFragment relActin =
                                ArtistFragmentDirections.actionArtistFragmentToArtistRelationsFragment(artist.getName());
                        navigate(relActin);
                    } else {
                        showInfoSnackbar(R.string.no_results);
                    }
                    break;

                case R.id.tagsItem:
                    getActivityViewModel(ArtistEvent.class).artist.setValue(artist);
                    navigate(R.id.action_artistFragment_to_artistTagsPagerFragment);
                    break;

                case R.id.linksItem:
                    List<Url> urls = new RelationExtractor(artist).getUrls();
                    if (!urls.isEmpty()) {
                        getActivityViewModel(LinksEvent.class).urls.setValue(urls);
                        NavGraphDirections.ActionGlobalLinksFragment linksAction =
                                NavGraphDirections.actionGlobalLinksFragment(artist.getName());
                        navigate(linksAction);
                    } else {
                        showInfoSnackbar(R.string.no_results);
                    }
                    break;

                case R.id.wikiItem:
                    getActivityViewModel(LinksEvent.class).urls.setValue(new RelationExtractor(artist).getUrls());
                    NavGraphDirections.ActionGlobalWikiFragment wikiAction =
                            NavGraphDirections.actionGlobalWikiFragment(artist.getName());
                    navigate(wikiAction);
                    break;

                case R.id.addToCollectionItem:

                    break;
                case R.id.shareItem:

                    break;
            }
        }
    }
}
