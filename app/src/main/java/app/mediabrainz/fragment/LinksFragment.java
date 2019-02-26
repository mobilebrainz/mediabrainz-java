package app.mediabrainz.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.LinkAdapter;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.LinksVM;


public class LinksFragment extends BaseFragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.fragment_recycler_view, container);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            LinksFragmentArgs args = LinksFragmentArgs.fromBundle(getArguments());
            setSubtitle(args.getSubTitle());

            getActivityViewModel(LinksVM.class).urlsld.observe(this, urls -> {
                if (urls != null) {
                    if (urls.isEmpty()) {
                        showInfoSnackbar(R.string.no_results);
                    } else {
                        Collections.sort(urls);
                        LinkAdapter adapter = new LinkAdapter(urls);
                        adapter.setHolderClickListener(position ->
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position).getResource())))
                        );
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setItemViewCacheSize(100);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        }
    }

}
