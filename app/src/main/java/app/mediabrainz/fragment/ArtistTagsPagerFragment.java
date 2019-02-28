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
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.viewmodel.ArtistTagsVM;
import app.mediabrainz.viewmodel.GenresVM;
import app.mediabrainz.viewmodel.TagsVM;
import app.mediabrainz.viewmodel.event.ArtistEvent;

import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.GENRES;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.TAGS;


public class ArtistTagsPagerFragment extends BaseFragment {

    private static final String TAGS_TAB = "ArtistTagsPagerFragment.TAGS_TAB";
    private final int defaultTagsTab = EditTagsPagerAdapter.TagsTab.GENRES.ordinal();

    private List<String> allGenres = new ArrayList<>();
    private Artist artist;
    private ArtistTagsVM artistTagsVM;
    private TagsVM tagsVM;
    private GenresVM genresVM;
    private int tagsTab;
    private ArrayAdapter<String> adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView loginWarningView;
    private AutoCompleteTextView tagInputView;
    private ImageButton tagButton;
    private ViewPager pagerView;
    private TabLayout tabsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.edit_tags_pager_fragment, container);

        if (savedInstanceState != null) {
            tagsTab = savedInstanceState.getInt(TAGS_TAB, defaultTagsTab);
        } else {
            tagsTab = defaultTagsTab;
        }

        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        loginWarningView = layout.findViewById(R.id.loginWarningView);
        tagInputView = layout.findViewById(R.id.tagInputView);
        tagButton = layout.findViewById(R.id.tagButton);

        pagerView = layout.findViewById(R.id.pagerView);
        tabsView = layout.findViewById(R.id.tabsView);

        setEditListeners();
        swipeRefreshLayout.setEnabled(false);
        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAGS_TAB, tagsTab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            setSubtitle(null);
            genresVM = getActivityViewModel(GenresVM.class);
            genresVM.getGenres();
            observeGenres();

            tagsVM = getActivityViewModel(TagsVM.class);
            artistTagsVM = getViewModel(ArtistTagsVM.class);
            getActivityViewModel(ArtistEvent.class).artist.observe(this, a -> {
                if (a != null) artistTagsVM.artistld.setValue(a);
            });
            artistTagsVM.artistld.observe(this, a -> {
                artist = a;
                setSubtitle(artist.getName());
                tagsVM.setTags(artist);
                configTags();
            });
            observeArtistTags();

            tagsVM.postTag.observe(this, tagVote -> {
                if (tagVote != null) postArtistTag(tagVote.getTag(), tagVote.getVoteType());
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        tagsTab = pagerView.getCurrentItem();
    }

    private void configTags() {
        EditTagsPagerAdapter pagerAdapter = new EditTagsPagerAdapter(getChildFragmentManager(), getResources());
        pagerView.setAdapter(pagerAdapter);
        pagerView.setOffscreenPageLimit(pagerAdapter.getCount());
        tabsView.setupWithViewPager(pagerView);
        tabsView.setTabMode(TabLayout.MODE_FIXED);
        pagerAdapter.setupTabViews(tabsView);
        pagerView.setCurrentItem(tagsTab);
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarningView.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        tagButton.setOnClickListener(v -> {
            if (swipeRefreshLayout.isRefreshing()) return;
            if (oauth.hasAccount()) {
                String tagString = tagInputView.getText().toString().trim();
                if (TextUtils.isEmpty(tagString)) {
                    tagInputView.setText("");
                } else {
                    tagsTab = allGenres.contains(tagString.toLowerCase()) ? GENRES.ordinal() : TAGS.ordinal();
                    postArtistTag(tagString, UserTagXML.VoteType.UPVOTE);
                }
            } else {
                navigate(R.id.action_global_loginFragment);
            }
        });
    }

    private void postArtistTag(String tag, UserTagXML.VoteType voteType) {
        artistTagsVM.postArtistTag(tag, voteType, MediaBrainzApp.getPreferences().isPropagateArtistTags());
    }

    private void observeGenres() {
        genresVM.progressld.observe(this, aBoolean -> swipeRefreshLayout.setRefreshing(aBoolean));

        genresVM.genresld.observe(this, genres -> {
            this.allGenres = genres;
            if (getContext() != null) {
                int size = allGenres.size();
                adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        allGenres.toArray(new String[size]));
                tagInputView.setThreshold(1);
                tagInputView.setAdapter(adapter);
            }
        });

        genresVM.errorld.observe(this, aBoolean -> {
            if (aBoolean) {
                showErrorSnackbar(R.string.connection_error, R.string.connection_error_retry, v -> genresVM.getGenres());
            } else {
                dismissErrorSnackbar();
            }
        });
    }

    private void observeArtistTags() {
        artistTagsVM.progressld.observe(this, aBoolean -> swipeRefreshLayout.setRefreshing(aBoolean));

        artistTagsVM.artistTags.observe(this, a -> {
            tagsVM.setTags(a);
            tagInputView.setText("");
            this.tagsTab = pagerView.getCurrentItem();
            configTags();
        });
        artistTagsVM.propagateEvent.observe(this, aBoolean ->
            showInfoSnackbar(aBoolean ? R.string.tag_propagated_to_albums : R.string.error_propagate_tag));

        artistTagsVM.errorTagld.observe(this, aBoolean -> {
            if (aBoolean) showInfoSnackbar(R.string.connection_error);
        });
        artistTagsVM.postArtistTagEvent.observe(this, aBoolean -> {
            if (!aBoolean) showInfoSnackbar(R.string.error_post_tag);
        });
    }

}
