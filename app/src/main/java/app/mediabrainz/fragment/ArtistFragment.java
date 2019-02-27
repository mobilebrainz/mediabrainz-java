package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import app.mediabrainz.NavGraphDirections;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Url;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.viewmodel.LinksVM;
import app.mediabrainz.viewmodel.WikiVM;
import app.mediabrainz.viewmodel.event.ArtistEvent;
import app.mediabrainz.viewmodel.event.ArtistRelationsEvent;
import app.mediabrainz.viewmodel.event.LinksEvent;


public class ArtistFragment extends BaseArtistFragment implements
        View.OnClickListener {

    public static final String TAG = "ArtistFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.artist_fragment, container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        initTopMenu(view);
        return view;
    }

    private void initTopMenu(View view) {
        view.findViewById(R.id.releasesItem).setOnClickListener(this);
        view.findViewById(R.id.relationsItem).setOnClickListener(this);
        view.findViewById(R.id.tagsItem).setOnClickListener(this);
        view.findViewById(R.id.linksItem).setOnClickListener(this);
        view.findViewById(R.id.wikiItem).setOnClickListener(this);
        view.findViewById(R.id.addToCollectionItem).setOnClickListener(this);
        view.findViewById(R.id.shareItem).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            ArtistFragmentArgs args = ArtistFragmentArgs.fromBundle(getArguments());
            artistVM.getArtist(args.getMbid());
        }
    }

    @Override
    protected void show(Artist artist) {
        if (getView() != null) {
            TextView artistNameView = getView().findViewById(R.id.artistNameView);
            artistNameView.setText(artist.getName());
        }
    }

    @Override
    public void onClick(View v) {
        if (!isLoading && !isError) {
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
