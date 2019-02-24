package app.mediabrainz.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.LinkAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.RelationExtractor;
import app.mediabrainz.api.model.Url;


public class ArtistLinksFragment extends BaseArtistFragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_swipe_recycler_view, container);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        return view;
    }

    @Override
    protected void show(Artist artist) {
        List<Url> urls = new RelationExtractor(artist).getUrls();
        if (urls != null) {
            Collections.sort(urls);
            if (urls.isEmpty()) {
                showInfoSnackbar(swipeRefreshLayout, R.string.no_results);
            } else {
                configLinksRecycler();
                LinkAdapter adapter = new LinkAdapter(urls);
                adapter.setHolderClickListener(position ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position).getResource())))
                );
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void configLinksRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

}
