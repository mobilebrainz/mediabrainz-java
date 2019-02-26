package app.mediabrainz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.mediabrainz.R;
import app.mediabrainz.api.model.Artist;


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
                    navigate(R.id.action_artistFragment_to_artistReleasesFragment);
                    break;
                case R.id.relationsItem:
                    navigate(R.id.action_artistFragment_to_artistRelationsFragment);
                    break;
                case R.id.tagsItem:
                    navigate(R.id.action_artistFragment_to_artistTagsPagerFragment);
                    break;
                case R.id.linksItem:
                    navigate(R.id.action_artistFragment_to_artistLinksFragment);
                    break;
                case R.id.wikiItem:
                    navigate(R.id.action_artistFragment_to_artistWikiFragment);
                    break;
                case R.id.addToCollectionItem:

                    break;
                case R.id.shareItem:

                    break;
            }
        }
    }
}
