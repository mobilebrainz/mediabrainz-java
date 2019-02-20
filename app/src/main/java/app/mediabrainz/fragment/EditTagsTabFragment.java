package app.mediabrainz.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.adapter.recycler.TagAdapter;
import app.mediabrainz.api.model.Tag;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.TagsVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class EditTagsTabFragment extends BaseFragment {

    public interface TagInterface {
        void postTag(String tag, UserTagXML.VoteType voteType, int tagsTab);
    }

    private static final String TAGS_TAB = "EditTagsTabFragment.TAGS_TAB";

    private TagsVM tagsVM;
    private int tagsTab = 0;

    private View noresultsView;
    private RecyclerView recyclerView;

    public static EditTagsTabFragment newInstance(int tagsTab) {
        Bundle args = new Bundle();
        args.putInt(TAGS_TAB, tagsTab);
        EditTagsTabFragment fragment = new EditTagsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_recycler_view, container);

        noresultsView = layout.findViewById(R.id.noresultsView);
        recyclerView = layout.findViewById(R.id.recyclerView);

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            tagsTab = getArguments().getInt(TAGS_TAB);
            tagsVM = getActivityViewModel(TagsVM.class);
            load();
        }
    }

    private void configRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
    }

    private void load() {
        noresultsView.setVisibility(View.GONE);

        final List<Tag> tags = new ArrayList<>();
        final List<Tag> userTags = new ArrayList<>();
        final List<Tag> parentUserGenres = tagsVM.getUserItemGenres();
        final List<Tag> parentUserTags = tagsVM.getUserItemTags();

        EditTagsPagerAdapter.TagsTab tagType = EditTagsPagerAdapter.TagsTab.values()[tagsTab];
        switch (tagType) {
            case TAGS:
                List<Tag> genres = tagsVM.getItemGenres();
                for (Tag tag : tagsVM.getItemtags()) {
                    if (!genres.contains(tag)) {
                        tags.add(tag);
                    }
                }
                if (parentUserGenres != null && parentUserTags != null) {
                    for (Tag tag : parentUserTags) {
                        if (!parentUserGenres.contains(tag)) {
                            userTags.add(tag);
                        }
                    }
                }
                break;
            case GENRES:
                tags.addAll(tagsVM.getItemGenres());
                if (parentUserGenres != null) {
                    userTags.addAll(parentUserGenres);
                }
                break;
        }

        if (!tags.isEmpty()) {
            configRecycler();
            TagAdapter adapter = new TagAdapter(tags, userTags);

            adapter.setHolderClickListener(pos -> {
                //((OnTagCommunicator) getContext()).onTag(tags.get(pos).getName(), tagType.equals(EditTagsPagerAdapter.TagsTab.GENRES));
            });
            recyclerView.setAdapter(adapter);

            TagInterface parent = (TagInterface) getParentFragment();
            adapter.setOnVoteTagListener((position) -> {
                if (oauth.hasAccount()) {
                    String tag = tags.get(position).getName();
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.show();
                    Window win = alertDialog.getWindow();
                    if (win != null) {
                        win.setContentView(R.layout.dialog_vote_tag);
                        ImageView voteUpButton = win.findViewById(R.id.voteUpButton);

                        voteUpButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.UPVOTE, tagsTab);
                        });

                        ImageView voteWithdrawButton = win.findViewById(R.id.voteWithdrawButton);
                        voteWithdrawButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.WITHDRAW, tagsTab);
                        });

                        ImageView voteDownButton = win.findViewById(R.id.voteDownButton);
                        voteDownButton.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            parent.postTag(tag, UserTagXML.VoteType.DOWNVOTE, tagsTab);
                        });
                    }
                } else {
                    //ActivityFactory.startLoginActivity(getContext());
                }
            });
        }
    }

}
