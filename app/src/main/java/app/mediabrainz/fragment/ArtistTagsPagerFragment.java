package app.mediabrainz.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.viewmodel.TagsVM;

import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.GENRES;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.TAGS;


public class ArtistTagsPagerFragment extends BaseArtistFragment implements
        EditTagsTabFragment.TagInterface {

    private ArrayAdapter<String> adapter;

    private List<String> allGenres = new ArrayList<>();
    private Artist artist;
    private TagsVM tagsVM;
    private int tagsTab = EditTagsPagerAdapter.TagsTab.GENRES.ordinal();

    private TextView loginWarningView;
    private AutoCompleteTextView tagInputView;
    private ImageButton tagButton;

    private ViewPager pagerView;
    private TabLayout tabsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_edit_tags_pager, container);

        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        tagInputView = layout.findViewById(R.id.tagInputView);
        tagButton = layout.findViewById(R.id.tagButton);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        setEditListeners();
        return layout;
    }

    @Override
    protected void show(Artist artist) {
        this.artist = artist;

        tagsVM = getActivityViewModel(TagsVM.class);
        observe();
        tagsVM.setItemtags(artist.getTags());
        tagsVM.setUserItemTags(artist.getUserTags());
        tagsVM.setItemGenres(artist.getGenres());
        tagsVM.setUserItemGenres(artist.getUserGenres());

        EditTagsPagerAdapter pagerAdapter = new EditTagsPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        tabsView.setTabMode(TabLayout.MODE_FIXED);
        pagerAdapter.setupTabViews(tabsView);
        pagerView.setCurrentItem(tagsTab);

        tagsVM.genresld.observe(this, genres -> {
            this.allGenres = genres;
            adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    allGenres.toArray(new String[allGenres.size()]));
            tagInputView.setThreshold(1);
            tagInputView.setAdapter(adapter);
        });
        tagsVM.getGenres();
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        tagButton.setOnClickListener(v -> {
            if (isLoading) return;
            if (oauth.hasAccount()) {
                String tagString = tagInputView.getText().toString().trim();
                if (TextUtils.isEmpty(tagString)) {
                    tagInputView.setText("");
                } else {
                    tagsTab = allGenres.contains(tagString.toLowerCase()) ? GENRES.ordinal() : TAGS.ordinal();
                    postArtistTag(tagString, UserTagXML.VoteType.UPVOTE);
                }
            } else {
                //ActivityFactory.startLoginActivity(getContext());
            }
        });
    }

    @Override
    public void postTag(String tag, UserTagXML.VoteType voteType, int tagsTab) {
        this.tagsTab = tagsTab;
        postArtistTag(tag, voteType);
    }

    private void observe() {
        tagsVM.artistTags.observe(this, a -> {
            artist.setTags(a.getTags());
            artist.setUserTags(a.getUserTags());
            artist.setGenres(a.getGenres());
            artist.setUserGenres(a.getUserGenres());
            tagInputView.setText("");
            show(artist);
        });

        tagsVM.postArtistTagEvent.observe(this, aBoolean -> {
            if (!aBoolean) {
                snackbarNotAction(swipeRefreshLayout, R.string.error_post_tag);
            }
        });

        tagsVM.propagateEvent.observe(this, aBoolean -> {
            if (aBoolean) {
                snackbarNotAction(swipeRefreshLayout, R.string.tag_propagated_to_albums);
            } else {
                snackbarNotAction(swipeRefreshLayout, R.string.error_post_tag);
            }
        });
    }

    private void postArtistTag(String tag, UserTagXML.VoteType voteType) {
        tagsVM.postArtistTag(artist.getId(), tag, voteType, MediaBrainzApp.getPreferences().isPropagateArtistTags());
    }

}
