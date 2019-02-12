package app.mediabrainz.viewmodels;

import androidx.annotation.NonNull;
import app.mediabrainz.core.viewmodel.BaseViewModel;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.data.room.repository.SuggestionRepository;


public class SearchVM extends BaseViewModel {

    private final SuggestionRepository suggestionRepository = new SuggestionRepository();

    public void insertSuggestion(@NonNull String word, @NonNull Suggestion.SuggestionField suggestionField) {
        suggestionRepository.insert(new Suggestion(word, suggestionField));
    }

}
