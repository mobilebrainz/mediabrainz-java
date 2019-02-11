package app.mediabrainz.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import app.mediabrainz.MediaBrainzApp;
import app.mediabrainz.R;
import app.mediabrainz.adapter.SuggestionListAdapter;
import app.mediabrainz.core.fragment.BaseFragment;
import app.mediabrainz.core.util.UiUtils;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.viewmodels.MainVM;
import app.mediabrainz.viewmodels.SearchVM;

import static app.mediabrainz.MediaBrainzApp.oauth;


public class SearchFragment extends BaseFragment {

    private static final String TAG = "SearchFragment";

    private List<String> genres = new ArrayList<>();
    private MainVM mainVM;
    private boolean isLoading;
    private boolean isError;

    private View contentView;
    private View errorView;
    private View progressView;
    private View logInButton;
    private AutoCompleteTextView artistFieldView;
    private AutoCompleteTextView albumFieldView;
    private AutoCompleteTextView trackFieldView;
    private AutoCompleteTextView queryInputView;
    private Spinner searchSpinner;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflate(R.layout.search_fragment, container);

        contentView = view.findViewById(R.id.contentView);
        logInButton = view.findViewById(R.id.logInButton);
        errorView = view.findViewById(R.id.errorView);
        progressView = view.findViewById(R.id.progressView);
        artistFieldView = view.findViewById(R.id.artistFieldView);
        albumFieldView = view.findViewById(R.id.albumFieldView);
        trackFieldView = view.findViewById(R.id.trackFieldView);
        searchSpinner = view.findViewById(R.id.searchSpinner);
        queryInputView = view.findViewById(R.id.queryInputView);

        queryInputView.setOnEditorActionListener((v, actionId, event) -> selectedSearch());
        view.findViewById(R.id.selectedSearchButton).setOnClickListener(v -> selectedSearch());
        view.findViewById(R.id.inputSearchButton).setOnClickListener(v -> inputSearch());

        if (!oauth.hasAccount()) {
            logInButton.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(v -> {
                if (!isLoading && !isError) {
                    Navigation.findNavController(v).navigate(R.id.action_searchFragment2_to_loginFragment);
                }
            });
        }
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainVM = getActivityViewModel(MainVM.class);
        mainVM.genresResource.observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    viewProgressLoading(true);
                    break;
                case ERROR:
                    showConnectionWarning(resource.getThrowable());
                    break;
                case SUCCESS:
                    viewProgressLoading(false);
                    genres = resource.getData();
                    break;
            }
        });

        setupSearchTypeSpinner();
        if (checkNetworkConnection()) load();
        else showConnectionWarning(null);
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
                if (getContext() != null) {
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
        String query = queryInputView.getText().toString().trim().toLowerCase();
        if (!TextUtils.isEmpty(query)) {
            if (getActivity() != null) {
                UiUtils.hideKeyboard(getActivity());
            }
            if (genres.contains(query)) {
                Log.i(TAG, "selectedSearch: ");
                //ActivityFactory.startTagActivity(getContext(), query, true);
            } else {
                Log.i(TAG, "selectedSearch: ");
                SearchType searchType = SearchType.values()[searchSpinner.getSelectedItemPosition()];
                //((SelectedSearchFragmentListener) getContext()).searchType(searchType, query);
            }
        }
        return false;
    }

    private void inputSearch() {
        String artist = artistFieldView.getText().toString().trim();
        String album = albumFieldView.getText().toString().trim();
        String track = trackFieldView.getText().toString().trim();

        if (!TextUtils.isEmpty(track) || !TextUtils.isEmpty(album) || !TextUtils.isEmpty(artist)) {
            if (getActivity() != null) {
                UiUtils.hideKeyboard(getActivity());
            }
            Log.i(TAG, "inputSearch: ");
            //((SearchFragmentListener) getContext()).searchEntity(artist, album, track);
        }
    }

    private void load() {
        viewError(false);
        mainVM.getGenres();
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

    private void showConnectionWarning(Throwable t) {
        viewProgressLoading(false);
        viewError(true);
        errorView.findViewById(R.id.retryButton).setOnClickListener(v -> load());
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            contentView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            errorView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            contentView.setAlpha(0.25F);
            progressView.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            contentView.setAlpha(1.0F);
            progressView.setVisibility(View.GONE);
        }
    }
}
