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
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.pager.EditTagsPagerAdapter;
import app.mediabrainz.api.model.Artist;
import app.mediabrainz.api.model.xml.UserTagXML;
import app.mediabrainz.viewmodel.GenresVM;
import app.mediabrainz.viewmodel.TagsVM;

import static app.mediabrainz.MediaBrainzApp.oauth;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.GENRES;
import static app.mediabrainz.adapter.pager.EditTagsPagerAdapter.TagsTab.TAGS;


public class ArtistTagsPagerFragment extends BaseArtistFragment implements
        EditTagsTabFragment.TagInterface {

    private static final String TAGS_TAB = "ArtistTagsPagerFragment.TAGS_TAB";
    private final int defaultTagsTab = EditTagsPagerAdapter.TagsTab.GENRES.ordinal();

    private List<String> allGenres = new ArrayList<>();
    private Artist artist;
    private TagsVM tagsVM;
    private GenresVM genresVM;
    private int tagsTab;
    private ArrayAdapter<String> adapter;

    private TextView loginWarningView;
    private AutoCompleteTextView tagInputView;
    private ImageButton tagButton;
    private ViewPager pagerView;
    private TabLayout tabsView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflate(R.layout.fragment_edit_tags_pager, container);

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
        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAGS_TAB, tagsTab);
    }

    @Override
    public void onPause() {
        super.onPause();
        tagsTab = pagerView.getCurrentItem();
    }

    @Override
    protected void show(Artist artist) {
        this.artist = artist;

        genresVM = getActivityViewModel(GenresVM.class);
        genresVM.getGenres();
        observeGenres();

        tagsVM = getActivityViewModel(TagsVM.class);
        observeTags();
        configTags();
    }

    private void configTags() {
        tagsVM.setTags(artist);

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
                Navigation.findNavController(v).navigate(R.id.action_global_loginFragment);
            }
        });
    }

    @Override
    public void postTag(String tag, UserTagXML.VoteType voteType) {
        postArtistTag(tag, voteType);
    }

    private void postArtistTag(String tag, UserTagXML.VoteType voteType) {
        tagsVM.postArtistTag(artist.getId(), tag, voteType, MediaBrainzApp.getPreferences().isPropagateArtistTags());
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
                snackbarWithAction(swipeRefreshLayout, R.string.connection_error, R.string.connection_error_retry,
                        v -> genresVM.getGenres());
            } else if (getErrorSnackbar() != null && getErrorSnackbar().isShown()) {
                getErrorSnackbar().dismiss();
            }
        });
    }

    private void observeTags() {
        tagsVM.progressld.observe(this, aBoolean -> swipeRefreshLayout.setRefreshing(aBoolean));
        tagsVM.artistTags.observe(this, a -> {
            artist.setTags(a.getTags());
            artist.setUserTags(a.getUserTags());
            artist.setGenres(a.getGenres());
            artist.setUserGenres(a.getUserGenres());
            tagInputView.setText("");
            this.tagsTab = pagerView.getCurrentItem();
            configTags();
        });
        tagsVM.propagateEvent.observe(this, aBoolean -> {
            snackbarNotAction(swipeRefreshLayout, aBoolean ? R.string.tag_propagated_to_albums : R.string.error_propagate_tag);
        });
        tagsVM.errorTagld.observe(this, aBoolean -> {
            if (aBoolean) snackbarNotAction(swipeRefreshLayout, R.string.connection_error);
        });
        tagsVM.postArtistTagEvent.observe(this, aBoolean -> {
            if (!aBoolean) snackbarNotAction(swipeRefreshLayout, R.string.error_post_tag);
        });
    }

}
