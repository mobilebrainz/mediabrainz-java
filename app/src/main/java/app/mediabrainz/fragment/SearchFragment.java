package app.mediabrainz.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.SuggestionListAdapter;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.util.UiUtils;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.viewmodel.TagsVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class SearchFragment extends BaseFragment {

    private static final String TAG = "SearchFragment";

    private List<String> genres = new ArrayList<>();
    private TagsVM tagsVM;
    private boolean isLoading;
    private boolean isError;

    private AutoCompleteTextView artistFieldView;
    private AutoCompleteTextView albumFieldView;
    private AutoCompleteTextView trackFieldView;
    private AutoCompleteTextView queryInputView;
    private Spinner searchSpinner;
    private ArrayAdapter<String> adapter;
    protected SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.search_fragment, container);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        artistFieldView = view.findViewById(R.id.artistFieldView);
        albumFieldView = view.findViewById(R.id.albumFieldView);
        trackFieldView = view.findViewById(R.id.trackFieldView);
        searchSpinner = view.findViewById(R.id.searchSpinner);
        queryInputView = view.findViewById(R.id.queryInputView);

        queryInputView.setOnEditorActionListener((v, actionId, event) -> selectedSearch());
        view.findViewById(R.id.selectedSearchButton).setOnClickListener(v -> selectedSearch());
        view.findViewById(R.id.inputSearchButton).setOnClickListener(v -> inputSearch());

        if (!oauth.hasAccount()) {
            View logInButton = view.findViewById(R.id.logInButton);
            logInButton.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(v -> {
                if (!isLoading && !isError) {
                    Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_loginFragment);
                }
            });
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            tagsVM = getActivityViewModel(TagsVM.class);
            observe();
            setupSearchTypeSpinner();
        }
    }

    private void observe() {
        tagsVM.genresld.observe(this, genres -> this.genres = genres);
        tagsVM.progressld.observe(this, aBoolean -> {
            isLoading = aBoolean;
            swipeRefreshLayout.setRefreshing(aBoolean);
        });
        tagsVM.errorld.observe(this, aBoolean -> {
            isError = aBoolean;
            if (aBoolean) {
                snackbarWithAction(swipeRefreshLayout, R.string.connection_error, R.string.connection_error_retry,
                        v -> tagsVM.getGenres());
            } else if (getErrorSnackbar() != null && getErrorSnackbar().isShown()) {
                getErrorSnackbar().dismiss();
            }
        });
    }

    private void setupSearchTypeSpinner() {
        List<CharSequence> types = new ArrayList<>();
        for (SearchType searchType : SearchType.values()) {
            types.add(getResources().getText(searchType.getRes()));
        }
        ArrayAdapter<CharSequence> typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(typeAdapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (SearchType.TAG.ordinal() == pos && tagsVM.getGenres() == null) return;

                if (getContext() != null) {
                    //todo: не подкидывает теги, а только жанры
                    if (SearchType.TAG.ordinal() == pos && !genres.isEmpty()) {
                        if (adapter == null) {
                            int size = genres.size();
                            adapter = new ArrayAdapter<>(
                                    Objects.requireNonNull(getContext()),
                                    android.R.layout.simple_dropdown_item_1line,
                                    genres.toArray(new String[size]));
                        }
                        queryInputView.setThreshold(1);
                        queryInputView.setAdapter(adapter);
                    } else if (SearchType.USER.ordinal() == pos && MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
                        queryInputView.setThreshold(2);
                        queryInputView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.USER));
                    } else {
                        queryInputView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private boolean selectedSearch() {
        if (isLoading || isError) return false;

        String query = queryInputView.getText().toString().trim().toLowerCase();
        if (!TextUtils.isEmpty(query)) {
            if (getActivity() != null) {
                UiUtils.hideKeyboard(getActivity());
            }
            if (genres.contains(query)) {
                //ActivityFactory.startTagActivity(getContext(), query, true);
            } else {
                SearchFragmentDirections.ActionSearchFragmentToResultSearchFragment action = SearchFragmentDirections.actionSearchFragmentToResultSearchFragment(
                        null, null, null, query);
                action.setSearchType(SearchType.values()[searchSpinner.getSelectedItemPosition()].ordinal());
                Navigation.findNavController(queryInputView).navigate(action);
            }
        }
        return false;
    }

    private void inputSearch() {
        if (isLoading || isError) return;

        String artist = artistFieldView.getText().toString().trim();
        String album = albumFieldView.getText().toString().trim();
        String track = trackFieldView.getText().toString().trim();

        if (!TextUtils.isEmpty(track) || !TextUtils.isEmpty(album) || !TextUtils.isEmpty(artist)) {
            if (getActivity() != null) {
                UiUtils.hideKeyboard(getActivity());
            }
            SearchFragmentDirections.ActionSearchFragmentToResultSearchFragment action = SearchFragmentDirections.actionSearchFragmentToResultSearchFragment(
                    artist, album, track, null);
            Navigation.findNavController(artistFieldView).navigate(action);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            if (MediaBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
                artistFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.ARTIST));
                albumFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.ALBUM));
                trackFieldView.setAdapter(new SuggestionListAdapter(getContext(), Suggestion.SuggestionField.TRACK));
            } else {
                artistFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
                albumFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
                trackFieldView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.layout_dropdown_item, new String[]{}));
            }
        }
    }

}
